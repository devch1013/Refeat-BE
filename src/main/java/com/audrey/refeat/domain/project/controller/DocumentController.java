package com.audrey.refeat.domain.project.controller;

import com.audrey.refeat.common.response.RestResponse;
import com.audrey.refeat.common.response.RestResponseSimple;
import com.audrey.refeat.domain.chat.entity.enums.Language;
import com.audrey.refeat.domain.project.dto.request.*;
import com.audrey.refeat.domain.project.dto.response.*;
import com.audrey.refeat.domain.project.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Document", description = "Document API")
@RestController
@AllArgsConstructor
@RequestMapping("/project")
public class DocumentController {

    private final DocumentService documentService;

    @Operation(summary = "Document(Reference) 추가", description = "Document(Reference) 추가" +
            "<br>access token을 헤더에 담아 주셔야합니다." +
            "<br>DocumentType은 WEB, PDF 중 하나여야 합니다." +
            "<br>WEB일 경우 url에 웹사이트 주소를 넣어주시면 됩니다." +
            "<br>PDF일 경우 pdf 파일을 form-data로 보내주시면 됩니다." +
            "<br>name은 web의 경우 웹 메타 데이터의 타이틀, pdf의 경우 pdf 파일 이름을 넣어주시면 됩니다." +
            "<br>정상적으로 업로드 될 경우 summary 텍스트가 반환됩니다.(약 30초 소요)")
    @PostMapping("/{project_id}/document")
    public ResponseEntity<RestResponse<FileUploadResponseDto>> addDocument(@PathVariable(name = "project_id") Long projectId,
                                                                           @RequestParam(name = "lang", defaultValue = "ko", required = false) String lang,
                                                                           @ModelAttribute AddDocumentRequestDto addDocumentRequestDto) throws Exception {

        return ResponseEntity.ok(documentService.addDocument(addDocumentRequestDto, projectId, Language.fromString(lang)));
    }

    @Operation(summary = "Document 전체 id, 이름 조회", description = "reference 태그를 위한 전체 document 목록입니다." +
            "<br>embedding 단계가 끝나지 않은 문서들을 포함되지 않습니다." +
            "<br>(400, \"PE001\", \"프로젝트가 존재하지 않습니다.\")")
    @GetMapping("/{project_id}/document")
    public ResponseEntity<RestResponse<List<GetAllDocumentDataResponseDto>>> getAllDocument(@PathVariable(name = "project_id") Long projectId) throws Exception {
        return ResponseEntity.ok(documentService.getDocumentList(projectId));
    }


    /////////////////// Document

    @Operation(summary = "Document(Reference) 삭제", description = "Document(Reference) 삭제" +
            "<br>Document id는 uuid 형태입니다." +
            "<br>(400, \"DE001\", \"Document가 존재하지 않습니다.\")")
    @DeleteMapping("/document/{document_id}")
    public ResponseEntity<RestResponseSimple> deleteDocument(@PathVariable(name = "document_id") String documentId) throws Exception {
        return ResponseEntity.ok(documentService.deleteDocument(documentId));
    }

    @Operation(summary = "Document(Reference) 제목 수정", description = "Document(Reference) 제목 수정" +
            "<br>제목 수정에 성공할 경우 수정된 제목이 반환됩니다." +
            "<br>(400, \"DE001\", \"Document가 존재하지 않습니다.\"")
    @PutMapping("/document/{document_id}")
    public ResponseEntity<RestResponse<DocumentRenameResponseDto>> updateDocumentTitle(@PathVariable(name = "document_id") String documentId,
                                                                                       @RequestBody DocumentRenameResponseDto documentRenameResponseDto) throws Exception {
        return ResponseEntity.ok(documentService.updateDocumentTitle(documentId, documentRenameResponseDto.name()));
    }

    @Operation(summary = "document 응답(뷰어용 텍스트 응답)", description = "document 응답(뷰어용 텍스트 응답)" +
            "<br>Document id는 uuid 형태입니다." +
            "<br>PDF, WEB에 상관없이 html 형태의 데이터로 반환됩니다." +
            "<br>(400, \"DE001\", \"Document가 존재하지 않습니다.\")")
    @GetMapping("/document/{document_id}")
    public ResponseEntity<RestResponse<String>> getDocument(@PathVariable(name = "document_id") String documentId) throws Exception {
        return ResponseEntity.ok(documentService.getDocument(documentId));
    }

