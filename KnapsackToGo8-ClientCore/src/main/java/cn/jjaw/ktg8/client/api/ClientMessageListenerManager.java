package cn.jjaw.ktg8.client.api;

public interface ClientMessageListenerManager {
    /**
     * 注册一个消息监听器
     * @param plugin 插件
     * @param id 监听器id
     * @param messageListener 自己实现一个监听器工作类
     */
    ClientMessageListenWorker regListener(KTG8ClientPlugin plugin, String id, ClientMessageListener messageListener);
    /**
     * 注销一个监听器
     * @param plugin 插件
     * @param id 监听器id
     * @return 如果找到对应的监听器并注销则返回监听器工作对象，否则返回null
     */
    ClientMessageListenWorker removeListener(KTG8ClientPlugin plugin, String id);

    /**
     * 获取一个监听器工作类
     * @param plugin 插件
     * @param id 监听器id
     * @return 没找到对应工作类返回null
     */
    ClientMessageListenWorker getListenWorker(KTG8ClientPlugin plugin, String id);
}
