package com.project.chatting.chat.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.chatting.chat.entity.Chat;
import com.project.chatting.chat.entity.ChatRead;
import com.project.chatting.chat.repository.ChatRepository;
import com.project.chatting.chat.repository.ChatRoomRepository;
import com.project.chatting.chat.request.ChatListRequest;
import com.project.chatting.chat.request.ChatReadRequest;
import com.project.chatting.chat.request.ChatRequest;
import com.project.chatting.chat.response.ChatListResponse;
import com.project.chatting.chat.response.ChatResponse;
import com.project.chatting.chat.response.ChatRoomResponse;
import com.project.chatting.chat.request.CreateJoinRequest;
import com.project.chatting.chat.request.CreateRoomRequest;
import com.project.chatting.chat.request.LeaveChatRoomRequest;
import com.project.chatting.chat.response.CreateRoomResponse;
import com.project.chatting.common.ErrorCode;
import com.project.chatting.exception.ConflictException;
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
	
	@Autowired
	  private RedisTemplate<String, ChatRequest> redisChatTemplate;
	
	public ChatResponse insertMessage(ChatRequest req) {
		// 시간 score로 관리하기 위해 숫자로 변환
		String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		Long now_long = Long.parseLong(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
		// 현재 시간 set
		req.setCreateAt(now);
		int allMember = chatRepository.getChatMemberCnt(req.getRoomId());
		int connectMember = chatRoomRepository.getUserCount(Integer.toString(req.getRoomId())).length;
		
		// 안읽음 숫자
		req.setReadCnt(allMember - connectMember);
		req.setUsers(Arrays.asList(chatRoomRepository.getUserCount(Integer.toString(req.getRoomId()))));
		
        // redis messageData 저장
		ZSetOperations<String, ChatRequest> zSetOperations = redisChatTemplate.opsForZSet();
		zSetOperations.add("roomId:"+req.getRoomId(), req, now_long);
		
		//System.out.println(zSetOperations.range("ZKey", 0, -1));
		ChatResponse res = ChatResponse.toDto(req);
		
		return res;
	}
	
	@Transactional
	public void saveMessages() {
		Set<String> keys = redisTemplate.keys("roomId:*");
		Iterator<String> it = keys.iterator();
		
		while(it.hasNext()) {
			List<ChatRequest> convertedlist = new ArrayList<>();
			
			String data = it.next();
			
			ZSetOperations<String, ChatRequest> zSetOperations = redisChatTemplate.opsForZSet();
			
			//
			Long lastDate = Long.parseLong(chatRepository.getMessageList(Integer.parseInt(data.split(":")[1]), 1, 0).get(0).getCreateAt());
			zSetOperations.removeRangeByScore(data, 0, lastDate);
			//
			Set<ChatRequest> list = zSetOperations.range(data, 0, -1);

			list.forEach(convertedlist::add);
			//
			chatRepository.setChatMessage(convertedlist);
			for (int i=0; i < convertedlist.size(); i++) {
				int roomId = convertedlist.get(i).getRoomId();
				String createAt = convertedlist.get(i).getCreateAt();
				String creater = convertedlist.get(i).getUserId();
				
				List<String> allUsers = chatRepository.getRoomMember(roomId);
				List<String> savedUsers = convertedlist.get(i).getUsers();
				//리스트 합치기
				List<String> join = Stream.concat(allUsers.stream(), savedUsers.stream())
						.distinct().collect(Collectors.toList());
				
				List<ChatReadRequest> listmap = new ArrayList<>();
				
				join.forEach(item -> {
					Map<String, String> map = new HashMap<String, String>();
					
					map.put("creater", creater);
					map.put("id", item);
					map.put("yn", savedUsers.contains(item) ? "1" : "0");
					map.put("at", createAt);
					
					listmap.add(new ChatReadRequest(roomId, map));
				});
				
				chatRepository.setChatRead(listmap);
			}
			
			redisTemplate.delete(data);

		}
	}

	// 채팅방 생성
	public CreateRoomResponse createRoom(CreateRoomRequest createRoomRequest){

		// 1:1 인지 그룹 인지 먼저 체크
		if(createRoomRequest.getUserId().size() == 2){
			// 1:1 채팅방 로직

			// 이미 존재하는 방인지 체크 필요
			Collections.sort(createRoomRequest.getUserId());
			String users = createRoomRequest.getUserId().stream().collect(Collectors.joining(","));
			System.out.println("Users : " + users);

			String roomId = chatRepository.findChatRoomByUserId(users);

			if(roomId != null){
				// 채팅방 존재 
				return CreateRoomResponse.toDto(Integer.parseInt(roomId), true);
			}
		}

		// 채팅방 생성 로직
		chatRepository.setChatRoom(createRoomRequest);

		List<CreateJoinRequest> createJoinRequestList = new ArrayList<>();
		for(String user : createRoomRequest.getUserId()){
			createJoinRequestList.add(new CreateJoinRequest(user, createRoomRequest.getRoomId(), "Y"));
		}

		chatRepository.setChatJoin(createJoinRequestList);

		return CreateRoomResponse.toDto(createRoomRequest.getRoomId(), false);
	}

	// 채팅방 나가기
	@Transactional
	public void leaveChatRoom(LeaveChatRoomRequest leaveChatRoomRequest){

		// 채팅방 존재하는지 확인
		if(chatRepository.existChatRoom(leaveChatRoomRequest.getRoomId()) == 0){
			throw new ConflictException("채팅방이 없습니다.", ErrorCode.CONFLICT_ROOM_EXIST_EXCEPTION);
		}

		// room_state Y => N 으로 변경
		chatRepository.setLeaveChatJoin(leaveChatRoomRequest);

		// 참여 인원수 조회
		int joinUsers = chatRepository.getChatJoinUsers(leaveChatRoomRequest.getRoomId());
		System.out.println("참여자 인원수 : " + joinUsers);

		// 모두 나갔을 경우 채팅방 삭제
		if(joinUsers == 0){
			chatRepository.deleteChatRoom(leaveChatRoomRequest.getRoomId());
		}
	}

	// 채팅방 목록 조회
   	public List<ChatRoomResponse> findAll(String userId) {
        return chatRepository.selectChatRoomList(userId);
    }
   	
   	public List<ChatListResponse> getMessageList(ChatListRequest req) {
   		List<ChatListResponse> resList = new ArrayList<ChatListResponse>();
   		List<ChatRequest> li = new ArrayList<>();
   		List<Chat> tempLi = new ArrayList<>();
   		
   		ZSetOperations<String, ChatRequest> zSetOperations = redisChatTemplate.opsForZSet();
   		int start = req.getCnt() * (req.getPageNum() - 1);
   		int end = req.getCnt() + start - 1;

   		int listCnt = zSetOperations.reverseRange("roomId:"+req.getRoomId(), start, end).size();
   		
   		int cntforlist = zSetOperations.reverseRange("roomId:"+req.getRoomId(), 0, -1).size();
   		int limit = req.getCnt()*2;
   		int offset = cntforlist - (cntforlist%req.getCnt());
   		
   		if (listCnt != req.getCnt() || listCnt == 0) {
   			// db에서 조회하여 저장
			tempLi = chatRepository.getMessageList(req.getRoomId(), limit, offset);
			tempLi.forEach(item -> {
				//
				List<ChatRead> readUsers = chatRepository.getChatMessageUsers(item.getChatId());
				List<String> userList = new ArrayList<>();
				readUsers.forEach(user -> {
					userList.add(user.getUserId());
					
				});
				
				ChatRequest chatreq = ChatRequest.toDto(item, userList);
				Long crdate = Long.parseLong(chatreq.getCreateAt());
				zSetOperations.add("roomId:"+req.getRoomId(), chatreq, crdate);
			});
   		}
   		
   		//redis에서 list불러오기
   		Set<ChatRequest> list = zSetOperations.reverseRange("roomId:"+req.getRoomId(), start, end);
   		
		list.forEach(li::add);
		
		li.forEach(item -> {
			ChatListResponse resChat = ChatListResponse.toReqDto(item);
			
			resList.add(resChat);
		});
		
   		return resList;
   	}

}
