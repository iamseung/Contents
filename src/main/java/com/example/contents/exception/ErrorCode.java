package com.example.contents.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    NOT_EXIST_CONTENT("존재하지 않는 작품입니다.", HttpStatus.NOT_FOUND),
    CANT_BUY_CONTENT("구매할 수 없는 상품입니다.", HttpStatus.BAD_REQUEST);


    private final String message;
    private final HttpStatus status;
}
