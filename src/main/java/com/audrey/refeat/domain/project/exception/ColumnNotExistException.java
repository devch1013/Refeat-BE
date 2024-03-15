package com.audrey.refeat.domain.project.exception;

import com.audrey.refeat.common.exception.CustomException;
import com.audrey.refeat.common.exception.ErrorCode;

public class ColumnNotExistException extends CustomException {
    public ColumnNotExistException() {
        super(ErrorCode.COLUMN_TITLE_NOT_EXISTED);
    }
}
