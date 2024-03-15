package com.audrey.refeat.domain.project.dto.response;

import com.audrey.refeat.domain.user.entity.UserInfo;

import java.util.List;

public record ProjectUserResponseDto(
        Long id,
        String nickname,
        String profileImage
) {

    public static List<ProjectUserResponseDto> fromUserList(List<UserInfo> userList) {
        return userList.stream()
                .map(user -> new ProjectUserResponseDto(
                        user.getId(),
                        user.getNickname(),
                        user.getProfileImage()
                ))
                .toList();
    }
}
