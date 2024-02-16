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
import java.util.Base64;
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

import com.project.chatting.auth.JwtTokenProvider;
import com.project.chatting.chat.entity.Chat;
import com.project.chatting.chat.entity.ChatRead;
import com.project.chatting.chat.entity.ChatSet;
import com.project.chatting.chat.repository.ChatRepository;
import com.project.chatting.chat.repository.ChatRoomRepository;
import com.project.chatting.chat.repository.ChatSetRepository;
import com.project.chatting.chat.request.ChatFileRequest;
import com.project.chatting.chat.request.ChatListRequest;
import com.project.chatting.chat.request.ChatReadRequest;
import com.project.chatting.chat.request.ChatRequest;
import com.project.chatting.chat.response.ChatFileResponse;
import com.project.chatting.chat.response.ChatListResponse;
import com.project.chatting.chat.response.ChatResponse;
import com.project.chatting.chat.response.ChatRoomResponse;
import com.project.chatting.chat.request.CreateJoinRequest;
import com.project.chatting.chat.request.CreateRoomRequest;
import com.project.chatting.chat.request.LeaveChatRoomRequest;
import com.project.chatting.chat.response.CreateRoomResponse;
import com.project.chatting.common.ErrorCode;
import com.project.chatting.exception.ConflictException;
import com.project.chatting.exception.NotFoundException;
import com.project.chatting.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;



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
	
