package com.tjq.triple.sample.spring.consumer.controller;

import com.tjq.triple.sample.api.HelloService;
import com.tjq.triple.sample.api.ResultDTO;
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
@RestController
@RequestMapping("/display")
public class DisplayController {

    @Resource
    private HelloService helloService;

    @GetMapping("/sayHello")
    public ResultDTO<String> sayHello(String msg) {
        try {
            return helloService.sayHello(msg, System.currentTimeMillis());
        }catch (Exception e) {
            return ResultDTO.failed(e);
        }
    }

}
