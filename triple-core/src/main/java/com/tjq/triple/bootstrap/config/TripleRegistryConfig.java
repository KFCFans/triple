package com.tjq.triple.bootstrap.config;

import com.tjq.triple.common.enums.TripleRegisterType;
import lombok.*;

import java.util.List;

/**
 * 注册中心配置
 *
 * @author tjq
 * @since 2020/1/4
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TripleRegistryConfig {

    /**
     * 注册中心类型
     */
    private TripleRegisterType registerType;
    /**
     * 地址，格式为 ip:port
     */
    private List<String> addressList;
    /**
     * 超市时间，可选
     */
    private Integer timeoutMS;
}
