package com.jesse.hitrpc.starter.config;


// 省略 @EnableAspectJAutoProxy


import com.jesse.annotation.HitService;
import com.jesse.spring.ServerInitializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {
    @Value("${xxl-rpc.remoting.port}")
    private int port;

    @Value("${xxl-rpc.registry.xxlrpcadmin.address}")
    private String address;

    @Bean
    public ServerInitializer serverInitializer() {
        ServerInitializer initializer = new ServerInitializer();
        initializer.setPort(port);
        initializer.setRegistryCenterAddr(address);
        return initializer;
    }
}

