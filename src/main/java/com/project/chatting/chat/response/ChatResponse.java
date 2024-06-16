package com.project.chatting.chat.response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
	private String message;
	private String messageType;
	private String createAt;
	private int readCnt;
	private List<String> users;
	private String fileName;
	private String fileExt;
	private String fileUrl;
	
	public static ChatResponse toDto(ChatRequest chatReq) {
		return ChatResponse.builder()
				.roomId(chatReq.getRoomId())
				.userId(chatReq.getUserId())
				.message(chatReq.getMessage())
				.messageType(chatReq.getMessageType())
				.createAt(chatReq.getCreateAt())
				.readCnt(chatReq.getReadCnt())
				.users(chatReq.getUsers())
				.fileExt(chatReq.getFileExt())
				.fileName(chatReq.getFileName())
				.fileUrl(chatReq.getFileUrl())
				.build();
				
	}
	
	public static ChatResponse toDto(Chat chat) {
		return ChatResponse.builder()
				.roomId(chat.getRoomId())
				.userId(chat.getUserId())
				.message(chat.getMessage())
				.messageType(chat.getMessageType())
				.createAt(chat.getCreateAt())
				.readCnt(chat.getReadCnt())
				.build();
				
	}
}