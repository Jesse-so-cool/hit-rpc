package com.jesse.netty.server;

import com.jesse.entity.RpcRequest;
import com.jesse.entity.RpcResponse;
import com.jesse.netty.codec.MyCodec;
import com.jesse.netty.codec.MyDecoder;
import com.jesse.netty.codec.MyEncoder;
import com.jesse.serialization.KryoSerialization;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jesse hsj
 * @date 2020/10/10
 */
public class NettyServer {

    private Map<String, Object> exportServices;


//    public static void main(String[] args) throws Exception {
//        new NettyServer(8080).start();
//    }

    private int port;

    public NettyServer(int port, Map<String, Object> exportServices) {
        this.port = port;
        this.exportServices = exportServices;
    }

    public void start() throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            // 添加日志
                            ch.pipeline().addLast("logging", new LoggingHandler(LogLevel.INFO))
                                    .addLast(new MyDecoder(RpcRequest.class, new KryoSerialization()))
                                    .addLast(new MyEncoder(RpcResponse.class, new KryoSerialization())) // 将 RPC 请求进行编码（为了发送请求）
                                    .addLast(new NettyServerHandler(exportServices));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();

            System.out.println("Server start listen at " + port);

            f.channel().closeFuture().sync();

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
