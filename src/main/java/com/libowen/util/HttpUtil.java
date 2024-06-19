package com.libowen.util;

import com.libowen.model.RequestFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @Author: libw1
 * @Date: 2024/04/02/16:26
 * @Description:
 */
@Slf4j
public class HttpUtil {

    private static final CloseableHttpAsyncClient httpAsyncClient;

    private static final RequestConfig requestConfig;

    static {
        httpAsyncClient = HttpAsyncClientBuilder.create().build();
        requestConfig = RequestConfig.custom()
                .setConnectTimeout(10000)
                .setSocketTimeout(15000)
                .setConnectionRequestTimeout(1000)
                .build();
        httpAsyncClient.start();
    }

    public static CompletableFuture<HttpResponse> asyncGet(String url) {
        CompletableFuture<HttpResponse> future = new CompletableFuture<>();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        httpAsyncClient.execute(httpGet, new FutureCallbackAdapter<>(future));
        return future;
    }

    public static CompletableFuture<HttpResponse> asyncPost(String url,Object data) {
        CompletableFuture<HttpResponse> future = new CompletableFuture<>();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        if(data!=null){
            String dataString = SerializableUtil.toJsonString(data);
            if(dataString!=null){
                StringEntity entity = new StringEntity(dataString, "UTF-8");
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
                httpAsyncClient.execute(httpPost, new FutureCallbackAdapter<>(future));
            }
        }else {
            httpPost.setEntity(null);
            httpAsyncClient.execute(httpPost, new FutureCallbackAdapter<>(future));
        }
        return future;
    }

    public static CompletableFuture<HttpResponse> asyncGet(RequestFile requestFile) {
        CompletableFuture<HttpResponse> future = new CompletableFuture<>();
        HttpGet httpGet = new HttpGet(requestFile.getUrl());
        httpGet.setConfig(requestConfig);
        Map<String, String> header = requestFile.getHeader();
        for (Map.Entry<String, String> entry : header.entrySet()) {
            httpGet.addHeader(entry.getKey(), entry.getValue());
        }
        httpAsyncClient.execute(httpGet, new FutureCallbackAdapter<>(future));
        return future;
    }

    public static CompletableFuture<HttpResponse> asyncPost(RequestFile requestFile) {
        CompletableFuture<HttpResponse> future = new CompletableFuture<>();
        HttpPost httpPost = new HttpPost(requestFile.getUrl());
        httpPost.setConfig(requestConfig);
        Map<String, String> header = requestFile.getHeader();
        for (Map.Entry<String, String> entry : header.entrySet()) {
            httpPost.addHeader(entry.getKey(), entry.getValue());
        }
        Map<String, String> body = requestFile.getBody();
        if(body!=null){
            String dataString = SerializableUtil.toJsonString(body);
            if(dataString!=null){
                StringEntity entity = new StringEntity(dataString, "UTF-8");
                httpPost.setEntity(entity);
            }else {
                log.info("请求体解析失败，发送空请求体！");
                httpPost.setEntity(null);
            }
        }else {
            httpPost.setEntity(null);
        }
        httpAsyncClient.execute(httpPost, new FutureCallbackAdapter<>(future));
        return future;
    }

    @PreDestroy
    public static void close() {
        try {
            httpAsyncClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class FutureCallbackAdapter<T> implements org.apache.http.concurrent.FutureCallback<T> {

        private final CompletableFuture<T> future;

        public FutureCallbackAdapter(CompletableFuture<T> future) {
            this.future = future;
        }

        @Override
        public void completed(T result) {
            future.complete(result);
        }

        @Override
        public void failed(Exception ex) {
            future.completeExceptionally(ex);
        }

        @Override
        public void cancelled() {
            future.cancel(true);
        }
    }
}
