package cn.jjaw.ktg8.server.core.communication;

import cn.jjaw.ktg8.communication.type.censor.Check;
import cn.jjaw.ktg8.communication.type.message.BaseMessage;
import cn.jjaw.ktg8.communication.type.message.data.DataMessage;
import cn.jjaw.ktg8.communication.type.message.handshake.server.HandshakeMessageServer;
import cn.jjaw.ktg8.server.api.communication.KTG8Server;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

import static cn.jjaw.ktg8.server.Logger.logger;

public class IKTG8Server extends WebSocketServer implements KTG8Server {
    private final IMessageListenerManager messageListenerManager = new IMessageListenerManager();
    private final IClientManager clientManager = new IClientManager(this);

    public IKTG8Server() {
        super(new InetSocketAddress(CommunicationConfig.hostname,CommunicationConfig.port));
    }

    @Override
    public void start() {
        super.start();
        logger.info("服务器已经在 ws://"+CommunicationConfig.hostname+":"+CommunicationConfig.port+" 上启动");
    }

    @Override
    public IMessageListenerManager getMessageListenerManager() {
        return messageListenerManager;
    }

    @Override
    public IClientManager getClientManager() {
        return clientManager;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        logger.info(conn.getRemoteSocketAddress()+" 正在连接..");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        clientManager.onDisconnect(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        BaseMessage baseMessage = ofBaseMessage(message);
        if(baseMessage==null){
            conn.close();
            return;
        }
        switch (baseMessage.type){
            case handshake -> {
                HandshakeMessageServer handshakeMessageServer = ofHandshakeMessageServer(baseMessage.data);
                if(handshakeMessageServer ==null){
                    break;
                }
                clientManager.onHandshake(conn,handshakeMessageServer);
            }
            case data -> {
                DataMessage dataMessage = ofDataMessage(baseMessage.data);
                if(dataMessage==null){
                    break;
                }
                clientManager.onMessage(conn,dataMessage);
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        conn.close();
    }

    @Override
    public void onStart() {

    }

    /**
     * 将json字符串解析为BaseMessage并检查合法性
     * @return null 发生错误或字符串为空
     */
    BaseMessage ofBaseMessage(String json){
        JSONObject jsonObject = null;
        try {
            jsonObject = JSON.parseObject(json);
        }catch (JSONException exception){
            exception.printStackTrace();
        }
        if(jsonObject==null){
            logger.error("json解析错误！");
            return null;
        }
        return Check.check(jsonObject.to(BaseMessage.class));
    }
    /**
     * 将json对象解析为HandshakeMessageServer并检查合法性
     * @return null 发生错误或字符串为空
     */
    HandshakeMessageServer ofHandshakeMessageServer(JSONObject jsonObject){
        return Check.check(jsonObject.to(HandshakeMessageServer.class));
    }
    /**
     * 将json对象解析为HandshakeMessageServer并检查合法性
     * @return null 发生错误或字符串为空
     */
    DataMessage ofDataMessage(JSONObject jsonObject){
        return Check.check(jsonObject.to(DataMessage.class));
    }

}
