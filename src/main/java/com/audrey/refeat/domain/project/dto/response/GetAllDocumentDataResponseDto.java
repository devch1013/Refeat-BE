package com.audrey.refeat.domain.project.dto.response;

import com.audrey.refeat.domain.project.entity.Document;

import java.util.List;
import java.util.UUID;

public record GetAllDocumentDataResponseDto(
        UUID documentId,
        String title
) {

    public static List<GetAllDocumentDataResponseDto> fromList(List<Document> documentList){
        return documentList.stream()
                .map(document -> new GetAllDocumentDataResponseDto(document.getId(), document.getName()))
                .toList();
    }
}
