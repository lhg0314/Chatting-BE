package com.project.chatting.exception;

import com.project.chatting.common.ErrorCode;

public class ValidationException extends ChatException {
	public ValidationException(String message) {
        super(message, ErrorCode.VALIDATION_EXCEPTION);
    }

    public ValidationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

}
