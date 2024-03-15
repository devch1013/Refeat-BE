package com.audrey.refeat.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SocialAuthInfo {
    private String jwtToken;
    private int user_num;
    private String accessToken;
    private String tokenType;
}
