package com.project.chatting.exception;

import com.project.chatting.common.ErrorCode;

public class ConflictException extends ChatException{
	public ConflictException(String message) {
        super(message, ErrorCode.CONFLICT_EXCEPTION);
    }

    public ConflictException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

}
