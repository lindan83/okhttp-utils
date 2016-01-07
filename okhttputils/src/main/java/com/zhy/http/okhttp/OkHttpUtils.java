package com.zhy.http.okhttp;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.PostFileBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.builder.PostStringBuilder;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.cookie.SimpleCookieJar;
import com.zhy.http.okhttp.https.HttpsUtils;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OkHttp集成管理工具
 */
public class OkHttpUtils {
    public static final String TAG = "OkHttpUtils";
    public static final long DEFAULT_MILLISECONDS = 10000;//默认请求超时时间10秒
    private static OkHttpUtils mInstance;//本类唯一实例
    private OkHttpClient mOkHttpClient;//请求客户端
    private Handler mDelivery;//主线程的Handler
    private boolean debug;
    private String tag;

    private OkHttpUtils() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        //cookie enabled
        okHttpClientBuilder.cookieJar(new SimpleCookieJar());
        mDelivery = new Handler(Looper.getMainLooper());

        if (true) {
            okHttpClientBuilder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }
        mOkHttpClient = okHttpClientBuilder.build();
    }

    /**
     * 设置标识
     *
     * @param tag 标识
     * @return 本类实例
     */
    public OkHttpUtils debug(String tag) {
        debug = true;
        this.tag = tag;
        return this;
    }

    /**
     * 单例，获取本类唯一实例
     *
     * @return
     */
    public static OkHttpUtils getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtils();
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取主线程的Handler
     *
     * @return 主线程的Handler
     */
    public Handler getDelivery() {
        return mDelivery;
    }

    /**
     * 获取客户端对象
     *
     * @return 客户端
     */
    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    /**
     * 获取GET请求构建器
     *
     * @return GET请求构建器
     */
    public static GetBuilder get() {
        return new GetBuilder();
    }

    /**
     * 获取POST字符串请求构建器
     *
     * @return POST字符串请求构建器
     */
    public static PostStringBuilder postString() {
        return new PostStringBuilder();
    }

    /**
     * 获取文件上传请求构建器
     *
     * @return 文件上传请求构建器
     */
    public static PostFileBuilder postFile() {
        return new PostFileBuilder();
    }

    /**
     * 获取多文件上传请求构建器
     *
     * @return 多文件上传请求构建器
     */
    public static PostFormBuilder post() {
        return new PostFormBuilder();
    }

    /**
     * 执行请求
     *
     * @param requestCall 请求任务
     * @param callback    回调
     */
    public void execute(final RequestCall requestCall, Callback callback) {
        if (debug) {
            if (TextUtils.isEmpty(tag)) {
                tag = TAG;
            }
            Log.d(tag, "{method:" + requestCall.getRequest().method() + ", detail:" + requestCall.getOkHttpRequest().toString() + "}");
        }

        if (callback == null)
            callback = Callback.CALLBACK_DEFAULT;
        final Callback finalCallback = callback;

        requestCall.getCall().enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                sendFailResultCallback(request, e, finalCallback);
            }

            @Override
            public void onResponse(final Response response) {
                if (response.code() >= 400 && response.code() <= 599) {
                    try {
                        sendFailResultCallback(requestCall.getRequest(), new RuntimeException(response.body().string()), finalCallback);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                try {
                    Object o = finalCallback.parseNetworkResponse(response);
                    sendSuccessResultCallback(o, finalCallback);
                } catch (IOException e) {
                    sendFailResultCallback(response.request(), e, finalCallback);
                }
            }
        });
    }

    /**
     * 处理出错的情况
     *
     * @param request  请求
     * @param e        异常
     * @param callback 回调
     */
    public void sendFailResultCallback(final Request request, final Exception e, final Callback callback) {
        if (callback == null) return;

        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(request, e);
                callback.onAfter();
            }
        });
    }

    /**
     * 处理成功的情况
     *
     * @param object   响应内容
     * @param callback 回调
     */
    public void sendSuccessResultCallback(final Object object, final Callback callback) {
        if (callback == null) return;
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(object);
                callback.onAfter();
            }
        });
    }

    /**
     * 根据标识取消某次请求
     *
     * @param tag 标识
     */
    public void cancelTag(Object tag) {
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    /**
     * 为安装连接设置证书
     *
     * @param certificates 证书的数据输入流
     */
    public void setCertificates(InputStream... certificates) {
        mOkHttpClient = getOkHttpClient().newBuilder()
                .sslSocketFactory(HttpsUtils.getSslSocketFactory(certificates, null, null))
                .build();
    }

    /**
     * 设置连接超时时间
     *
     * @param timeout 超时时间
     * @param units   时间单位
     */
    public void setConnectTimeout(int timeout, TimeUnit units) {
        mOkHttpClient = getOkHttpClient().newBuilder()
                .connectTimeout(timeout, units)
                .build();
    }
}