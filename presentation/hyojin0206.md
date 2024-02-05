## 채팅 프로젝트 (~ 2월 1주차)


<br>

<br>

#### 회원가입 & 로그인 프로세스

---

#### 로그인 프로세스

1. 로그인을 하면 서버에서 Access Token 과 Refresh Token을 발급해준다. 

2. 클라이언트는 API를 호출할 때마다 발급받은 Access Token을 활용하여 요청을 한다.

3. 토큰을 사용하던 중, 만료되어 Invalid Token Error가 발생한다면 사용자가 보낸 Access Token으로 레디스의 Refresh Token을 찾아보고 Refresh 토큰이 유효하다면, Access Token을 다시 발급해준다.

4. 만약, Refresh Token도 만료되었다면, 다시 로그인을 하도록 요청한다.

5. 사용자가 로그아웃을 하면, refresh token을 삭제하고 사용이 불가하도록 한다.

#### 레디스 사용 이점

##### 1) 유효기간 지정 가능

> Redis는 데이터의 유효기간을 정할수 있어서 주기적으로 토큰을 삭제 처리 필요 없음


```
redisTemplate.opsForValue().set("RT:"+ accessToken , refreshToken,REFRESH_TOKEN_EXPIRE_TIME,TimeUnit.MILLISECONDS); 
```
##### 2) 성능 이점

> Redis는 인메모리 방식이라 DB조회 보다 비용이 절감

##### 3) 다양한 자료구조 제공

> String, Hash, List, Set 등 다양한 자료구조 형태 제공

메소드명|	반환 오퍼레이션|	Redis 자료구조
|------|---|---|
|opsForValue()|	ValueOperations|	String
|opsForList()|	ListOperations|	List
|opsForSet()|	SetOperations|	Set
|opsForZSet()|	ZSetOperations|	Sorted Set
|opsForHash()|	HashOperations|	Hash

<br>

<br>

#### 소스코드

>service

```
User userDetails = userRepository.findMemberById(signinReq.getUserId());
		
        if(userDetails == null) throw new ConflictException(String.format("아이디에 해당하는 회원정보가 없습니다."), ErrorCode.VALIDATION_EXCEPTION);
        
        checkPassword(signinReq.getUserPw(), userDetails.getUserPw());
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getUserId(), userDetails.getUserPw());
        String accessToken = jwtTokenProvider.createAccessToken(authentication); // Access Token 발급
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication); // Refresh Token 발급
        

        RefreshToken token = new RefreshToken(authentication.getName(), accessToken, refreshToken,userDetails.getName()); 
        redisTemplate.opsForValue().set("RT:"+signinReq.getUserId(),refreshToken,REFRESH_TOKEN_EXPIRE_TIME,TimeUnit.MILLISECONDS); // redis 캐시에 refrash Token 저장
        //tokenRepository.save(token); queryDsl 방식도 사용가능
```

<br>

>securityConfig

```
@Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()))
          .csrf(AbstractHttpConfigurer::disable).exceptionHandling(exceptionHandling -> exceptionHandling
                  .authenticationEntryPoint(unauthorizedHandler)
                ).
          sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .authorizeHttpRequests(authorizeRequests -> authorizeRequests
            // 모두 허용
            .requestMatchers(
            		  "/user/auth/signin", 
            	      "/user/auth/signup",
            	      "/ws/**"
            ).permitAll()
            // 그 외는 인증 필요
            .anyRequest().authenticated())
          // jwt filter 추가
          .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
```

<br>

>jwtTokenFilter

```
@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
        String path = request.getRequestURI();
        System.out.println("path: " + path);
        boolean flag = false;

        if (Arrays.stream(WHITELIST).anyMatch(pattern -> antPathMatcher.match(pattern, path))) {
            filterChain.doFilter(request, response);
            return;
        }
        String accessToken = resolveToken(request);
        try {
            if(accessToken == null) {
                request.setAttribute("exception", new UnAuthorizedException("로그인이 필요합니다.", ErrorCode.UNAUTHORIZED_EXCEPTION));
            }else{
               
                    jwtTokenProvider.validateAccessToken(accessToken);
                    String blToken = (String)redisTemplate.opsForValue().get(accessToken);
                  
                    if (ObjectUtils.isEmpty(blToken)) {
                        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                
            }
```

#### 소켓통신 공통 프로세스

#### STOMP원리

---

