package com.example.contents.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiSuccessResponse<T> {
    private final String message;
    private final T data;

    private ApiSuccessResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public static <T> ApiSuccessResponse<T> of(String message, T data) {
        return new ApiSuccessResponse<>(message, data);
    }

    public static ApiSuccessResponse<Void> message(String message) {
        return new ApiSuccessResponse<>(message, null);
    }

    public static ApiSuccessResponse<Void> ok() {
        return new ApiSuccessResponse<>("OK", null);
    }

    public static <T> ApiSuccessResponse<T> ok(T data) {
        return new ApiSuccessResponse<>("OK", data);
    }
}
