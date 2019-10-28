package com.shrill.service;


import static org.slf4j.LoggerFactory.getLogger;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.PostConstruct;

import com.shrill.util.Proxy;
import com.shrill.util.Reflect;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent;
import org.springframework.cloud.netflix.eureka.CloudEurekaClient;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
public class InstanceRegisterEventListener implements ApplicationListener<InstanceRegisteredEvent> {

    private static final Logger log = getLogger(InstanceRegisterEventListener.class);

    private ThreadPoolExecutor ex;

    private final ReentrantLock lock = new ReentrantLock();

    private final Condition available = lock.newCondition();

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private EurekaWorkerIdRegister workIdRegister;

    @Override
    public void onApplicationEvent(InstanceRegisteredEvent event) {

        Object config  = event.getConfig();
        if( !(config instanceof EurekaInstanceConfigBean)){
            return;
        }

        final EurekaInstanceConfigBean eic = (EurekaInstanceConfigBean)event.getConfig();

        ex.execute(() -> {
            try {
                log.info("InstanceId:{}", workIdRegister.getInstanceId());
                lock.lockInterruptibly();
                long nanos = TimeUnit.MILLISECONDS.toNanos(400);

                InstanceInfo info;
                String instanceId = eic.getInstanceId();
                do {
                    info = workIdRegister
                        .getInstanceInfoInRegistry(eic.getAppname(), instanceId);
                    if( null == info ) {
                        nanos = available.awaitNanos(nanos);
                    } else{
                        Map<String, String> metadataMap = info.getMetadata();
                        if (metadataMap.containsKey(EurekaWorkerIdRegister.INSTANCEID_KEY) && metadataMap.get(
                            EurekaWorkerIdRegister.INSTANCEID_KEY)
                            .equals(workIdRegister.getInstanceId())) {
                            return;
                        }

                        reRegister(eic);
                        instanceId = workIdRegister.getWorkerId().toString();
                    }
                } while (true);

            } catch (Exception e) {
                log.info(e.getMessage(),e);
            } finally {
                available.signal();
                lock.unlock();
            }
        });
    }

    private void reRegister(final EurekaInstanceConfigBean eic) throws Exception {
        EurekaClient eurekaClient = (EurekaClient) Reflect.getField(discoveryClient, "eurekaClient");
        if (null != eurekaClient) {
            CloudEurekaClient cloudEurekaClient = (CloudEurekaClient) Proxy.getTargetObject(eurekaClient);
            if (null != cloudEurekaClient) {
                cloudEurekaClient = (CloudEurekaClient) Proxy.getTargetObject(cloudEurekaClient);
                if (null != cloudEurekaClient) {
                    InstanceInfo instanceInfo = (InstanceInfo) Reflect
                        .getFieldFromSuperclass(
                            cloudEurekaClient, "instanceInfo");

                    String instanceId = workIdRegister
                        .getWorkerIdByString(Integer.valueOf(eic.getInstanceId()) + 1);
                    
                    Reflect.setField(instanceInfo, EurekaWorkerIdRegister.INSTANCEID_KEY, instanceId);

                    boolean ret = (boolean)Reflect.invokeMethodFromSuperclass(cloudEurekaClient,"register", null);
                    log.info("EurekaClient reRegister instanceId:{}, ret:{}", instanceId, ret);
                    workIdRegister.setWorkerId(Long.parseLong(instanceId));
                }
            }
        }
    }

    @PostConstruct
    private void init() {
        this.ex = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(4),
            new ThreadFactoryBuilder()
                .setNameFormat("OrderService-Executor-%d")
                .setDaemon(true)
                .build());
    }

}
