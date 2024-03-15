package com.audrey.refeat.domain.chat.controller;

import com.audrey.refeat.domain.chat.dto.request.AIQueryRequestDto;
import com.audrey.refeat.domain.chat.dto.request.SendAiMessageDto;
import com.audrey.refeat.domain.chat.dto.request.SendMessageDto;
import com.audrey.refeat.domain.chat.dto.response.AiQueryResponseDto;
import com.audrey.refeat.domain.chat.dto.response.ChatListResponseDto;
import com.audrey.refeat.domain.chat.dto.response.ChatListResponseWrapperDto;
import com.audrey.refeat.domain.chat.dto.response.MessageResponseDto;
import com.audrey.refeat.common.response.RestResponse;
import com.audrey.refeat.domain.chat.entity.enums.Language;
import com.audrey.refeat.domain.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.http.impl.io.ChunkedOutputStream;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.CharBuffer;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Tag(name = "Chat", description = "Chat API")
@RestController
@AllArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "채팅 전송", description = "채팅 전송" +
            "<br>access token을 헤더에 담아 주셔야합니다." +
            "<br>메세지에 이미지가 있을 경우 이미지를 form-data로 따로 보내주시면 됩니다." +
            "<br>사용자가 이미지를 업로드하는 위치는 상관 없이 채팅 하나에 이미지 하나만 가능합니다." +
            "<br>사용자 멘션의 경우 현재는 개인 워크스페이스 단계이기 때문에 @Refeat만 가능합니다. " +
            "<br>사용자가 @Refeat을 입력할 경우 텍스트에서 <@Refeat>으로 바꿔 보내주시면 백엔드에서 멘션으로 인식합니다." +
            "<br>사용자가 /를 입력할 경우 프로젝트 정보에서 받으신 문서 uuid를 선택지로 띄워주시면 되고 선택할 경우 </{uuid}> 형식으로 담아주시면 reference 멘션으로 인식합니다." +
            "<br>(400, \"PE001\", \"프로젝트가 존재하지 않습니다.\")" +
            "<br>(400, \"RE004\", \"UUID 형식이 올바르지 않습니다.\") - reference document의 uuid가 올바르지 않은 형식일 경우")
    @PostMapping(value = "/send/{project_id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RestResponse<MessageResponseDto>> sendChat(@PathVariable(name = "project_id") Long projectId,
                                                                     @ModelAttribute SendMessageDto sendMessageDto) throws Exception {
        return ResponseEntity.ok(chatService.sendChat(projectId, sendMessageDto));
    }

//    @Operation(summary = "AI 채팅", description =
//            "<br>ai 챗봇 응답 스트리밍의 경우 추후 설명 추가하겠습니다.")
//    @PostMapping("/ai/{project_id}")
//    public Flux<String> sendAiChat(@PathVariable(name = "project_id") Long projectId,
//                                   @ModelAttribute SendAiMessageDto sendAiMessageDto) throws Exception {
//
//        return chatService.sendAiChat(projectId, sendAiMessageDto);
//    }


    @Operation(hidden = true)
    @GetMapping(value = "/ai/{project_id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> sendAiChatTest(@PathVariable(name = "project_id") Long projectId,
                                                        @ModelAttribute SendAiMessageDto sendAiMessageDto) throws Exception {
        return chatService.sendAiChat(projectId, sendAiMessageDto);
    }

    @Operation(hidden = true)
    @PostMapping(value = "/aichat")
    public ResponseEntity<RestResponse<AiQueryResponseDto>> getAiChatData(@RequestBody AIQueryRequestDto aiQueryRequestDto) throws Exception {
        return ResponseEntity.ok(chatService.getAiChatData(aiQueryRequestDto));
    }

    @Operation(summary = "채팅 리스트 조회", description = "채팅 리스트 조회" +
            "<br>access token을 헤더에 담아 주셔야합니다." +
            "<br>지금까지의 채팅 기록을 최신순으로 반환합니다.(가장 먼저 있는게 가장 최근의 대화 기록입니다." +
            "함꼐 반환되는 유저 id, 유저 이름, 유저의 프로필 사진으로 유저 정보를 띄우시면 됩니다.")
    @GetMapping("/{project_id}")
    public ResponseEntity<RestResponse<ChatListResponseWrapperDto>> getChat(@PathVariable(name = "project_id") Long projectId,
                                                                            @RequestParam(required = false, defaultValue = "0") int page,
                                                                            @RequestParam(required = false, defaultValue = "10") int size,
                                                                            @RequestParam(name = "lang", defaultValue = "ko", required = false) String lang) throws Exception {
        return ResponseEntity.ok(chatService.getChat(projectId, PageRequest.of(page, size), Language.fromString(lang)));
    }

}
