package com.project.chatting.chat.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class CreateRoomResponse {

  private int roomId;
  private boolean existRoom;

  public static CreateRoomResponse toDto(int roomId, boolean existRoom){
    return CreateRoomResponse.builder()
    .roomId(roomId)
    .existRoom(existRoom)
    .build();
  }
}
