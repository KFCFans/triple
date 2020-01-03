package com.tjq.triple.provider.invoker;

import lombok.AllArgsConstructor;

import java.lang.reflect.Method;

/**
 * 利用 JDK 原生反射进行调用
 *
 * @author tjq
 * @since 2020/1/3
 */
@AllArgsConstructor
public class JDKReflectInvoker implements LocalInvoker {

    private final Object bean;

    @Override
    public Object invoke(String methodName, Class<?>[] parameterTypes, Object[] parameters) throws ReflectiveOperationException {

        Class<?> beanClass = bean.getClass();
        Method method = beanClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(bean, parameters);

    }

}
