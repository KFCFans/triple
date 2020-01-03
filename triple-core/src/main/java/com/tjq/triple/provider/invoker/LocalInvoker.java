package com.tjq.triple.provider.invoker;

/**
 * 反射调用本地对象
 *
 * @author tjq
 * @since 2020/1/3
 */
public interface LocalInvoker {

    Object invoke(String methodName, Class<?>[] parameterTypes, Object[]   parameters) throws Exception;
}
