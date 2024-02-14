package com.project.chatting.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {

    // Common
    UNAUTHORIZED_EXCEPTION("C001", "세션이 만료되었습니다. 다시 로그인 해주세요"),
    VALIDATION_EXCEPTION("C002", "아이디 또는 비밀번호를 확인해주세요"),
    INTERNAL_SERVER_EXCEPTION("C003", "서버 내부에서 에러가 발생하였습니다"),
    BAD_GATEWAY_EXCEPTION("C004", "외부 연동 중 에러가 발생하였습니다"),
    CONFLICT_EXCEPTION("C005", "이미 존재합니다"),
    NOT_FOUND_EXCEPTION("C404", "존재하지 않습니다"),

    CONFLICT_NICKNAME_EXCEPTION("C006", "이미 존재하는 닉네임입니다"),
    CONFLICT_MEMBER_EXCEPTION("C007", "이미 존재하는 아이디입니다"),
	

	TOKEN_EXPIRED_EXCEPTION("C008", "토큰이 만료되었습니다. 다시 로그인 해주세요"),
    CONFLICT_TOKEN_EXCEPTION("C009","토큰이 유효하지 않습니다. 다시 로그인 해주세요"),

    CONFLICT_ROOM_EXIST_EXCEPTION("C010","채팅방이 존재하지 않습니다."),
    CONFLICT_FILE_EXCEPTION("C011", "파일 업로드중 오류가 발생했습니다.");

    private final String code;
    private final String message;

}
