package com.audrey.refeat.common.exception;


import com.audrey.refeat.common.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class PresetExceptionControllerException {

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponse> IllegalArgumentException(IllegalArgumentException e) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        log.error("Error message : {}", errorCode.getMessage());
        log.error("{}", e.getMessage());
        log.error("{}", (Object)e.getStackTrace());
        return new ResponseEntity<>(new ErrorResponse(errorCode), HttpStatus.resolve(errorCode.getStatus()));

    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> HttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        ErrorCode errorCode = ErrorCode.HTTP_REQUEST_METHOD_NOT_SUPPORTED;
        log.error("Error message : {}", errorCode.getMessage());
        log.error("{}", e.getMessage());
        log.error("{}", (Object)e);
        return new ResponseEntity<>(new ErrorResponse(errorCode), HttpStatus.resolve(errorCode.getStatus()));

    }

    @ExceptionHandler(TypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> TypeMismatchException(TypeMismatchException e) {
        ErrorCode errorCode = ErrorCode.PARAMETER_TYPE_INVALID;
        log.error("Error message : {}", errorCode.getMessage());
        log.error("{}", e.getMessage());
        log.error("{}", (Object)e);
        return new ResponseEntity<>(new ErrorResponse(errorCode), HttpStatus.resolve(errorCode.getStatus()));

    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> HttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ErrorCode errorCode = ErrorCode.HTTP_MESSAGE_NOT_READABLE;
        log.error("Error message : {}", errorCode.getMessage());
        log.error("{}", e.getMessage());
        log.error("{}", (Object)e);
        return new ResponseEntity<>(new ErrorResponse(errorCode), HttpStatus.resolve(errorCode.getStatus()));

    }
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> Exception(Exception e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        log.error("Error message : {}", errorCode.getMessage());
        log.error("{}", e.getMessage());
        log.error("{}", (Object) e.getStackTrace());
        return new ResponseEntity<>(new ErrorResponse(errorCode), HttpStatus.resolve(errorCode.getStatus()));

    }
}
