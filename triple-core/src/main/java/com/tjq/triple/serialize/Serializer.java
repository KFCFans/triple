package com.tjq.triple.serialize;

/**
 * 线程安全的序列化器
 *
 * @author tjq
 * @since 2020/1/3
 */
public interface Serializer {

    /**
     * 线程安全的 序列化
     */
    <T> byte[] serialize(T obj);

    /**
     * 线程安全的 反序列化
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
