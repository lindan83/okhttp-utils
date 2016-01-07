package com.zhy.sample_okhttp;

import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;
import java.util.List;

import okhttp3.Response;

/**
 * 自定义的集合转换回调
 */
public abstract class ListUserCallback extends Callback<List<User>> {
    @Override
    public List<User> parseNetworkResponse(Response response) throws IOException {
        String string = response.body().string();
        List<User> user = new Gson().fromJson(string, List.class);
        return user;
    }
}