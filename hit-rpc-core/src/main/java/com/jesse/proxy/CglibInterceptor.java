package com.jesse.proxy;

import com.jesse.cluster.LoadBalance;
import com.jesse.cluster.loadbalance.RoundRobinLoadBalance;
import com.jesse.entity.ResponseFuture;
import com.jesse.entity.RpcRequest;
import com.jesse.entity.RpcResponse;
import com.jesse.netty.client.NettyClient;
import com.jesse.netty.client.NettyClientHandler;
import com.jesse.reflect.CglibRefletUtils;
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

    private String version = null;

    public CglibInterceptor(String version) {
        this.version = version;
    }

    CglibInterceptor() {

    }

    public void setLoadBalance(LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
    }

    @Override
    public Object intercept(Object object, Method method, Object[] objects,
                            MethodProxy methodProxy) throws Throwable {
        Class<?> declaringClass = method.getDeclaringClass();
        List<String> ls = new RegistryCenter().discover(CglibRefletUtils.getServiceKey(declaringClass.getName(),this.version));

        //负载均衡策略
        String address = loadBalance.select(ls,CglibRefletUtils.getServiceKey(declaringClass.getName(),this.version));
        RpcRequest request = new RpcRequest();

        request.setRequestId(UUID.randomUUID().toString());
        request.setInterfaceName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(objects);
        request.setVersion(this.version);
        //map.getHandler.request
        NettyClientHandler client = NettyClient.getInstance().getClientHandler(address);
        long l = System.currentTimeMillis();
        ResponseFuture future = client.request(request);
        System.out.println(System.currentTimeMillis() - l);

        return future.get().getValue();
    }

}
