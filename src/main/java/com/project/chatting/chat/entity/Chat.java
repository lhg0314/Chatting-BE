package com.project.chatting.chat.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.data.annotation.Id;

import com.project.chatting.chat.request.ChatRequest;

import lombok.Data;

@Data
public class Chat{
	//chatId roomId userId message messageType createAt readYn
	
	private int chatId;
	private int roomId;
	private String userId;
	private String message;
	private String messageType;
	private String createAt;
	private int readCnt;
	private String fileName;
	private String fileExt;
	private String fileUrl;
	
	public Chat() {
		
	}
	
	public Chat(ChatRequest req) {
		this.roomId = req.getRoomId();
		this.userId = req.getUserId();
		this.message = req.getMessage();
		this.messageType = req.getMessageType();
		this.createAt = req.getCreateAt();
		this.readCnt = req.getReadCnt();
	}
	
	public Chat(int roomId, String userId, String messageType, int readCnt) {
		this.roomId = roomId;
		this.userId = userId;
		this.message = "";
		this.messageType = messageType;
		this.readCnt = readCnt;
	}
	
	public Chat(int roomId, String userId, String message, String messageType, String createAt, int readCnt) {
		this.roomId = roomId;
		this.userId = userId;
		this.message = message;
		this.messageType = messageType;
		this.createAt = createAt;
		this.readCnt = readCnt;
	}
	
}