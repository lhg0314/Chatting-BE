package com.project.chatting.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.chatting.auth.request.refreshRequest;
import com.project.chatting.auth.service.AuthService;
import com.project.chatting.common.ApiResponse;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private AuthService authService;

    // 토큰 재발급
    @PostMapping("/refresh")
    public ApiResponse<Object> reGenerateAccessToken(@Valid @RequestBody refreshRequest user){

        ValueOperations<String, String> vop = stringRedisTemplate.opsForValue();
        
        String refreshToken = (String)vop.get("RT:" + user.getUserId());
        // accessToken 재발급 

        return ApiResponse.success(authService.reGenerateAccessToken(user.getUserId(), refreshToken));
    }
}
