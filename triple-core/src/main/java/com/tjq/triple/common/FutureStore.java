package com.tjq.triple.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.concurrent.Future;

/**
 * 存储引用变量
 *
 * @author tjq
 * @since 2020/1/3
 */
@Setter
@Getter
@NoArgsConstructor
public class FutureStore<T> {
    private boolean success;
    private T data;
    private Throwable throwable;
    
    
    public void fill(Future<T> future) {
        try {
            this.data = future.get();
            success = true;
        }catch (Throwable t) {
            success = false;
            throwable = t;
        }
    }
}
