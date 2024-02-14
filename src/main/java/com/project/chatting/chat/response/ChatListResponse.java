package com.project.chatting.chat.response;

import java.util.List;

import com.project.chatting.chat.entity.Chat;
import com.project.chatting.chat.request.ChatRequest;
import com.project.chatting.user.response.UserListResponse;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Builder
public class ChatListResponse {
	private int chatId;
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
	
	public static ChatListResponse toChatDto(Chat chat) {
		return ChatListResponse.builder()
				.chatId(chat.getChatId())
				.roomId(chat.getRoomId())
				.userId(chat.getUserId())
				.message(chat.getMessage())
				.messageType(chat.getMessageType())
				.fileExt(chat.getFileExt())
				.fileName(chat.getFileName())
				.fileUrl(chat.getFileUrl())
				.createAt(chat.getCreateAt())
				.readCnt(chat.getReadCnt())
				.build();
	}
	
	public static ChatListResponse toReqDto(ChatRequest chatReq) {
		return ChatListResponse.builder()
				.userId(chatReq.getUserId())
				.message(chatReq.getMessage())
				.messageType(chatReq.getMessageType())
				.createAt(chatReq.getCreateAt())
				.readCnt(chatReq.getReadCnt())
				.build();
	}
}
