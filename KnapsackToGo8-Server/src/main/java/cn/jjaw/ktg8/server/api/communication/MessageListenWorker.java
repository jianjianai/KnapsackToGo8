package cn.jjaw.ktg8.server.api.communication;

import cn.jjaw.ktg8.server.api.plugin.KTG8Plugin;

public interface MessageListenWorker {

    /**
     * 获取此监听器的插件
     */
    KTG8Plugin getPlugin();

    /**
     * 获取此监听器的id
     */
    String getId();

    /**
     * 获取监听器的执行器
     */
    MessageListener getListener();
}
