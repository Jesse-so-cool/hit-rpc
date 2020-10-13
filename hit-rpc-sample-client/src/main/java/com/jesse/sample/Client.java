package com.jesse.sample;

import com.jesse.proxy.RpcProxy;
import com.jesse.service.HitService;

/**
 * TODO
 *
 * @author jesse hsj
 * @date 2020/10/12 10:16
 */
public class Client {

    public static void main(String[] args) {
        HitService hitService = new RpcProxy<HitService>().create(HitService.class);
        System.out.println(hitService.hit());

        for (int i = 0; i < 100; i++) {
            System.out.println(hitService.hit());
        }
    }

}
