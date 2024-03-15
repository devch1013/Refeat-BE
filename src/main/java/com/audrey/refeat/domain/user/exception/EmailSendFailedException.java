package com.audrey.refeat.domain.user.exception;

import com.audrey.refeat.common.exception.CustomException;
import com.audrey.refeat.common.exception.ErrorCode;

public class EmailSendFailedException extends CustomException {
    public EmailSendFailedException() {
        super(ErrorCode.EMAIL_SEND_FAILED);
    }
}
