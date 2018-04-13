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
public class JHOkHttpTest {

    private QingStor storService;
    private EvnContext evn;
    private Bucket bucket;

    OkHttpClient client;

    private String[] cities = {
            "android/street/lib/lib/alaskacedar.assetbundle",
            "android/street/lib/lib/028006018t0099.assetbundle",
            "ios/street/0280/build/018039006001_tex.assetbundle",
            "ios/street/0280/build/028000060107.assetbundle",
            "ios/street/0280/shop/028001001008_atl.assetbundle",
            "ios/street/0280/shop/028001001012_atl.assetbundle",
            "ios/street/0280/shop/028001001016_atl.assetbundle",
            "ios/street/0280/shop/028001001017_atl.assetbundle",
            "ios/street/0280/shop/028001001034_atl.assetbundle",
            "ios/street/0280/shop/028001001a01_atl.assetbundle",
            "ios/street/0280/shop/028001001a02_atl.assetbundle",
            "ios/street/0280/shop/028001001a03_atl.assetbundle",
            "ios/street/0280/shop/028001001a04_atl.assetbundle",
            "ios/street/0280/shop/028001001a05_atl.assetbundle",
            "ios/street/0280/build/028001001a04.assetbundle",
            "ios/street/0280/build/028001001a04_tex.assetbundle",
            "ios/street/0280/build/028001001a05.assetbundle",
            "ios/street/0280/build/028001001a05_tex.assetbundle",
            "ios/street/0280/build/028001001a06.assetbundle",
            "ios/street/0280/build/028001001a06_tex.assetbundle",
            "ios/street/0280/build/028001001a07.assetbundle",
            "ios/street/0280/build/028001001a07_tex.assetbundle",
            "ios/street/0280/build/028001001a08.assetbundle",
            "ios/street/0280/build/028001001a08_tex.assetbundle",
            "ios/street/0280/build/028001001a09.assetbundle",
            "ios/street/0280/build/028001001a09_tex.assetbundle",
            "ios/street/0280/build/028001001a10.assetbundle",
            "ios/street/0280/build/028001001a10_tex.assetbundle",
            "ios/street/0280/build/028001001a11.assetbundle",
            "ios/street/0280/build/028001001a11_tex.assetbundle",
            "ios/street/0280/build/028001001a12.assetbundle",
            "ios/street/0280/build/028001001a12_tex.assetbundle",
            "ios/street/0280/build/028001001a13.assetbundle",
            "ios/street/0280/build/028001001a13_tex.assetbundle",
            "ios/street/0280/build/028001001a14.assetbundle",
            "ios/street/0280/build/028001001a14_tex.assetbundle",
            "ios/street/0280/build/028001001a15.assetbundle",
            "ios/street/0280/build/028001001a15_tex.assetbundle",
            "ios/street/0280/build/028001001a16.assetbundle",
            "ios/street/0280/build/028001001a16_tex.assetbundle",
            "ios/street/0280/build/028001001a17.assetbundle",
            "ios/street/0280/build/028001001a17_tex.assetbundle",
            "ios/street/0280/build/028001001a18.assetbundle",
            "ios/street/0280/build/028001001a18_tex.assetbundle",
            "ios/street/0280/build/028001001a19.assetbundle",
            "ios/street/0280/build/028001001a19_tex.assetbundle",
            "ios/street/0280/build/028001001a21.assetbundle",
            "ios/street/0280/build/028001001a21_tex.assetbundle",
            "ios/street/0280/build/028001001a22.assetbundle",
            "ios/street/0280/build/028001001a22_tex.assetbundle",
            "ios/street/0280/build/028001001a23.assetbundle",
            "ios/street/0280/build/028001001a23_tex.assetbundle",
            "ios/street/0280/build/028001001a24.assetbundle",
            "ios/street/0280/build/028001001a24_tex.assetbundle",
            "ios/street/0280/build/028001001a25.assetbundle",
            "ios/street/0280/build/028001001a25_tex.assetbundle",
            "ios/street/0280/build/028001001a26.assetbundle",
            "ios/street/0280/build/028001001a26_tex.assetbundle",
            "ios/street/0280/build/028001001a27.assetbundle",
            "ios/street/0280/build/028001001a27_tex.assetbundle",
            "ios/street/0280/build/028001001a28.assetbundle",
            "ios/street/0280/build/028001001a28_tex.assetbundle",
            "ios/street/0280/build/028001001a29.assetbundle",
            "ios/street/0280/build/028001001a29_tex.assetbundle",
            "ios/street/0280/build/028001001a30.assetbundle",
            "ios/street/0280/build/028001001a30_tex.assetbundle",
            "ios/street/0280/build/028001001a31.assetbundle",
            "ios/street/0280/build/028001001a31_tex.assetbundle",
            "ios/street/0280/build/028001001a32.assetbundle",
            "ios/street/0280/build/028001001a32_tex.assetbundle",
            "ios/street/0280/build/028001002a01.assetbundle",
            "ios/street/0280/build/028001002a01_tex.assetbundle",
            "ios/street/0280/build/028001002a02.assetbundle",
            "ios/street/0280/build/028001002a02_tex.assetbundle",
            "ios/street/0280/build/028001002a03.assetbundle",
            "ios/street/0280/build/028001002a03_tex.assetbundle",
            "ios/street/0280/build/028001002a04.assetbundle",
            "ios/street/0280/build/028001002a04_tex.assetbundle",
            "ios/street/0280/build/028001002a05.assetbundle",
            "ios/street/0280/build/028001002a05_tex.assetbundle",
            "ios/street/0280/build/028001002a06.assetbundle",
            "ios/street/0280/build/028001002a06_tex.assetbundle",
            "ios/street/0280/build/028001002a07.assetbundle",
            "ios/street/0280/build/028001002a07_tex.assetbundle",
            "ios/street/0280/build/028001002a09.assetbundle",
            "ios/street/0280/build/028001002a09_tex.assetbundle",
            "ios/street/0280/build/028001002a10.assetbundle",
            "ios/street/0280/build/028001002a10_tex.assetbundle",
            "ios/street/0280/build/028001002a12.assetbundle",
            "ios/street/0280/build/028001002a12_tex.assetbundle",
            "ios/street/0280/build/028001002a14.assetbundle",
            "ios/street/0280/build/028001002a14_tex.assetbundle",
            "ios/street/0280/build/028001002a16.assetbundle",
            "ios/street/0280/build/028001002a16_tex.assetbundle",
            "ios/street/0280/build/028001002a20.assetbundle",
            "ios/street/0280/build/028001002a20_tex.assetbundle",
            "ios/street/0280/build/028001002a22.assetbundle",
            "ios/street/0280/build/028001002a22_tex.assetbundle",
            "ios/street/0280/build/028001002b01.assetbundle",
            "ios/street/0280/build/028001002b01_tex.assetbundle",
            "ios/street/0280/build/028001002b02.assetbundle",
            "ios/street/0280/build/028001002b02_tex.assetbundle",
            "ios/street/0280/build/028001002b03.assetbundle",
            "ios/street/0280/build/028001002b03_tex.assetbundle",
            "ios/street/0280/build/028001003002.assetbundle",
            "ios/street/0280/build/028001003002_tex.assetbundle",
            "ios/street/0280/build/028001003004.assetbundle",
            "ios/street/0280/build/028001003004_tex.assetbundle",
            "ios/street/0280/build/028001003a01.assetbundle",
            "ios/street/0280/build/028001003a01_tex.assetbundle",
            "ios/street/0280/build/028001003a02.assetbundle",
            "ios/street/0280/build/028001003a02_tex.assetbundle",
            "ios/street/0280/build/028001003a03.assetbundle",
            "ios/street/0280/build/028001003a03_tex.assetbundle",
    };

