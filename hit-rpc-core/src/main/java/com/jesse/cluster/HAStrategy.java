package com.jesse.cluster;

/**
 * @author jesse hsj
 * @date 2020/10/10
 */
public interface HAStrategy {
    void setUrl(String url);
    //Response call(Request connect, LoadBalance loadBalance);//<1>
}
