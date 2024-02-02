package com.project.chatting.chat.entity;

import java.util.Date;

import com.project.chatting.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ChatSet {
	
	private int roomId;
	private String userId;

}
