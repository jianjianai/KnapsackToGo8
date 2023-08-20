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
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.jjaw.ktg8.client.core.Logger.logger;

public abstract class IKTG8Client implements KTG8Client{
    private ScheduledThreadPoolExecutor executor;
    private final IPluginManager pluginManager = new IPluginManager(this);
    private final IMessageListenerManager messageListenerManager = new IMessageListenerManager();
    private WebSocketClient webSocketClient;

    /**
     * 是否处于连接状态
     */
    private boolean isConnected = false;
    /**
     * 是否启动
     */
    private boolean isStart = false;
    /**
     * 是否关闭
     */
    private boolean isClose = false;

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
                BaseMessage.BaseMessageType.handshake,
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
        if (isClose){
            try {
                onClose();
            }catch (Throwable throwable){
                throwable.printStackTrace();
            }
            return;
        }
        logger.warn("服务器连接已断开，正在重新连接..");
        executor.schedule(()->{
            if (isClose){
                try {
                    onClose();
                }catch (Throwable throwable){
                    throwable.printStackTrace();
                }
                return;
            }
            webSocketClient = new WebSocketClient(serverUri);
            webSocketClient.connect();
        },1, TimeUnit.SECONDS);
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
                BaseMessage.BaseMessageType.data,
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

    @Override
    public ScheduledThreadPoolExecutor getExecutor() {
        return executor;
    }

    /**
     * 打开连接
     */
    public void connect(){
        if (isClose){
            throw new Error("IKTG8Client不可用从用，如果已经关闭则需要创建新的对象");
        }
        if (isStart){
            return;
        }
        isStart = true;
        executor = new ScheduledThreadPoolExecutor(1);
        webSocketClient.connect();
    }

    /**
     * 关闭
     */
    public void close(){
        if (!isStart){
            throw new Error("在没有使用connect连接之前不需要使用close关闭。");
        }
        if (isClose){
            return;
        }
        isClose = true;
        executor.shutdown();
        webSocketClient.close();
    }


    /**
     * 当连接断开时，或连接失败时触发，此时不能发送消息
     */
    protected abstract void onConnectionDisconnected();

    /**
     * 当连接成功时，或者重新连接成功时触发，此诗句触发后才可发送消息
     */
    protected abstract void onSuccessfullyConnected();

    /**
     * 当关闭时，此时已经关闭
     */
    protected abstract void onClose();




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
