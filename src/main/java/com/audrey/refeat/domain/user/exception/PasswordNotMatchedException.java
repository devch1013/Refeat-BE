package com.audrey.refeat.domain.user.exception;

import com.audrey.refeat.common.exception.CustomException;
import com.audrey.refeat.common.exception.ErrorCode;

public class PasswordNotMatchedException extends CustomException {
    public PasswordNotMatchedException() {
        super(ErrorCode.PASSWORD_NOT_MATCHED);
    }
}
