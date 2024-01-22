package com.project.chatting.user.repository;

import org.apache.ibatis.annotations.Mapper;

import com.project.chatting.user.entity.User;


@Mapper
public interface UserRepository {
	public int setInsertMember(User user);

	public User findMemberById(String userId);
}
