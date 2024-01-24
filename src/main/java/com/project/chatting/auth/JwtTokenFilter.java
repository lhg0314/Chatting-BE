package com.project.chatting.auth;


import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.project.chatting.common.ErrorCode;
import com.project.chatting.exception.TokenException;
import com.project.chatting.exception.UnAuthorizedException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class JwtTokenFilter extends OncePerRequestFilter {
    // 토큰 검증이 필요 없는 url
    private static final String[] WHITELIST = {
      "/user/auth/signin", // 로그인
      "/user/auth/signup"     // 회원가입
    };
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    @Autowired
    private  JwtTokenProvider jwtTokenProvider;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    
	@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
        String path = request.getRequestURI();
        if (Arrays.stream(WHITELIST).anyMatch(pattern -> antPathMatcher.match(pattern, path))) {
            filterChain.doFilter(request, response);
            return;
        }
        String accessToken = resolveToken(request);
        try {
            // Access Token 유효성 검사
        	// to-be jwt exception 추가 개발 필요 (현재는 500에러 내려옴)
            if(jwtTokenProvider.validateAccessToken(accessToken)) {
            	String blToken = (String)redisTemplate.opsForValue().get(accessToken);
            	// 해당 토큰이 블랙리스트 처리된 토큰인지 확인
            	if (ObjectUtils.isEmpty(blToken)) {
            		Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                	SecurityContextHolder.getContext().setAuthentication(authentication);
            	}
            }else {
            	throw new TokenException("토큰이 만료되었습니다.", ErrorCode.TOKEN_EXPIRED_EXCEPTION);
            }
        } catch (UnAuthorizedException e) {
        	throw new TokenException("토큰이 유효하지 않습니다..", ErrorCode.TOKEN_EXPIRED_EXCEPTION);
        }
        filterChain.doFilter(request, response);
    }
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(BEARER.length());
        }
        return null;
    }
}