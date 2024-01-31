package com.project.chatting.chat.repository;

import org.apache.ibatis.annotations.Mapper;

import com.project.chatting.chat.request.CreateJoinRequest;
import com.project.chatting.chat.request.CreateRoomRequest;

@Mapper
public interface ChatRepository {

	public String findChatRoomByUserId(CreateRoomRequest createRoomRequest);

	public int setChatRoom(CreateRoomRequest createRoomRequest);

	public void setChatJoin(CreateJoinRequest createJoinRequest);
}