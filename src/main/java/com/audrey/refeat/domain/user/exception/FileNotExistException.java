package com.audrey.refeat.domain.user.exception;

import com.audrey.refeat.common.exception.CustomException;
import com.audrey.refeat.common.exception.ErrorCode;

public class FileNotExistException extends CustomException {
    public FileNotExistException() {
        super(ErrorCode.FILE_NOT_EXIST);
    }
}
