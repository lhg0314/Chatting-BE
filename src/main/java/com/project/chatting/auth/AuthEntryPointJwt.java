package com.project.chatting.auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

	private final HandlerExceptionResolver resolver;

	public AuthEntryPointJwt(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver){
		// HanlderExceptionResolver의 두 종류 중 handlerExceptionResolver를 주입 받겠다고 명시
		this.resolver = resolver;
	}

	// @Override
	// public void commence(HttpServletRequest request, HttpServletResponse response,
	// 		AuthenticationException authException) throws IOException, ServletException {
	// 	log.error("Unauthorized error: {}", authException.getMessage());
	// 	response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
	// }

	/**
	 * spring security 인증 관련 예외 처리를 담당
	 * ControllerAdvice에서 모든 예외를 처리하여 응답하기에 HandlerExceptionResolver에 예외처리를 위임
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        resolver.resolveException(request, response, null, (Exception) request.getAttribute("exception"));
    }

}
