package com.project.chatting.chat.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LeaveChatRoomRequest {
  
  @NotBlank
  private String userId;
  
  private int roomId;

  @NotBlank
  private String roomState;
}
