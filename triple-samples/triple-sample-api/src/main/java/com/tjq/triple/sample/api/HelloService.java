package com.tjq.triple.sample.api;

/**
 * common api
 *
 * @author tjq
 * @since 2020/1/2
 */
public interface HelloService {

    ResultDTO<String> sayHello(String msg, long time);

}
