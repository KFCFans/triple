package com.tjq.triple.serialize.java;

import com.tjq.triple.serialize.Serializer;

/**
 * Java 序列化
 *
 * @author tjq
 * @since 2020/1/4
 */
public class JavaSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T obj) {
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return null;
    }
}
