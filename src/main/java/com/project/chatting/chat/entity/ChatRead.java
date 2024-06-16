package com.project.chatting.chat.entity;

import lombok.Data;

@Data
public class ChatRead {
	private int readId;
	private int chatId;
	private int roomId;
	private String userId;
	private String readYn;
}
