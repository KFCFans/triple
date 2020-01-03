package com.tjq.triple.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 存储引用变量
 *
 * @author tjq
 * @since 2020/1/3
 */
@Setter
@Getter
@NoArgsConstructor
public class TripleStore<T> {
    private T data;
}
