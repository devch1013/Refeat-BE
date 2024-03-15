package com.audrey.refeat.common.s3.exception;

import com.audrey.refeat.common.exception.CustomException;
import com.audrey.refeat.common.exception.ErrorCode;

public class FileUploadFailException extends CustomException {
    public FileUploadFailException() {
        super(ErrorCode.FILE_UPLOAD_FAIL);
    }
}
