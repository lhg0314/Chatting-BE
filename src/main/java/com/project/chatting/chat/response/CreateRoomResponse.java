package com.project.chatting.chat.response;

import java.text.StringCharacterIterator;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class CreateRoomResponse {
  private int roomId;
  public static CreateRoomResponse toDto(int roomId){
    return CreateRoomResponse.builder().roomId(roomId).build();
  }
}
