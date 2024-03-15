package com.audrey.refeat.common.authenticate;

import com.audrey.refeat.common.exception.ErrorCode;
import com.audrey.refeat.domain.user.component.JwtComponent;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtComponent jwtComponent;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        String token = parseBearerToken(request);
        ErrorCode errorCode = jwtComponent.validateToken(token, false);
        if(errorCode == ErrorCode.AUTH_SUCCESS){
            Authentication authentication = jwtComponent.getAuthentication(token);
            if (authentication != null){
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }else{
                request.setAttribute("errorCode", ErrorCode.USER_NOT_FOUND);
            }
        }else{
            request.setAttribute("errorCode", errorCode);
        }
        filterChain.doFilter(request, response);
    }

    private String parseBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}