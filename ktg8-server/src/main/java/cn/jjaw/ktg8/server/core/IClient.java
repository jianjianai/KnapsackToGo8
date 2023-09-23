package cn.jjaw.ktg8.server.core;

import cn.jjaw.ktg8.server.event.client.SendingClientMessagesEvent;
import cn.jjaw.ktg8.type.core.BaseMessage;
import cn.jjaw.ktg8.type.core.DataMessage;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.java_websocket.WebSocket;

class IClient implements Client {
    final IClientManager clientManager;
    final WebSocket webSocket;
    /**
     * 服务器id
     */
    final String serverID;

    public IClient(IClientManager clientManager, WebSocket webSocket, String serverID) {
        this.clientManager = clientManager;
        this.webSocket = webSocket;
        this.serverID = serverID;
    }

    @Override
    public String getServerID() {
        return serverID;
    }

    @Override
    public void sendMessage(KTG8Plugin plugin, String id, JSONObject jsonObject) {
        sendMessage(plugin.getName(),id,jsonObject);
    }

    void sendMessage(String pluginName, String iId, JSONObject jsonObject) {
        SendingClientMessagesEvent event = new SendingClientMessagesEvent(this,pluginName,iId,jsonObject);
        KTG8.getEventManager().execute(event);
        if (!event.isCancel()){
            if (!webSocket.isOpen()) {
                new Error(webSocket.getRemoteSocketAddress()+" "+this+" 连接已关闭！ 无法发送消息。").printStackTrace();
                return;
            }
            //发送数据消息
            webSocket.send(JSON.toJSONString(new BaseMessage(
                    BaseMessage.BaseMessageType.data,
                    JSONObject.from(new DataMessage(
                            event.getPlugin(),
                            event.getId(),
                            event.getData()
                    ))
            )));
        }
    }

    /**
     * 关闭连接
     */
    @Override
    public void close(){
        webSocket.close();
    }


    @Override
    public String toString() {
        return "id:"+serverID;
    }
}
