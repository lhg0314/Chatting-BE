package com.project.chatting.chat.request;

import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class ChatReadRequest {
	private int roomId;
	private Map<String, String> readMap;
	
	public ChatReadRequest() {}
	
	public ChatReadRequest(int roomId, Map<String, String> map) {

		this.roomId = roomId;
		this.readMap = map;
	}
}
