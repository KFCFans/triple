package com.tjq.triple.transport.netty4.codec;

import com.tjq.triple.protocol.TripleProtocol;
import com.tjq.triple.serialize.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * encoder
 *
 * @author tjq
 * @since 2020/1/3
 */
public class NettyEncoder extends MessageToByteEncoder<TripleProtocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx, TripleProtocol msg, ByteBuf out) throws Exception {

        // 序列化
        byte[] data = SerializerFactory.getSerializer().serialize(msg.getData());

        // 协议头
        out.writeShort(msg.getMagic());
        out.writeByte(msg.getExtension());
        out.writeByte(msg.getCmd());
        out.writeInt(data.length);

        // 数据
        out.writeBytes(data);
    }
}
