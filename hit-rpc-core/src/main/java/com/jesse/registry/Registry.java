package com.jesse.registry;

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
     * @param serviceAddress 服务地址
     */
    void register(String serviceName, String serviceAddress);

    /**
     * 根据服务名称查找服务地址
     *
     * @param serviceName 服务名称
     * @return 服务地址
     */
    List<String> discover(String serviceName);
}