    private String[] resource = {"artist_work1473838957007.zip"};
    private String[] vcity = {
            "jingpin/jingpinstreet/android/tfgc",
            "jingpin/jingpinstreet/android/kzxz",
            "quanjing/4004.jpg",
            "quanjing/4002_s.jpg",
            "quanjing/4004_s.jpg"
    };

    @Before
    public void setUp() throws Exception {
        evn = new EvnContext("ZUAHOMJFVEQYADIVQKHB", "U9rVaMXTiWHJ0HodKLKSGETrjLWE6SPSPtMeS3Pq");
        storService = new QingStor(evn);
//        bucket = storService.getBucket("artistwork", "pek3a");
        //
        Mac.getInstance("HmacSHA256");
        QSOkHttpRequestClient c = QSOkHttpRequestClient.getInstance();

        client =
                new OkHttpClient.Builder()
                        .connectTimeout(QSConstant.HTTPCLIENT_CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                        .readTimeout(QSConstant.HTTPCLIENT_READ_TIME_OUT, TimeUnit.SECONDS)
                        .writeTimeout(QSConstant.HTTPCLIENT_WRITE_TIME_OUT, TimeUnit.SECONDS)
                        .protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
                        .build();
    }


    public void getObject() {

    }

    @Test
    public void getObjecQyThr3() {
        for( int i = 0; i < 3; ++i ) {
            long start = System.currentTimeMillis();
            getObjectOneThrQy("http://139.198.1.229");
            System.out.println("---------------------qingyun time :" + (System.currentTimeMillis() - start));

        }
    }

    @Test
    public void getObjecWaSuThr3() {
        for( int i = 0; i < 3; ++i ) {
            long start = System.currentTimeMillis();
            getObjectOneThrQy("http://3rdapiwangsu.pxsj.com");
            System.out.println("---------------------WaSu time :" + (System.currentTimeMillis() - start));

        }
    }

    @Test
    public void getObjecAliThr3() {
        for( int i = 0; i < 3; ++i ) {
            long start = System.currentTimeMillis();
            getObjectOneThrQy("http://3rdapiali.pxsj.com");
            System.out.println("---------------------Ali time :" + (System.currentTimeMillis() - start));

        }
    }

    public void getObjectOneThrQy(String domain) {
        long start = System.currentTimeMillis();
        for (String ab : cities ) {
            String url = String.format("%s/api/v1/resource/object?name=/%s&bucket=%s&zone=pek3a", domain, ab, "cities");
            doGet(ab, url);
        }
        for (String ab : resource ) {
            String url = String.format("%s/api/v1/resource/object?name=/%s&bucket=%s&zone=pek3a", domain, ab, "resource");
            doGet(ab, url);
        }
        for (String ab : vcity ) {
            String url = String.format("%s/api/v1/resource/object?name=/%s&bucket=%s&zone=pek3a", domain, ab, "vcity-pc");
            doGet(ab, url);
        }
        System.out.println("time :" + (System.currentTimeMillis() - start));
    }

    private void doGet(String ab, String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (null != response.body().byteStream()) {
                String fileName = ab.substring(ab.lastIndexOf("/") + 1);
                byte2File(response.body().byteStream(), "f:\\tmp\\assetbundle", fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getObjectThr() {
        long start = System.currentTimeMillis();
        ForkJoinPool pool = new ForkJoinPool();
        GetObjectTask task = new GetObjectTask(0, 300);
        pool.invoke(task);
        pool.shutdown();
        System.out.println("time :" + (System.currentTimeMillis() - start));
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
//                getObject(start);

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
