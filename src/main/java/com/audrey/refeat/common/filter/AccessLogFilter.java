package com.audrey.refeat.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class AccessLogFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("path: " + request.getMethod() +
                "  /  " + request.getRequestURL().toString() +
                " query: " + request.getQueryString() +
                " IP: " + this.getRemoteAddr(request));
        filterChain.doFilter(request, response);
    }

    private String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        List<String> headerNames = List.of("X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR", "X-Real-IP", "X-RealIP", "REMOTE_ADDR");
        for (String headerName : headerNames) {
            String header = request.getHeader(headerName);
            if (header != null && header.length() != 0 && !"unknown".equalsIgnoreCase(header)) {
                ip = header;
                break;
            }
        }
        return ip;
    }
}
