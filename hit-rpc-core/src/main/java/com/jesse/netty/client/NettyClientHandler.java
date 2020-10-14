package com.jesse.netty.client;

import com.jesse.entity.ResponseFuture;
import com.jesse.entity.RpcRequest;
import com.jesse.entity.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
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
        log.info(channel.toString());
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
            if (!channel.isActive()) {
                log.info(channel.toString());
                System.out.println("Send isActive {} error" + request.getRequestId());
            }
            ChannelFuture channelFuture = channel.writeAndFlush(request).sync();
            if (!channelFuture.isSuccess()) {
                System.out.println("Send request {} error" + request.getRequestId());
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }

        return responseFuture;
    }


}
