package com.jesse.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jesse hsj
 * @date 2020/10/11
 */
@Slf4j
public class RegistryCenter implements Registry {

    private static NamingService naming;

    public RegistryCenter(String address) {
        try {
            naming = NamingFactory.createNamingService(address);
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    public static final String IP_PORT = ":";

    @Override
    public void register(String serviceName, String ip, int port) throws NacosException {
        naming.registerInstance(serviceName, ip, port);

    }

    @Override
    public List<String> discover(String serviceName) throws NacosException {
        List<Instance> allInstances = naming.getAllInstances(serviceName);
        List<String> result = new ArrayList<>();
        allInstances.forEach(instance -> {
            result.add(instance.getIp() + IP_PORT + instance.getPort());
        });
        return result;
    }

    @Override
    public void deregisterInstance(String serviceName, String ip, int port) throws NacosException {
        naming.deregisterInstance(serviceName, ip, port);
    }


}
