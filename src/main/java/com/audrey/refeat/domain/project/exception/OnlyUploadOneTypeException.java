package com.audrey.refeat.domain.project.exception;

import com.audrey.refeat.common.exception.CustomException;
import com.audrey.refeat.common.exception.ErrorCode;

public class OnlyUploadOneTypeException extends CustomException {
    public OnlyUploadOneTypeException() {
        super(ErrorCode.ONLY_UPLOAD_ONE_TYPE);
    }
}
