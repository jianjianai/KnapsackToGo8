package cn.jjaw.ktg8.server.core.communication;

import cn.jjaw.ktg8.communication.type.message.BaseMessage;
import cn.jjaw.ktg8.communication.type.message.BaseType;
import cn.jjaw.ktg8.communication.type.message.data.DataMessage;
import cn.jjaw.ktg8.communication.type.message.handshake.client.HandshakeMessageClient;
import cn.jjaw.ktg8.communication.type.message.handshake.server.HandshakeMessageServer;
import cn.jjaw.ktg8.server.api.communication.Client;
import cn.jjaw.ktg8.server.api.communication.ClientManager;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.java_websocket.WebSocket;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cn.jjaw.ktg8.server.Logger.logger;

/***
 * 客户端管理器
 */
class IClientManager implements ClientManager {
    private final IMessageListenerManager listenerManager;
    private final Map<String,IClient> clientNameMap = new ConcurrentHashMap<>();
    private final Map<WebSocket, IClient> clientMap = new ConcurrentHashMap<>();

    public IClientManager(IKTG8Server ktg4Server) {
        this.listenerManager = ktg4Server.getMessageListenerManager();
    }

    void addIClient(IClient iClient){
        clientNameMap.put(iClient.serverID,iClient);
        clientMap.put(iClient.webSocket, iClient);
    }
    void removeIClient(IClient iClient){
        clientNameMap.remove(iClient.serverID);
        clientMap.remove(iClient.webSocket);
    }
    @Override
    public IClient getClient(String name){
        return clientNameMap.get(name);
    }

    @Override
    public Collection<? extends Client> getAllClient() {
        return clientNameMap.values();
    }

    IClient getClient(WebSocket webSocket){
        return  clientMap.get(webSocket);
    }

    /**
     * 握手,一个新的连接握手
     */
    void onHandshake(WebSocket webSocket, HandshakeMessageServer handshakeMessageServer){
        HandshakeMessageClient handshakeMessageClient;
        IClient client = null;
        a:synchronized(this){
            if(getClient(webSocket)!=null){
                handshakeMessageClient = new HandshakeMessageClient(){{
                    ok=false;
                    reason = "已经完成握手，请勿重复发送握手包！";
                }};
                break a;
            }
            if(getClient(handshakeMessageServer.serverID)!=null){
                handshakeMessageClient = new HandshakeMessageClient(){{
                    ok=false;
                    reason = "服务器ID"+handshakeMessageServer.serverID+"已经连接，清确保所有服务器ID唯一。";
                }};
                break a;
            }
            addIClient(client = new IClient(
                    this,webSocket,
                    handshakeMessageServer.serverID,
                    handshakeMessageServer.serverType
            ));
            handshakeMessageClient = new HandshakeMessageClient(){{
                ok=true;
                reason = null;
            }};
        }
        //发送握手成功信息
        webSocket.send(JSON.toJSONString(new BaseMessage(){{
            type = BaseType.handshake;
            data = JSONObject.from(handshakeMessageClient);
        }}));
        //打印日志
        if(client!=null){
            logger.info(webSocket.getRemoteSocketAddress()+" 建立连接成功 "+client);
        }else {
            logger.info(webSocket.getRemoteSocketAddress()+" 建立连接失败,原因:"+handshakeMessageClient.reason);
        }

    }

    /**
     * 收到新消息
     */
    void onMessage(WebSocket webSocket, DataMessage dataMessage){
        IClient client = getClient(webSocket);
        if(client==null){
            return;
        }
        IMessageListenWorker listenWorker = listenerManager.getListenWorker(dataMessage.plugin, dataMessage.id);
        if(listenWorker==null){
            logger.warn("没有找到"+dataMessage.plugin+":"+dataMessage.id+"监听器！");
            return;
        }
        listenWorker.onMessage(client,dataMessage.data);
    }

    /**
     * 关闭，一个客户端断开连接
     */
    void onDisconnect(WebSocket webSocket){
        synchronized(this){
            IClient iClient = getClient(webSocket);
            if(iClient!=null){
                removeIClient(iClient);
            }
        }
        logger.info(webSocket.getRemoteSocketAddress()+" 已断开连接");
    }


}
