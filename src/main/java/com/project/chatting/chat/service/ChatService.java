package com.project.chatting.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.project.chatting.chat.repository.ChatRepository;
import com.project.chatting.user.repository.UserRepository;

@Service
public class ChatService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ChatRepository chatRepository;
	
	@Autowired
    private RedisTemplate<String, Object> redisTemplate;
}