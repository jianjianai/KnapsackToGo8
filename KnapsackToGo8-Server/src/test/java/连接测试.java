import cn.jjaw.ktg8.communication.type.message.BaseMessage;
import cn.jjaw.ktg8.communication.type.message.BaseType;
import cn.jjaw.ktg8.communication.type.message.data.DataMessage;
import cn.jjaw.ktg8.communication.type.message.handshake.server.HandshakeMessageServer;
import cn.jjaw.ktg8.communication.type.message.handshake.server.ServerType;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class 连接测试 {
    public static void main(String[] args) throws URISyntaxException {
        WebSocketClient webSocketClient = new WebSocketClient(new URI("ws://localhost:33338")) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                System.out.println("已连接");
                this.send(JSON.toJSONString(new BaseMessage(){{
                    type = BaseType.handshake;
                    data = JSONObject.from(new HandshakeMessageServer(){{
                        version = 0;
                        serverID = "uuid";
                        serverType = ServerType.Bukkit;
                    }});
                }}));


            }

            @Override
            public void onMessage(String message) {
                System.out.println(message);
                this.send(JSON.toJSONString(new BaseMessage(){{
                    type = BaseType.data;
                    data = JSONObject.from(new DataMessage(){{
                        plugin = "KTG8";
                        id = "page";
                    }});
                }}));
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("eeeeeeeeeeeeeeeeeeeeeeeee");
                this.connect();
            }

            @Override
            public void onError(Exception ex) {
                System.out.println(ex);
            }
        };
        webSocketClient.connect();
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaa");
    }
}
