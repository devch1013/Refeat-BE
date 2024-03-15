package com.audrey.refeat.domain.user.exception;

import com.audrey.refeat.common.exception.CustomException;
import com.audrey.refeat.common.exception.ErrorCode;

public class ExistedNicknameException extends CustomException {
    public ExistedNicknameException() {
        super(ErrorCode.EXISTED_NICKNAME);
    }
}
