package com.audrey.refeat.domain.chat.dto.response;

import java.util.List;

public record AiQueryResponseDto(
        Long projectId,
        String query,
        List<ReferenceDto> reference,
        List<List<String>> history
) {
}

