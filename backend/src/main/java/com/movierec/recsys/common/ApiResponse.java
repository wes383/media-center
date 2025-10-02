package com.movierec.recsys.common;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class ApiResponse<T> {
    @JsonUnwrapped
    private T data;

    public ApiResponse(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data);
    }
}