package com.audrey.refeat.domain.user.dto.request;

public record UpdatePasswordRequestDto(
        String email,
        String password
) {
}
