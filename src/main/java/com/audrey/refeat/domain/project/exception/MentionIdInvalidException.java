package com.audrey.refeat.domain.project.exception;

import com.audrey.refeat.common.exception.CustomException;
import com.audrey.refeat.common.exception.ErrorCode;

public class MentionIdInvalidException extends CustomException {
    public MentionIdInvalidException() {
        super(ErrorCode.MENTION_ID_INVALID);
    }
}
