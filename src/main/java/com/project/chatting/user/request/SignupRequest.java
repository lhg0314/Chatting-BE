package com.project.chatting.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SignupRequest {
	@NotBlank
	private String userId;
	@NotBlank
	private String userPw;
	@NotBlank
	private String name;
}
