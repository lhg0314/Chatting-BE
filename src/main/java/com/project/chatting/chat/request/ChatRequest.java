package com.project.chatting.chat.request;

import java.util.List;

import com.project.chatting.chat.entity.Chat;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRequest {
	
	@NotBlank
	private int roomId;
	@NotBlank
	private String userId;
	private String message;
	@NotBlank
	private String messageType;
	private String fileName;
	private String fileExt;
	private String fileUrl;
	private String createAt;
	private int readCnt;
	
	private List<String> users;
	
	public static ChatRequest toDto(Chat chat, List<String> users) {
		return ChatRequest.builder()
				.roomId(chat.getRoomId())
				.userId(chat.getUserId())
				.message(chat.getMessage())
				.messageType(chat.getMessageType())
				.createAt(chat.getCreateAt())
				.readCnt(chat.getReadCnt())
				.userId(chat.getUserId())
				.users(users)
				.build();
	}
}