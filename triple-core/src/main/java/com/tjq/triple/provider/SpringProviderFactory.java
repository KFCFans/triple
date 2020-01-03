package com.tjq.triple.provider;

import com.tjq.triple.annotation.TripleProvider;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;

import java.util.Collection;

/**
 * 支持 Spring 框架
 *
 * @author tjq
 * @since 2020/1/3
 */
@AllArgsConstructor
public class SpringProviderFactory extends ProviderFactory {

    private final ApplicationContext ctx;

    @Override
    protected Collection<Object> providerBeans() {
        return ctx.getBeansWithAnnotation(TripleProvider.class).values();
    }
}
