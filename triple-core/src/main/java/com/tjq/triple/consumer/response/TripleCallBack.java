package com.tjq.triple.consumer.response;

/**
 * 回调方法
 *
 * @author tjq
 * @since 2020/1/4
 */
@FunctionalInterface
public interface TripleCallBack<T> {

    /**
     * 完成 RPC 请求后的回调
     * @param success 是否执行成功（RPC调用成功 + provider 执行成功才视为成功）
     * @param result 远程调用结果（success == true）/ null（success == false）
     * @param throwable null（success == true）/ 异常信息 （success == false）
     */
    void onComplete(boolean success, T result, Throwable throwable);

}
