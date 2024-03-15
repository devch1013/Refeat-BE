package com.audrey.refeat.domain.user.exception;

import com.audrey.refeat.common.exception.CustomException;
import com.audrey.refeat.common.exception.ErrorCode;

public class EmailVerificationExpiredException extends CustomException {
    public EmailVerificationExpiredException() {
        super(ErrorCode.EMAIL_VERIFICATION_EXPIRED);
    }
}
