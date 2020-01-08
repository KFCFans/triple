package com.tjq.triple.serialize.java;

import com.tjq.triple.common.exception.TripleRpcException;
import com.tjq.triple.protocol.TripleProtocol;
import com.tjq.triple.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * Java 序列化
 *
 * @author tjq
 * @since 2020/1/4
 */
@Slf4j
public class JavaSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T obj) {
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
        ) {
            oos.writeObject(obj);
            oos.flush();
            return bos.toByteArray();
        }catch (IOException e) {
            log.error("[Triple] serialize by java failed.", e);
            throw new TripleRpcException("serialize by java failed", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (T) ois.readObject();
        }catch (Exception e) {
            log.error("[Triple] deserialize by java failed.", e);
            throw new TripleRpcException("deserialize by java failed", e);
        }
    }
}
