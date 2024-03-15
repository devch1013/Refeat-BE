package com.audrey.refeat.common.authenticate;

import com.audrey.refeat.common.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ErrorCode errorCode;
    public JwtAccessDeniedHandler() {
        this.errorCode = ErrorCode.FORBIDDEN;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        FilterResponse.sendErrorResponse(response, this.errorCode);
    }
}
