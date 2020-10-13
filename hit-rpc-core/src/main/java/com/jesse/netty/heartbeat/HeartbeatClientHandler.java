package com.jesse.netty.heartbeat;

import com.jesse.entity.RpcRequest;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * TODO
 *
 * @author jesse hsj
 * @date 2020/10/13 11:11
 */
public class HeartbeatClientHandler extends ChannelInboundHandlerAdapter {

    private Channel channel;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            channel = ctx.channel();
            // send heartbeat
            sendHeartBeat();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    private void sendHeartBeat() {
        channel.writeAndFlush(Beat.BEAT_CLIENT);
        System.out.println("client send heartbeat! ");
    }
}
