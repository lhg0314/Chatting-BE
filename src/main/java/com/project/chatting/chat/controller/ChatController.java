package com.project.chatting.chat.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.chatting.chat.request.ChatListRequest;
import com.project.chatting.chat.request.ChatRequest;
import com.project.chatting.chat.response.ChatResponse;
import com.project.chatting.chat.response.ChatRoomResponse;
import com.project.chatting.chat.request.CreateJoinRequest;
import com.project.chatting.chat.request.CreateRoomRequest;
import com.project.chatting.chat.request.LeaveChatRoomRequest;
import com.project.chatting.chat.response.CreateRoomResponse;
import com.project.chatting.chat.service.ChatService;
import com.project.chatting.common.ApiResponse;
import com.project.chatting.user.service.UserService;
import jakarta.validation.Valid;

@RestController
public class ChatController {
	@Autowired 
	private UserService userService;
	
	@Autowired 
	private ChatService chatService;
	
	@MessageMapping("/chat/{roomId}")
    @SendTo("/sub/room/{roomId}")
	public ApiResponse<ChatResponse> sendMessage(@DestinationVariable(value = "roomId") int roomId, ChatRequest req) {
		req.setRoomId(roomId);
		if (req.getMessageType() == "FILE") {
			req.setMessage("");
		}
		
		return ApiResponse.success(chatService.insertMessage(req));
	}
	
	@PostMapping(value="/chat/saveMessage")
	public void send() {
		chatService.saveMessages();
	}


	/**
	 * 채팅방 생성
	 */
	@PostMapping("/chat/room")
	public ApiResponse<CreateRoomResponse> createChatRoom(@Valid @RequestBody CreateRoomRequest createRoomRequest) {
		CreateRoomResponse res = chatService.createRoom(createRoomRequest);
		return ApiResponse.success(res);

	}
	
	// 채팅방 목록 조회
	@GetMapping("/chat/roomList")
	public ApiResponse<Map<String, Object>> findAll(@RequestParam String userId) {
		Map<String, Object> chatRoomList = new HashMap<String, Object>();
		chatRoomList.put("roomList", chatService.findAll(userId));

		return ApiResponse.success(chatRoomList);
	}
	
	@GetMapping("/chat/messageList")
	public ApiResponse<Map<String, List>> getMessages(@RequestBody ChatListRequest req) {
		Map<String, List> map = new HashMap<String, List>();
		map.put("msgList", chatService.getMessageList(req));
		
		return ApiResponse.success(map);
	}

	/**
	 * 채팅방 나가기
	 */
	@PostMapping("/chat/leave")
	public ApiResponse<String> leaveChatRoom(@Valid @RequestBody LeaveChatRoomRequest leaveChatRoomRequest){
		chatService.leaveChatRoom(leaveChatRoomRequest);
		return ApiResponse.SUCCESS;
	}
}
