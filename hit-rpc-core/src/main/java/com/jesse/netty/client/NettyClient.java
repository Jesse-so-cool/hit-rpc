package com.jesse.netty.client;

import com.jesse.entity.RpcRequest;
import com.jesse.entity.RpcResponse;
import com.jesse.netty.codec.MyCodec;
import com.jesse.netty.codec.MyDecoder;
import com.jesse.netty.codec.MyEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author jesse hsj
 * @date 2020/10/11
 */
public class NettyClient {

    private final String host;
    private final int port;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public RpcResponse request(RpcRequest rpcRequest) throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            NettyClientHandler nettyClientHandler = new NettyClientHandler();
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast("codec", new MyCodec());
                    ch.pipeline().addLast(nettyClientHandler);
                }
            });

            // 启动客户端
            ChannelFuture f = b.connect(host, port).sync();
            Channel channel = f.channel();
            channel.writeAndFlush(rpcRequest);
            channel.closeFuture().sync();
            return nettyClientHandler.getRpcResponse();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
