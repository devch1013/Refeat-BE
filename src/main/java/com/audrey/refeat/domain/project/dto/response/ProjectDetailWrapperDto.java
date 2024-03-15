package com.audrey.refeat.domain.project.dto.response;

import java.util.List;

public record ProjectDetailWrapperDto (
        Long id,
        String title,
        Boolean hasNext,
        List<ProjectDetailDto> projectDetail,
        List<AddColumnResponseDto> columns
) {
}
