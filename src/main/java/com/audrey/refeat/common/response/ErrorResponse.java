package com.audrey.refeat.common.response;

import com.audrey.refeat.common.exception.ErrorCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
public class ErrorResponse {
    private int status;
    private String code;
    private String message;

    public ErrorResponse(ErrorCode errorCode) {

        this.message = errorCode.getMessage();
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus();
    }
}