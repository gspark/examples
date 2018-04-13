package com.shrill.okhttp;

import com.qingstor.sdk.constants.QSConstant;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Created by zg on 2017-7-4.
 */
public class Http2Test {

    @Test
    public void okHttpClientTest() {
        OkHttpClient client =
                new OkHttpClient.Builder()
                        .connectTimeout(QSConstant.HTTPCLIENT_CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                        .readTimeout(QSConstant.HTTPCLIENT_READ_TIME_OUT, TimeUnit.SECONDS)
                        .writeTimeout(QSConstant.HTTPCLIENT_WRITE_TIME_OUT, TimeUnit.SECONDS)
                        .protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
                        .build();

        Request request = new Request.Builder()
                .url("http://127.0.0.1:8080")
                .build();
        long startTime = System.nanoTime();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            long duration = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startTime);
            System.out.println("After " + duration + " seconds: " + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                long duration = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startTime);
//                System.out.println("After " + duration + " seconds: " + response.body().string());
//            }
//        });
    }

//    private static OkHttpClient getUnsafeOkHttpClient() {
//        try {
//            // Install the all-trusting trust manager
//            final SSLContext sslContext = SSLContext.getInstance("SSL");
//            sslContext.init(null, TRUST_ALL_CERTS, new java.security.SecureRandom());
//            // Create an ssl socket factory with our all-trusting manager
//            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
//
//            OkHttpClient okHttpClient = new OkHttpClient();
//            okHttpClient.setSslSocketFactory(sslSocketFactory);
//            okHttpClient.setHostnameVerifier((hostname, session) -> true);
//
//            return okHttpClient;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

}
