package com.project.chatting.chat.request;

import org.springframework.web.multipart.MultipartFile;

import com.project.chatting.chat.entity.Chat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatFileRequest {

    private int roomId;
    private String userId;
    private String message;
    private String messageType;
    private String createAt;
	private int readCnt;
    private String imageCode;
    private String imageName;

    public static ChatFileRequest toDto(Chat chat, String imageCode, String imageName){
        return ChatFileRequest.builder()
            .roomId(chat.getRoomId())
            .userId(chat.getUserId())
            .message(chat.getMessage())
            .messageType(chat.getMessageType())
            .createAt(chat.getCreateAt())
            .readCnt(chat.getReadCnt())
            .imageCode(imageCode)
            .imageName(imageName)
            .build();
    }


}
