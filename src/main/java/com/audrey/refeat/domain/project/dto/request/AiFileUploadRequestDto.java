package com.audrey.refeat.domain.project.dto.request;
import com.audrey.refeat.domain.chat.entity.enums.Language;
import com.audrey.refeat.domain.project.entity.enums.DocumentType;

public record AiFileUploadRequestDto(
        Integer project_id,
        String document_id,
        String path,
        DocumentType file_type,
        Language lang
) {
}
