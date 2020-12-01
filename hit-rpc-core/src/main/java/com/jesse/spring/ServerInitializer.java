package com.jesse.spring;

import com.alibaba.nacos.api.exception.NacosException;
import com.jesse.annotation.HitService;
import com.jesse.config.Properties;
import com.jesse.netty.server.NettyServer;
import com.jesse.reflect.CglibRefletUtils;
import com.jesse.registry.RegistryCenter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author jesse hsj
 * @date 2020/10/12 15:35
 */
@Slf4j
public class ServerInitializer extends NettyServer implements ApplicationContextAware, InitializingBean, DisposableBean {

    public InetAddress addr;

    public RegistryCenter registryCenter;

    public void setRegistryCenterAddr(String addr) {
        this.registryCenter = new RegistryCenter(addr);
    }

    {
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void exportServices(Object o, String version) throws NacosException {
        exportServices.put(CglibRefletUtils.getServiceKey(o.getClass().getInterfaces()[0].getName(), version), o);
        registryCenter.register(CglibRefletUtils.getServiceKey(o.getClass().getInterfaces()[0].getName(), version), addr.getHostAddress(), port);
    }

    private ApplicationContext applicationContext = null;

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(HitService.class);
        if (beans != null && !beans.isEmpty()) {
            for (Object bean : beans.values()) {
                HitService annotation = bean.getClass().getAnnotation(HitService.class);
                String version = annotation.version();
                exportServices.put(CglibRefletUtils.getServiceKey(bean.getClass().getInterfaces()[0].getName(), version), bean);
            }
        }


    }


}
