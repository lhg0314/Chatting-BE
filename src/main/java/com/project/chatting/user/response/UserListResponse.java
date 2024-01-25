package com.project.chatting.user.response;

import com.project.chatting.user.entity.User;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@ToString
@Getter
@Builder
public class UserListResponse {
	private String userId;
	private String userName;
	
	public static UserListResponse toDto(User user) {
		return UserListResponse.builder()
				.userId(user.getUserId())
				.userName(user.getName())
				.build();
	}
	
}
