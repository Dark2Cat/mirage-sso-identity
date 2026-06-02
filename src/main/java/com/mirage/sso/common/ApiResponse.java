package com.mirage.sso.common;

public record ApiResponse<T>(
        String code,
        String message,
        T data,
        String requestId
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("0", "success", data, RequestIdHolder.get());
    }

    public static ApiResponse<Void> success() {
        return success(null);
    }

    public static ApiResponse<Void> failed(String code, String message) {
        return new ApiResponse<>(code, message, null, RequestIdHolder.get());
    }

    public static <T> ApiResponse<T> failedData(String code, String message) {
        return new ApiResponse<>(code, message, null, RequestIdHolder.get());
    }
}
