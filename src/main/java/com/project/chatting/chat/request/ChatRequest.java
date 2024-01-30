package com.project.chatting.chat.request;

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
	private String msg;
	@NotBlank
	private String msgType;
	private String createAt;
	private String readYn;
}