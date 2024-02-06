package com.project.chatting.chat.response;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class ChatFileResponse {

    private int chatId;
    private String fileName;
    private String fileUrl;

    public static ChatFileResponse toDto(int chatId, String fileName, String fileUrl){
        return ChatFileResponse.builder()
            .chatId(chatId)
            .fileName(fileName)
            .fileUrl(fileUrl)
            .build();
    }
}
