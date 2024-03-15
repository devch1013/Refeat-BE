package com.audrey.refeat.domain.user.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record ProfileImageRequestDto(
        MultipartFile profileImage
) {
}
