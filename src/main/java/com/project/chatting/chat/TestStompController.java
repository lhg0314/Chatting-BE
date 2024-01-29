package com.project.chatting.chat;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TestStompController {
	
	@Autowired
	private  SimpMessagingTemplate templat; 
	
	@MessageMapping("/room/{roomId}")
    public void sendMessage(@DestinationVariable(value = "roomId") String roomId, String message) {
        log.info("# roomId = {}", roomId);
        log.info("# message = {}", message);

        templat.convertAndSend("/sub/room/" + roomId, message);
    }

}
