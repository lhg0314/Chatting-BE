package com.project.chatting.chat.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.project.chatting.chat.request.ChatReadRequest;
import com.project.chatting.chat.request.ChatRequest;
import com.project.chatting.chat.request.CreateJoinRequest;
import com.project.chatting.chat.request.CreateRoomRequest;
import com.project.chatting.chat.request.LeaveChatRoomRequest;
import com.project.chatting.chat.response.ChatRoomResponse;

@Mapper
public interface ChatRepository {

	// public String findChatRoomByUserId(CreateRoomRequest createRoomRequest);
	public List<String> findChatRoomByUserId(String users);

	public int setChatRoom(CreateRoomRequest createRoomRequest);

	public void setChatJoin(List<CreateJoinRequest> createJoinRequest);
	
	public int getChatMemberCnt(int roomId);

	public int setChatMessage(ChatRequest req);
	
	public int setChatRead(ChatReadRequest req);

	public List<ChatRoomResponse> selectChatRoomList(String userId);

	public int existChatRoom(int roomId);

	public void setLeaveChatJoin(LeaveChatRoomRequest leaveChatRoomRequest);

	public int getChatJoinUsers(int roomId);

	public void deleteChatRoom(int roomId);
}