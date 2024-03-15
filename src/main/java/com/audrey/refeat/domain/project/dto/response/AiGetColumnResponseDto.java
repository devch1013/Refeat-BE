package com.audrey.refeat.domain.project.dto.response;

import java.util.UUID;

public record AiGetColumnResponseDto(
        UUID documentId,
        String value
) {
}
