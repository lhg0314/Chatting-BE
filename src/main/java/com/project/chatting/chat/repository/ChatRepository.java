package com.project.chatting.chat.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

import com.project.chatting.chat.entity.Chat;
import com.project.chatting.chat.request.ChatReadRequest;
import com.project.chatting.chat.request.ChatRequest;
import com.project.chatting.chat.request.CreateJoinRequest;
import com.project.chatting.chat.request.CreateRoomRequest;
import com.project.chatting.chat.response.ChatListResponse;
import com.project.chatting.chat.response.ChatRoomResponse;

@Mapper
public interface ChatRepository {

	// public String findChatRoomByUserId(CreateRoomRequest createRoomRequest);
	public String findChatRoomByUserId(String users);

	public int setChatRoom(CreateRoomRequest createRoomRequest);

	public void setChatJoin(List<CreateJoinRequest> createJoinRequest);
	
	public int getChatMemberCnt(int roomId);

	public int setChatMessage(ChatRequest req);
	
	public int setChatRead(List<ChatReadRequest> req);

	public List<ChatRoomResponse> selectChatRoomList(String userId);
	
	public List<String> getRoomMember(int roomId);
	
	public List<Chat> getMessageList(int roomId, int limit, int offset);
}