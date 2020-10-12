package com.jesse.netty.server;

import com.jesse.entity.RpcRequest;
import com.jesse.entity.RpcResponse;
import com.jesse.reflect.CglibRefletUtils;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author jesse hsj
 * @date 2020/10/10
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private final Map<String, Object> exportServices;

    public NettyServerHandler(Map<String, Object> exportServices) {
        this.exportServices = exportServices;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        /*
         * todo 异步处理
         */
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(rpcRequest.getRequestId());
        rpcResponse.setValue(invoke(rpcRequest));
        // 写入 RPC 响应对象并自动关闭连接
        ctx.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE);//直接关闭 考虑是否设计为长连接
    }

    private Object invoke(RpcRequest rpcRequest) throws Exception {
        String version = rpcRequest.getVersion();
        String serviceName = rpcRequest.getInterfaceName();
        Object o = exportServices.get(CglibRefletUtils.getServiceKey(serviceName, version));
        if (o == null) {
            log.error("Can not find service ,interface name: {} and version: {}", serviceName, version);
            return null;
        }
        //cglib反射调用
        return CglibRefletUtils.invoke(o, rpcRequest.getMethodName(), rpcRequest.getParameterTypes(), rpcRequest.getParameters());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
