package com.audrey.refeat.domain.user.exception;

import com.audrey.refeat.common.exception.CustomException;
import com.audrey.refeat.common.exception.ErrorCode;

public class ExistedEmailException extends CustomException {
    public ExistedEmailException() {
        super(ErrorCode.EXISTED_EMAIL);
    }
}
