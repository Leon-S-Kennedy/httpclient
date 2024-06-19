package com.libowen.util;

import com.libowen.model.RequestFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Author: libw1
 * @Date: 2024/04/02/16:25
 * @Description:
 */
@Slf4j
public class RequestUtil {
    public static void sendAndPrint(String requestPath){
        CompletableFuture<HttpResponse> future = RequestUtil.requestParseAndSend(requestPath);
        if(future!=null){
            String responseEntity = RequestUtil.getResponseEntity(future);
            if(responseEntity!=null){
                String pretty = SerializableUtil.StringGetPretty(responseEntity);
                if(pretty!=null){
                    System.out.println("本次请求的结果是\n"+pretty);
                }
            }else {
                log.info("请求发送成功,但是响应为null");
            }
        }
    }


    public static CompletableFuture<HttpResponse> requestParseAndSend(String requestPath){
        CompletableFuture<HttpResponse> future =null;
        if(requestPath!=null){
            File file = new File(requestPath);
            if(file.exists()&&file.isFile()){
                RequestFile requestFile = SerializableUtil.jsonFileToObject(requestPath, RequestFile.class);
                if(requestFile!=null){
                    future =sendRequestByFile(requestFile);
                }else {
                    log.info("请求文件解析失败");
                }
            }else {
                log.info("请求文件不存在或者不是文件");
            }
        }else {
            log.info("未指定请求文件的路径");
        }
        return future;
    }
    public static CompletableFuture<HttpResponse> sendRequestByFile(RequestFile requestFile){
        CompletableFuture<HttpResponse> future=null;
        if(requestFile.getUrl()!=null){
            if("get".equalsIgnoreCase(requestFile.getMethod())){
                if (null != requestFile.getBody()) {
                    log.info("Get请求的请求体已经自动忽略，请手动将其URLEncode");
                }
                future = HttpUtil.asyncGet(requestFile);
            }else if ("post".equalsIgnoreCase(requestFile.getMethod())){
                future = HttpUtil.asyncPost(requestFile);
            }else {
                log.info("不支持{}这种请求方法",requestFile.getMethod());
            }
        }else {
            log.info("url为null");
        }
        return future;
    }

    private static String getResponseEntity(CompletableFuture<HttpResponse> future) {
        String responseEntityString=null;
        HttpResponse httpResponse=null;
        try {
            httpResponse = future.get(20, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        if(httpResponse!=null){
            try {
                responseEntityString = EntityUtils.toString(httpResponse.getEntity());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseEntityString;
    }


}
