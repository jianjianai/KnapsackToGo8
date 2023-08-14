package cn.jjaw.ktg8.client.core;

import cn.jjaw.ktg8.client.api.ClientMessageListenerManager;
import cn.jjaw.ktg8.client.api.ClientPluginManager;
import cn.jjaw.ktg8.client.api.KTG8Client;
import cn.jjaw.ktg8.client.api.KTG8ClientPlugin;
import cn.jjaw.ktg8.communication.type.message.BaseMessage;
import cn.jjaw.ktg8.communication.type.message.BaseType;
import cn.jjaw.ktg8.communication.type.message.data.DataMessage;
import cn.jjaw.ktg8.communication.type.message.handshake.client.HandshakeMessageClient;
import cn.jjaw.ktg8.communication.type.message.handshake.server.HandshakeMessageServer;
import cn.jjaw.ktg8.communication.type.message.handshake.server.ServerType;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import static cn.jjaw.ktg8.client.core.Logger.logger;
import static cn.jjaw.ktg8.communication.type.parse.Parser.*;

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
        webSocketClient.send(JSON.toJSONString(new BaseMessage(){{
            type = BaseType.handshake;
            data = JSONObject.from(new HandshakeMessageServer(){{
                this.version = 0;
                this.serverID = IKTG8Client.this.serverID;
                this.serverType = IKTG8Client.this.serverType;
            }});
        }}));
    }

    private void onMessage(String message) {
        BaseMessage baseMessage = ofBaseMessage(message);
        if (baseMessage==null){
            logger.error("收到来自服务器的无法解析或不规范的消息！ : "+message);
            webSocketClient.close();
            return;
        }
        switch (baseMessage.type) {
            case handshake -> {
                HandshakeMessageClient handshakeMessageClient = ofHandshakeMessageClient(baseMessage.data);
                if (handshakeMessageClient == null) {
                    logger.error("收到来自服务器的握手消息不规范！ : " + message);
                    webSocketClient.close();
                    break;
                }
                if(!handshakeMessageClient.ok){
                    webSocketClient.close();
                    onDenied(handshakeMessageClient.reason);
                    break;
                }
                logger.info("连接KTG8服务器成功！");
                onHandshake();
            }
            case data -> {
                DataMessage dataMessage = ofDataMessage(baseMessage.data);
                if (dataMessage==null){
                    webSocketClient.close();
                    logger.error("收到来自服务器的消息不规范！ : " + message);
                    break;
                }
                IMessageListenWorker listenWorker = messageListenerManager.getListenWorker(dataMessage.plugin, dataMessage.id);
                if(listenWorker==null){
                    logger.warn("没有找到"+dataMessage.plugin+":"+dataMessage.id+"监听器！");
                    return;
                }
                listenWorker.onMessage(dataMessage.data);
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
        webSocketClient.send(JSON.toJSONString(new BaseMessage(){{
            this.type = BaseType.data;
            this.data = JSONObject.from(new DataMessage(){{
                this.plugin = pluginName;
                this.id = iId;
                this.data = jsonObject;
            }});
        }}));
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
