package com.project.chatting.chat.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
public class Chat {
	//chatId roomId userId message messageType createAt readYn
	
	@Id
	private int chatId;
	private int roomId;
	private String userId;
	private String message;
	private String messageType;
	private String createAt;
	private String readYn;
	
	public Chat(int roomId, String userId, String messageType, String readYn) {
		this.roomId = roomId;
		this.userId = userId;
		this.message = "";
		this.messageType = messageType;
		this.readYn = readYn;
	}
	
}