package com.project.chatting.chat.response;

import com.project.chatting.chat.request.ChatFileRequest;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class ChatFileResponse {
    private int roomId;
    private String userId;
    private String message;
    private String messageType;
    private String createAt;
    private int readCnt;
    private String imageCode;
    private String imageName;


    public static ChatFileResponse toDto(ChatFileRequest chatFileRequest){
        return ChatFileResponse.builder()
            .roomId(chatFileRequest.getRoomId())
            .userId(chatFileRequest.getUserId())
            .message(chatFileRequest.getMessage())
            .messageType(chatFileRequest.getMessageType())
            .createAt(chatFileRequest.getCreateAt())
            .readCnt(chatFileRequest.getReadCnt())
            .imageCode(chatFileRequest.getImageCode())
            .imageName(chatFileRequest.getImageName())
            .build();
    }
}
