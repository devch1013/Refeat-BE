package com.audrey.refeat.domain.user.dto.response;

import com.audrey.refeat.domain.user.entity.UserInfo;

public record UserDataResponseDto(
        Long id,
        String email,
        String name,
        String profileImage
) {

    public static UserDataResponseDto fromUserInfo(UserInfo userInfo) {
        return new UserDataResponseDto(
                userInfo.getId(),
                userInfo.getEmail(),
                userInfo.getNickname(),
                userInfo.getProfileImage()
        );
    }
}
