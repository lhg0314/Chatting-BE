package com.project.chatting.chat.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.chatting.chat.repository.ChatRepository;
import com.project.chatting.chat.request.ChatFileRequest;
import com.project.chatting.chat.response.ChatFileResponse;
import com.project.chatting.common.ErrorCode;
import com.project.chatting.exception.ConflictException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatFileService {
    
    @Autowired
    private ChatRepository chatRepository;

    // 이미지 저장 경로, 이미지 이름을 저장함
    public ChatFileResponse setFile(ChatFileRequest chatFileRequest, String path){

        String baseName = chatFileRequest.getFile().getOriginalFilename();
        String ext = baseName.substring(baseName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + "_" + baseName + ext;

        // 파일 저장 상위 경로
        String root = path + "\\upload";
        
        // 채팅방 별 파일 저장을 위한 디렉토리 경로
        String realUrl = root + "\\" + chatFileRequest.getRoomId();

        File file = new File(root);
        if(!file.exists()) file.mkdirs();

        // 채팅방 별 파일 저장
        File saveFile = new File(realUrl);

        try{
            chatFileRequest.getFile().transferTo(saveFile);
            log.info("[파일 업로드] 성공 ");
            // DB에 insert 
            chatRepository.setFile(ChatFileRequest.toDto(chatFileRequest.getRoomId(), chatFileRequest.getChatId(), fileName, ext, realUrl, null));
            return ChatFileResponse.toDto(chatFileRequest.getChatId(), fileName, realUrl);
            
        }catch(IllegalStateException | IOException e){
            throw new ConflictException("파일 업로드중 오류가 발생했습니다.", ErrorCode.CONFLICT_FILE_EXCEPTION);
        }

    }
}
