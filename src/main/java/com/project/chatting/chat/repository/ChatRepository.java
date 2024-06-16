package com.project.chatting.chat.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.project.chatting.chat.entity.Chat;
import com.project.chatting.chat.entity.ChatRead;
import com.project.chatting.chat.request.ChatFileRequest;
import com.project.chatting.chat.request.ChatListRequest;
import com.project.chatting.chat.request.ChatReadRequest;
import com.project.chatting.chat.request.ChatRequest;
import com.project.chatting.chat.request.CreateJoinRequest;
import com.project.chatting.chat.request.CreateRoomRequest;
import com.project.chatting.chat.response.ChatListResponse;
import com.project.chatting.chat.request.LeaveChatRoomRequest;
import com.project.chatting.chat.response.ChatRoomResponse;
import com.project.chatting.chat.response.CreateRoomResponse;

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
	
	public List<Chat> getMessageList(ChatListRequest req);

	public int existChatRoom(int roomId);

	public void setLeaveChatJoin(LeaveChatRoomRequest leaveChatRoomRequest);

	public int getChatJoinUsers(int roomId);

	public void deleteChatRoom(int roomId);
	
	public List<ChatRead> getChatMessageUsers(int chatId);

	public void setFile(ChatFileRequest chatFileRequest);
	
	public int getNextDataYn(ChatListRequest req);
	
	public int getExistRoom(@Param("roomId") int roomId, @Param("userId") String userId);
}