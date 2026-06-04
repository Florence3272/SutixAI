package com.sutix.common.result;

import lombok.Data;

/**
 * 全局统一返回结果
 */
@Data
public class Result<T> {
    // 响应码
    private Integer code;
    // 响应消息
    private String msg;
    // 响应数据
    private T data;

    // 私有化构造方法
    private Result() {}

    // 新增方法：返回数据 + 自定义消息
    public static <T> Result<T> success(T data, String msg) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    // 成功返回（无数据）
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("操作成功");
        return result;
    }

    // 成功返回（带数据）
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    // 失败返回
    public static <T> Result<T> fail(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }

    // 自定义返回
    public static <T> Result<T> build(Integer code, String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    // 失败返回（带错误码和消息）
    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    // 重载：默认500错误码
    public static <T> Result<T> error(String msg) {
        return error(500, msg);
    }
}