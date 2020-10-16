package com.jesse.netty.server;

import com.jesse.entity.RpcRequest;
import com.jesse.entity.RpcResponse;
import com.jesse.netty.client.NettyClientHandler;
import com.jesse.netty.heartbeat.Beat;
import com.jesse.reflect.CglibRefletUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

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

    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);

    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.error("channelInactive");
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        if (rpcRequest.getRequestId().equals(Beat.BEAT_ID)) {
            log.debug("心跳接收成功!");
            return;
        }
        /*
         * todo 异步处理
         */
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(rpcRequest.getRequestId());
        rpcResponse.setValue(invoke(rpcRequest));
        // 写入 RPC 响应对象并自动关闭连接
        ChannelFuture future = ctx.writeAndFlush(rpcResponse);
        future.addListener((ChannelFutureListener) channelFuture1 -> {
            if (!future.isSuccess()) {
                log.error("发送到缓冲失败");
            }
        });
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
