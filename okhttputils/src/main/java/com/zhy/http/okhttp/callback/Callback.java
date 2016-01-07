package com.zhy.http.okhttp.callback;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * 抽象回调类
 *
 * @param <T>
 */
public abstract class Callback<T> {
    /**
     * UI Thread
     * 请求之前调用
     *
     * @param request 請求對象
     */
    public void onBefore(Request request) {
    }

    /**
     * UI Thread
     * 请求之后调用
     */
    public void onAfter() {
    }

    /**
     * UI Thread
     * 请求过程中刷新进度
     *
     * @param progress 进度值
     */
    public void inProgress(float progress) {
    }

    /**
     * Thread Pool Thread
     * 解析响应内容到对应泛型类型
     *
     * @param response 响应对象
     */
    public abstract T parseNetworkResponse(Response response) throws IOException;

    /**
     * 请求出错时调用
     *
     * @param request 请求对象
     * @param e       异常
     */
    public abstract void onError(Request request, Exception e);

    /**
     * 请求成功时调用
     *
     * @param response 响应对象
     */
    public abstract void onResponse(T response);

    /**
     * 默认的回调对象，空实现
     */
    public static Callback CALLBACK_DEFAULT = new Callback() {

        @Override
        public Object parseNetworkResponse(Response response) throws IOException {
            return null;
        }

        @Override
        public void onError(Request request, Exception e) {
        }

        @Override
        public void onResponse(Object response) {
        }
    };
}