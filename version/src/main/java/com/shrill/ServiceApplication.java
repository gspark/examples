package com.shrill;

import java.io.Closeable;
import java.io.IOException;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@EnableDiscoveryClient
@SpringBootApplication
public class ServiceApplication implements ApplicationContextAware, Closeable {

    private ApplicationContext context;

//    @Autowired
//    private DiscoveryClient discoveryClient;


    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;

    }
}

