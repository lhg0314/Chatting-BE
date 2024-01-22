package com.project.chatting.exception;

import com.project.chatting.common.ErrorCode;

public class TokenException extends ChatException{
	
	 public TokenException(String message) {
	        super(message, ErrorCode.TOKEN_EXPIRED_EXCEPTION);
	    }

	  public TokenException(String message, ErrorCode errorCode) {
	        super(message, errorCode);
	    }
}
