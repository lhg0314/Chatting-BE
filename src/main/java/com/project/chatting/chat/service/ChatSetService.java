package com.project.chatting.chat.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
		
		//redis data update 안쓰기로 정해서 주석처리
		//updateRedisMessage(readReq.getRoomId(),readReq.getUserId());
		
		chatsetRepo.updateReadYn(readReq); // 해당 체팅방 메시지 모두읽음 처리	
		chatsetRepo.updateReadCnt(readReq.getRoomId()); // chatContent테이블 읽지 않은 사람 수 업데이트
	}
	
	public void updateRedisMessage(int roomId,String userId) {
		try {
			
			Long now_long = Long.parseLong(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
			ZSetOperations<String, ChatRequest> zSetOperations = redisChatTemplate.opsForZSet();
	     
	       
			List<ChatRequest> chatList = new ArrayList<>();
	    	Set<ChatRequest> list = zSetOperations.range("roomId:"+roomId, 0, -1);

			list.forEach(chatList::add);
			
			System.out.println(chatList);
			
			redisTemplate.delete("roomId:"+roomId); // redis에서 기존 채팅방 데이터 삭제
			
			for(ChatRequest chat : chatList) {
				if(!chat.getUsers().contains(userId)) { // 읽은사람 목록에 포함되지 않은 경우
					chat.getUsers().add(userId);
					chat.setReadCnt(chat.getReadCnt() -1); // 읽지 않은 인원 -1
				}
				
				zSetOperations.add("roomId:"+roomId, chat, now_long);
			}
			
			
		}catch(Exception e) {
			log.error(e.getMessage());
		}
	}
	}

