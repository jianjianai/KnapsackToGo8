package cn.jjaw.ktg8.client.core;

import cn.jjaw.ktg8.client.api.ClientMessageListenerManager;
import cn.jjaw.ktg8.client.api.ClientPluginManager;
import cn.jjaw.ktg8.client.api.KTG8Client;
import cn.jjaw.ktg8.client.api.KTG8ClientPlugin;
import cn.jjaw.ktg8.type.core.*;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import static cn.jjaw.ktg8.client.core.Logger.logger;

public abstract class IKTG8Client implements KTG8Client{
    private final IPluginManager pluginManager = new IPluginManager(this);
    private final IMessageListenerManager messageListenerManager = new IMessageListenerManager();
    private WebSocketClient webSocketClient;
    /**
     * 是否处于连接状态
     */
    private boolean isConnected = false;

    private final URI serverUri;
    private final ServerType serverType;
    private final String serverID;

    public IKTG8Client(URI serverUri, ServerType serverType, String serverID) {
        this.serverUri = serverUri;
        this.serverType = serverType;
        this.serverID = serverID;
        this.webSocketClient = new WebSocketClient(serverUri);
    }

    private void onOpen(ServerHandshake handshakedata) {
        //发送握手包
        webSocketClient.send(JSON.toJSONString(new BaseMessage(
                BaseMessageType.handshake,
                JSONObject.from(new HandshakeMessageServer(
                        0,
                        serverID,
                        serverType
                )))
        ));
    }

    private void onMessage(String message) {
        BaseMessage baseMessage = null;
        try {
            baseMessage = JSON.parseObject(message,BaseMessage.class);
        }catch (JSONException jsonException){
            jsonException.printStackTrace();
        }
        if (baseMessage==null || baseMessage.type()==null || baseMessage.data()==null){
            logger.warn("收到来自服务器的消息不规范 obj:BaseMessage:"+baseMessage+" json:"+message);
            webSocketClient.close();
            return;
        }
        switch (baseMessage.type()) {
            case handshake -> {
                HandshakeMessageClient handshake = baseMessage.data().to(HandshakeMessageClient.class);
                if (handshake == null) {
                    logger.warn("收到来自服务器的消息不规范 obj:HandshakeMessageClient:null json:"+baseMessage.data());
                    webSocketClient.close();
                    break;
                }
                if(!handshake.ok()){
                    webSocketClient.close();
                    onDenied(handshake.reason());
                    break;
                }
                logger.info("连接KTG8服务器成功！");
                onHandshake();
            }
            case data -> {
                DataMessage dataMessage = baseMessage.data().to(DataMessage.class);
                if (dataMessage==null || dataMessage.id()==null || dataMessage.plugin()==null){
                    logger.warn("收到来自服务器的消息不规范 obj:HandshakeMessageClient:"+dataMessage+" json:"+baseMessage.data());
                    webSocketClient.close();
                    break;
                }
                IMessageListenWorker listenWorker = messageListenerManager.getListenWorker(dataMessage.plugin(), dataMessage.id());
                if(listenWorker==null){
                    logger.warn("没有找到"+dataMessage.plugin()+":"+dataMessage.id()+"监听器！");
                    return;
                }
                listenWorker.onMessage(dataMessage.data());
            }
        }


    }

    /**
     * 当连接被拒绝
     */
    private void onDenied(String reason){
        logger.error("服务器拒绝连接，原因:"+reason);
    }

    /**
     * 当完成握手时
     */
    private void onHandshake(){
        isConnected = true;
        try {
            onSuccessfullyConnected();
        }catch (Throwable e){
            e.printStackTrace();
        }
    }

    private void onClose(int code, String reason, boolean remote) {
        isConnected = false;
        try {
            onConnectionDisconnected();
        }catch (Throwable e){
            e.printStackTrace();
        }
        logger.warn("服务器连接已断开，正在重新连接..");
        webSocketClient = new WebSocketClient(serverUri);
        try {Thread.sleep(1000);} catch (InterruptedException ignored) {}
        webSocketClient.connect();
    }

    private void onError(Exception ex) {
        logger.error("连接服务器出现错误!");
        ex.printStackTrace();
    }

    private void sendMessage(String pluginName, String iId, JSONObject jsonObject) {
        if(!isConnected){
            new Error("还未与服务器建立连接，无法发送消息！").printStackTrace();
            return;
        }
        if (!webSocketClient.isOpen()) {
            new Error(webSocketClient.getRemoteSocketAddress()+" "+this+" 连接已关闭！ 无法发送消息。").printStackTrace();
            return;
        }
        //发送数据消息
        webSocketClient.send(JSON.toJSONString(new BaseMessage(
                BaseMessageType.data,
                JSONObject.from(new DataMessage(
                        pluginName,
                        iId,
                        jsonObject
                )))
        ));
    }

    @Override
    public void sendMessage(KTG8ClientPlugin plugin, String id, JSONObject jsonObject) {
        sendMessage(plugin.getName(),id,jsonObject);
    }

    @Override
    public ClientMessageListenerManager getMessageListenerManager() {
        return messageListenerManager;
    }

    @Override
    public ClientPluginManager getPluginManager() {
        return pluginManager;
    }


    @Override
    public ServerType getServerType() {
        return serverType;
    }
    @Override
    public String getServerID() {
        return serverID;
    }

    /**
     * 打开连接
     */
    public void connect(){
        this.webSocketClient.connect();
    }


    /**
     * 当连接断开时，或连接失败时触发
     */
    protected abstract void onConnectionDisconnected();

    /**
     * 当连接成功时，或者重新连接成功时触发，此诗句触发后才可发送消息
     */
    protected abstract void onSuccessfullyConnected();




    class WebSocketClient extends org.java_websocket.client.WebSocketClient{
        public WebSocketClient(URI serverUri) {
            super(serverUri);
        }
        @Override
        public void onOpen(ServerHandshake handshakedata) {
            IKTG8Client.this.onOpen(handshakedata);
        }
        @Override
        public void onMessage(String message) {
            IKTG8Client.this.onMessage(message);
        }
        @Override
        public void onClose(int code, String reason, boolean remote) {
            IKTG8Client.this.onClose(code,reason,remote);
        }
        @Override
        public void onError(Exception ex) {
            IKTG8Client.this.onError(ex);
        }
    }
}
