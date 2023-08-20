import cn.jjaw.ktg8.client.api.ClientRequestSend;
import cn.jjaw.ktg8.client.api.KTG8ClientPlugin;
import cn.jjaw.ktg8.client.core.IKTG8Client;
import cn.jjaw.ktg8.type.core.ServerType;
import com.alibaba.fastjson2.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class 连接测试 {
    public static class A{
        public String ss = "asdasd";
    }
    public static void main(String[] args) throws URISyntaxException {
        IKTG8Client iktg8Client = new IKTG8Client(new URI("ws://localhost:33338"), ServerType.Spigot, "666") {

            @Override
            protected void onConnectionDisconnected() {
                System.out.println("断开了啊啊啊");
            }

            @Override
            protected void onSuccessfullyConnected() {
                System.out.println("连接了啊啊啊");

                KTG8ClientPlugin ktg8ClientPlugin = this.getPluginManager().createPlugin("my");
                ClientRequestSend requestSend = new ClientRequestSend(this.getMessageListenerManager(), ktg8ClientPlugin,"a").start();
                requestSend.sendRequest(
                        JSONObject.from(new A()),
//                        response -> System.out.println(response),
                        null,
//                        (code, reason) -> System.out.println(code+"  "+reason)
                        null
                );
            }

            @Override
            protected void onClose() {

            }
        };

        iktg8Client.connect();

    }

}
