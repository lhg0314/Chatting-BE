package com.project.chatting.chat.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateRoomRequest {
  
  @Size(min=1, max=40, message="채팅방 이름은 40글자를 넘을 수 없습니다!")
  @NotBlank
  private String roomName;
  
  private List<@NotBlank String> userId;

  private int roomId;

}
