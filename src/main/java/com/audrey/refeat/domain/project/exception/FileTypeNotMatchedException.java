package com.audrey.refeat.domain.project.exception;

import com.audrey.refeat.common.exception.CustomException;
import com.audrey.refeat.common.exception.ErrorCode;

public class FileTypeNotMatchedException extends CustomException {
    public FileTypeNotMatchedException() {
        super(ErrorCode.FILE_TYPE_NOT_MATCHED);
    }
}
