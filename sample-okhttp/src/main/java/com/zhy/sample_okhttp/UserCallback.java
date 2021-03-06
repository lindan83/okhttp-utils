package com.zhy.sample_okhttp;

import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;

import okhttp3.Response;

/**
 * 自定义的对象转换回调
 */
public abstract class UserCallback extends Callback<User> {
    @Override
    public User parseNetworkResponse(Response response) throws IOException {
        String string = response.body().string();
        User user = new Gson().fromJson(string, User.class);
        return user;
    }
}