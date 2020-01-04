package com.tjq.triple.common.exception;

import lombok.NoArgsConstructor;

/**
 * 远程服务执行产生的异常（RPC调用成功）
 *
 * @author tjq
 * @since 2020/1/4
 */
@NoArgsConstructor
public class ProviderExecuteException extends RuntimeException {

    public ProviderExecuteException(String msg) {
        super(msg);
    }

    public ProviderExecuteException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ProviderExecuteException(Throwable cause) {
        super(cause);
    }
}
