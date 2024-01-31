package com.project.chatting.chat.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.project.chatting.chat.request.CreateJoinRequest;
import com.project.chatting.chat.request.CreateRoomRequest;
import com.project.chatting.chat.response.ChatRoomResponse;

@Mapper
public interface ChatRepository {

	public String findChatRoomByUserId(CreateRoomRequest createRoomRequest);

	public int setChatRoom(CreateRoomRequest createRoomRequest);

	public void setChatJoin(CreateJoinRequest createJoinRequest);

	public List<ChatRoomResponse> selectChatRoomList(String userId);
}