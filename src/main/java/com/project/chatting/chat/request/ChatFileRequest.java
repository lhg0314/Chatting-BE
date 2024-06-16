package com.project.chatting.chat.request;

import java.util.Map;

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
    private String fileName;
    private String fileExt;
    private String fileUrl;
    private MultipartFile file;
    private Map<String, String> fileMap;

    public static ChatFileRequest toDto(int roomId, int chatId, String fileName, String fileExt, String fileUrl, MultipartFile file){
        return ChatFileRequest.builder()
            .roomId(roomId)
            .fileName(fileName)
            .fileExt(fileExt)
            .fileUrl(fileUrl)
            .file(file)
            .build();
    }


}
