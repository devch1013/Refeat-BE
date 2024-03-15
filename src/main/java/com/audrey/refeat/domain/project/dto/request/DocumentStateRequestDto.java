package com.audrey.refeat.domain.project.dto.request;

import java.util.List;
import java.util.UUID;

public record DocumentStateRequestDto(
        List<String> document_id
) {

    public static List<UUID> getDocumentIds(DocumentStateRequestDto documentStateRequestDto) {
        return documentStateRequestDto.document_id.stream()
                .map(UUID::fromString)
                .toList();
    }
}
