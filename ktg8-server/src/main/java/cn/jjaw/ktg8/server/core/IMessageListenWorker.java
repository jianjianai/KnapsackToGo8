package cn.jjaw.ktg8.server.core;

import com.alibaba.fastjson2.JSONObject;

/**
 * 监听器
 */
class IMessageListenWorker implements MessageListenWorker {
    KTG8Plugin plugin;
    String id;
    MessageListener messageListener;

    public IMessageListenWorker(KTG8Plugin plugin, String id, MessageListener messageListener) {
        this.plugin = plugin;
        this.id = id;
        this.messageListener = messageListener;
    }

    /**
     * 当收到消息时
     */
    void onMessage(IClient client, JSONObject data) {
        try {
            messageListener.onMessage(client,data,this);
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }
    }

    @Override
    public KTG8Plugin getPlugin() {
        return plugin;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public MessageListener getListener() {
        return messageListener;
    }
}
