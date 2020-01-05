package com.tjq.triple.sample.spring.provider;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * no
 *
 * @author tjq
 * @since 2020/1/5
 */
@RestController
@RequestMapping("/empty")
public class EmptyController {

    @RequestMapping("/e")
    public String ee() {
        return "zz";
    }
}
