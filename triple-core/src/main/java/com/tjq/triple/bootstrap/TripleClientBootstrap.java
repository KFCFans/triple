package com.tjq.triple.bootstrap;

import com.tjq.triple.bootstrap.annotation.TripleConsumer;
import com.tjq.triple.common.exception.TripleRpcException;
import com.tjq.triple.provider.SpringProviderFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;

/**
 * triple rpc client 启动器
 *
 * @author tjq
 * @since 2020/1/6
 */
@Slf4j
public class TripleClientBootstrap extends InstantiationAwareBeanPostProcessorAdapter implements InitializingBean, ApplicationContextAware, BeanFactoryAware {
    @Override
    public void afterPropertiesSet() throws Exception {
    }


    private void init() {

    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext ctx) throws BeansException {
        // 双向RPC，故client也注册服务
        SpringProviderFactory factory = new SpringProviderFactory(ctx);
        factory.init();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

    }

    /**
     * Spring容器的大致初始化流程
     * 1. 实例化dependsOn的bean
     * 2. 创建目标 bean
     * 3. 在真正实例化前先进行预先操作，给用户一个机会来进行非正常实例化，实现该方法即可 InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation
     * 4. 真正开始实例化，开始构造mbd，可通过 MergedBeanDefinitionPostProcessor#postProcessMergedBeanDefinition 方法干预
     * 5. 填充属性阶段，先调用 InstantiationAwareBeanPostProcessor#postProcessAfterInstantiation，然后填充 @AutoWire 或 @Resource
     */
    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {

        ReflectionUtils.doWithFields(bean.getClass(), (filed) -> {
            if (!filed.isAnnotationPresent(TripleConsumer.class)) {
                return;
            }
            if (!filed.getType().isInterface()) {
                log.error("[Triple] @TripleConsumer should be used on interface");
                throw new TripleRpcException("@TripleConsumer should be used on interface");
            }

            TripleConsumer tripleConsumer = filed.getAnnotation(TripleConsumer.class);
        });


        return super.postProcessAfterInstantiation(bean, beanName);
    }
}
