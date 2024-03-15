package com.audrey.refeat.domain.project.dto.response;

import com.audrey.refeat.domain.project.entity.Project;

import java.time.LocalDateTime;
import java.util.List;

public record UserProjectListResponseDto(
        Long id,
        String name,
        String description,
        String thumbnail,
        String createdAt,
        String updatedAt
) {
    public static List<UserProjectListResponseDto> getDtoFromList(List<Project> ProjectList) {
        return ProjectList.stream()
                .map(UserProjectListResponseDto::getDtoFromProject)
                .toList();
    }

    private static UserProjectListResponseDto getDtoFromProject(Project project) {
        return new UserProjectListResponseDto(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getThumbnail(),
                project.getCreatedAt().toString(),
                project.getUpdatedAt().toString()
        );
    }
}
