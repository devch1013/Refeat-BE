package com.audrey.refeat.domain.chat.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record SendMessageDto(
        String content,
        MultipartFile image
) {
}
