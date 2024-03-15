package com.audrey.refeat.domain.project.dto.response;

import com.audrey.refeat.domain.project.entity.ColumnValue;
import com.audrey.refeat.domain.project.entity.Document;
import com.audrey.refeat.domain.project.entity.enums.DocumentStatus;
import com.audrey.refeat.domain.project.entity.enums.DocumentType;

import java.util.List;
import java.util.stream.Collectors;

public record ProjectDetailDto(
        String id,
        String name,
        String summary,
        DocumentType type,
        String link,
        String favicon,
        int summaryDone,
        int embeddingDone
) {
    public static List<ProjectDetailDto> fromList(List<Document> documents, String s3Endpoint) {
        return documents.stream()
                .map(document -> new ProjectDetailDto(
                        document.getId().toString(),
                        document.getName(),
                        document.getSummary(),
                        document.getType(),
                        document.getOriginLink(),
                        document.getFavicon(s3Endpoint),
                        document.getSummaryDone().ordinal(),
                        document.getEmbeddingDone().ordinal()
                ))
                .toList();
    }

    private record ColumnInfo(
            Long id,
            String name
    ) {
        public static List<ColumnInfo> fromList(List<ColumnValue> columnValues) {
            return columnValues.stream()
                    .map(columnValue -> new ColumnInfo(columnValue.getId(), columnValue.getColumnDescription()))
                    .toList();
        }
    }
}


