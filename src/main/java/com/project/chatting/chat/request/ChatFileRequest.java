package com.project.chatting.chat.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class ChatFileRequest {

    private int chatId;
    private int roomId;
    private String userId;
    private String message;
    private String messageType;
    private MultipartFile[] files;


}
