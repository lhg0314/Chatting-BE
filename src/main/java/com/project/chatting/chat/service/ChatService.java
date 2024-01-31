package com.project.chatting.chat.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import com.project.chatting.chat.repository.ChatRepository;
import com.project.chatting.chat.repository.ChatRoomRepository;
import com.project.chatting.chat.request.ChatRequest;
import com.project.chatting.chat.response.ChatResponse;
import com.project.chatting.chat.response.ChatRoomResponse;
import com.project.chatting.chat.request.CreateJoinRequest;
import com.project.chatting.chat.request.CreateRoomRequest;
import com.project.chatting.chat.response.CreateRoomResponse;
import com.project.chatting.user.repository.UserRepository;

@Service
public class ChatService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ChatRoomRepository chatRoomRepository;

	@Autowired
	private ChatRepository chatRepository;
	
	@Autowired
    private RedisTemplate<String, Object> redisTemplate;
	
	public ChatResponse insertMessage(ChatRequest req) {
		// 시간 score로 관리하기 위해 숫자로 변환
		String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		Long now_long = Long.parseLong(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
		req.setCreateAt(now);
		req.setReadYn("N");
		
		// socket에 연결된 사람이 둘일때만 처리
		if (chatRoomRepository.getUserCount(Integer.toString(req.getRoomId())) == 2) {
			req.setReadYn("Y");
		}
		
        // redis 저장
		ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
		zSetOperations.add("roomId:"+req.getRoomId(), req, now_long);
		
		
		//System.out.println(zSetOperations.range("ZKey", 0, -1));
		ChatResponse res = ChatResponse.toDto(req);
		
		return res;
	}

	// 채팅방 조회
	public int existChatRoom(CreateRoomRequest createRoomRequest){
		String result = chatRepository.findChatRoomByUserId(createRoomRequest);

		return result == null ? -1 : Integer.parseInt(result) ;
	}

	// 채팅방 생성
	public CreateRoomResponse createRoom(CreateRoomRequest createRoomRequest){
		chatRepository.setChatRoom(createRoomRequest);
		
		CreateRoomResponse createRoomResponse = CreateRoomResponse.toDto(createRoomRequest.getRoomId());
		return createRoomResponse;
	}

	// 채팅방 참여 생성
	public void createJoin(CreateJoinRequest createJoinRequest) {
		chatRepository.setChatJoin(createJoinRequest);
	}
	
	// 채팅방 목록 조회
	public List<ChatRoomResponse> findAll(String userId) {
        return chatRepository.selectChatRoomList(userId);
    }
}