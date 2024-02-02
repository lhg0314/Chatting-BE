package com.project.chatting.chat.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Builder
public class ExistChatRoomListResponse {
  private int roomId;
  private String roomName;
  private String userNames;
  public static ExistChatRoomListResponse toDto(int roomId, String roomName, String userNames){
    return ExistChatRoomListResponse.builder()
    .roomId(roomId)
    .roomName(roomName)
    .userNames(userNames)
    .build();
  }

}
