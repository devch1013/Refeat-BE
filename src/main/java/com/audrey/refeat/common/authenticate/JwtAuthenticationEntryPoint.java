package com.audrey.refeat.common.authenticate;

import com.audrey.refeat.common.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@NoArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private ErrorCode errorCode;


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx) throws IOException, ServletException {
        // authentication 지정 안되면 실행됨
        if (request.getAttribute("errorCode") != null) {
            this.errorCode = (ErrorCode) request.getAttribute("errorCode");
        } else {
            this.errorCode = ErrorCode.UNAUTHORIZED;
        }
        FilterResponse.sendErrorResponse(response, this.errorCode);
    }
}