package com.audrey.refeat.domain.user.controller;

import com.audrey.refeat.common.response.RestResponseSimple;
import com.audrey.refeat.domain.user.entity.AuthProvider;
import com.audrey.refeat.domain.user.service.SocialAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "User", description = "User API")
@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class SocialAuthController {
    private final SocialAuthService socialAuthService;
    @Operation(summary = "소셜로그인", description = "해당 API로 요청을 보내시면 구글 로그인 화면으로 리다이렉트 됩니다." +
            "<br>socialLoginType은 \"google\"을 담아주시면 됩니다." +
            "<br>리다이렉트 된 화면에서 사용자가 로그인을 성공하면 정해주시는 url(ex. https://refeat.ai/login/callback?access={jwt access token}&refresh={jwt refresh token})의 형태로 " +
            "<br>토큰들이 발급됩니다. 해당 토큰들을 저장 후 메인 화면으로 돌려 주시면 됩니다." +
            "<br>만약 사용자가 이메일로 이미 가입되어있는 경우 https://refeat.ai/login/callback?error=registered_with_another_provider 로 리다이렉트 됩니다." +
            "<br>해당 error url로 리다이렉트되는 경우 에러 메세지를 띄우고 로그인 화면으로 돌려주시면 됩니다.")
    @GetMapping("/auth/{socialLoginType}")
    public void socialLoginRedirect(@PathVariable(name="socialLoginType") String socialLoginPath) throws IOException {
        AuthProvider authProvider = AuthProvider.valueOf(socialLoginPath.toUpperCase());
        socialAuthService.redirectAuth(authProvider);
    }
    @Operation(hidden = true)
    @GetMapping(value = "/auth/{socialLoginType}/callback")
    public void callback (
            @PathVariable(name = "socialLoginType") String socialLoginType,
            @RequestParam(name = "code") String code,
            @RequestParam(name = "redirect", defaultValue = "https://refeat.vercel.app/login/callback") String tokenRedirctUrl)throws IOException,Exception{
        AuthProvider authProvider = AuthProvider.valueOf(socialLoginType.toUpperCase());
        socialAuthService.oAuthLogin(authProvider,code, tokenRedirctUrl);
    }
}
