package com.tjq.triple.sample.spring.consumer.controller;

import com.tjq.triple.sample.api.HelloService;
import com.tjq.triple.sample.api.ResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Controller
 *
 * @author tjq
 * @since 2020/1/2
 */
@Slf4j
@RestController
@RequestMapping("/display")
public class DisplayController {

    @Resource
    private HelloService helloService;

    @GetMapping("/sayHello")
    public ResultDTO<String> sayHello(String msg) {
        System.out.println("invoking.....");
        try {
            return helloService.sayHello(msg, System.currentTimeMillis());
        }catch (Exception e) {
            log.error("[Triple]", e);
            return ResultDTO.failed(e);
        }
    }

}
