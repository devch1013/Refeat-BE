package com.audrey.refeat.domain.project.dto.response;

import com.audrey.refeat.domain.project.entity.Document;
import com.audrey.refeat.domain.project.entity.enums.DocumentStatus;

import java.util.List;

public record DocumentStateResponseDto(
        String document_id,
        int summaryDone,
        int embeddingDone,
        String summary
) {

    public static List<DocumentStateResponseDto> fromDocumentList(List<Document> documentList){
        return documentList.stream()
                .map(document -> new DocumentStateResponseDto(
                        document.getId().toString(),
                        document.getSummaryDone().ordinal(),
                        document.getEmbeddingDone().ordinal(),
                        document.getSummary()
                ))
                .toList();
    }
}
