package com.tjq.triple;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tjq.triple.registry.ProviderBeanInfo;
import com.tjq.triple.registry.zookeeper.ZKRegister;
import org.junit.jupiter.api.Test;

/**
 * 服务注册与发现测试
 *
 * @author tjq
 * @since 2020/1/8
 */
public class RegistryTests {

    @Test
    public void testRegister() throws Exception {
        ZKRegister zkRegister = new ZKRegister(Lists.newArrayList("115.159.215.229:2181"));
        ProviderBeanInfo beanInfo = ProviderBeanInfo.builder()
                .group("triple")
                .version("1.0.0.pre")
                .serviceName("com.tjq.HelloService")
                .build();
        zkRegister.register(Lists.newArrayList(beanInfo));
    }

}