> - 메시지전송을 위한 프로토콜로 sub/pub 기반으로 동작.
>
> - 메시지 송신/수신에 대한 처리가 명확하게 정의 가능
>


![img](https://img1.daumcdn.net/thumb/R1280x0/?fname=http://t1.daumcdn.net/brunch/service/user/2MrI/image/ko_3rg5sAR3vacUzluXutcMhAY0.png)

<br>

### 예제

> backend

```
@MessageMapping("/room/{roomId}")
    public void sendMessage(@DestinationVariable(value = "roomId") String roomId, String message) {
        log.info("# roomId = {}", roomId);
        log.info("# message = {}", message);

        templatd.convertAndSend("/sub/room/" + roomId, message);
 }

```
<br>

> frontend

```
    var socket = new SockJS('/ws');
 
    stompClient = Stomp.over(socket);
    stompClient.connect(headers, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        
        
        stompClient.subscribe('/sub/room/1', function (greeting) {
			console.log(JSON.parse(greeting.body))
            showGreeting(JSON.parse(greeting.body).msg);
        },headers);
    });


    
```

```
 stompClient.send("/pub/room/1", {}, JSON.stringify({'msg': $("#name").val()}));
```

<br>

> configurationSource

```
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub"); // 메시지 send
        config.setApplicationDestinationPrefixes("/pub"); // 메시지 recieve
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // 엔드포인트 설정
                .setAllowedOriginPatterns("*") // cors모두 허용
                .withSockJS();
        registry.setErrorHandler(stompErrorHandler);
    }
    
    // 소켓 연결시 jwt유효성 검증

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }

```

#### stompHandler

>websocket stomp로 연결하는 흐름을 제어하는 interceptor, jwt인증 / 채팅방 입장인원 관리 / 읽음처리 구현을위해 사용

##### CONNECT

 > stompClient.connect 함수 실행 시 수행 , jwt인증 수행

---

```

if(accessor.getNativeHeader("Authorization")!= null) {
    String accesstoken = accessor.getNativeHeader("Authorization").get(0);
    try {
        jwtTokenProvider.validateAccessToken(accesstoken);
    }catch(Exception e) {
            throw new MessageDeliveryException("UNAUTHORIZED");
    }
    }else {
        throw new MessageDeliveryException("UNAUTHORIZED");
        
    }
}
```




##### SUBSCRIBE

 > stompClient.subscribe 함수 실행 시 수행 > 채팅방에 입장한 유저 +1 처리 / 채팅방입장 시 채팅 읽음처리 

---


```
if(StompCommand.SUBSCRIBE.equals(accessor.getCommand())){
        	
    String destination = accessor.getDestination();
    int lastIndex = destination.lastIndexOf('/');
    String roomId = destination.substring(lastIndex + 1);
    String userId = "";
    String sessionId = (String) message.getHeaders().get("simpSessionId");
    
    if(accessor.getNativeHeader("Authorization")!= null) {
        userId = jwtTokenProvider.getUserIdFromToken(accessor.getNativeHeader("Authorization").get(0));
        chatRepo.setUserEnterInfo(sessionId, roomId,userId);	            	            
        chatRepo.plusUserCount(roomId,userId); // 유저 +1 처리	            
      
        ChatSet readReq = new ChatSet(Integer.parseInt(roomId),userId);
        
        chatsetService.updateReadYn(readReq); 
        
        
    }
```

##### DISCONNECT

> stompClient.disconnect 함수 실행 혹은 채팅방 벗어났을 시 수행 > 채팅방에 입장한 유저 -1처리

---

```
if(StompCommand.DISCONNECT.equals(accessor.getCommand())){
 
    String sessionId = (String) message.getHeaders().get("simpSessionId");
    String roomaAndUserInfo = chatRepo.getUserEnterRoomId(sessionId);
    int index = 0;
    String roomId = "";
    String userId = "";
    if(roomaAndUserInfo != null) {
        index = roomaAndUserInfo.indexOf("/");
            roomId = roomaAndUserInfo.substring(0, index);
            userId = roomaAndUserInfo.substring(index+1);
            chatRepo.minusUserCount(roomId, userId);
    }
    
    
    chatRepo.removeUserEnterInfo(sessionId); // 세션 정보 삭제
}
```



 - [stompHandler](http://git.openobject.net:8880/education/chatting-server/-/blob/main/src/main/java/com/project/chatting/config/StompHandler.java)

