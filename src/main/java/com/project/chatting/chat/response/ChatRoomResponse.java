package com.project.chatting.chat.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ChatRoomResponse {
	@Schema(description = "채팅방 id")
    private int roomId;
	@Schema(description = "채팅방 이름")
    private String roomName;
	@Schema(description = "채팅방 참여자 수")
    private int participantsCount;
	@Schema(description = "채팅방 읽지 않은 메시지")
    private int unreadMessages;
	@Schema(description = "채팅방 마지막 메시지")
    private String lastMessage;
	@Schema(description = "마지막 메시지 전송일자")
    private String lastMessageDate;
}
