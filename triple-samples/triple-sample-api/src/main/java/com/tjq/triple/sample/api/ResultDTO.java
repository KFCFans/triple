package com.tjq.triple.sample.api;

import lombok.Data;
import lombok.NonNull;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.Serializable;

/**
 * common result dto
 *
 * @author tjq
 * @since 2020/1/2
 */
@Data
public class ResultDTO<T> implements Serializable {

    private boolean success;
    private T data;
    private String msg;

    public static <E> ResultDTO<E> success(E data) {
        ResultDTO<E> res = new ResultDTO<>();
        res.success = true;
        res.data = data;
        return res;
    }

    @NonNull
    public static <E> ResultDTO<E> failed(String msg) {
        ResultDTO<E> res = new ResultDTO<>();
        res.success = false;
        res.msg = msg;
        return res;
    }

    @NonNull
    public static <E> ResultDTO<E> failed(Throwable t) {
        return failed(ExceptionUtils.getStackTrace(t));
    }
}