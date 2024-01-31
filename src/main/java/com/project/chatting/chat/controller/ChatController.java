package com.project.chatting.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.project.chatting.chat.request.ChatRequest;
import com.project.chatting.chat.response.ChatResponse;
import com.project.chatting.chat.request.CreateJoinRequest;
import com.project.chatting.chat.request.CreateRoomRequest;
import com.project.chatting.chat.response.CreateRoomResponse;
import com.project.chatting.chat.service.ChatService;
import com.project.chatting.common.ApiResponse;
import com.project.chatting.user.service.UserService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

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
		if (req.getMsgType() == "FILE") {
			req.setMsg("");
		}
		
		return ApiResponse.success(chatService.insertMessage(req));
	}
//	
//	@PostMapping(value="/chat/send")
//	public ApiResponse<ChatResponse> send(@RequestBody ChatRequest req) {
//
//		return ApiResponse.success(chatService.insertMessage(req));
//	}


	/**
	 * 채팅방 생성
	 */
	@PostMapping("/chat/room")
	public ApiResponse<Integer> createChatRoom(@Valid @RequestBody CreateRoomRequest createRoomRequest) {

		// 채팅방이 존재하는지 확인
		int roomId = -1;
		roomId = chatService.existChatRoom(createRoomRequest);
		if (roomId == -1) {
			// 채팅방 없음
			CreateRoomResponse createRoomResponse = chatService.createRoom(createRoomRequest);

			// 채팅방 참여
			CreateJoinRequest createJoinRequest = new CreateJoinRequest(createRoomRequest.getFromUserId(),
					createRoomResponse.getRoomId(), "Y"); // user 1
			chatService.createJoin(createJoinRequest);
			createJoinRequest = new CreateJoinRequest(createRoomRequest.getToUserId(), createRoomResponse.getRoomId(),
					"Y"); // user 1
			chatService.createJoin(createJoinRequest);

			roomId = createRoomRequest.getRoomId();
		}

		return ApiResponse.success(roomId);

	}
	
	// 채팅방 목록 조회
	@GetMapping("/chat/roomList")
	public ApiResponse<Map<String, Object>> findAll(@RequestParam String userId) {
		Map<String, Object> chatRoomList = new HashMap<String, Object>();
		chatRoomList.put("roomList", chatService.findAll(userId));

		return ApiResponse.success(chatRoomList);
	}
}
