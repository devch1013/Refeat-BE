package com.audrey.refeat.domain.project.dto;

import com.audrey.refeat.domain.project.entity.ColumnTitle;
import com.audrey.refeat.domain.project.entity.ColumnValue;

public record ColumnInfoDto(
        ColumnTitle columnTitle,
        ColumnValue columnValue
) {
}
