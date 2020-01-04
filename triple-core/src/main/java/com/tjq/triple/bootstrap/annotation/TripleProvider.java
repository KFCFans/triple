package com.tjq.triple.bootstrap.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 声明为 RPC 服务提供者
 *
 * @author tjq
 * @since 2020/1/2
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Component
public @interface TripleProvider {

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
    String version() default "1.0.0";

    /**
     * Service group, default value is empty string
     */
    String group() default "Triple";

    /**
     * Timeout value for service invocation, default value is 0
     */
    int timeout() default 0;
}
