package com.audrey.refeat.domain.project.exception;

import com.audrey.refeat.common.exception.CustomException;
import com.audrey.refeat.common.exception.ErrorCode;

public class AiServerErrorException extends CustomException {
    public AiServerErrorException() {
        super(ErrorCode.AI_SERVER_ERROR);
    }
}
