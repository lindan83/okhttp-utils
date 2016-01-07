package com.zhy.http.okhttp.request;

import java.util.Map;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 表示一个GET请求
 */
public class GetRequest extends OkHttpRequest {
    /**
     * 构造方法
     *
     * @param url     请求URL
     * @param tag     标识请求的TAG
     * @param params  参数
     * @param headers 请求头
     */
    public GetRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers) {
        super(url, tag, params, headers);
    }

    /**
     * 构建请求体
     *
     * @return 请求体
     */
    @Override
    protected RequestBody buildRequestBody() {
        return null;
    }

    /**
     * 构建请求
     *
     * @param builder     构建器对象
     * @param requestBody 请求体
     * @return 请求对象
     */
    @Override
    protected Request buildRequest(Request.Builder builder, RequestBody requestBody) {
        return builder.get().build();
    }
}