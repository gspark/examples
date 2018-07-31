package com.shrill.okhttp;

import com.qingstor.sdk.constants.QSConstant;
import okhttp3.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by zg on 2017-7-4.
 */
public class Http2Test {


    OkHttpClient mClient;

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

    @Before
    public void before() {
        mClient =
            new OkHttpClient.Builder()
                .connectTimeout(QSConstant.HTTPCLIENT_CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(QSConstant.HTTPCLIENT_READ_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(QSConstant.HTTPCLIENT_WRITE_TIME_OUT, TimeUnit.SECONDS)
                .protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
                .build();
    }

    @Test
    public void testAllStreetsInfo() {
//        RequestBody body = new FormBody.Builder()
//            .add("cityCode", "0100")
//            .add("pageNum", "0")
//            .add("pageSize", "20")
//            .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON,
            "{\"cityCode\": \"0100\",\n"
                + "  \"pageNum\": 0,\n"
                + "  \"pageSize\": 0\n"
                + "}");

        Request request = new Request.Builder()
            .url("http://192.168.31.233:8310/v1/geoinfoService/allStreetsInfo?city=0100")
            .post(body)
            .build();
        long startTime = System.nanoTime();

        Response response = null;
        try {
            response = mClient.newCall(request).execute();
            long duration = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startTime);
            System.out.println("After " + duration + " seconds: " + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * encode string
     *
     * @param algorithm
     * @param str
     * @return String
     */
    public static String encode(String algorithm, String str) {
        if (str == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(str.getBytes("utf-8"));
            return getFormattedText(messageDigest.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /**
     * Takes the raw bytes from the digest and formats them correct.
     *
     * @param bytes
     *            the raw bytes from the digest.
     * @return the formatted bytes.
     */
    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        // 把密文转换成十六进制的字符串形式
        for (byte aByte : bytes) {
            buf.append(HEX_DIGITS[(aByte >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[aByte & 0x0f]);
        }
        return buf.toString();
    }

    @Test
    public void testAllStreetsInfo1() {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        String data = "{\"cityCode\": \"0283\",\n"
            + "  \"pageNum\": 10,\n"
            + "  \"pageSize\": 10\n"
            + "}";

        RequestBody body = RequestBody.create(JSON, data);
        String timestamp = String.valueOf(new Date().getTime());
        String sign = getSign(data, timestamp, "7qp9ap2omqsmsjuyc455kmqrf3zqd81p");
        Request request = new Request.Builder()
            .url("http://192.168.31.242:8010/openapi/resources/v2/geoinfo/allStreetsInfo")
            .addHeader("appID", "model_resources_5")
            .addHeader("Timestamp", timestamp)
            .addHeader("Sign", sign)
            .post(body)
            .build();
        long startTime = System.nanoTime();

        Response response = null;
        try {
            response = mClient.newCall(request).execute();
            long duration = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startTime);
            System.out.println("After " + duration + " seconds: " + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getSign(String body, String timestamp, String secretId) {
        String sign = String.format("%s_%s_%s",body,timestamp,secretId);
        sign = encode("MD5", sign);
        sign = sign.toUpperCase();
        return sign;
    }
}
