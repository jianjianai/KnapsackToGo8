package cn.jjaw.ktg8.server.api.communication;

public interface KTG8Server {

    /**
     * 获取消息监听器管理器
     */
    MessageListenerManager getMessageListenerManager();

    /**
     * 获取客户端管理器
     */
    ClientManager getClientManager();
}
