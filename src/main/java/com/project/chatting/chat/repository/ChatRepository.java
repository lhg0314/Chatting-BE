package com.project.chatting.chat.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.project.chatting.chat.request.ChatReadRequest;
import com.project.chatting.chat.request.ChatRequest;
import com.project.chatting.chat.request.CreateJoinRequest;
import com.project.chatting.chat.request.CreateRoomRequest;

@Mapper
public interface ChatRepository {

	// public String findChatRoomByUserId(CreateRoomRequest createRoomRequest);
	public String findChatRoomByUserId(String users);

	public int setChatRoom(CreateRoomRequest createRoomRequest);

	public void setChatJoin(List<CreateJoinRequest> createJoinRequest);
	
	public int getChatMemberCnt(int roomId);
	
	public int setChatMessage(ChatRequest req);
	
	public int setChatRead(ChatReadRequest req);
}