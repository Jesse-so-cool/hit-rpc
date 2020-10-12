package com.jesse.spring;

import com.jesse.annotation.HitService;
import com.jesse.config.Properties;
import com.jesse.netty.server.NettyServer;
import com.jesse.reflect.CglibRefletUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

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

    private Map<String, Object> exportServices = new HashMap<String, Object>();

    public void exportServices(Object o, String version) {
        exportServices.put(CglibRefletUtils.getServiceKey(o.getClass().getInterfaces()[0].getName(), version), o);
    }

    private ApplicationContext applicationContext = null;

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (applicationContext == null) {
            NettyServer nettyServer = new NettyServer(8080, exportServices);
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
