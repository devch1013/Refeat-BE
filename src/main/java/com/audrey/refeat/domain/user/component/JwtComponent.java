package com.audrey.refeat.domain.user.component;

import com.audrey.refeat.common.exception.ErrorCode;
import com.audrey.refeat.domain.user.dto.JwtUser;
import com.audrey.refeat.domain.user.dto.response.TokenResponseDto;
import com.audrey.refeat.domain.user.entity.Authority;
import com.audrey.refeat.domain.user.entity.UserInfo;
import com.audrey.refeat.domain.user.entity.dao.UserInfoRepository;
import com.audrey.refeat.domain.user.exception.UserNotFoundException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtComponent {
    private final UserInfoRepository userInfoRepository;
    private final JwtTokenProvider jwtTokenProvider;



    public ErrorCode validateToken(String token, boolean refresh) {
        return jwtTokenProvider.verifyToken(token, refresh);
    }

    public TokenResponseDto createToken(UserInfo userInfo) {
        String accessToken = jwtTokenProvider.createAccessToken(userInfo);
        String refreshToken = jwtTokenProvider.createRefreshToken(userInfo.getId());
        return new TokenResponseDto(
                accessToken,
                refreshToken
        );
    }

    public Authentication getAuthentication(String token){
        Claims claims = jwtTokenProvider.extractAllClaims(token, false);
        JwtUser jwtUser = new JwtUser(Long.parseLong(claims.getSubject()), claims.get("email", String.class), Authority.valueOf(claims.get("role", String.class)));
//        UserInfo userInfo = userInfoRepository.findById(Long.parseLong(claims.getSubject())).orElse(null);
//        if (userInfo == null) {
//            return null;
//        }
        return new UsernamePasswordAuthenticationToken(jwtUser, "", null);
    }

    public UserInfo getUserInfo(String token, boolean refresh){
        Claims claims = jwtTokenProvider.extractAllClaims(token, refresh);
        return userInfoRepository.findById(Long.parseLong(claims.getSubject())).orElseThrow();
    }

    public static JwtUser getUserInfo(){
        return (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
