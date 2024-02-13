package com.project.chatting.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.project.chatting.auth.JwtTokenProvider;
import com.project.chatting.chat.entity.ChatSet;
import com.project.chatting.chat.repository.ChatRoomRepository;
import com.project.chatting.chat.request.ChatReadRequest;
import com.project.chatting.chat.service.ChatSetService;
import com.project.chatting.common.ErrorCode;
import com.project.chatting.exception.TokenException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class StompHandler implements ChannelInterceptor {

	@Autowired
    private  JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private ChatRoomRepository chatRepo;
	
	@Autowired
	private ChatSetService chatsetService;
	

	@Transactional(readOnly = true)
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);       
      
        if(accessor.getCommand() == StompCommand.CONNECT) {
        	log.info("now :::::::::: CONNECT");
        	log.info(accessor.toString());
        	 if(accessor.getNativeHeader("Authorization")!= null) {
 	            String accesstoken = accessor.getNativeHeader("Authorization").get(0);
 	            try {
 	            	jwtTokenProvider.validateAccessToken(accesstoken);
 	            }catch(Exception e) {
 	            	 throw new MessageDeliveryException("UNAUTHORIZED");
 	            }
             }else {
            	 throw new MessageDeliveryException("UNAUTHORIZED");
            	 
             }
        	
        }else if(StompCommand.SUBSCRIBE.equals(accessor.getCommand())){
        	
        	log.info("SUBSCRIBE"); // stompClient.subscribe 실행 시 호출
        	log.info(accessor.toString());
        	String destination = accessor.getDestination();
        	if(!destination.contains("/sub/room")) return message; // room입장이 아닐때 return
            int lastIndex = destination.lastIndexOf('/');
            String roomId = destination.substring(lastIndex + 1);
            String userId = "";
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            
            if(accessor.getNativeHeader("Authorization")!= null) {
	            userId = jwtTokenProvider.getUserIdFromToken(accessor.getNativeHeader("Authorization").get(0));
	            chatRepo.setUserEnterInfo(sessionId, roomId,userId);	            	            
	            chatRepo.plusUserCount(roomId,userId); // 유저 +1 처리	            
	            log.info("인원수 조회:::::"+Arrays.toString(chatRepo.getUserCount(roomId)));
	           
	            ChatSet readReq = new ChatSet(Integer.parseInt(roomId),userId);
	            chatsetService.updateReadYn(readReq); // 해당체팅방 DB메시지 읽음처리
	            
            }
            
            //to-be
            // 1. 채팅방 입장시 안읽은 메시지 읽은 처리(DB 완료)
            // 2. 입장메시지 필요한가..? -> controller에서 처리
            
        }else if(StompCommand.DISCONNECT.equals(accessor.getCommand())){
        	System.out.println(accessor);
        	log.info("DISCONNECT"); // 소켓 연결 끊었을때 후에 필요할 경우 추가
        	String sessionId = (String) message.getHeaders().get("simpSessionId");
            String roomaAndUserInfo = chatRepo.getUserEnterRoomId(sessionId);
            int index = 0;
            String roomId = "";
            String userId = "";
            if(roomaAndUserInfo != null) {
            	index = roomaAndUserInfo.indexOf("/");
            	 roomId = roomaAndUserInfo.substring(0, index);
            	 userId = roomaAndUserInfo.substring(index+1);
            	 chatRepo.minusUserCount(roomId, userId);
            	 chatRepo.removeUserEnterInfo(sessionId); // 세션 정보 삭제
            }
            
          
        }
        return message;
    }
}