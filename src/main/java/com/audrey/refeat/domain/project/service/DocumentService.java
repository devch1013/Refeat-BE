package com.audrey.refeat.domain.project.service;

import com.audrey.refeat.common.component.AuthComponent;
import com.audrey.refeat.common.component.RequestComponent;
import com.audrey.refeat.common.exception.CustomException;
import com.audrey.refeat.common.exception.ErrorCode;
import com.audrey.refeat.common.response.RestResponse;
import com.audrey.refeat.common.response.RestResponseSimple;
import com.audrey.refeat.common.s3.S3Component;
import com.audrey.refeat.domain.chat.entity.enums.Language;
import com.audrey.refeat.domain.project.dto.request.*;
import com.audrey.refeat.domain.project.dto.response.*;
import com.audrey.refeat.domain.project.entity.*;
import com.audrey.refeat.domain.project.entity.dao.*;
import com.audrey.refeat.domain.project.entity.enums.DocumentStatus;
import com.audrey.refeat.domain.project.entity.enums.DocumentType;
import com.audrey.refeat.domain.project.exception.*;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final ProjectRepository projectRepository;
    private final DocumentRepository documentRepository;
    private final RequestComponent requestComponent;
    private final ColumnTitleRepository columnTitleRepository;
    private final ColumnValueRepository columnValueRepository;
    private final DocumentESRepository documentESRepository;
    private final S3Component s3Component;
    private final AuthComponent authComponent;

    public RestResponse<FileUploadResponseDto> addDocument(AddDocumentRequestDto addDocumentRequestDto, Long projectId, Language lang) throws Exception {
        Project project = authComponent.getAuthProject(projectId);
        DocumentType documentType = checkDocumentDto(addDocumentRequestDto);
        String path;
        String originPath;
        UUID documentId = UUID.randomUUID();
        String name;
        String tempFilePath = null;

        if (documentType == DocumentType.WEB && addDocumentRequestDto.link().endsWith(".pdf")) {
            InputStream in = new URI(addDocumentRequestDto.link()).toURL().openStream();

            tempFilePath = "temp_folder/" + documentId + ".pdf";
            Files.copy(in, Paths.get(tempFilePath), StandardCopyOption.REPLACE_EXISTING);
            documentType = DocumentType.PDF;
        }


        if (documentType == DocumentType.PDF) {
            path = "files/" + documentId + ".pdf";
            originPath = "files/" + documentId + ".pdf";
            if (tempFilePath == null) {
                s3Component.uploadFileS3(path, addDocumentRequestDto.file());
                name = addDocumentRequestDto.file().getOriginalFilename();
            } else {
                s3Component.uploadLocalPDFFile(path, tempFilePath);
                name = addDocumentRequestDto.link().split("/")[addDocumentRequestDto.link().split("/").length - 1];
            }

            path = s3Component.getEndpoint(path);
            originPath = s3Component.getEndpoint(originPath);


        } else {
            originPath = addDocumentRequestDto.link();
            path = s3Component.getEndpoint("pdf/" + documentId + ".pdf");
            name = "";
        }

        // add document data
        Document document = Document.builder()
                .id(documentId)
                .project(project)
                .id(documentId)
                .type(documentType)
                .favicon(s3Component.getDefaultFavicon(documentType))
                .link(path)
                .originLink(originPath)
                .name(name)
                .lang(lang)
                .build();
        projectRepository.save(project);
        documentRepository.save(document);

        try {
            AiFileUploadResponseDto response = requestComponent.jsonPost("/document",
                    new AiFileUploadRequestDto(projectId.intValue(), documentId.toString(), originPath, documentType, lang),
                    AiFileUploadResponseDto.class);
            document.updateAiResponse(response);
            project.setThumbnail(s3Component.getEndpoint("screenshot/" + documentId + ".png"));

            project.updateTime();
            projectRepository.save(project);
            documentRepository.save(document);
            return RestResponse.ok(new FileUploadResponseDto(documentId.toString(), document.getName(), s3Component.getEndpoint(response.favicon())));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.AI_SERVER_ERROR);
        }

    }

    public RestResponse<List<GetAllDocumentDataResponseDto>> getDocumentList(Long projectId) throws Exception {
        Project project = authComponent.getAuthProject(projectId);
        List<Document> documentList = documentRepository.findByProjectAndEmbeddingDoneTrueAndSummaryDoneTrueOrderByCreatedAtDesc(project);
        return RestResponse.ok(GetAllDocumentDataResponseDto.fromList(documentList));
    }


    public RestResponseSimple deleteDocument(String documentId) throws Exception {

        Document document = documentRepository.findById(UUID.fromString(documentId)).orElseThrow(DocumentNotExistedException::new);
        Project project = document.getProject();
        authComponent.checkAuth(project);

        requestComponent.jsonPost(
                "/document/delete",
                new DeleteDocumentRequestDto(document.getProject().getId(), document.getId().toString()),
                String.class
        );
        document.delete();
        documentRepository.save(document);
        try {
            Document latestDocument = documentRepository.findByProjectOrderByCreatedAtDescPaging(project, PageRequest.of(0, 1)).getContent().get(0);
            project.setThumbnail(s3Component.getEndpoint("screenshot/" + latestDocument.getId() + ".png"));
        } catch (Exception e) {
            project.setThumbnail(s3Component.getEndpoint("images/empty_project.png"));
        }
        project.updateTime();
        projectRepository.save(project);


        return RestResponseSimple.success();
    }

    public RestResponse<DocumentRenameResponseDto> updateDocumentTitle(String documentId, String name) throws Exception {
        Document document = documentRepository.findById(UUID.fromString(documentId)).orElseThrow(DocumentNotExistedException::new);
        Project project = document.getProject();
        authComponent.checkAuth(project);
        document.updateName(name);

        project.updateTime();
        projectRepository.save(project);
        documentRepository.save(document);
        return RestResponse.ok(new DocumentRenameResponseDto(document.getName()));
    }

    public RestResponse<String> getDocument(String documentId) throws Exception {

        Document document = documentRepository.findById(UUID.fromString(documentId)).orElseThrow(DocumentNotExistedException::new);
        authComponent.checkAuth(document.getProject());

        return RestResponse.ok(s3Component.getTextFromHtml(document.getId()));
    }

    public RestResponse<List<DocumentStateResponseDto>> getDocumentState(Long projectId, DocumentStateRequestDto documentStateRequestDto) throws Exception {
        authComponent.getAuthProject(projectId);
        List<UUID> documentIdList = DocumentStateRequestDto.getDocumentIds(documentStateRequestDto);
        List<Document> documentList = documentRepository.findByIds(documentIdList);
        return RestResponse.ok(DocumentStateResponseDto.fromDocumentList(documentList));
    }

    public RestResponse<AddColumnResponseDto> addColumn(Long projectId, AddColumnRequestDto addColumnRequestDto, boolean custom) throws Exception {
        Project project = authComponent.getAuthProject(projectId);

        // 3개 초과 추가 불가
        if (columnTitleRepository.countByDeletedFalseAndProject(project) >= 3) {
            throw new CustomException(ErrorCode.COLUMN_MAX_EXCEEDED);
        }
        boolean general = false;
        if (!custom) {
            AiAddColumnResponseDto aiAddColumnResponseDto = requestComponent.jsonPost(
                    "/add_column",
                    new AiAddColumnRequestDto(addColumnRequestDto.columnName()),
                    AiAddColumnResponseDto.class
            );
            general = aiAddColumnResponseDto.isGeneral();
        }

        ColumnTitle columnTitle = columnTitleRepository.save(
                ColumnTitle.builder()
                        .project(project)
                        .title(addColumnRequestDto.columnName())
                        .general(general)
                        .custom(custom)
                        .build()
        );
        // custom column이면 빈 컬럼 제작
        if (custom) {
            List<Document> documentList = documentRepository.findByProjectOrderByCreatedAtDesc(project);
            for (Document document : documentList) {
                columnValueRepository.save(ColumnValue.builder()
                        .columnDescription("")
                        .title(columnTitle)
                        .document(document)
                        .isDone(true)
                        .build());
            }
        }
        project.updateTime();
        projectRepository.save(project);

        return RestResponse.ok(new AddColumnResponseDto(columnTitle.getId(), columnTitle.getTitle()));
    }

    public RestResponse<GetColumnContentResponseDto> getColumnContent(Long projectId, String documentId, Long columnId) throws Exception {
        ColumnTitle columnTitle = getColumnTitle(projectId, columnId);
        Document document = documentRepository.findById(UUID.fromString(documentId)).orElseThrow(DocumentNotExistedException::new);
        authComponent.checkAuth(document.getProject());
        if (!document.getProject().getId().equals(projectId)) { // Project에 document가 속하는지 체크
            throw new CustomException(ErrorCode.DOCUMENT_NOT_BELONG_TO_PROJECT);
        }
        if (document.getEmbeddingDone() != DocumentStatus.SUCCESS) {
            throw new CustomException(ErrorCode.DOCUMENT_EMBEDDING_NOT_DONE);
        }
        ColumnValue columnValue = columnValueRepository.findByTitleAndDocument(columnTitle, document).orElse(null);
        if (columnValue == null) {
            AiGetColumnResponseDto aiGetColumnResponseDto = requestComponent.jsonPost(
                    "/get_column",
                    new AiGetColumnRequestDto(columnTitle.getTitle(), columnTitle.getGeneral(), document.getId().toString()),
                    AiGetColumnResponseDto.class
            );

            columnValue = columnValueRepository.save(ColumnValue.builder()
                    .title(columnTitle)
                    .document(document)
                    .columnDescription(aiGetColumnResponseDto.value())
                    .isDone(true)
                    .build());
        }


        return RestResponse.ok(new GetColumnContentResponseDto(columnValue.getColumnDescription()));
    }

    public RestResponseSimple updateColumnContent(Long projectId, UpdateColumnContentRequestDto updateColumnContentRequestDto) throws Exception {
        ColumnTitle columnTitle = getColumnTitle(projectId, updateColumnContentRequestDto.columnId());
        Document document = documentRepository.findById(UUID.fromString(updateColumnContentRequestDto.documentId())).orElseThrow(DocumentNotExistedException::new);
        Project project = document.getProject();
        authComponent.checkAuth(project);
        project.updateTime();
        // document - project 관계 검사
        if (!document.getProject().getId().equals(projectId)) { // Project에 document가 속하는지 체크
            throw new CustomException(ErrorCode.DOCUMENT_NOT_BELONG_TO_PROJECT);
        }
        ColumnValue columnValue = columnValueRepository.findByTitleAndDocument(columnTitle, document).orElseThrow(ColumnValueNotExistException::new);
        columnValue.updateColumnValue(updateColumnContentRequestDto.content());

        projectRepository.save(project);
        columnValueRepository.save(columnValue);
        return RestResponseSimple.success();
    }

    public RestResponseSimple deleteColumn(Long projectId, Long columnId) throws Exception {
        ColumnTitle columnTitle = getColumnTitle(projectId, columnId);
        // title delete 상태로 변경
        columnTitle.delete();
        Project project = columnTitle.getProject();
        authComponent.checkAuth(project);
        project.updateTime();
        projectRepository.save(project);
        columnTitleRepository.save(columnTitle);
        return RestResponseSimple.success();
    }

    public RestResponseSimple test() {
        List<DocumentES> documents = documentESRepository.findByProjectId("39");
        System.out.println(documents);
        return RestResponseSimple.success();
    }

    ///////////// Private method

    private DocumentType checkDocumentDto(AddDocumentRequestDto addDocumentRequestDto) throws Exception {
        if ((addDocumentRequestDto.file() != null) == (addDocumentRequestDto.link() != null)) {
            throw new OnlyUploadOneTypeException();
        }
        if (addDocumentRequestDto.file() != null) {
            if (addDocumentRequestDto.type() != DocumentType.PDF)
                throw new FileTypeNotMatchedException();
            return DocumentType.PDF;
        } else {
            if (addDocumentRequestDto.type() != DocumentType.WEB)
                throw new FileTypeNotMatchedException();
            return DocumentType.WEB;
        }
    }

    /*
    문제 없을때만 column title return
     */
    private ColumnTitle getColumnTitle(Long projectId, Long columnId) throws Exception {
        ColumnTitle columnTitle = columnTitleRepository.findById(columnId).orElseThrow(ColumnNotExistException::new);
        if (!columnTitle.getProject().getId().equals(projectId) || columnTitle.getDeleted()) {
            throw new CustomException(ErrorCode.COLUMN_TITLE_NOT_EXISTED);
        }
        return columnTitle;
    }

    private InputStream getInputStream(String path) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(path);

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // Read the content of the response and save it as a PDF file
                // Implement logic to save inputStream to a file
                return response.getEntity().getContent();
            } else {
                // Handle unsuccessful response
                return null;
            }
        } catch (IOException e) {
            throw new Exception();
            // Handle IO exception
        }


    }
}
