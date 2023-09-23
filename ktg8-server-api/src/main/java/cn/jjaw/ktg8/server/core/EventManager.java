package cn.jjaw.ktg8.server.core;

import java.util.function.Function;

public interface EventManager {
    /**
     * 注册一个事件监听器
     * @param event 要监听的事件
     * @param function 处理事件的函数
     * @return 事件监听器对象
     */
    <E> EventListener<E> regEventListener(Class<E> event, Function<E,Void> function);
}
