package com.audrey.refeat.domain.user.exception;

import com.audrey.refeat.common.exception.CustomException;
import com.audrey.refeat.common.exception.ErrorCode;

public class EmailVerificationNotCommittedException extends CustomException {
    public EmailVerificationNotCommittedException() {
        super(ErrorCode.EMAIL_VERIFICATION_NOT_PERMITTED);
    }
}
