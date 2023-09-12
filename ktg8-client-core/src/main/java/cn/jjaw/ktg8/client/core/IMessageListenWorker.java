package cn.jjaw.ktg8.client.core;

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
        try {
            messageListener.onMessage(data,this);
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }
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
