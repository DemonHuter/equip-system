package com.equip.common;

import lombok.Data;

@Data
public class Response<T> {

    private int code;
    private String msg;
    private T data;

    public Response(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(200, "success", data);
    }

    public static <T> Response<T> success() {
        return new Response<>(200, "success", null);
    }

    public static <T> Response<T> error(String msg) {
        return new Response<>(500, msg, null);
    }

}