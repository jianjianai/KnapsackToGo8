package cn.jjaw.ktg8.server.builtin.clientCommunication;

import cn.jjaw.ktg8.server.api.*;
import com.alibaba.fastjson2.JSONObject;

/**
 * 一个简化客户端之间相互通信的内置插件
 */
public class KTG8ClientCommunication extends KTG8Plugin{
    public KTG8ClientCommunication() {
        super("KTG8ClientCommunication");
    }

    @Override
    public void onEnable() {
        KTG8.getKTG8Server().getMessageListenerManager().regListener(this,"relay",this::onRelay);
        KTG8.getKTG8Server().getMessageListenerManager().regListener(this,"broad",this::onBroad);
    }

    /**
     * 转发
     */
    public void onRelay(Client client, JSONObject data, MessageListenWorker messageListenWorker) {

    }

    /**
     * 广播
     */
    public void onBroad(Client client, JSONObject data, MessageListenWorker messageListenWorker) {

    }
}
