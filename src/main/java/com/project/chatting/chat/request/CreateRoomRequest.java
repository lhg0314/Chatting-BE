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
public class CreateRoomRequest {
  
  @NotBlank
  private String roomName;

  @NotBlank
  private String toUserId;

  @NotBlank
  private String fromUserId;

  private int roomId;
}
