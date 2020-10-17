package com.jesse.proxy;

import net.sf.cglib.proxy.Enhancer;

/**
 * @author jesse hsj
 * @date 2020/10/11
 */
public class RpcProxy<T> {

    public T create(Class<T> clazz,String version) {
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(new CglibInterceptor(version));
        enhancer.setInterfaces(new Class[]{clazz});
        return (T) enhancer.create();
    }

}
