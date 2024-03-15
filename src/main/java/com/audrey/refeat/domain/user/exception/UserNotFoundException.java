package com.audrey.refeat.domain.user.exception;

import com.audrey.refeat.common.exception.CustomException;
import com.audrey.refeat.common.exception.ErrorCode;

public class UserNotFoundException extends CustomException {
    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}
