package com.project.chatting.chat.request;


import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class CreateJoinRequest {
  private String userId;
  private int roomId;
  private String roomState;

  public CreateJoinRequest(){}

  public CreateJoinRequest(String userId, int roomId, String roomState){
    this.userId = userId;
    this.roomId = roomId;
    this.roomState = roomState;
  }
}
