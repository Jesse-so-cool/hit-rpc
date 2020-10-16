package com.jesse.sample;

import com.jesse.proxy.RpcProxy;
import com.jesse.service.HitService;
import org.slf4j.LoggerFactory;

/**
 * TODO
 *
 * @author jesse hsj
 * @date 2020/10/12 10:16
 */
public class Client {

    public static void main(String[] args) throws InterruptedException {
        HitService hitService = new RpcProxy<HitService>().create(HitService.class);
        try {
            System.out.println(hitService.hit());
        } catch (Throwable e) {
            LoggerFactory.getLogger(Client.class).error(e.getMessage(), e);
        }

//        for (int i = 0; i < 10000; i++) {
//            System.out.println(hitService.hit(i + ""));
//        }
//        Thread.sleep(10000);
    }

}
