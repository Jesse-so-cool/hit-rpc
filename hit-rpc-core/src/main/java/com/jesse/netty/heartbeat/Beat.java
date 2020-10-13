package com.jesse.netty.heartbeat;

import com.jesse.entity.RpcRequest;

public final class Beat {

    public static final int BEAT_CLIENT = 5;
    public static final int BEAT_SERVER = 3 * BEAT_CLIENT;
    public static final String BEAT_ID = "BEAT_PING_PONG";

    public static RpcRequest BEAT_REQUEST = new RpcRequest();

    static {
        BEAT_REQUEST.setRequestId(BEAT_ID);
    }

}
