package com.audrey.refeat.domain.project.dto.request;

public record UpdateColumnContentRequestDto(
        String documentId,
        Long columnId,
        String content
) {
}
