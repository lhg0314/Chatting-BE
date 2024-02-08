package com.project.chatting.chat.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;

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
    public void setFile(ChatFileRequest chatFileRequest, String path){

        log.info("[파일 업로드 파라미터 경로] : " + path);

        String originalName = chatFileRequest.getFile().getOriginalFilename();
        String baseName = originalName.substring(0, originalName.lastIndexOf("."));
        String ext = originalName.substring(originalName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + "_" + baseName + ext;

        // 이미지 저장 경로
        String imageUploadPath = path + File.separator + chatFileRequest.getRoomId();
        
        log.info("[이미지 파일 업로드 경로] : " + imageUploadPath);

        // 채팅방 별 이미지 저장을 위한 폴더 생성
        File imageUploadFolder = new File(imageUploadPath);
        if(!imageUploadFolder.exists()){
            imageUploadFolder.mkdir();
        }
    }
}
