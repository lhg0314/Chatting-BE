package com.project.chatting.chat.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;

import com.project.chatting.chat.repository.ChatRepository;
import com.project.chatting.chat.request.ChatFileRequest;
import com.project.chatting.chat.response.ChatFileResponse;
import com.project.chatting.common.FileUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatFileService {
    
    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    ResourceLoader rsLoader;

    // 이미지 저장 경로, 이미지 이름을 저장함 (서버쪽 파일만 저장)
    public ChatFileResponse setFile(ChatFileRequest chatFileRequest, String path){

        log.info("[파일 업로드 파라미터 경로] : " + path);
        
        FileUtils.validateImageFile(chatFileRequest.getFile().getContentType());

        String originalName = chatFileRequest.getFile().getOriginalFilename();
        String baseName = originalName.substring(0, originalName.lastIndexOf("."));
        String ext = originalName.substring(originalName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + "_" + baseName;
        //String returnPath = "static\\";
        // 이미지 저장 경로
        String imageUploadPath = path.substring(path.indexOf("upload")) + File.separator + chatFileRequest.getRoomId() + File.separator+ fileName + ext;
        Path folderPath = Paths.get(path, String.valueOf(chatFileRequest.getRoomId()));
        Path filePath = Paths.get(path,String.valueOf(chatFileRequest.getRoomId()),fileName + ext);
        log.info("[이미지 파일 업로드 경로] : " + filePath.toString());

        // 채팅방 별 이미지 저장을 위한 폴더 생성
        //File imageUploadFolder = new File(filePath.toString());
        //if(!imageUploadFolder.exists()){
        //    imageUploadFolder.mkdir();
        //}

        try{
            if(!Files.exists(folderPath)){
                Files.createDirectories(folderPath);
            }
            Files.write(filePath, chatFileRequest.getFile().getBytes());
        }catch(IOException e){
            log.error(e.getMessage());
        }

        return ChatFileResponse.toDto(fileName, ext, imageUploadPath);
    }

    // 이미지 DB 데이터 삽입
    public void insertFile(ChatFileRequest chatFileRequest){
        chatRepository.setFile(chatFileRequest);
    }

    // 이미지 파일 삭제
    public void deleteFile(int roomId){
        Path rootPath = Paths.get("src\\main\\resources\\static\\upload");
        Path folderPath = Paths.get("src\\main\\resources\\static\\upload" + File.separator + roomId);

        File folder = new File(folderPath.toString());
        File root = new File(rootPath.toString());

        if(folder.exists()){
            File[] fileList = folder.listFiles();
            for(File f : fileList){
                f.delete();
            }
            folder.delete();
        }

        if(root.listFiles().length == 0){
            root.delete();
        }
                
    }

}
