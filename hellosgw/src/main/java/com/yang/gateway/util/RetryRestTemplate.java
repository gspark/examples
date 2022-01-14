package com.yang.gateway.util;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RetryRestTemplate {

    public static RestTemplate build() {
        return build(3, 1000, false);
    }

    public static RestTemplate build(int retryTimes, long retryInterval, boolean ssl) {
        HttpClientBuilder clientBuilder = HttpClients.custom();
        if (ssl) {
            try {
                SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy())
                    .build();
                SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext,
                    NoopHostnameVerifier.INSTANCE);
                clientBuilder.setSSLSocketFactory(csf);
            } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
                e.printStackTrace();
            }

        }

        RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(5000).setConnectTimeout(5000)
            .setSocketTimeout(10000).build();

        ServiceUnavailableRetryStrategy retryStrategy = new ServiceUnavailableRetryStrategy() {
            @Override
            public boolean retryRequest(HttpResponse response, int executionCount, HttpContext httpContext) {
                int statusCode = response.getStatusLine().getStatusCode();
                return executionCount < retryTimes && statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE;
            }

            @Override
            public long getRetryInterval() {
                return retryInterval;
            }
        };
        HttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler(retryTimes, false);
        clientBuilder.setDefaultRequestConfig(config).setRetryHandler(retryHandler)
            .setServiceUnavailableRetryStrategy(retryStrategy);

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(clientBuilder.build());
        return new RestTemplate(requestFactory);

    }
}
