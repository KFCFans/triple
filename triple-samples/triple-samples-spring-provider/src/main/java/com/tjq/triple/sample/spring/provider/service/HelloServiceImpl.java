package com.tjq.triple.sample.spring.provider.service;

import com.tjq.triple.bootstrap.annotation.TripleProvider;
import com.tjq.triple.sample.api.HelloService;
import com.tjq.triple.sample.api.ResultDTO;
import org.springframework.stereotype.Service;

/**
 * provider
 *
 * @author tjq
 * @since 2020/1/2
 */
@Service
@TripleProvider(interfaceClass = HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public ResultDTO<String> sayHello(String msg, long time) {

        System.out.println("=== provider ===");
        System.out.println("msg = " + msg);
        System.out.println("time = " + time);

        if ("EXCEPTION".equals(msg)) {
            throw new RuntimeException("this is a exception");
        }

        String res = "接受消息:" + msg + "耗时:" + (System.currentTimeMillis() - time);
        return ResultDTO.success(res);
    }
}
