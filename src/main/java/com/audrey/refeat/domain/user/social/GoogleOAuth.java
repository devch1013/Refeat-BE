package com.audrey.refeat.domain.user.social;

import com.audrey.refeat.domain.user.dto.GoogleOAuthToken;
import com.audrey.refeat.domain.user.dto.GoogleUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GoogleOAuth implements SocialOAuth {
    //applications.yml 에서 value annotation을 통해서 값을 받아온다.
    @Value("${social-login.google.url}")
    private String GOOGLE_SNS_LOGIN_URL;

    @Value("${social-login.google.client-id}")
    private String GOOGLE_SNS_CLIENT_ID;

    @Value("${social-login.google.callback-url}")
    private String GOOGLE_SNS_CALLBACK_URL;

    @Value("${social-login.google.client-secret}")
    private String GOOGLE_SNS_CLIENT_SECRET;
    @Value("${social-login.google.scope}")
    private String GOOGLE_DATA_ACCESS_SCOPE;

    private final ObjectMapper objectMapper;

    @Override
    public String getOAuthRedirectURL() {

        Map<String, Object> params = new HashMap<>();
        params.put("scope", GOOGLE_DATA_ACCESS_SCOPE);
        params.put("response_type", "code");
        params.put("client_id", GOOGLE_SNS_CLIENT_ID);
        params.put("redirect_uri", GOOGLE_SNS_CALLBACK_URL);

        //parameter를 형식에 맞춰 구성해주는 함수
        String parameterString = params.entrySet().stream()
                .map(x -> x.getKey() + "=" + x.getValue())
                .collect(Collectors.joining("&"));
        String redirectURL = GOOGLE_SNS_LOGIN_URL + "?" + parameterString;
        System.out.println("redirectURL = " + redirectURL);

        return redirectURL;
        /*
         * https://accounts.google.com/o/oauth2/v2/auth?scope=profile&response_type=code
         * &client_id="할당받은 id"&redirect_uri="access token 처리")
         * 로 Redirect URL을 생성하는 로직을 구성
         * */
    }

    public GoogleOAuthToken getAccessToken(ResponseEntity<String> responseEntity) {
        try {
            return objectMapper.readValue(responseEntity.getBody(), GoogleOAuthToken.class);
        } catch (Exception e) {
            throw new RuntimeException("액세스 토큰 정보 조회 실패");
        }
    }

    public ResponseEntity<String> requestAccessToken(String code) {
        String GOOGLE_TOKEN_REQUEST_URL = "https://oauth2.googleapis.com/token";
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", GOOGLE_SNS_CLIENT_ID);
        params.put("client_secret", GOOGLE_SNS_CLIENT_SECRET);
        params.put("redirect_uri", GOOGLE_SNS_CALLBACK_URL);
        params.put("grant_type", "authorization_code");

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(GOOGLE_TOKEN_REQUEST_URL,
                params, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {

            return responseEntity;
        }
        return null;

    }

    public ResponseEntity<String> requestUserInfo(GoogleOAuthToken googleOAuthToken) {
        String GOOGLE_USER_INFO_REQUEST_URL = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(GOOGLE_USER_INFO_REQUEST_URL + googleOAuthToken.getAccess_token(), String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity;
        }
        return null;
    }

    public GoogleUser getUserInfo(ResponseEntity<String> responseEntity) {
        try {
            return objectMapper.readValue(responseEntity.getBody(), GoogleUser.class);
        } catch (Exception e) {
            throw new RuntimeException("사용자 정보 조회 실패");
        }
    }
}
