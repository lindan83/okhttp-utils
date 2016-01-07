package com.zhy.http.okhttp.callback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;

import okhttp3.Response;

/**
 * 获取图片Bitmap的响应回调，用于下载图片
 */
public abstract class BitmapCallback extends Callback<Bitmap> {
    @Override
    public Bitmap parseNetworkResponse(Response response) throws IOException {
        return BitmapFactory.decodeStream(response.body().byteStream());
    }
}