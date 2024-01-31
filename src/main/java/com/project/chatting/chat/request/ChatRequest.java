package com.project.chatting.chat.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
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
	private String createAt;
	private int readCnt;
	
	private List<String> users;
}