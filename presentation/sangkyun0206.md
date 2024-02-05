## 채팅 프로젝트 (~ 2월 1주차)

<br>

<br>

### JWT 재발급

##### 프로세스

1. Access Token이 만료되었을 경우 Redis에 저장되어 있는 Refresh Token을 가져옵니다.

2. Access Token의 만료 여부는 필터를 통해 확인하며, 토큰 재발급 요청을 따로 하지 않습니다.

3. Refresh Token의 유효성을 검사하고 유효하다면 Access Token을 발급해 기존 요청 헤더에 담긴 Token값을 대체합니다.

4. 요청을 정상 처리하고 응답 헤더에 새로 발급해준 Access Token을 담아 보냅니다.

5. Refresh Token이 유효하지 않다면, 공통 응답 코드를 사용해 재로그인 하도록 유도합니다.

```java
Authentication authentication = new UsernamePasswordAuthenticationToken(userId, userDetails.getPassword());
String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
RefreshToken token = new RefreshToken(authentication.getName(), newAccessToken, refreshToken);

authentication = jwtTokenProvider.getAuthentication(newAccessToken);
SecurityContextHolder.getContext().setAuthentication(authentication);
HttpServletRequest httpRequest = (HttpServletRequest) request;
HttpServletResponse httpResponse = (HttpServletResponse) response;
httpResponse.setHeader("Authorization", "Bearer " + newAccessToken);
httpResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(httpRequest){
    @Override
    public String getHeader(String name){
        if("Authorization".equalsIgnoreCase(name)){
            return "Bearer " + token.getAccessToken();
        }
        return super.getHeader(name);
    }
};
flag = true;
filterChain.doFilter(requestWrapper, response);
```

### HttpServletRequestWrapper

- 기존의 HTTP 요청 객체를 감싸거나 변경할 때 사용되는 부분입니다.
- 해당 클래스를 통해 이전 요청의 헤더에 토큰 값을 바꾸어 요청을 정상 실행할 수 있습니다.

<br>

### 채팅방 생성

#### 프로세스

1. 1대1 채팅인지, 그룹 채팅인지 먼저 구별합니다.

2. 그룹 채팅의 경우 같은 그룹원이 존재하는 채팅방이 있어도 생성 됩니다.

3. 1대1 채팅의 경우 같은 그룹원이 참여하고 있는 채팅방이 있을 경우 해당 채팅방 번호를 응답합니다.

4. 1대1 채팅에서 한명이 나간 경우에는 존재하지 않는 걸로 판단하여 채팅방을 생성하고 생성된 채팅방 번호를 응답합니다.

```sql
select
	a.room_id
from
	(select
		count(*) userCount,
		group_concat(cj.user_id order by cj.user_id asc) users,
		group_concat(user_name order by cj.user_id asc) userNames,
		group_concat(cj.room_state) userStates,
		room_id
	from
		chatJoin cj
	join
		user u
	on cj.user_id = u.user_id
	group by room_id
	having userCount = 2) a
 join chatRoom cr
on a.room_id = cr.room_id
where a.users = ("id3,id4") and a.userStates = ("Y,Y");
```

<br>

### 채팅방 삭제

#### 프로세스

1. 채팅방이 존재하는지 확인합니다.

2. 참여하고 있는 채팅방을 나갑니다.

3. 참여 테이블에서 상태를 "Y" 에서 "N" 으로 수정합니다.

4. 해당 채팅방에 참여자의 상태가 모두 "N" 이라면 채팅방을 삭제합니다.

5. ON DELETE CASCADE 옵션으로 채팅방 테이블의 방 번호를 외래키로 가지고 있기 때문에 채팅방이 삭제되면, 참여자 정보 또한 삭제됩니다.

```java
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
```

<br>

### 파일 업로드 (진행중)

#### 프로세스

1. /chat/upload/roomId 로 요청이 들어옵니다.

2. Stomp 프로토콜을 통해 파일을 전송합니다.

3. Base64로 인코딩되어 전달된 바이너리 데이터 값을 디코딩해 파일을 저장하고 DB에 데이터를 저장합니다.

4. redis에 채팅 메시지를 저장하는 것과는 별개로 데이터를 저장합니다.
