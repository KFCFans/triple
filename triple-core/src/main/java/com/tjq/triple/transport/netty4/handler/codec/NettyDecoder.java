package com.tjq.triple.transport.netty4.handler.codec;

import com.tjq.triple.protocol.TripleProtocol;
import com.tjq.triple.protocol.TripleRpcCMD;
import com.tjq.triple.protocol.TripleTransportObject;
import com.tjq.triple.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * decoder
 *
 * @author tjq
 * @since 2020/1/3
 */
@AllArgsConstructor
public class NettyDecoder extends ByteToMessageDecoder {

    private Serializer serializer;
    // 协议首部定长 8字节
    private static final int PROTOCOL_HEAD_LENGTH = 8;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < PROTOCOL_HEAD_LENGTH) {
            return;
        }
        in.markReaderIndex();

        short magic = in.readShort();
        byte ext = in.readByte();
        byte cmd = in.readByte();
        int length = in.readInt();

        // 如果此时发生粘包，后面的 RPC 请求也会被丢弃
        if (magic != TripleProtocol.TRIPLE_PROTOCOL_MAGIC) {
            in.skipBytes(in.readableBytes());
        }

        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[length];
        in.readBytes(data);

        // 序列化
        Class<? extends TripleTransportObject> clazz = TripleRpcCMD.of(cmd).getClazz();
        out.add(serializer.deserialize(data, clazz));
    }
}
