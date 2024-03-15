package com.audrey.refeat.domain.project.dto.response;

import com.audrey.refeat.domain.project.entity.ColumnTitle;

import java.util.List;

public record AddColumnResponseDto(
        Long id,
        String title
) {
    public static List<AddColumnResponseDto> fromList(List<ColumnTitle> columnTitles) {
        return columnTitles.stream()
                .map(columnTitle -> new AddColumnResponseDto(
                        columnTitle.getId(),
                        columnTitle.getTitle()
                ))
                .toList();
    }
}
