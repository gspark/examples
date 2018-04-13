package com.shrill.service;

import static org.slf4j.LoggerFactory.getLogger;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.resolver.DefaultEndpoint;
import com.netflix.discovery.shared.transport.EurekaHttpClient;
import com.netflix.discovery.shared.transport.EurekaHttpResponse;
import com.netflix.discovery.shared.transport.TransportClientFactory;
import com.netflix.discovery.shared.transport.jersey.JerseyEurekaHttpClientFactory;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EurekaWorkerIdRegister {

    private static final Logger log = getLogger(EurekaWorkerIdRegister.class);

    public static final String INSTANCEID_KEY = "instanceId";
    
    private EurekaHttpClient eurekaHttpClient;

    @Value("${spring.application.name}")
    private String appName;
    
    @Value("${eureka.client.serviceUrl.defaultZone}")
    private String eurekaServiceUrl;

    private String instanceId;
    
    private AtomicLong workerId = new AtomicLong();

    @PostConstruct
    private void init() {
        TransportClientFactory httpClientFactory = JerseyEurekaHttpClientFactory.newBuilder()
            .withClientName(appName)
            .withConnectionTimeout(10000)
            .withReadTimeout(10000)
            .withMaxConnectionsPerHost(1)
            .withMaxTotalConnections(1)
            .withConnectionIdleTimeout(10000)
            .build();
        
        eurekaHttpClient = httpClientFactory.newClient(new DefaultEndpoint(eurekaServiceUrl));
    }

    private InstanceInfo getInstanceId(String instance) {
        try {
            EurekaHttpResponse<InstanceInfo> ret = eurekaHttpClient.getInstance(instance);
            if (ret.getStatusCode() == 200) {
                InstanceInfo info = ret.getEntity();
                if (null != info) {
                    return info;
                }
            }    
        } catch( Exception e ) {
            log.info(e.getMessage(), e);
        }
        
        return null;
    }
    
    /**
     * 保存EurekaInstanceConfigBean 生成的instanceId
     * @param instanceId
     */
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getWorkerIdByString() {
        return getWorkerIdByString(0);
    }

    /**
     *
     * @param index 起点
     * @return
     */
    public String getWorkerIdByString(int index) {
        for (int i = index; i <= 255; ++i) {
            String id = Integer.toString(i);

            if (null == getInstanceId(id)) {
                return id;
            }
        }
        return null;
    }

    public InstanceInfo getInstanceInfoInRegistry(String appName, String id) {
        try {
            EurekaHttpResponse<InstanceInfo> ret = eurekaHttpClient.getInstance(appName, id);
            if (ret.getStatusCode() == 200) {
                InstanceInfo info = ret.getEntity();
                if (null != info) {
                    return info;
                }
            }    
        } catch( Exception e ) {
            log.info(e.getMessage(), e);
        }
        return null;
    }

    public void setWorkerId(long workerid) {
        this.workerId.set(workerid);
    }
    
    public Long getWorkerId() {
        return workerId.get();
    }

}
