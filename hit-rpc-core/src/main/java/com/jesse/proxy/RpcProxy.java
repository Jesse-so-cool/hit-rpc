package com.jesse.proxy;

import net.sf.cglib.proxy.Enhancer;

/**
 * @author jesse hsj
 * @date 2020/10/11
 */
public class RpcProxy<T> {

    public T create(T delegate) {
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(new CglibInterceptor(delegate));
        enhancer.setInterfaces(new Class[]{delegate.getClass()});
        return (T) enhancer.create();
    }

}
