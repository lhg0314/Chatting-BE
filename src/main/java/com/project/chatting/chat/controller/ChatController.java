package com.project.chatting.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.chatting.chat.service.ChatService;
import com.project.chatting.user.service.UserService;

@RestController
@RequestMapping("/chat")
public class ChatController {
	@Autowired 
	private UserService userService;
	
	@Autowired 
	private ChatService chatService;

}
