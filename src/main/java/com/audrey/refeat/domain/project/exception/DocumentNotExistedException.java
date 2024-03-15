package com.audrey.refeat.domain.project.exception;

import com.audrey.refeat.common.exception.CustomException;
import com.audrey.refeat.common.exception.ErrorCode;

public class DocumentNotExistedException extends CustomException {
    public DocumentNotExistedException() {
        super(ErrorCode.DOCUMENT_NOT_EXISTED);
    }
}
