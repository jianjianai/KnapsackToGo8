import cn.jjaw.ktg8.client.core.IKTG8Client;
import cn.jjaw.ktg8.communication.type.message.handshake.server.ServerType;

import java.net.URI;
import java.net.URISyntaxException;

public class 连接测试 {
    public static void main(String[] args) throws URISyntaxException {
        IKTG8Client iktg8Client = new IKTG8Client(new URI("ws://localhost:33338"), ServerType.Bukkit, "666") {
            @Override
            protected void onConnectionDisconnected() {
                System.out.println("断开了啊啊啊");
            }

            @Override
            protected void onSuccessfullyConnected() {
                System.out.println("连接了啊啊啊");
            }
        };
        iktg8Client.connect();
    }
}
