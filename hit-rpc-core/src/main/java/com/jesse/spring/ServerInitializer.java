package com.jesse.spring;

import com.alibaba.nacos.api.exception.NacosException;
import com.jesse.annotation.HitService;
import com.jesse.config.Properties;
import com.jesse.netty.server.NettyServer;
import com.jesse.reflect.CglibRefletUtils;
import com.jesse.registry.RegistryCenter;
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
public class ServerInitializer implements ApplicationContextAware, InitializingBean, DisposableBean {

    static InetAddress addr;

    private static int port = 23333;

    static {
        try {
            port = System.getProperty("hit.rpc.port") == null ? port : Integer.parseInt(System.getProperty("hit.rpc.port"));
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Object> exportServices = new HashMap<String, Object>();


    public void exportServices(Object o, String version) throws NacosException {
        exportServices.put(CglibRefletUtils.getServiceKey(o.getClass().getInterfaces()[0].getName(), version), o);
        new RegistryCenter().register(CglibRefletUtils.getServiceKey(o.getClass().getInterfaces()[0].getName(), version), addr.getHostAddress(), port);
    }

    private ApplicationContext applicationContext = null;

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (applicationContext == null) {
            NettyServer nettyServer = new NettyServer(port, exportServices);
            nettyServer.start();
            return;
        }

        Properties properties = (Properties) applicationContext.getBean("properties");
        if (properties == null || properties.getPort() == null) {
            log.info("properties error...");
        }
        NettyServer nettyServer = new NettyServer(properties.getPort(), exportServices);
        nettyServer.start();
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
