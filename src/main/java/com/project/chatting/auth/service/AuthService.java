package com.project.chatting.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.project.chatting.auth.JwtTokenFilter;
import com.project.chatting.auth.JwtTokenProvider;
import com.project.chatting.auth.RefreshToken;
import com.project.chatting.common.ErrorCode;
import com.project.chatting.exception.ConflictException;
import com.project.chatting.user.entity.User;
import com.project.chatting.user.repository.UserRepository;

import io.jsonwebtoken.ExpiredJwtException;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // refresh Token 재발급
    public RefreshToken reGenerateAccessToken(String userId, String refreshToken){
        System.out.println("재발급 Service 진입");
        User userDetails = userRepository.findMemberById(userId);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userId, userDetails.getPassword());
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        RefreshToken token = new RefreshToken(authentication.getName(), accessToken, refreshToken);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token.getAccessToken());
        return token;

    }
}
