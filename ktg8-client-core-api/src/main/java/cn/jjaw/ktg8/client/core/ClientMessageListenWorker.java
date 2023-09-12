package cn.jjaw.ktg8.client.core;

public interface ClientMessageListenWorker {

    /**
     * 获取此监听器的插件
     */
    KTG8ClientPlugin getPlugin();

    /**
     * 获取此监听器的id
     */
    String getId();

    /**
     * 获取监听器的执行器
     */
    ClientMessageListener getListener();
}
