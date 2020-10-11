package com.jesse.netty.client;

import com.jesse.entity.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j;

/**
 * @author jesse hsj
 * @date 2020/10/11
 */
@Log4j
public class NettyClientHandler  extends SimpleChannelInboundHandler<RpcResponse> {

    private RpcResponse rpcResponse;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {

        this.rpcResponse = rpcResponse;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exceptionCaught",cause);
        ctx.close();
    }

    public RpcResponse getRpcResponse() {
        return rpcResponse;
    }
}
