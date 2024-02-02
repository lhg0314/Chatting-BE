package com.project.chatting.chat.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import java.util.List;

@ToString
@Getter
@Builder
public class CreateRoomResponse {

  private int roomId;
  private boolean existRoom;
  private List<ExistChatRoomListResponse> roomList;

  public static CreateRoomResponse toDto(int roomId, boolean existRoom, List<ExistChatRoomListResponse> roomList){
    return CreateRoomResponse.builder()
    .roomId(roomId)
    .existRoom(existRoom)
    .roomList(roomList)
    .build();
  }
}
