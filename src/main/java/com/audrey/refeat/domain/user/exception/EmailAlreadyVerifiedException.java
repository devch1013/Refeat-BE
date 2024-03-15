package com.audrey.refeat.domain.user.exception;

import com.audrey.refeat.common.exception.CustomException;
import com.audrey.refeat.common.exception.ErrorCode;

public class EmailAlreadyVerifiedException extends CustomException {
    public EmailAlreadyVerifiedException() {
        super(ErrorCode.EMAIL_ALREADY_VERIFIED);
    }
}