//	@Autowired
//	private RedisTemplate<String, ChatFileRequest> redisChatFileTemplate;
	
	@Autowired
	private ChatFileService chatFileService;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private ChatSetService chatSetService;

	@Transactional
	public ChatResponse insertMessage(ChatRequest req) {
		String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSS"));
		
		req.setCreateAt(now);
		int allMember = chatRepository.getChatMemberCnt(req.getRoomId());
		int connectMember = chatRoomRepository.getUserCount(Integer.toString(req.getRoomId())).length;
		List<String> allUsers = chatRepository.getRoomMember(req.getRoomId());
		List<String> connectUsers = Arrays.asList(chatRoomRepository.getUserCount(Integer.toString(req.getRoomId())));
		List<ChatReadRequest> listmap = new ArrayList<>();
		
		// 안읽음 숫자
		req.setReadCnt(allMember - connectMember);
		req.setUsers(connectUsers);

		if ("EXIT".equals(req.getMessageType())){
			req.setMessage(req.getUserId() + " 님 퇴장");
		} else if ("FILE".equals(req.getMessageType())) { 
			req.setMessage("사진을 보냈습니다.");
		}
		
		chatRepository.setChatMessage(req); //채팅 내용 insert
		
		List<String> joinList = Stream.concat(allUsers.stream(), connectUsers.stream())
				.distinct().collect(Collectors.toList());
		
		joinList.forEach(item -> {
			Map<String, String> map = new HashMap<String, String>();
			
			map.put("creater", req.getUserId());
			map.put("id", item);
			map.put("yn", connectUsers.contains(item) || req.getUserId().equals(item) ? "1" : "0");
			map.put("at", now);
			
			listmap.add(new ChatReadRequest(req.getRoomId(), map));
		});
		
		chatRepository.setChatRead(listmap); //채팅 읽음 insert

		if ("FILE".equals(req.getMessageType())) {
			// 파일 업로드 함수 호출
			Map<String, String> fileMap = new HashMap<>();
			
			fileMap.put("creater", req.getUserId());
			fileMap.put("at", now);
			
			ChatFileRequest cr = ChatFileRequest.builder()
			.roomId(req.getRoomId())
			.fileName(req.getFileName())
			.fileExt(req.getFileExt())
			.fileUrl(req.getFileUrl())
			.fileMap(fileMap)
			.build();
			
			chatFileService.insertFile(cr);
		}
		
		ChatResponse res = ChatResponse.toDto(req);
		
		return res;
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

		chatSetService.updateReadYn(new ChatSet(leaveChatRoomRequest.getRoomId(), leaveChatRoomRequest.getUserId()));

		// room_state Y => N 으로 변경
		chatRepository.setLeaveChatJoin(leaveChatRoomRequest);

		// 참여 인원수 조회
		int joinUsers = chatRepository.getChatJoinUsers(leaveChatRoomRequest.getRoomId());
		System.out.println("참여자 인원수 : " + joinUsers);

		if(joinUsers == 0){
			// 모두 나갔을 경우 채팅방 삭제
			chatFileService.deleteFile(leaveChatRoomRequest.getRoomId());
			chatRepository.deleteChatRoom(leaveChatRoomRequest.getRoomId());
		}
	}


	// 채팅방 목록 조회
   	public List<ChatRoomResponse> findAll(String userId) {
        return chatRepository.selectChatRoomList(userId);
    }
   	
   	public Map<String, Object> getMessageList(ChatListRequest req, HttpServletRequest httpReq) {
   		String userId = jwtTokenProvider.getUserIdFromToken(jwtTokenProvider.resolveToken(httpReq));
   		System.out.println("::userId = "+userId);
   		if(chatRepository.getExistRoom(req.getRoomId(), userId) == 0){
			throw new NotFoundException("존재하지 않습니다.", ErrorCode.NOT_FOUND_EXCEPTION);
		}
   		
   		Map<String, Object> map = new HashMap<String, Object>();
   		List<ChatListResponse> resList = new ArrayList<ChatListResponse>();
   		List<ChatRequest> li = new ArrayList<>();
   		List<Chat> tempLi = new ArrayList<>();
   		
   		tempLi = chatRepository.getMessageList(req);
   		
		int nextYn = chatRepository.getNextDataYn(req);
		tempLi.forEach(item -> {
			List<ChatRead> readUsers = chatRepository.getChatMessageUsers(item.getChatId());
			List<String> userList = new ArrayList<>();
			readUsers.forEach(user -> {
				userList.add(user.getUserId());
			});
			ChatListResponse resChat = ChatListResponse.toChatDto(item);
			resChat.setUsers(userList);
			
			resList.add(resChat);
		});
		
		map.put("msgList", resList);
		map.put("nextYn", nextYn == 1 ? "Y" : "N");
		
   		return map;
   	}

	/**
	 * redis 파일 전송
	 */
	// public ChatFileResponse inserFile(ChatFileRequest chatFileRequest){
	// 	// 시간 score로 관리하기 위해 숫자로 변환
	// 	String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
	// 	Long now_long = Long.parseLong(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
	// 	// 현재 시간 set
	// 	chatFileRequest.setCreateAt(now);

	// 	int allMember = chatRepository.getChatMemberCnt(chatFileRequest.getRoomId());
	// 	int connectMember = chatRoomRepository.getUserCount(Integer.toString(chatFileRequest.getRoomId())).length;
		
	// 	// 안읽음 숫자
	// 	chatFileRequest.setReadCnt(allMember - connectMember);
	// 	//req.setUsers(Arrays.asList(chatRoomRepository.getUserCount(Integer.toString(req.getRoomId()))));
		

	// 	// redis에 저장전에 base64로 인코딩된 문자열을 가공
	// 	try{
	// 		String[] strings = chatFileRequest.getImageCode().split(",");
	// 		String base64Image = strings[1];
	// 		String ext = "";

	// 		if(strings[0].equals("data:image/jpeg;base64")){
	// 			ext = "jpeg";
	// 		}else if(strings[0].equals("data:image/png;base64")){
	// 			ext = "png";
	// 		}else{
	// 			ext = "jpg";
	// 		}

	// 		byte[] imageBytes = Base64.getDecoder().decode(base64Image);
	// 		System.out.println("디코딩된 이미지 바이트 값 : " + imageBytes);
	// 	}catch(Exception e){
	// 		System.out.println("Error");
	// 	}


    //     // redis messageData 저장
	// 	ZSetOperations<String, ChatFileRequest> zSetOperations = redisChatFileTemplate.opsForZSet();
	// 	zSetOperations.add("roomId(file):"+chatFileRequest.getRoomId(), chatFileRequest, now_long);
		
	// 	//System.out.println(zSetOper  ations.range("ZKey", 0, -1));
	// 	ChatFileResponse chatFileResponse = ChatFileResponse.toDto(chatFileRequest);
		
	// 	return chatFileResponse;
	// }

}
