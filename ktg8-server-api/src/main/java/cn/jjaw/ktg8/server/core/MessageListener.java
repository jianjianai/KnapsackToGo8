package cn.jjaw.ktg8.server.core;

import com.alibaba.fastjson2.JSONObject;

public interface MessageListener {
    /**
     * 当收到消息时
     * @param client 消息的发送者
     * @param data json数据
     */
    void onMessage(Client client, JSONObject data,MessageListenWorker messageListenWorker);
}
