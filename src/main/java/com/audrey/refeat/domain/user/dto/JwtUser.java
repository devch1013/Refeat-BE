package com.audrey.refeat.domain.user.dto;

import com.audrey.refeat.domain.user.entity.Authority;

public record JwtUser(
        Long id,
        String email,
        Authority role
) {

}
