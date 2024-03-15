package com.audrey.refeat.domain.user.exception;

import com.audrey.refeat.common.exception.CustomException;
import com.audrey.refeat.common.exception.ErrorCode;

public class EmailVerificationFailedException extends CustomException {
    public EmailVerificationFailedException() {
        super(ErrorCode.EMAIL_VERIFICATION_FAILED);
    }
}
