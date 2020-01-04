package com.tjq.triple.bootstrap.annotation;

import java.lang.annotation.*;

/**
 * 声明为 RPC 服务消费者
 *
 * @author tjq
 * @since 2020/1/2
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface TripleConsumer {

    /**
     * Interface class, default value is void.class
     */
    Class<?> interfaceClass() default void.class;

    /**
     * Interface class name, default value is empty string
     */
    String interfaceName() default "";

    /**
     * Service version, default value is empty string
     */
    String version() default "";

    /**
     * Service group, default value is empty string
     */
    String group() default "";

    /**
     * Timeout value for service invocation, default value is 0
     */
    int timeout() default 0;
}
