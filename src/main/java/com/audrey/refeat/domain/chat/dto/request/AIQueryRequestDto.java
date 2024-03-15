package com.audrey.refeat.domain.chat.dto.request;

public record AIQueryRequestDto(
        Long projectId,
        String query
) {
}
