package com.audrey.refeat.domain.chat.service;

import com.audrey.refeat.common.component.AuthComponent;
import com.audrey.refeat.common.component.RequestComponent;
import com.audrey.refeat.common.exception.CustomException;
import com.audrey.refeat.common.exception.ErrorCode;
import com.audrey.refeat.common.response.RestResponse;
import com.audrey.refeat.common.s3.S3Component;
import com.audrey.refeat.domain.chat.dto.request.AIQueryRequestDto;
import com.audrey.refeat.domain.chat.dto.request.SendAiMessageDto;
import com.audrey.refeat.domain.chat.dto.request.SendMessageDto;
import com.audrey.refeat.domain.chat.dto.response.*;
import com.audrey.refeat.domain.chat.entity.Chat;
import com.audrey.refeat.domain.chat.entity.dao.ChatRepository;
import com.audrey.refeat.domain.chat.entity.enums.Language;
import com.audrey.refeat.domain.project.entity.Document;
import com.audrey.refeat.domain.project.entity.Project;
import com.audrey.refeat.domain.project.entity.dao.DocumentRepository;
import com.audrey.refeat.domain.project.entity.dao.ProjectRepository;
import com.audrey.refeat.domain.project.exception.DocumentNotExistedException;
import com.audrey.refeat.domain.project.exception.MentionIdInvalidException;
import com.audrey.refeat.domain.user.component.JwtComponent;
import com.audrey.refeat.domain.user.dto.JwtUser;
import com.audrey.refeat.domain.user.entity.UserInfo;
import com.audrey.refeat.domain.user.entity.dao.UserInfoRepository;
import com.audrey.refeat.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ProjectRepository projectRepository;
    private final DocumentRepository documentRepository;
    private final ChatRepository chatRepository;
    private final RequestComponent requestComponent;
    private final S3Component s3Component;
    private final UserInfoRepository userInfoRepository;
    private final AuthComponent authComponent;

    public RestResponse<MessageResponseDto> sendChat(Long projectId, SendMessageDto sendMessageDto) throws Exception {
        JwtUser jwtUser = JwtComponent.getUserInfo();
        UserInfo userInfo = userInfoRepository.findById(jwtUser.id()).orElseThrow(UserNotFoundException::new);
        Project project = authComponent.getAuthProject(projectId);
        List<Long> mentionIds = extractMention(sendMessageDto.content());
        List<UUID> documentId = extractDocumentTag(sendMessageDto.content());

        String chatRefined = refineContent(sendMessageDto.content(), mentionIds, documentId);

        Chat chat = Chat.builder()
                .project(project)
                .content(sendMessageDto.content())
                .contentRefined(chatRefined)
                .mention(String.join(",", mentionIds.stream().map(String::valueOf).toList()))
                .reference(String.join(",", documentId.stream().map(UUID::toString).toList()))
                .user(userInfo)
                .build();
        if (sendMessageDto.image() != null && !sendMessageDto.image().isEmpty()) {
            String path = "chat_images/" + UUID.randomUUID().toString();
            s3Component.uploadFileS3(path, sendMessageDto.image());
            chat.setImage(s3Component.getEndpoint(path));
        }
        project.updateTime();
        projectRepository.save(project);
        chatRepository.save(chat);

        return RestResponse.ok(new MessageResponseDto(
                chat.getId(),
                chat.getContent(),
                mentionIds,
                documentId)
        );
    }

    public RestResponse<AiQueryResponseDto> getAiChatData(AIQueryRequestDto aiQueryRequestDto) throws Exception {
        List<Long> mentionIds = extractMention(aiQueryRequestDto.query());
        if (mentionIds.isEmpty() || mentionIds.get(0) != 1L)
            throw new CustomException(ErrorCode.AI_NOT_MENTIONED);
        JwtUser jwtUser = JwtComponent.getUserInfo();
        UserInfo userInfo = userInfoRepository.findById(jwtUser.id()).orElseThrow(UserNotFoundException::new);
        Project project = authComponent.getAuthProject(aiQueryRequestDto.projectId());
        if (!documentRepository.existsByProject(project))
            throw new CustomException(ErrorCode.DOCUMENT_NOT_EXISTED);
        List<Chat> chatList = chatRepository.findByProjectOrderByCreated_atDesc(project, PageRequest.of(0, 3)).getContent();
        List<List<String>> history = getChatHistoryList(chatList);
        List<UUID> documentId = extractDocumentTag(aiQueryRequestDto.query());
        List<ReferenceDto> references = checkDocument(documentId, project);
        String refinedContent = refineContent(aiQueryRequestDto.query(), mentionIds, documentId);

        Chat chat = Chat.builder()
                .project(project)
                .content(aiQueryRequestDto.query())
                .contentRefined(refinedContent)
                .mention(String.join(",", mentionIds.stream().map(String::valueOf).toList()))
                .reference(String.join(",", documentId.stream().map(UUID::toString).toList()))
                .user(userInfo)
                .build();
        project.updateTime();
        projectRepository.save(project);
        chatRepository.save(chat);

        return RestResponse.ok(new AiQueryResponseDto(
                aiQueryRequestDto.projectId(),
                refinedContent,
                references,
                history
        ));
    }


    public Flux<ServerSentEvent<String>> sendAiChat(Long projectId, SendAiMessageDto sendAiMessageDto) throws Exception {
        return requestComponent.getStreamingText(sendAiMessageDto, projectId);
    }

    public RestResponse<ChatListResponseWrapperDto> getChat(Long projectId, Pageable pageable, Language lang) throws Exception {
        Project project = authComponent.getAuthProject(projectId);
        Page<Chat> chatPage = chatRepository.findByProjectOrderByCreated_atDesc(project, pageable);
        List<Chat> chatList = chatPage.getContent();
        List<Chat> result = new ArrayList<>(chatList.reversed());
        boolean hasNext = chatPage.hasNext();
        if (!hasNext && (chatPage.getNumberOfElements() == pageable.getPageSize())) {
            hasNext = true;
        } else if (!chatPage.hasNext()) {
            String content = lang.equals(Language.KO) ?
                    """
                            안녕하세요 👋\n
                            저는 현재 프로젝트에 업로드된 자료들을 바탕으로  정리를 도와주는 AI assistant Refeat 입니다.\n
                            수집한 모든 자료들에서 구체적인 정보가 필요할 땐 AI 토글 버튼을 켜고 메세지를 보내주세요.\n
                            특정 자료에 대해 요청하시려면 Reference에서 선택해주세요.
                            """ :
                    """
                            Hi 👋\n
                            I'm Refeat, an AI assistant that helps you organize the knowledge uploaded to your current project.\n
                            If you need specific information from all the resources you've collected, please turn on the AI toggle and send me a message.\n
                            To ask for a specific reference, please select it from Reference.""";

            result.add(0, Chat.builder()
                    .id(-1L)
                    .content(content)
                    .user(userInfoRepository.findById(1L).orElseThrow(UserNotFoundException::new))
                    .build());
        }


        return RestResponse.ok(new ChatListResponseWrapperDto(hasNext, ChatListResponseDto.fromChatList(result)));
    }

    ////////////////// private method ///

    private List<List<String>> getChatHistoryList(List<Chat> chatList) {
        List<List<String>> history = new ArrayList<>();
        boolean aiFlag = false;
        String aiContent = "";
        List<String> chatHistory;
        for (Chat chat : chatList) {
            if (chat.getUser().getId() == 1) {
                // ai 채팅일때
                aiFlag = true;
                aiContent = chat.getContentRefined();
                continue;
            } else {
                // ai 채팅이 아닐때
                chatHistory = new ArrayList<>();
                chatHistory.add(chat.getContentRefined());
                if (aiFlag) {
                    chatHistory.add(aiContent);
                    aiFlag = false;
                } else {
                    chatHistory.add("");
                }
            }
            history.add(0, chatHistory);
        }
        return history;
    }

    private List<ReferenceDto> checkDocument(List<UUID> documentIds, Project project) throws Exception {
        List<ReferenceDto> references = new ArrayList<>();
        if (documentIds.isEmpty()) {
            List<Document> documents = documentRepository.findByProject(project);
            for (Document document : documents) {
                references.add(new ReferenceDto(document.getId(), document.getName()));
            }
            return references;
        }
        for (UUID documentId : documentIds) {
            Document document = documentRepository.findById(documentId).orElseThrow(DocumentNotExistedException::new);
            if (Boolean.FALSE.equals(document.getEmbeddingDone())) {
                throw new CustomException(ErrorCode.DOCUMENT_EMBEDDING_NOT_DONE);
            }
            if (!document.getProject().equals(project)) {
                throw new CustomException(ErrorCode.DOCUMENT_NOT_BELONG_TO_PROJECT);
            }
            references.add(new ReferenceDto(documentId, document.getName()));
        }
        return references;
    }

    /////////////// regex extractor

    private List<Long> extractMention(String inputString) throws MentionIdInvalidException {
        Pattern regex = Pattern.compile("<@(.*?)>");
        Matcher matcher = regex.matcher(inputString);

        List<Long> extractedUserIds = new ArrayList<>();

        while (matcher.find()) {
            String userId = matcher.group(1);
            try {
                extractedUserIds.add(Long.parseLong(userId));
            } catch (NumberFormatException e) {
                throw new MentionIdInvalidException();
            }

        }
        return extractedUserIds;
    }

    private List<UUID> extractDocumentTag(String inputString) throws Exception {
        Pattern regex = Pattern.compile("</(.*?)>");
        Matcher matcher = regex.matcher(inputString);

        List<UUID> extractedDocumentTag = new ArrayList<>();

        while (matcher.find()) {
            String documentId = matcher.group(1);
            try {
                extractedDocumentTag.add(UUID.fromString(documentId));
            } catch (IllegalArgumentException e) {
                throw new CustomException(ErrorCode.INVALID_UUID_VALUE);
            }

        }
        return extractedDocumentTag;
    }

    private String refineContent(String inputString, List<Long> mentionIds, List<UUID> documentIds) {
        String refinedContent = inputString;
        for (Long mentionId : mentionIds) {
            refinedContent = refinedContent.replace("<@" + mentionId + ">", "");
        }
        for (UUID documentId : documentIds) {
            refinedContent = refinedContent.replace("</" + documentId + ">", "");
        }
        return refinedContent;
    }


}
