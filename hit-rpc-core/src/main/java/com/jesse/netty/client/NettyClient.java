package com.jesse.netty.client;

import com.jesse.entity.RpcRequest;
import com.jesse.entity.RpcResponse;
import com.jesse.netty.codec.MyDecoder;
import com.jesse.netty.codec.MyEncoder;
import com.jesse.netty.heartbeat.Beat;
import com.jesse.netty.heartbeat.HeartbeatClientHandler;
import com.jesse.serialization.KryoSerialization;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author jesse hsj
 * @date 2020/10/11
 */
public class NettyClient {
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 8,
            600L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1000));

    private static Map<String, NettyClientHandler> pool = new ConcurrentHashMap<>();

    private static Object lock = new Object();

    private static class SingletonHolder {
        private static final NettyClient instance = new NettyClient();
    }

    public static NettyClient getInstance() {
        return SingletonHolder.instance;
    }

    public NettyClientHandler getClientHandler(String address) throws Exception {
        connect(address);
        synchronized (lock) {
            while (pool.values().size() == 0) {
                try {
                    lock.wait(500);
                } catch (InterruptedException ee) {

                }
            }
        }
        NettyClientHandler handler = pool.get(address);
        if (handler != null) {
            return handler;
        } else {
            throw new Exception("Can not get available connection");
        }
    }

    public void connect(String address) throws InterruptedException {
        if(pool.containsKey(address))
            return;
        Thread thread = new Thread(() -> {

            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline()
                                        //客户端只需要设置read超时
                                        .addLast(new IdleStateHandler(Beat.BEAT_CLIENT, 0, 0, TimeUnit.SECONDS))    // beat N, close if fail
                                        .addLast(new HeartbeatClientHandler())
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
                ChannelFuture channelFuture = bootstrap.connect(split[0], Integer.parseInt(split[1]));
                channelFuture.addListener((ChannelFutureListener) channelFuture1 -> {
                    if (channelFuture.isSuccess()) {
                        NettyClientHandler handler = channelFuture1.channel().pipeline().get(NettyClientHandler.class);
                        pool.put(address, handler);
                        signalAvailableHandler();
                        //handler.request(new RpcRequest().setRequestId("aaaaaaaaaaaaaaaaaaaaa"));
                    } else {
                        System.out.println("出事,连接失败");
                    }
                }).sync();
//              等待连接关闭
                channelFuture.channel().closeFuture().sync();//去掉这句代码会有大问题 TODO 需要学习下
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        });
        threadPoolExecutor.submit(thread);
    }

    private static void signalAvailableHandler() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
