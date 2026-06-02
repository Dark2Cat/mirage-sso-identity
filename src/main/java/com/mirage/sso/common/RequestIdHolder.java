package com.mirage.sso.common;

import java.util.UUID;

/**
 * 请求ID生成器
 */
public final class RequestIdHolder {
    private static final ThreadLocal<String> REQUEST_ID = new ThreadLocal<>();

    private RequestIdHolder() {
    }

    public static void set(String requestId) {
        REQUEST_ID.set(requestId);
    }

    public static String get() {
        String requestId = REQUEST_ID.get();
        return requestId == null ? UUID.randomUUID().toString() : requestId;
    }

    public static void clear() {
        REQUEST_ID.remove();
    }
}
