package com.jesse.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author jesse hsj
 * @date 2020/10/10
 */
@Accessors(chain = true)
@Data
public class RpcRequest implements Serializable {
    private String interfaceName;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
    private String requestId;
    private String version;

    private int retries = 0;
    private byte rpcProtocolVersion;
}
