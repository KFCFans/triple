package com.tjq.triple.serialize;

/**
 * 序列化器
 *
 * @author tjq
 * @since 2020/1/3
 */
public interface Serializer {

    <T> byte[] serialize(T obj);
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
