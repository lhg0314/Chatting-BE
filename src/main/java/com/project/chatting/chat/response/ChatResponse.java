package com.project.chatting.chat.response;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.project.chatting.chat.entity.Chat;
import com.project.chatting.chat.request.ChatRequest;
import com.project.chatting.user.response.SigninResponse;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class ChatResponse {
	private int roomId;
	private String userId;
	private String msg;
	private String msgType;
	private String createAt;
	private int readCnt;
	
	public static ChatResponse toDto(ChatRequest chatReq) {
		return ChatResponse.builder()
				.roomId(chatReq.getRoomId())
				.userId(chatReq.getUserId())
				.msg(chatReq.getMessage())
				.msgType(chatReq.getMessageType())
				.createAt(chatReq.getCreateAt())
				.readCnt(chatReq.getReadCnt())
				.build();
				
	}
	
	public static ChatResponse toDto(Chat chat) {
		return ChatResponse.builder()
				.roomId(chat.getRoomId())
				.userId(chat.getUserId())
				.msg(chat.getMessage())
				.msgType(chat.getMessageType())
				.createAt(chat.getCreateAt())
				.readCnt(chat.getReadCnt())
				.build();
				
	}
}