package com.xm.cyg.thread.cache;

/**
 * Created by wm on 15/6/16.
 *
 */
public interface Computable<V, T> {

    /**
     * 进行计算的泛型接口
     * @param args
     * @return
     */
    T compute(V args);

}
