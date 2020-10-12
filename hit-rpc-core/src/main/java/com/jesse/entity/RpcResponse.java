package com.jesse.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 长度+body
 * @author jesse hsj
 * @date 2020/10/10
 */
@Data
public class RpcResponse implements Serializable {
    private String requestId;
    private Object value;
    private Exception exception;


    private long processTime;
    private int timeout;
    private Map<String, String> attachments;// rpc 协议版本兼容时可以回传一些额外的信息
    private byte rpcProtocolVersion;
}
