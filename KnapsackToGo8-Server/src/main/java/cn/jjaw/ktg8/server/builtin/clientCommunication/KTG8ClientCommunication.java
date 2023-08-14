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
        String toServer = data.getString("toServer");
        JSONObject toData = data.getJSONObject("toData");
        if(toServer==null){
            getLogger().warn("来自服务器"+client.getServerID()+"的转发请求不规范:"+data);
            return;
        }
        Client toClient = KTG8.getKTG8Server().getClientManager().getClient(toServer);
        if (toClient==null){
            getLogger().warn("来自服务器"+client.getServerID()+"的转发请求,"+toServer+"服务器不在线或不存在！");
            return;
        }

        toClient.sendMessage(this,"relay",toData);
    }

    /**
     * 广播
     */
    public void onBroad(Client client, JSONObject data, MessageListenWorker messageListenWorker) {

    }
}
