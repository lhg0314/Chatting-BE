package com.project.chatting.exception;

import com.project.chatting.common.ErrorCode;

public class UnAuthorizedException extends ChatException{

	  public UnAuthorizedException(String message, ErrorCode errorCode) {
	        super(message, errorCode);
	    }
}
