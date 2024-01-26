package com.project.chatting.chat.entity;

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
	private Date createAt;
	private String readYn;
	
}