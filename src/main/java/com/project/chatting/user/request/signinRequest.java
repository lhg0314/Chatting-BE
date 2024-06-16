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
public class signinRequest {
	@NotBlank
	@Schema(description = "로그인 ID", nullable = false, example = "id1")
	private String userId;
	@NotBlank
	@Schema(description = "로그인 PW", nullable = false, example = "pw1")
	private String userPw;
}
