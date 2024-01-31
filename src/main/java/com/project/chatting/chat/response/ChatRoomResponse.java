package com.project.chatting.chat.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ChatRoomResponse {
    private int roomId;
    private String roomName;
}
