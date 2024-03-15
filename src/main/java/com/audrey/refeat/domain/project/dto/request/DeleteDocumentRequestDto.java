package com.audrey.refeat.domain.project.dto.request;

public record DeleteDocumentRequestDto(
        Long project_id,
        String document_id
) {
}
