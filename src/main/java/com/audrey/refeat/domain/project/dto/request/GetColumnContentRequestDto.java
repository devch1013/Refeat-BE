package com.audrey.refeat.domain.project.dto.request;

public record GetColumnContentRequestDto(
        String documentId,
        Long columnId
) {
}
