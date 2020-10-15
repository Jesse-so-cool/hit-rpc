package com.jesse.netty.client;

import com.jesse.entity.ResponseFuture;
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
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jesse hsj
 * @date 2020/10/11
 */
public class NettyClient {

    private static Map<String, NettyClientHandler> map = new ConcurrentHashMap<>();

    private final String host;
    private final int port;
    private static Object lock = new Object();

    public static NettyClientHandler getClientHandler(String address) throws Exception {
        new Thread(() -> {
            try {
                connect(address);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        //int size = map.values().size();
        synchronized (lock) {
            while (map.values().size() == 0) {
                try {
                    lock.wait(500);
                } catch (InterruptedException ee) {

                }
            }
        }
        NettyClientHandler handler = map.get(address);
        if (handler != null) {
            return handler;
        } else {
            throw new Exception("Can not get available connection");
        }
    }

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void connect(String address) throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            NettyClientHandler clientHandler = new NettyClientHandler();

            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    //.option(ChannelOption.SO_KEEPALIVE, true)
                    //.option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    //客户端只需要设置read超时
                                    .addLast(new IdleStateHandler(0, 0, Beat.BEAT_CLIENT, TimeUnit.SECONDS))    // beat N, close if fail
                                    //.addLast(new HeartbeatClientHandler())
                                    .addLast(new MyEncoder(RpcRequest.class, new KryoSerialization()))
                                    .addLast(new MyDecoder(RpcResponse.class, new KryoSerialization()))
                                    .addLast(new NettyClientHandler());
                        }
                    })
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
            ;
            String[] split = address.split(":");
            // 启动客户端
            InetSocketAddress inetSocketAddress = new InetSocketAddress(split[0], Integer.parseInt(split[1]));
            ChannelFuture channelFuture = bootstrap.connect(split[0], Integer.parseInt(split[1])).sync();
            System.out.println(channelFuture.channel().isActive());
            channelFuture.addListener((ChannelFutureListener) channelFuture1 -> {
                if (channelFuture.isSuccess()) {
                    NettyClientHandler handler = channelFuture1.channel().pipeline().get(NettyClientHandler.class);
                    map.put(address, handler);
                    signalAvailableHandler();
                } else {
                    System.out.println("我失败了？？？？？？？？？？？");
                }
            });

        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    private static void signalAvailableHandler() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
