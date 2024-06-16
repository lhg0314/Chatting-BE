package com.project.chatting.exception;

import com.project.chatting.common.ErrorCode;

import lombok.Getter;

@Getter
public abstract class ChatException extends RuntimeException {
	private ErrorCode errorCode;

    public ChatException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ChatException(String message) {
        super(message);
    }

}
