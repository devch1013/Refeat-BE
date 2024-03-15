package com.audrey.refeat.domain.project.exception;

import com.audrey.refeat.common.exception.CustomException;
import com.audrey.refeat.common.exception.ErrorCode;

public class ColumnValueNotExistException extends CustomException {
    public ColumnValueNotExistException() {
        super(ErrorCode.COLUMN_VALUE_NOT_EXISTED);
    }
}
