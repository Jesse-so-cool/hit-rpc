package com.jesse.netty.server;

import com.jesse.entity.RpcRequest;
import com.jesse.entity.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * @author jesse hsj
 * @date 2020/10/10
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        RpcResponse rpcResponse = invoke(rpcRequest);
        // 写入 RPC 响应对象并自动关闭连接
        ctx.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE);
    }

    private RpcResponse invoke(RpcRequest rpcRequest) {
        return null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
