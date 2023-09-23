package cn.jjaw.ktg8.server.core;

import java.util.function.Function;

public interface EventListener<E> {
    /**
     * 关闭这个监听器，不在监听任何事件
     */
    void close();

    /**
     * 设置事件处理函数
     * @param newFunction 新的执行函数
     * @return 旧的执行函数
     */
    Function<E,Void> setExcuteFunction(Function<E,Void> newFunction);
}
