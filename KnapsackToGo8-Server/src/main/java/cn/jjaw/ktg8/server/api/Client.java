package cn.jjaw.ktg8.server.api;

import cn.jjaw.ktg8.communication.type.message.handshake.server.ServerType;
import com.alibaba.fastjson2.JSONObject;

public interface Client {

    /**
     * 获取服务器ID，服务器配置的唯一名称
     */
    String getServerID();

    /**
     * 获取服务器类型
     */
    ServerType getServerType();

    /**
     * 向客户端发送消息
     */
    void sendMessage(KTG8Plugin plugin, String id, JSONObject jsonObject);
    /**
     * 向客户端发送消息
     */
    default void sendMessage(MessageListenWorker messageListenWorker, JSONObject jsonObject){
        sendMessage(messageListenWorker.getPlugin(), messageListenWorker.getId(), jsonObject);
    }

    /**
     * 关闭连接
     */
    void close();

}
