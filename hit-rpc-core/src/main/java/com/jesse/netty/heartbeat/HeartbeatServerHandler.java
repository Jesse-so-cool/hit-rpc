package com.jesse.netty.heartbeat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

/**
 * @author jesse hsj
 * @date 2020/10/13 10:37
 */
public class HeartbeatServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {
//        if (evt instanceof IdleStateEvent) {
//            ctx.channel().close();
//            System.out.println("读写超时 直接断开");
//        } else {
            super.userEventTriggered(ctx, evt);
        //}
    }

}
