package com.shrill.okhttp;

import com.qingstor.sdk.config.EvnContext;
import com.qingstor.sdk.constants.QSConstant;
import com.qingstor.sdk.exception.QSException;
import com.qingstor.sdk.request.QSOkHttpRequestClient;
import com.qingstor.sdk.service.Bucket;
import com.qingstor.sdk.service.QingStor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.Mac;
import java.io.*;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

/**
 * Created by zg on 2017-6-21.
 */
public class JHOkHttpRequestClientTest {

    private QingStor storService;
    private EvnContext evn;
    private Bucket bucket;

    @Before
    public void setUp() throws Exception {
        System.out.println("current thread:" + Thread.currentThread().getId());
        evn = new EvnContext("ZUAHOMJFVEQYADIVQKHB", "U9rVaMXTiWHJ0HodKLKSGETrjLWE6SPSPtMeS3Pq");
        storService = new QingStor(evn);
        bucket = storService.getBucket("artistwork", "pek3a");
        //
        Mac.getInstance("HmacSHA256");
        QSOkHttpRequestClient c = QSOkHttpRequestClient.getInstance();

//        Bucket.GetObjectOutput bgo = bucket.getObject("filecrc/028008069a04.fbx_388127183_android.assetbundle", null);
//        if( null != bgo.getBodyInputStream()) {
//            bgo.getBodyInputStream().close();
//        }
//        bucket.getObject("filecrc/028008069a04.fbx_388127183_android.assetbundle", null);
//        getObject(100);
//        getObject(101);

//        Request request = new Request.Builder()
//                .url("https://www.qingstor.com")
//                .build();
//        Bucket.GetObjectOutput om = (Bucket.GetObjectOutput)c.requestAction(request,true,Bucket.GetObjectOutput.class);
//        if( null != om.getBodyInputStream() ) {
//            om.getBodyInputStream().close();
//        }
    }

