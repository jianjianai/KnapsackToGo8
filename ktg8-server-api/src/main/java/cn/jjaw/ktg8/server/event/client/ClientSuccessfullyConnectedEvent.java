package cn.jjaw.ktg8.server.event.client;

import cn.jjaw.ktg8.server.core.Client;

/**
 * 客户端成功连接事件
 */
public class ClientSuccessfullyConnectedEvent extends ClientEvent {

    public ClientSuccessfullyConnectedEvent(Client client) {
        super(client);
    }
}
