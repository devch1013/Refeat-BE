package com.audrey.refeat.domain.project.exception;

import com.audrey.refeat.common.exception.CustomException;
import com.audrey.refeat.common.exception.ErrorCode;

public class ProjectNotExistException extends CustomException {
    public ProjectNotExistException() {
        super(ErrorCode.PROJECT_NOT_EXIST);
    }
}
