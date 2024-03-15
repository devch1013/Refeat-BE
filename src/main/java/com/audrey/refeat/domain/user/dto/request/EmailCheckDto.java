package com.audrey.refeat.domain.user.dto.request;

public record EmailCheckDto(
        String email,
        int code
) {
}
