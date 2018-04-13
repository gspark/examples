package com.shrill.config;

import static org.springframework.cloud.commons.util.IdUtils.getDefaultInstanceId;

import com.shrill.service.EurekaWorkerIdRegister;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

@Configuration
public class DiscoveryClientConfiguration {

    @Value("${server.port:${SERVER_PORT:${PORT:8080}}}")
    int nonSecurePort;

    @Value("${management.port:${MANAGEMENT_PORT:${server.port:${SERVER_PORT:${PORT:8080}}}}}")
    int managementPort;

    @Value("${eureka.instance.hostname:${EUREKA_INSTANCE_HOSTNAME:}}")
    String hostname;

    @Autowired
    ConfigurableEnvironment env;

    @Autowired
    private EurekaWorkerIdRegister workIdRegister;

    @Bean
    public EurekaInstanceConfigBean eurekaInstanceConfigBean() {
        EurekaInstanceConfigBean instance = new EurekaInstanceConfigBean(
            new InetUtils(new InetUtilsProperties()));
        instance.setNonSecurePort(this.nonSecurePort);
        instance.setInstanceId(getDefaultInstanceId(this.env));
        if (this.managementPort != this.nonSecurePort && this.managementPort != 0) {
            if (StringUtils.hasText(this.hostname)) {
                instance.setHostname(this.hostname);
            }
            String scheme = instance.getSecurePortEnabled() ? "https" : "http";
            instance.setStatusPageUrl(scheme + "://" + instance.getHostname() + ":"
                + this.managementPort + instance.getStatusPageUrlPath());
            instance.setHealthCheckUrl(scheme + "://" + instance.getHostname() + ":"
                + this.managementPort + instance.getHealthCheckUrlPath());
        }

        setInstanceConfigBean(instance);

        return instance;
    }

    private void setInstanceConfigBean(EurekaInstanceConfigBean instance) {
        String instanceId = instance.getInstanceId();
        Map<String, String> metadataMap = instance.getMetadataMap();
        if (null == metadataMap) {
            metadataMap = new ConcurrentHashMap<>();
            instance.setMetadataMap(metadataMap);
        }
        metadataMap.put(EurekaWorkerIdRegister.INSTANCEID_KEY, instanceId);

        String id = workIdRegister.getWorkerIdByString();
        id = "0";
        if (null != id) {
            instance.setInstanceId(id);
            workIdRegister.setInstanceId(instanceId);
            workIdRegister.setWorkerId(Long.parseLong(id));
        }
    }

}
