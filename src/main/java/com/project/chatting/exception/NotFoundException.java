package com.project.chatting.exception;

import com.project.chatting.common.ErrorCode;

public class NotFoundException extends ChatException {

	public NotFoundException(String message) {
        super(message, ErrorCode.NOT_FOUND_EXCEPTION);
    }

    public NotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
