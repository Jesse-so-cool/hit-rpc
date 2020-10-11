package com.jesse.cluster;

import com.jesse.entity.RpcRequest;

import java.util.List;
import java.util.Set;

/**
 * @author jesse hsj
 * @date 2020/10/10
 */
public interface LoadBalance {

    String select(List<String> addressList,String serviceName);//<1>

}
