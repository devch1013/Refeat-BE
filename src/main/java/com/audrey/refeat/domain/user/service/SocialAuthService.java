package com.audrey.refeat.domain.user.service;

import com.audrey.refeat.domain.user.component.JwtComponent;
import com.audrey.refeat.domain.user.component.JwtTokenProvider;
import com.audrey.refeat.domain.user.dto.GoogleOAuthToken;
import com.audrey.refeat.domain.user.dto.GoogleUser;
import com.audrey.refeat.domain.user.dto.response.TokenResponseDto;
import com.audrey.refeat.domain.user.entity.AuthProvider;
import com.audrey.refeat.domain.user.entity.UserInfo;
import com.audrey.refeat.domain.user.entity.dao.UserInfoRepository;
import com.audrey.refeat.domain.user.exception.RegisteredWithAnotherProviderException;
import com.audrey.refeat.domain.user.social.GoogleOAuth;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SocialAuthService {
    private final GoogleOAuth googleOAuth;
    private final HttpServletResponse response;
    private final UserInfoRepository userInfoRepository;
    private final JwtComponent jwtComponent;

    @Value("${social-login.google.redirect-url}")
    private String googleRedirectUrl;


    public void redirectAuth(AuthProvider authProvider) throws IOException {
        String redirectUrl;
        switch (authProvider) {
            case GOOGLE: {
                redirectUrl = googleOAuth.getOAuthRedirectURL();
            }
            break;
            default: {
                throw new IllegalArgumentException("알 수 없는 소셜 로그인 형식입니다.");
            }
        }
        response.sendRedirect(redirectUrl);
    }


    public void oAuthLogin(AuthProvider authProvider, String code, String tokenRedirectUrl) throws IOException, Exception {
        UserInfo userInfo;
        String callbackUrl;
        switch (authProvider) {
            case GOOGLE: {
                //구글로 일회성 코드를 보내 액세스 토큰이 담긴 응답객체를 받아옴
                ResponseEntity<String> accessTokenResponse = googleOAuth.requestAccessToken(code);
                //응답 객체가 JSON형식으로 되어 있으므로, 이를 deserialization해서 자바 객체에 담을 것이다.
                GoogleOAuthToken oAuthToken = googleOAuth.getAccessToken(accessTokenResponse);

                //액세스 토큰을 다시 구글로 보내 구글에 저장된 사용자 정보가 담긴 응답 객체를 받아온다.
                ResponseEntity<String> userInfoResponse = googleOAuth.requestUserInfo(oAuthToken);
                //다시 JSON 형식의 응답 객체를 자바 객체로 역직렬화한다.
                GoogleUser googleUser = googleOAuth.getUserInfo(userInfoResponse);
                userInfo = this.validateUser(googleUser);
            }
            break;
            default: {
                throw new IllegalArgumentException("알 수 없는 소셜 로그인 형식입니다.");
            }
        }
//        if (userInfo.getProvider() != authProvider) {
//            callbackUrl = UriComponentsBuilder.fromUriString(googleRedirectUrl).queryParam("error", "registered_with_another_provider").build().toUriString();
//        } else {
//            TokenResponseDto tokenResponseDto = jwtComponent.createToken(userInfo);
//            callbackUrl = createTokenCallbackUrl(googleRedirectUrl, tokenResponseDto.accessToken(), tokenResponseDto.refreshToken());
//        }
        TokenResponseDto tokenResponseDto = jwtComponent.createToken(userInfo);
        callbackUrl = createTokenCallbackUrl(googleRedirectUrl, tokenResponseDto.accessToken(), tokenResponseDto.refreshToken());
        response.sendRedirect(callbackUrl);
    }

    private UserInfo validateUser(GoogleUser googleUser) {
        UserInfo userInfo = userInfoRepository.findByEmail(googleUser.getEmail()).orElse(null);
        if (userInfo == null) {
            return userInfoRepository.save(UserInfo.fromSocialAuth(googleUser));
        }
        return userInfo;
    }

    private String createTokenCallbackUrl(String tokenRedirectUrl, String accessToken, String refreshToken) {
        return tokenRedirectUrl + "?access=" + accessToken + "&refresh=" + refreshToken;
    }
}
