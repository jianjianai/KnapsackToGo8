package cn.jjaw.ktg8.server.event.client;

import cn.jjaw.ktg8.server.core.Client;

/**
 * 客户端断开连接事件
 */
public class ClientDisconnectEvent extends ClientEvent {

    public ClientDisconnectEvent(Client client) {
        super(client);
    }
}
