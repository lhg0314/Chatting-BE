package com.project.chatting.common;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.project.chatting.exception.ValidationException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    private static final List<String> imageContentTypes = Arrays.asList("image/jpeg", "image/png");


    public static void validateImageFile(String contentType) {
        if (!imageContentTypes.contains(contentType)) {
            throw new ValidationException(String.format("허용되지 않은 파일 형식 (%s) 입니다", contentType), ErrorCode.VALIDATION_FILE_FORMAT_EXCEPTION);
        }
    }

}
