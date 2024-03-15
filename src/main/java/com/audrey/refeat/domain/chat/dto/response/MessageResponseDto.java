package com.audrey.refeat.domain.chat.dto.response;

import java.util.List;
import java.util.UUID;

public record MessageResponseDto(
        Long id,
        String content,
        List<Long> mention,
        List<UUID> reference
) {
}
