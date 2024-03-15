package com.audrey.refeat.domain.project.dto.response;

import java.util.List;

public record ProjectListWrapperDto(
        Boolean hasNext,
        List<UserProjectListResponseDto> projectList
) {
}
