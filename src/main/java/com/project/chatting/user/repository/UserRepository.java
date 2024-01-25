package com.project.chatting.user.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.project.chatting.user.entity.User;
import com.project.chatting.user.response.UserListResponse;


@Mapper
public interface UserRepository {
	public int setInsertMember(User user);

	public User findMemberById(String userId);
	
	public List<User> getSortedUserList();
}
