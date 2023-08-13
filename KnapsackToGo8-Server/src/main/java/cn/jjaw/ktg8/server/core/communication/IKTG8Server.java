package cn.jjaw.ktg8.server.core.communication;

import cn.jjaw.ktg8.communication.type.message.BaseMessage;
import cn.jjaw.ktg8.communication.type.message.data.DataMessage;
import cn.jjaw.ktg8.communication.type.message.handshake.server.HandshakeMessageServer;
import cn.jjaw.ktg8.server.api.communication.KTG8Server;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;

import static cn.jjaw.ktg8.communication.type.parse.Parser.*;
import static cn.jjaw.ktg8.server.Logger.logger;

public class IKTG8Server implements KTG8Server {
    private final WebSocketServer webSocketServer;
    private final IMessageListenerManager messageListenerManager = new IMessageListenerManager();
    private final IClientManager clientManager = new IClientManager(this);

    public IKTG8Server() {
        this.webSocketServer = new WebSocketServer(
                new InetSocketAddress(CommunicationConfig.hostname,CommunicationConfig.port)
        );
    }

    public void start() {
        this.webSocketServer.start();
        logger.info("服务器已经在 ws://"+CommunicationConfig.hostname+":"+CommunicationConfig.port+" 上启动");
    }

    private void onOpen(WebSocket conn, ClientHandshake handshake) {
        logger.info(conn.getRemoteSocketAddress()+" 正在连接..");
    }


    private void onClose(WebSocket conn, int code, String reason, boolean remote) {
        clientManager.onDisconnect(conn);
    }

    private void onMessage(WebSocket conn, String message) {
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

    private void onError(WebSocket conn, Exception ex) {
        conn.close();
    }
    private void onStart() {

    }


    @Override
    public IMessageListenerManager getMessageListenerManager() {
        return messageListenerManager;
    }

    @Override
    public IClientManager getClientManager() {
        return clientManager;
    }

    class WebSocketServer extends org.java_websocket.server.WebSocketServer {
        public WebSocketServer(InetSocketAddress address) {
            super(address);
        }
        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            IKTG8Server.this.onOpen(conn,handshake);
        }
        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            IKTG8Server.this.onClose(conn,code,reason,remote);
        }
        @Override
        public void onMessage(WebSocket conn, String message) {
            IKTG8Server.this.onMessage(conn,message);
        }
        @Override
        public void onError(WebSocket conn, Exception ex) {
            IKTG8Server.this.onError(conn,ex);
        }
        @Override
        public void onStart() {
            IKTG8Server.this.onStart();
        }
    }
}
