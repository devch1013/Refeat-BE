package com.audrey.refeat.domain.project.dto.request;

public record AiGetColumnRequestDto(
        String title,
        Boolean is_general,
        String document_id
) {
}
