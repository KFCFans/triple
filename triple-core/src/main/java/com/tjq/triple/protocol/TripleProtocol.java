package com.tjq.triple.protocol;

import java.io.Serializable;

/**
 * 自定义 RPC 协议
 *
 * @author tjq
 * @since 2020/1/3
 */
public class TripleProtocol implements Serializable {

    private short magic = 10086;
    private byte extension;
    private byte cmd;

    private int length;

    private byte[] data;
}
