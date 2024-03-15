package com.audrey.refeat.domain.user.exception;

import com.audrey.refeat.common.exception.CustomException;
import com.audrey.refeat.common.exception.ErrorCode;

public class RegisteredWithAnotherProviderException extends CustomException {
    public RegisteredWithAnotherProviderException() {
        super(ErrorCode.REGISTERED_WITH_ANOTHER_PROVIDER);
    }
}
