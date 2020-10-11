package com.jesse.serialization;

import java.io.IOException;

/**
 * @author jesse hsj
 * @date 2020/10/10
 */
public interface Serialization {

    byte[] serialize(Object obj) throws IOException;

    <T> T deserialize(byte[] bytes, Class<T> clz) throws IOException;
}
