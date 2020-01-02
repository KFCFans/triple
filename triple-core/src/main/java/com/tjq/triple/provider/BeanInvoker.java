package com.tjq.triple.provider;

import lombok.AllArgsConstructor;

/**
 * 本地方法反射调用
 *
 * @author tjq
 * @since 2020/1/2
 */
public class BeanInvoker {

    private final Object bean;

    public BeanInvoker(Object bean) {
        this.bean = bean;

        Class<?> beanClass = bean.getClass();

    }
}
