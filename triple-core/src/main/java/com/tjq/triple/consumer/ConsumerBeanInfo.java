package com.tjq.triple.consumer;

import lombok.Builder;
import lombok.Value;

/**
 * 封装 consumer 的基本信息
 *
 * @author tjq
 * @since 2020/1/8
 */
@Value
@Builder
public class ConsumerBeanInfo {

    private String group;
    private String version;
    private String serviceName;

    private boolean async;
}
