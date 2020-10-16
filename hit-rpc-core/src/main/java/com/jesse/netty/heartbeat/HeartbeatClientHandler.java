package com.jesse.netty.heartbeat;

import com.jesse.entity.RpcRequest;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO
 *
 * @author jesse hsj
 * @date 2020/10/13 11:11
 */
@Slf4j
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
        channel.writeAndFlush(Beat.BEAT_REQUEST);
        log.debug("client send heartbeat! " + channel.isActive());
    }
}
