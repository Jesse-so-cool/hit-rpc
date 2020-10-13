package com.jesse.netty.client;

import com.jesse.entity.ResponseFuture;
import com.jesse.entity.RpcRequest;
import com.jesse.entity.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jesse hsj
 * @date 2020/10/11
 */
@Slf4j
public class NettyClientHandler  extends SimpleChannelInboundHandler<RpcResponse> {

    private volatile Channel channel;

    private ConcurrentHashMap<String, ResponseFuture> futureMap = new ConcurrentHashMap<>();

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        ResponseFuture future = futureMap.get(rpcResponse.getRequestId());
        future.response(rpcResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exceptionCaught",cause);
        ctx.close();
    }

    public ResponseFuture request(RpcRequest request) {
        ResponseFuture responseFuture = new ResponseFuture(request);
        futureMap.put(request.getRequestId(), responseFuture);
        try {
            channel.writeAndFlush(request).sync();
        } catch (InterruptedException e) {
            log.error("Send request exception: " + e.getMessage());
        }

        return responseFuture;
    }


}
