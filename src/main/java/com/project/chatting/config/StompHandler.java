package com.project.chatting.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.project.chatting.auth.JwtTokenProvider;
import com.project.chatting.chat.repository.ChatRoomRepository;
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

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
      
        if(accessor.getCommand() == StompCommand.CONNECT) {
        	// jwt 연동해보기
        }else if(StompCommand.SUBSCRIBE.equals(accessor.getCommand())){
        	log.info("SUBSCRIBE"); // stompClient.subscribe 실행 시 호출
        	System.out.println(accessor);
        	String destination = accessor.getDestination();
            int lastIndex = destination.lastIndexOf('/');
            String roomId = destination.substring(lastIndex + 1);
           
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            chatRepo.setUserEnterInfo(sessionId, roomId);
            
            chatRepo.plusUserCount(roomId); // 유저 +1 처리
            
            //to-be
            // 1. 채팅방 입장시 안읽은 메시지 읽은 처리
            // 2. 입장메시지 필요한가..?
            
        }else if(StompCommand.DISCONNECT.equals(accessor.getCommand())){
        	System.out.println(accessor);
        	log.info("DISCONNECT"); // 소켓 연결 끊었을때 후에 필요할 경우 추가
        	String sessionId = (String) message.getHeaders().get("simpSessionId");
            String roomId = chatRepo.getUserEnterRoomId(sessionId);
          
            
        	 //채팅방의 인원수를 -1한다.
            chatRepo.minusUserCount(roomId);
            chatRepo.removeUserEnterInfo(sessionId); // 세션 정보 삭제
        }
        return message;
    }
}