    @Test
    public void getObject() {
//        InputStream is = JHOkHttpRequestClient.getInstance().requestAction(
//                JHOkHttpRequestClient.getInstance()
//                        .buildUrlRequest(
//                                "http://pek3a.qingstor.com/cities/android/street/lib/lib/028006018t0099.assetbundle"),
//                false);
        long start = System.currentTimeMillis();
        Bucket bucket = storService.getBucket("artistwork", "pek3a");
//        Bucket bucket = storService.getBucket("jhtest", "pek3a");
        System.out.println("get bucket time :" + (System.currentTimeMillis() - start));
        try {
            Bucket.GetObjectOutput bgo = bucket.getObject("materials/lib/009_android.assetbundle", null);

            if (null != bgo) {
                InputStream is = bgo.getBodyInputStream();
                if (null != is) {
                    try {
                        byte2File(is, "f:\\tmp", "009_android.assetbundle");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (QSException e) {
            e.printStackTrace();
        }
    }

    public void getObject(int idx) {
//        Bucket bucket = storService.getBucket("artistwork", "pek3a");
        try {
            Bucket.GetObjectOutput bgo = bucket.getObject("filecrc/028008069a04.fbx_388127183_android.assetbundle", null);

            if (null != bgo) {
                InputStream is = bgo.getBodyInputStream();
                if (null != is) {
                    try {
                        byte2File(is, "f:\\tmp\\assetbundle", "028008069a04.fbx_388127183_android.assetbundle" + idx);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (QSException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getObjectPerformance1Thr() {
        long start = System.currentTimeMillis();
        int count = 300;
        for (int i = 0; i < count; ++i) {
            getObject(i);
        }
        System.out.println("time :" + (System.currentTimeMillis() - start));
    }

    @Test
    public void getObjectPerformanceThr() {
        long start = System.currentTimeMillis();
        ForkJoinPool pool = new ForkJoinPool();
        GetObjectTask task = new GetObjectTask(0,300);
        pool.invoke(task);
        pool.shutdown();
        System.out.println("time :" + (System.currentTimeMillis() - start));
    }

    @Test
    public void getObjectPerformance2() {
        System.out.println("current thread getObjectPerformance2:" + Thread.currentThread().getId());
        int count = 10;
        for (int i = 0; i < count; ++i) {
            long start = System.currentTimeMillis();
            getObject(i);
            System.out.println("time " + i+ " : " +(System.currentTimeMillis() - start));
        }
    }

    @Test
    public void getObjectConnectPool() {
//        http://192.168.31.253:9310/api/v1/resource/object?name=%2fcharacters%2fandroid%2fandroid&bucket=jhtest&zone=pek3a
        Bucket bucket = storService.getBucket("jhtest", "pek3a");
        try {
//            bucket.listObjects(null);
            for (int i = 0; i < 3000; ++i) {
                long start = System.currentTimeMillis();
                Bucket.GetObjectOutput bgo = bucket.getObject("characters/android/android", null);
                if (null != bgo) {
                    InputStream is = bgo.getBodyInputStream();
                    if (null != is) {
                        try {
                            byte2File(is, "f:\\tmp\\assetbundle", "characters_android_android" + i);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("byte2File            time :" + (System.currentTimeMillis() - start));
                }
            }

        } catch (QSException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getObjectTest() {
        OkHttpClient client =
                new OkHttpClient.Builder()
                        .connectTimeout(QSConstant.HTTPCLIENT_CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                        .readTimeout(QSConstant.HTTPCLIENT_READ_TIME_OUT, TimeUnit.SECONDS)
                        .writeTimeout(QSConstant.HTTPCLIENT_WRITE_TIME_OUT, TimeUnit.SECONDS)
                        .protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
                        .build();

        Request request = new Request.Builder()
//                .url("http://192.168.31.253:9310/api/v1/resource/object?name=%2fcharacters%2fandroid%2fandroid&bucket=jhtest&zone=pek3a")
                .url("http://localhost:9310/api/v1/resource/object?name=%2fcharacters%2fandroid%2fandroid&bucket=jhtest&zone=pek3a")
//                .url("http://192.168.31.253:9310/api/v1/resource/object?name=filecrc/map_028001__025029.fbx_974474946_android.assetbundle&bucket=jhtest&zone=pek3a")
//                .url("http://localhost:9310/api/v1/resource/object?name=filecrc/map_028001__025029.fbx_974474946_android.assetbundle&bucket=jhtest&zone=pek3a")
                .build();

        try {
            for (int i = 0; i < 1; ++i) {
                long start = System.currentTimeMillis();
                Response response = client.newCall(request).execute();
                if (null != response.body().byteStream()) {
                    System.out.println("byte2File            time :" + (System.currentTimeMillis() - start));
                    byte2File(response.body().byteStream(), "f:\\tmp\\assetbundle",
                            "characters_android_android" + i);

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void byte2File(InputStream inputStream, String filePath, String fileName)
            throws IOException {
        File file = new File(filePath + File.separator + fileName);
        OutputStream out = new FileOutputStream(file);
        int bytesRead = 0;
        byte[] buffer = new byte[1024];
        while ((bytesRead = inputStream.read(buffer, 0, 1024)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        out.close();
        inputStream.close();
    }

    public class GetObjectTask extends RecursiveAction {

        private static final int THRESHOLD = 1;

        private int start;
        private int end;

        public GetObjectTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            boolean isProcess = (end - start) == THRESHOLD;
            if (isProcess) {
                System.out.println(Thread.currentThread().getName());
                getObject(start);

            } else {
                System.out.println(Thread.currentThread().getName() + "----");
                int partPos = (start + end) / 2;
                GetObjectTask taskl = new GetObjectTask(start, partPos);
                GetObjectTask taskr = new GetObjectTask(partPos, end);
                invokeAll(taskl, taskr);
            }
        }

    }
}