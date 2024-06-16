package com.project.chatting.chat.repository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ChatRoomRepository {
    // Redis CacheKeys
    private static final String CHAT_ROOMS = "CHAT_ROOM"; // 채팅룸 저장
    public static final String USER_COUNT = "USER_COUNT"; // 채팅룸에 입장한 클라이언트수 저장
    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOps;
    
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;
    
    // 유저 세션으로 입장해 있는 채팅방 ID 조회
    public String getUserEnterRoomId(String sessionId) {
        return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
    }
    
    // 유저가 입장한 채팅방ID와 유저 세션ID 맵핑 정보 저장
    public void setUserEnterInfo(String sessionId, String roomId,String userId) {
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId+"/"+userId );
    }
    
    // 유저 세션정보와 맵핑된 채팅방ID 삭제
    public void removeUserEnterInfo(String sessionId) {
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
    }

    // 채팅방 유저수 조회
    public String[] getUserCount(String roomId) {
    	String[] originList = (String.valueOf(redisTemplate.opsForValue().get(USER_COUNT + "_" + roomId))).split(",");
    	int position = originList.length;
    	HashSet<String> hashSet = new HashSet<>(Arrays.asList(originList));
    	String[] resultArr = hashSet.toArray(new String[0]); // 세션 user중복 제거
    	resultArr = Arrays.stream(resultArr)
        .filter(item -> !item.equals("null"))
        .toArray(String[]::new);
    	return Arrays.copyOfRange(resultArr,0,position-1);
    }

    // 채팅방에 입장한 유저수 +1
    public void plusUserCount(String roomId,String userId) {
    	redisTemplate.opsForValue().set(USER_COUNT + "_" + roomId,redisTemplate.opsForValue().get(USER_COUNT + "_" + roomId)+ ","+ userId);
        //return Optional.ofNullable(valueOps.increment(USER_COUNT + "_" + roomId)).orElse(0L);
    }

    // 채팅방에 입장한 유저수 -1
    public void minusUserCount(String roomId,String userId) {
    	System.out.println(userId);
    	redisTemplate.opsForValue().set(USER_COUNT + "_" + roomId,redisTemplate.opsForValue().get(USER_COUNT + "_" + roomId).toString().replace(","+ userId, ""));
    }
}