package com.audrey.refeat.common.exception;

import com.audrey.refeat.common.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExceptionControllerHandler {


    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> customException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();

        log.error("Error message : {}", errorCode.getMessage());

        return new ResponseEntity<>(new ErrorResponse(errorCode), HttpStatus.resolve(errorCode.getStatus()));
    }
}
