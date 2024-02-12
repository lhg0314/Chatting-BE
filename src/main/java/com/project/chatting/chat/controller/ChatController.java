package com.project.chatting.chat.controller;

import java.util.HashMap;
import java.util.Map;

import com.project.chatting.chat.response.ChatFileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.chatting.chat.request.ChatFileRequest;
import com.project.chatting.chat.request.ChatListRequest;
import com.project.chatting.chat.request.ChatRequest;
import com.project.chatting.chat.response.ChatResponse;
import com.project.chatting.chat.request.CreateRoomRequest;
import com.project.chatting.chat.request.LeaveChatRoomRequest;
import com.project.chatting.chat.response.CreateRoomResponse;
import com.project.chatting.chat.service.ChatFileService;
import com.project.chatting.chat.service.ChatService;
import com.project.chatting.common.ApiResponse;
import com.project.chatting.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class ChatController {
	@Autowired 
	private UserService userService;
	
	@Autowired 
	private ChatService chatService;
	
	@Autowired
	private ChatFileService chatFileService;

	@MessageMapping("/chat/{roomId}")
    @SendTo("/sub/room/{roomId}")
	public ApiResponse<ChatResponse> sendMessage(@DestinationVariable(value = "roomId") int roomId, ChatRequest req) {
		req.setRoomId(roomId);
		ChatResponse cr = null;
		//추후 Exit case추가
		if( "ENTER".equals(req.getMessageType())) {
			 cr = ChatResponse.builder()
					.roomId(req.getRoomId())
					.userId(req.getUserId())
					.message(req.getUserId() + " 님 입장")
					.messageType(req.getMessageType())
					.createAt("")					
					.build();
			
		}else {
			//getMessageType = TALK or FILE
			cr = chatService.insertMessage(req);
		}
		return ApiResponse.success(cr);
		
	}
	
	/**
	 * 파일 업로드 처리
	 */
	@PostMapping("/chat/upload")
	public ApiResponse<ChatFileResponse> sendFile(@ModelAttribute ChatFileRequest ChatFileRequest){
		System.out.println(ChatFileRequest.toString());
		return ApiResponse.success(chatFileService.setFile(ChatFileRequest, "src\\main\\resources\\static\\upload"));
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
	public ApiResponse<Map<String, Object>> findAll(@RequestParam (value="userId")String userId) {
		Map<String, Object> chatRoomList = new HashMap<String, Object>();
		chatRoomList.put("roomList", chatService.findAll(userId));

		return ApiResponse.success(chatRoomList);
	}
	
	@PostMapping("/chat/messageList")
	public ApiResponse<Map<String, Object>> getMessages(@RequestBody ChatListRequest req) {
		
		return ApiResponse.success(chatService.getMessageList(req));
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
