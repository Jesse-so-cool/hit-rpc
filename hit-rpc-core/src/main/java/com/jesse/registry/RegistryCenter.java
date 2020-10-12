package com.jesse.registry;

import java.util.Arrays;
import java.util.List;

/**
 * @author jesse hsj
 * @date 2020/10/11
 */
public class RegistryCenter implements Registry{
    @Override
    public void register(String serviceName, String serviceAddress) {

    }

    @Override
    public List<String> discover(String serviceName) {
        return Arrays.asList("localhost:8080");
    }
}
