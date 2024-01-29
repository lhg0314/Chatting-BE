package com.project.chatting.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.project.chatting.auth.JwtTokenProvider;
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

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if(accessor.getCommand() == StompCommand.CONNECT) {
        	System.out.println("소켓 연결됨");

        }else if(StompCommand.SUBSCRIBE.equals(accessor.getCommand())){
        	log.info("SUBSCRIBE"); // 메시지 날릴때 후에 필요할 경우 추가
        }else if(StompCommand.DISCONNECT.equals(accessor.getCommand())){
        	log.info("DISCONNECT"); // 소켓 연결 끊었을때 후에 필요할 경우 추가
        }
        return message;
    }
}