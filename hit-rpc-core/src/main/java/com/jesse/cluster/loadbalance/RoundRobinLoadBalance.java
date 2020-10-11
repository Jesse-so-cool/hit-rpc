package com.jesse.cluster.loadbalance;

import com.jesse.cluster.LoadBalance;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jesse hsj
 * @date 2020/10/10
 */
public class RoundRobinLoadBalance implements LoadBalance {
    public final static Map<String, AtomicInteger> roundRobinMap = new ConcurrentHashMap<>();


    @Override
    public String select(List<String> addressList, String serviceName) {
        if (addressList == null || addressList.size() <= 0) {
            throw new RuntimeException("addressList is empty..");
        }
        AtomicInteger atomicInteger = roundRobinMap.get(serviceName);
        if (atomicInteger == null) {
            atomicInteger = new AtomicInteger(0);
            roundRobinMap.put(serviceName,atomicInteger);
        }

        return addressList.get(atomicInteger.getAndIncrement() % addressList.size());
    }
}
