package com.tjq.triple.bootstrap.annotation;

import java.lang.annotation.*;

/**
 * 声明为 RPC 双向服务消费者（Server端使用）
 *
 * @author tjq
 * @since 2020/1/6
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface TripleDuplexConsumer {

    /**
     * Interface class, default value is void.class
     */
    Class<?> interfaceClass() default void.class;

    /**
     * Interface class name, default value is empty string
     */
    String interfaceName() default "";

    /**
     * Timeout value for service invocation, default value is 0
     */
    int timeout() default 0;
}
