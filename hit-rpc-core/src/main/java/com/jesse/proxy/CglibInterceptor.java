package com.jesse.proxy;

import com.jesse.cluster.LoadBalance;
import com.jesse.cluster.loadbalance.RoundRobinLoadBalance;
import com.jesse.entity.ResponseFuture;
import com.jesse.entity.RpcRequest;
import com.jesse.entity.RpcResponse;
import com.jesse.netty.client.NettyClient;
import com.jesse.netty.client.NettyClientHandler;
import com.jesse.registry.RegistryCenter;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

/**
 * @author jesse hsj
 * @date 2020/10/11
 */
public class CglibInterceptor implements MethodInterceptor {

    //final Object delegate;

    private LoadBalance loadBalance = new RoundRobinLoadBalance();

    CglibInterceptor() {

    }

    public void setLoadBalance(LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
    }

    @Override
    public Object intercept(Object object, Method method, Object[] objects,
                            MethodProxy methodProxy) throws Throwable {
        Class<?> declaringClass = method.getDeclaringClass();
        List<String> ls = new RegistryCenter().discover(declaringClass.getName());

        //负载均衡策略
        String address = loadBalance.select(ls,declaringClass.getName());
        String[] split = address.split(":");
        NettyClient nettyClient = new NettyClient(split[0], Integer.parseInt(split[1]));
        RpcRequest request = new RpcRequest();

        request.setRequestId(UUID.randomUUID().toString());
        request.setInterfaceName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(objects);

        //map.getHandler.request
        NettyClientHandler client = NettyClient.getClientHandler(address);
        ResponseFuture future = client.request(request);
        return future.get();
    }

}
