package com.tjq.triple.provider.invoker;

import lombok.AllArgsConstructor;
import org.springframework.cglib.reflect.FastClass;

/**
 * 利用 CGLib 原生反射进行调用
 *
 * @author tjq
 * @since 2020/1/3
 */
@AllArgsConstructor
public class CglibReflectInvoker implements LocalInvoker {

    private final Object originBean;
    private final FastClass fastClass;

    public CglibReflectInvoker(Object originBean) {
        this.originBean = originBean;
        fastClass = FastClass.create(originBean.getClass());
    }

    @Override
    public Object invoke(String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Exception {

        int methodIndex = fastClass.getIndex(methodName, parameterTypes);
        return fastClass.invoke(methodIndex, originBean, parameters);
    }
}
