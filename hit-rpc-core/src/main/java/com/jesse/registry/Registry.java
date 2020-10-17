package com.jesse.registry;

import com.alibaba.nacos.api.exception.NacosException;

import java.util.List;

/**
 * 服务注册接口
 *
 * @author huangyong
 * @since 1.0.0
 */
public interface Registry {

    /**
     * 注册服务名称与服务地址
     *
     * @param serviceName    服务名称
     */
    void register(String serviceName, String ip, int port) throws NacosException;

    /**
     * 根据服务名称查找服务地址
     *
     * @param serviceName 服务名称
     * @return 服务地址
     */
    List<String> discover(String serviceName) throws NacosException;

    void deregisterInstance(String serviceName, String ip, int port) throws NacosException;

}