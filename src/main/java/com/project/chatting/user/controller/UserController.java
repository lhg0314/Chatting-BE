package com.project.chatting.user.controller;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.project.chatting.common.ApiResponse;
import com.project.chatting.user.entity.User;
import com.project.chatting.user.request.SignupRequest;
import com.project.chatting.user.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired 
	private UserService userService;
	
	@Autowired 
	private PasswordEncoder encoder;
	
	
	/**
	 * 회원가입
	 * @throws Exception 
	 */
	@PostMapping("/auth/signup")
	public ApiResponse<String> registerUser(@Valid @RequestBody SignupRequest signUpReq) {
	

		// Create new user's account
		User user = new User(signUpReq.getUserId(), encoder.encode(signUpReq.getUserPw()),  signUpReq.getName());

		userService.setInsertMember(user);

		return ApiResponse.SUCCESS;
	}
	


}
