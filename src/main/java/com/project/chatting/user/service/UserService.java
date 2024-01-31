package com.project.chatting.user.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.chatting.auth.JwtTokenProvider;
import com.project.chatting.auth.RefreshToken;
import com.project.chatting.common.ErrorCode;
import com.project.chatting.exception.ConflictException;
import com.project.chatting.exception.TokenException;
import com.project.chatting.user.entity.User;
import com.project.chatting.user.repository.UserRepository;
import com.project.chatting.user.request.signinRequest;
import com.project.chatting.user.response.UserListResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Service
public class UserService  {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
    private RedisTemplate<String, Object> redisTemplate;
	
	@Value("${jwt.refresh-token-validity-in-seconds}")
    private Long REFRESH_TOKEN_EXPIRE_TIME;
	
	
	
	 public int setInsertMember(User user) {
		 User getuser = userRepository.findMemberById(user.getUserId());
	
			if (getuser != null) {
				throw new ConflictException(String.format("중복되는 멤버 (%s - %s) 입니다", user.getUserId(), getuser.getUsername()), ErrorCode.CONFLICT_MEMBER_EXCEPTION);
			}
	        return userRepository.setInsertMember(user);
	    }

	public RefreshToken login(signinRequest signinReq, HttpServletResponse response) {
		User userDetails = userRepository.findMemberById(signinReq.getUserId());
       
        checkPassword(signinReq.getUserPw(), userDetails.getUserPw());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getUserId(), userDetails.getUserPw());
        String accessToken = jwtTokenProvider.createAccessToken(authentication); // Access Token 발급
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication); // Refresh Token 발급
        

        RefreshToken token = new RefreshToken(authentication.getName(), accessToken, refreshToken,userDetails.getName()); 
        redisTemplate.opsForValue().set("RT:"+signinReq.getUserId(),refreshToken,REFRESH_TOKEN_EXPIRE_TIME,TimeUnit.MILLISECONDS); // redis 캐시에 refrash Token 저장
        //tokenRepository.save(token); queryDsl 방식도 사용가능
        
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token.getAccessToken());
        return token;
	}
	
	private void checkPassword(String password, String encodedPassword) {
        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw new ConflictException(String.format("비밀번호를 다시 입력해주세요"), ErrorCode.VALIDATION_EXCEPTION);
        }
    }

	public void logout(HttpServletRequest req) {
		jwtTokenProvider.deleteAccessToken(req);
	}
	
	public List<UserListResponse> getSortedUserList() {
		List<User> getList = userRepository.getSortedUserList();
		List<UserListResponse> resList = new ArrayList<UserListResponse>();
		
		for (int i=0; i<getList.size(); i++) {
			User user = getList.get(i);
			
			UserListResponse resUser = UserListResponse.toDto(user);
			
			resList.add(resUser);
		}
		
		return resList;
	}

}
