package com.jesse.netty.client;

import com.jesse.entity.RpcRequest;
import com.jesse.entity.RpcResponse;
import com.jesse.netty.codec.MyCodec;
import com.jesse.netty.codec.MyDecoder;
import com.jesse.netty.codec.MyEncoder;
import com.jesse.serialization.KryoSerialization;
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
                    ch.pipeline()
                            .addLast(new MyEncoder(RpcRequest.class, new KryoSerialization())) // 将 RPC 请求进行编码（为了发送请求）
                            .addLast(new MyDecoder(RpcResponse.class, new KryoSerialization()))
                            .addLast(nettyClientHandler);
                }
            });

            // 启动客户端
            ChannelFuture f = b.connect(host, port).sync();
            Channel channel = f.channel();
            channel.writeAndFlush(rpcRequest).sync();
            channel.closeFuture().sync();
            return nettyClientHandler.getRpcResponse();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
