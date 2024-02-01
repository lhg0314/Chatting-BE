package com.project.chatting.chat.response;

import com.project.chatting.chat.entity.Chat;
import com.project.chatting.chat.request.ChatRequest;
import com.project.chatting.user.response.UserListResponse;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class ChatListResponse {
	private String userId;
	private String msg;
	private String msgType;
	private String createAt;
	private int readCnt;
	
	public static ChatListResponse toChatDto(Chat chat) {
		return ChatListResponse.builder()
				.userId(chat.getUserId())
				.msg(chat.getMessage())
				.msgType(chat.getMessageType())
				.createAt(chat.getCreateAt())
				.readCnt(chat.getReadCnt())
				.build();
	}
	
	public static ChatListResponse toReqDto(ChatRequest chatReq) {
		return ChatListResponse.builder()
				.userId(chatReq.getUserId())
				.msg(chatReq.getMessage())
				.msgType(chatReq.getMessageType())
				.createAt(chatReq.getCreateAt())
				.readCnt(chatReq.getReadCnt())
				.build();
	}
}
