package com.project.chatting.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.chatting.common.ErrorCode;
import com.project.chatting.exception.ConflictException;
import com.project.chatting.user.entity.User;
import com.project.chatting.user.repository.UserRepository;




@Service
public class UserService  {
	
	@Autowired
	private UserRepository userRepository;
	

	
	 public int setInsertMember(User user) {
		 User getuser = userRepository.findMemberById(user.getUserId());
		 
			if (getuser != null) {
				throw new ConflictException(String.format("중복되는 멤버입니다", user.getUserId(), getuser.getUsername()), ErrorCode.CONFLICT_MEMBER_EXCEPTION);
			}
	        return userRepository.setInsertMember(user);
	    }



	

}
