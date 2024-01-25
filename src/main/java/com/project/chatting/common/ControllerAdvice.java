package com.project.chatting.common;



import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.project.chatting.exception.ConflictException;
import com.project.chatting.exception.TokenException;
import com.project.chatting.exception.UnAuthorizedException;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ControllerAdvice {

	/**
     * 에러 처리
     *  ResponseStatus - httpStatus
     *  ExceptionHandler - exception class (custom 도 가능)
     *  400 BadRequest
     *  401 UnAuthorized
     *  403 Forbidden
     *  404 NotFound
     *  405 Method Not Supported
     *  409 Conflict
     *  415 UnSupported Media Type
     *  500 Internal Server Error
     *  502 Bad Gateway
     */
  

    /**
     * 400 BadRequest
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    protected ApiResponse<Object> handleBadRequest(BindException e) {
        log.error(e.getMessage(), e);
        return ApiResponse.error(ErrorCode.VALIDATION_EXCEPTION, Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        InvalidFormatException.class,
        ServletRequestBindingException.class
    })
    protected ApiResponse<Object> handleInvalidFormatException(final Exception e) {
        log.error(e.getMessage(), e);
        return ApiResponse.error(ErrorCode.VALIDATION_EXCEPTION);
    }
    
    /**
     * 500 Internal Server Error
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    protected ApiResponse<Object> handleException(final Exception exception) {
        log.error(exception.getMessage(), exception);
        final ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_EXCEPTION;
        return ApiResponse.error(errorCode);
    
    }


    /**
     * 401 UnAuthorized
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnAuthorizedException.class)
    protected ApiResponse<Object> handleUnAuthorizedException(final UnAuthorizedException exception) {
        log.error(exception.getMessage(), exception);
        return ApiResponse.error(exception.getErrorCode());
    }
    
    /**
     * 409 Conflict
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictException.class)
    protected ApiResponse<Object> handleConflictException(final ConflictException exception) {
        log.error(exception.getMessage(), exception);
        return ApiResponse.error(exception.getErrorCode());
    }

    /**
     * Token Expired Exception 
     */
    @ExceptionHandler(TokenException.class)
    protected ApiResponse<Object> handleTokenExpiredException(final TokenException exception){
        log.error(exception.getMessage(), exception);
        return ApiResponse.error(exception.getErrorCode());
    }









    


    


}