    @Operation(summary = "각 Document들의 상태 확인", description = "각 Document들의 상태 확인" +
            "<br>Document id는 uuid 형태입니다." +
            "<br>Document id가 DB에 없는 경우 무시됩니다." +
            "<br>상태확인이 필요한 document의 id들을 보내주시면 각 document들의 summary, embedding이 끝났는지에 대한 정보가 반환됩니다." +
            "<br>Summary 생성이 끝났다면 summary도 함께 반환됩니다.")
    @PostMapping("/{project_id}/check")
    public ResponseEntity<RestResponse<List<DocumentStateResponseDto>>> getDocumentState(@PathVariable(name = "project_id") Long projectId,
                                                                                         @RequestBody DocumentStateRequestDto documentStateRequestDto) throws Exception {
        return ResponseEntity.ok(documentService.getDocumentState(projectId, documentStateRequestDto));
    }

    //////////////// Column

    @Operation(summary = "Column 추가", description = "Column 추가" +
            "<br>Column 추가에 성공할 경우 추가된 Column의 id와 값이 반환됩니다." +
            "<br>Column은 최대 3개까지 추가 가능합니다." +
            "<br>custom=true를 쿼리 파리미터로 넣어주시면 빈 컬럼이 만들어집니다." +
            "(400, \"CE003\", \"Column 최대 개수를 초과했습니다.\")" +
            "<br>(400, \"PE001\", \"프로젝트가 존재하지 않습니다.\")")
    @PostMapping("/{project_id}/column")
    public ResponseEntity<RestResponse<AddColumnResponseDto>> addColumn(@PathVariable(name = "project_id") Long projectId,
                                                                        @RequestBody AddColumnRequestDto addColumnRequestDto,
                                                                        @RequestParam(required = false, defaultValue = "false") boolean custom) throws Exception {
        return ResponseEntity.ok(documentService.addColumn(projectId, addColumnRequestDto, custom));
    }

    @Operation(summary = "Column 정보", description = "Column 정보" +
            "<br>각 칸의 column에 대해 따로 보내주시면 됩니다. column의 id와 document id가 필요합니다." +
            "<br>(400, \"DE002\", \"해당 프로젝트에 속하지 않는 문서입니다.\")" +
            "<br>(400, \"CE001\", \"ColumnTitle이 존재하지 않습니다.\")")
    @GetMapping("/{project_id}/document/{document_id}/column/{column_id}")
    public ResponseEntity<RestResponse<GetColumnContentResponseDto>> getColumnContent(@PathVariable(name = "project_id") Long projectId,
                                                                                      @PathVariable(name = "document_id") String documentId,
                                                                                      @PathVariable(name = "column_id") Long columnId,
                                                                                      @RequestParam(name = "lang", defaultValue = "ko", required = false) String lang
                                                                                      ) throws Exception {
        return ResponseEntity.ok(documentService.getColumnContent(projectId, documentId, columnId));
    }

    @Operation(summary = "Column 내용 수정", description = "Column 내용 수정" +
            "<br>(400, \"DE002\", \"해당 프로젝트에 속하지 않는 문서입니다.\")" +
            "<br>(400, \"CE001\", \"ColumnTitle이 존재하지 않습니다.\")" +
            "<br>(400, \"CE002\", \"ColumnValue가 존재하지 않습니다.\")")
    @PutMapping("/{project_id}/column/content")
    public ResponseEntity<RestResponseSimple> updateColumnContent(@PathVariable(name = "project_id") Long projectId,
                                                                  @RequestBody UpdateColumnContentRequestDto updateColumnContentRequestDto) throws Exception {
        return ResponseEntity.ok(documentService.updateColumnContent(projectId, updateColumnContentRequestDto));
    }

    @Operation(summary = "Column 삭제", description = "Column 삭제" +
            "<br>(400, \"CE001\", \"ColumnTitle이 존재하지 않습니다.\")")
    @DeleteMapping("/{project_id}/column/{column_id}")
    public ResponseEntity<RestResponseSimple> deleteColumn(@PathVariable(name = "project_id") Long projectId,
                                                           @PathVariable(name = "column_id") Long columnId) throws Exception {
        return ResponseEntity.ok(documentService.deleteColumn(projectId, columnId));
    }


    @Operation(hidden = true)
    @GetMapping("test")
    public ResponseEntity<RestResponseSimple> test() throws Exception {
        return ResponseEntity.ok(documentService.test());
    }


}
