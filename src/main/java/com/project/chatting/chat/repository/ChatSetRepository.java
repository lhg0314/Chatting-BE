package com.project.chatting.chat.repository;

import org.apache.ibatis.annotations.Mapper;

import com.project.chatting.chat.entity.ChatSet;

@Mapper
public interface ChatSetRepository {
	public void updateReadYn (ChatSet readReq);
	public void updateReadCnt(int roomId);
}
