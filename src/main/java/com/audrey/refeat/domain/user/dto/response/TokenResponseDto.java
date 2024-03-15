package com.audrey.refeat.domain.user.dto.response;

public record TokenResponseDto(
        String accessToken,
        String refreshToken
) {
}
