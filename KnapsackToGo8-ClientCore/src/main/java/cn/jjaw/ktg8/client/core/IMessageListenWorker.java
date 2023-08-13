package cn.jjaw.ktg8.client.core;

import cn.jjaw.ktg8.client.api.ClientMessageListenWorker;
import cn.jjaw.ktg8.client.api.ClientMessageListener;
import cn.jjaw.ktg8.client.api.KTG8ClientPlugin;
import com.alibaba.fastjson2.JSONObject;

/**
 * 监听器
 */
class IMessageListenWorker implements ClientMessageListenWorker {
    KTG8ClientPlugin plugin;
    String id;
    ClientMessageListener messageListener;

    public IMessageListenWorker(KTG8ClientPlugin plugin, String id, ClientMessageListener messageListener) {
        this.plugin = plugin;
        this.id = id;
        this.messageListener = messageListener;
    }

    /**
     * 当收到消息时
     */
    void onMessage(JSONObject data) {
        messageListener.onMessage(data,this);
    }

    @Override
    public KTG8ClientPlugin getPlugin() {
        return plugin;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ClientMessageListener getListener() {
        return messageListener;
    }
}
