package com.audrey.refeat.common.authenticate;

import com.audrey.refeat.common.exception.ErrorCode;
import com.audrey.refeat.common.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;

public class FilterResponse {
    private FilterResponse() {
    }

    public static void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().print(mapper.writeValueAsString(new ErrorResponse(errorCode)));
    }
}