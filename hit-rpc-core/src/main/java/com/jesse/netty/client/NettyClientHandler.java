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
        //RpcRequest request = new RpcRequest();
        //request.setRequestId("qweqweqweqwe");
        //ResponseFuture request1 = request(request);
        //log.info(channel.toString() + " " + this.channel.isActive());
        System.out.println("channelRegistered");

    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        System.out.println("channelUnregistered");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("channelActive");
        this.channel = ctx.channel();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.error("channelInactive");
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
//            if (!channel.isActive()) {
//                log.info(channel.toString());
//                log.error("Send is not Active ");
//            }
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
