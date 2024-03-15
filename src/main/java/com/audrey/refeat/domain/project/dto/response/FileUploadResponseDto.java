package com.audrey.refeat.domain.project.dto.response;

public record FileUploadResponseDto(
        String documentId,
        String documentName,
        String favicon
) {
}
