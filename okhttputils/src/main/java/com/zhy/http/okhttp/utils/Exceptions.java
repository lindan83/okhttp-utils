package com.zhy.http.okhttp.utils;

/**
 * 用于抛出IllegalArgumentException的工具类
 */
public class Exceptions {
    /**
     * 抛出IllegalArgumentException异常
     *
     * @param msg 异常信息
     */
    public static void illegalArgument(String msg) {
        throw new IllegalArgumentException(msg);
    }
}