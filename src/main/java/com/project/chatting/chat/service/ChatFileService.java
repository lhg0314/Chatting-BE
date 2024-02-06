package com.project.chatting.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.chatting.chat.repository.ChatRepository;
import com.project.chatting.chat.request.ChatFileRequest;
import com.project.chatting.chat.response.ChatFileResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatFileService {
    
    @Autowired
    private ChatRepository chatRepository;

    // 이미지 저장 경로, 이미지 이름을 저장함
    public ChatFileResponse setFile(ChatFileRequest chatFileRequest){

        // 파일 이름에 확장자가 포함되어 올 경우
        String fileName = chatFileRequest.getFileName();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1); // 확장자
        String baseFileName = fileName.substring(0, fileName.indexOf(".")); // 확장자를 뺀 파일 이름
        log.info("[파일명 가공] : 확장자를 뺀 파일 이름 = " + baseFileName + ", 확장자 = " + ext);

        chatRepository.setFile(ChatFileRequest.toDto(chatFileRequest.getChatId(), baseFileName, ext, chatFileRequest.getFileUrl()));
        return ChatFileResponse.toDto(chatFileRequest.getChatId(), fileName, chatFileRequest.getFileUrl());
    }
}
