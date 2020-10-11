/**
 * Welcome to https://waylau.com
 */
package com.jesse.netty.codec;

import com.jesse.entity.RpcRequest;
import com.jesse.entity.RpcResponse;
import com.jesse.serialization.KryoSerialization;
import io.netty.channel.CombinedChannelDuplexHandler;

/**
 * My Codec.
 * 
 * @since 1.0.0 2019年12月17日
 * @author <a href="https://waylau.com">Way Lau</a>
 */
public class MyCodec extends CombinedChannelDuplexHandler<MyDecoder, MyEncoder> {
	public MyCodec() {
		super(new MyDecoder(RpcResponse.class, new KryoSerialization()), new MyEncoder(RpcRequest.class,new KryoSerialization()));
	}

}
