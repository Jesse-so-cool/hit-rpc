/**
 * 
 */
package com.jesse.netty.codec;

import com.jesse.serialization.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

/**
 * My Encoder.
 * 
 * @since 1.0.0 2019年12月16日
 * @author <a href="https://waylau.com">Way Lau</a>
 */
public class MyEncoder extends MessageToByteEncoder {

	private Class<?> genericClass;

	private Serialization serialization;

	public MyEncoder(Class<?> genericClass,Serialization serialization) {
		this.genericClass = genericClass;
		this.serialization = serialization;
	}
	@Override
	protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
		if (genericClass.isInstance(o)) {
			byte[] data = serialization.serialize(o);
			byteBuf.writeInt(data.length);
			byteBuf.writeBytes(data);
		}
	}
}