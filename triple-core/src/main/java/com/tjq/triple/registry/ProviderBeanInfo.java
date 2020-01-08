package com.tjq.triple.registry;

import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Value;

import java.util.Map;

/**
 * 服务提供者的信息
 *
 * @author tjq
 * @since 2020/1/7
 */
@Value
@Builder
public class ProviderBeanInfo {

    private String group;
    private String serviceName;
    private String version;
    /**
     * 补充信息(写入当前IP的 ZNode 的数据)，比如当前时间、超时时间等
     * 注，dubbo中的数据为 : dubbo://192.168.1.180:20880/cn.edu.zju.arc.qm.client.rpc.RuleService?anyhost=true&application=supervisor&bean.name=ServiceBean:cn.edu.zju.arc.qm.client.rpc.RuleService&deprecated=false&dubbo=2.0.2&dynamic=true&generic=false&interface=cn.edu.zju.arc.qm.client.rpc.RuleService&logger=slf4j&methods=getAllWarningRules,getAllFieldsWeight,getAllMeasureRules&pid=27814&release=2.7.4.1&side=provider&timeout=2000&timestamp=1578399274345
     */
    private Map<String, Object> info = Maps.newHashMap();

    public void addInfo(String k, Object v) {
        info.put(k, v);
    }
}
