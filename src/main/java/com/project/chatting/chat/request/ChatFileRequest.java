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

    private int chatId;
    private String fileName;
    private String fileExt;
    private String fileUrl;

    public static ChatFileRequest toDto(int chatId, String fileName, String fileExt, String fileUrl){
        return ChatFileRequest.builder()
            .chatId(chatId)
            .fileName(fileName)
            .fileExt(fileExt)
            .fileUrl(fileUrl)
            .build();
    }


}
