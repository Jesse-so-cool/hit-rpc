package com.jesse.server;

import com.jesse.netty.server.NettyServer;
import com.jesse.server.service.HitServiceImpl;
import com.jesse.service.HitService;
import com.jesse.spring.ServerInitializer;

/**
 * TODO
 *
 * @author jesse hsj
 * @date 2020/10/12 10:52
 */
public class Server {

    public static void main(String[] args) throws Exception {
        ServerInitializer initializer = new ServerInitializer();
        initializer.setPort(23333);
        HitService o = new HitServiceImpl();
        initializer.exportServices(o, "1.1.1");
        initializer.start();
    }

}
