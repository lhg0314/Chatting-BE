package com.project.chatting.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class SignupRequest {
	@NotBlank
	 @Schema(description = "회원가입 ID", nullable = false)
	private String userId;
	@NotBlank
	 @Schema(description = "회원가입 PW", nullable = false)
	private String userPw;
	@NotBlank
	 @Schema(description = "회원가입 Name", nullable = false)
	private String name;
}
