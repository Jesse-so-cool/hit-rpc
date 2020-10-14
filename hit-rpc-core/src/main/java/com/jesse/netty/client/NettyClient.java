package com.jesse.netty.client;

import com.jesse.entity.RpcRequest;
import com.jesse.entity.RpcResponse;
import com.jesse.netty.codec.MyCodec;
import com.jesse.netty.codec.MyDecoder;
import com.jesse.netty.codec.MyEncoder;
import com.jesse.netty.heartbeat.Beat;
import com.jesse.netty.heartbeat.HeartbeatClientHandler;
import com.jesse.netty.heartbeat.HeartbeatServerHandler;
import com.jesse.serialization.KryoSerialization;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author jesse hsj
 * @date 2020/10/11
 */
public class NettyClient {

    private static Map<String, NettyClientHandler> map = new ConcurrentHashMap<>();

    private final String host;
    private final int port;

    public static NettyClientHandler getClientHandler(String address) throws InterruptedException {
        if (map.containsKey(address)) {
            return map.get(address);
        }
        connect(address);
        return map.get(address);
    }

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void connect(String address) throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            NettyClientHandler nettyClientHandler = new NettyClientHandler();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    //.option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    //客户端只需要设置read超时
                                    .addLast(new IdleStateHandler(Beat.BEAT_CLIENT, 0, 0, TimeUnit.SECONDS))    // beat N, close if fail
                                    .addLast(new HeartbeatClientHandler())
                                    .addLast(new MyEncoder(RpcRequest.class, new KryoSerialization()))
                                    .addLast(new MyDecoder(RpcResponse.class, new KryoSerialization()))
                                    .addLast(nettyClientHandler);
                        }
                    });
            String[] split = address.split(":");
            // 启动客户端
            InetSocketAddress inetSocketAddress = new InetSocketAddress(split[0], Integer.parseInt(split[1]));
            ChannelFuture channelFuture = bootstrap.connect(inetSocketAddress);
            channelFuture.addListener((ChannelFutureListener) channelFuture1 -> {
                if (channelFuture.isSuccess()) {
                    NettyClientHandler handler = channelFuture1.channel().pipeline().get(NettyClientHandler.class);
                    map.put(address, handler);
                    RpcRequest request = new RpcRequest();
                    request.setRequestId("qweqwe");
                    handler.request(request);
                }
            }).sync();
            //RpcRequest request = new RpcRequest();
            //request.setRequestId("qweqwe");
            //map.get(address).request(request);
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
