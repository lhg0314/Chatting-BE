package com.project.chatting.chat.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chatting.chat.entity.ChatSet;
import com.project.chatting.chat.repository.ChatRepository;
import com.project.chatting.chat.repository.ChatSetRepository;
import com.project.chatting.chat.request.ChatReadRequest;
import com.project.chatting.chat.request.ChatRequest;
import com.project.chatting.config.StompHandler;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatSetService {
	

	
	@Autowired
	private ChatSetRepository chatsetRepo;
	@Autowired
	  private RedisTemplate<String, ChatRequest> redisChatTemplate;
	@Autowired
	  private RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private ChatRepository chatRepository;

	@Transactional
	public void updateReadYn (ChatSet readReq) {
		
		//redis data update
		updateRedisMessage(readReq.getRoomId(),readReq.getUserId());
		
		//chatsetRepo.updateReadYn(readReq); // 해당 체팅방 메시지 모두읽음 처리	
		//chatsetRepo.updateReadCnt(readReq.getRoomId()); // chatContent테이블 읽지 않은 사람 수 업데이트
	}
	
	public void updateRedisMessage(int roomId,String userId) {
		try {
		  ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
	        ObjectMapper objectMapper = new ObjectMapper(); // linkedHashMap으로 저장된 redis 값들을 List로 변환해줌
	        Set<Object> result = zSetOps.reverseRange("roomId:"+roomId, 0, -1);
	        
	        System.out.println("조회결과 :::: "+result);
		}catch(Exception e) {
			log.error(e.getMessage());
		}
	}
	}

