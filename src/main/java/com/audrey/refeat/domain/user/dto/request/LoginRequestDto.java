package com.audrey.refeat.domain.user.dto.request;

public record LoginRequestDto(
        String email,
        String password
) {
}
