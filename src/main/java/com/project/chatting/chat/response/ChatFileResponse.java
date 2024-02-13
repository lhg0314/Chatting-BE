package com.project.chatting.chat.response;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class ChatFileResponse {

    private String fileName;
    private String fileExt;
    private String fileUrl;

    public static ChatFileResponse toDto(String fileName, String fileExt, String fileUrl){
        return ChatFileResponse.builder()
            .fileName(fileName)
            .fileExt(fileExt)
            .fileUrl(fileUrl)
            .build();
    }
}
