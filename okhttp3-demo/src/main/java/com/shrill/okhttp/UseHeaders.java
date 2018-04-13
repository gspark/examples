package com.shrill.okhttp;


import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UseHeaders {
    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://www.baidu.com")
                .header("User-Agent", "My super agent")
                .addHeader("Accept", "text/html")
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("服务器端错误: " + response);
        }

        System.out.println(response.header("Server"));
        System.out.println(response.headers("Set-Cookie"));
    }
}
