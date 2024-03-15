package com.audrey.refeat.domain.project.dto.request;

import com.audrey.refeat.domain.project.entity.enums.DocumentType;
import org.springframework.web.multipart.MultipartFile;

public record AddDocumentRequestDto(
        MultipartFile file,
        String link,
        DocumentType type
) {
}
