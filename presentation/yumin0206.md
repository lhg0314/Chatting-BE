## 채팅 프로젝트 (~ 2월 1주차)


<br>

<br>

#### 로그아웃 
```
public void deleteAccessToken(HttpServletRequest request) {
    	String accessToken = resolveToken(request);
    	
    	if (validateAccessToken(accessToken)) {
    		//레디스에서 해당 id-토큰 삭제
			redisTemplate.delete("RT:"+getUserIdFromToken(accessToken));
			
	        Long expiration = getExpiration(accessToken);
	        
	        //로그아웃 후 유효한 토큰으로 접근가능하기 때문에 만료전 로그아웃된 accesstoken은 블랙리스트로 관리
	        redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
    	}
    }
```

#### 사용자 목록 조회 api



#### 메시지 전송 api
---
#### 프로세스
1. 채팅방에서 메시지를 전송하면 redis서버에 저장된다.
2. 지정된 시간에 배치가 실행되며 redis서버에 저장된 데이터를 db에 insert한다.
3. db에 데이터가 정상적으로 저장되면 redis에 저장되어있던 데이터는 삭제되고 이후 채팅 내용을 저장하며 1~3을 반복한다.

<br>

#### 소스코드
>redisconfig
```
@Bean
public RedisTemplate<String, ChatRequest>redisChatTemplate(RedisConnectionFactory redisConnectionFactory) {
	// redisTemplate를 받아와서 set, get, delete를 사용
    RedisTemplate<String, ChatRequest> redisTemplate = new RedisTemplate<>();

    redisTemplate.setKeySerializer(new StringRedisSerializer());
    // 직렬화시 직렬화한 class로 저장됨
    redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatRequest.class));
    redisTemplate.setConnectionFactory(redisConnectionFactory());

    return redisTemplate;
}
```

<br>

>controller
```
@MessageMapping("/chat/{roomId}")
@SendTo("/sub/room/{roomId}")
public ApiResponse<ChatResponse> sendMessage(@DestinationVariable(value = "roomId") int roomId, ChatRequest req) {
	req.setRoomId(roomId);
	
	return ApiResponse.success(chatService.insertMessage(req));
}
```

<br>

```
public ChatResponse insertMessage(ChatRequest req) {
		// 시간 score로 관리하기 위해 숫자로 변환
		String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		
		...
		
        // redis messageData 저장
		ZSetOperations<String, ChatRequest> zSetOperations = redisChatTemplate.opsForZSet();
		zSetOperations.add("roomId:"+req.getRoomId(), req, now_long);
		
		//System.out.println(zSetOperations.range("ZKey", 0, -1));
	}
```

<br>

#### redis -> db 저장
```
Set<String> keys = redisTemplate.keys("roomId:*"); //채팅 메시지가 저장된 모든 key조회
Iterator<String> it = keys.iterator();
...
List<ChatRequest> listcontents = new ArrayList<>(); //직렬화한 클래스
...
Set<ChatRequest> list = zSetOperations.range(key, 0, -1);
list.forEach(listcontents::add);

chatRepository.setChatMessage(listcontents);
```
-> 배치에서 활용

<br>

#### 메시지 조회 api
---
#### 프로세스
1. 조회할 메시지 수를 파라미터로 받아, redis서버에서 해당 채팅방에 메시지 수만큼 데이터가 있으면 바로 반환해준다.
2. redis서버에 충분한 데이터가 없다면 있는 만큼만 redis에서 조회하고 부족한 만큼 db에서 조회해 온다.
3. redis서버에 데이터가 없으면 모두 db에서 조회해 온다.

<br>

>service
```
if (list.size() == req.getCnt()) {
	//redis에서 불러온 리스트가 필요한 cnt와 같으면 바로 반환(데이터 모두 존재)
	li.forEach(item -> {
		ChatListResponse resChat = ChatListResponse.toReqDto(item);
		
		resList.add(resChat);
	});
}else if (list.size() != req.getCnt() && list.size() != 0) {
	//redis에서 불러온 리스트가 일부만 있으면 redis + db 조회
	li.forEach(item -> {
		ChatListResponse resChat = ChatListResponse.toReqDto(item);
		
		resList.add(resChat);
	});
	
	//db조회
	int offset = limit * (req.getPageNum() - 1);

	tempLi = chatRepository.getMessageList(req.getRoomId(), limit, 0);
	tempLi.forEach(item -> {
		ChatListResponse resChat = ChatListResponse.toChatDto(item);
		
		resList.add(resChat);
	});
}else if (list.size() == 0) {
	int offset = ((limit * (req.getPageNum() - 1)) - cntforlist);
	
	//db에서 조회
	tempLi = chatRepository.getMessageList(req.getRoomId(), limit, offset);
	tempLi.forEach(item -> {
		ChatListResponse resChat = ChatListResponse.toChatDto(item);
		
		resList.add(resChat);
	});
}
```