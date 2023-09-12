package cn.jjaw.ktg8.client.core;

import com.alibaba.fastjson2.JSONObject;

public interface ClientMessageListener {
    /**
     * 当收到消息时
     * @param data json数据
     */
    void onMessage(JSONObject data,ClientMessageListenWorker messageListenWorker);
}
