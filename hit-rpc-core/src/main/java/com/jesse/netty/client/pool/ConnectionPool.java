package com.jesse.netty.client.pool;

import com.jesse.netty.client.NettyClientHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TODO
 *
 * @author jesse hsj
 * @date 2020/10/15 16:15
 */
@Slf4j
public class ConnectionPool {

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 8,
            600L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1000));

    private Map<String, NettyClientHandler> connectedClients = new ConcurrentHashMap<>();
    //private CopyOnWriteArraySet<RpcProtocol> rpcProtocolSet = new CopyOnWriteArraySet<>();
    private ReentrantLock lock = new ReentrantLock();
    private Condition connected = lock.newCondition();
    private long waitTimeout = 5000;

    private static class SingletonHolder {
        private static final ConnectionPool instance = new ConnectionPool();
    }

    public static ConnectionPool getInstance() {
        return SingletonHolder.instance;
    }

    private void updateConnectedClients() {

    }


}
