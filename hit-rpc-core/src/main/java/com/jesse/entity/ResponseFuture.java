package com.jesse.entity;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * TODO
 *
 * @author jesse hsj
 * @date 2020/10/13 16:21
 */
public class ResponseFuture implements Future<RpcResponse> {
    private final RpcRequest request;
    private RpcResponse response = null;
    private final Object lock = new Object();
    private boolean isDone = false;

    public void response(RpcResponse response) {
        synchronized (lock) {
            this.response = response;
            this.isDone = true;
        }
    }

    public ResponseFuture(RpcRequest request) {
        this.request = request;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return this.isDone;
    }

    @Override
    public RpcResponse get() throws InterruptedException, ExecutionException {
        if (isDone) {
            return response;
        }
        synchronized (lock) {
            lock.wait();
        }
        return response;
    }

    @Override
    public RpcResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
}
