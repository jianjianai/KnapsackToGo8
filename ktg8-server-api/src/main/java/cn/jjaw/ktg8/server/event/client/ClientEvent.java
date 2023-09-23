package cn.jjaw.ktg8.server.event.client;

import cn.jjaw.easyevent.Event;
import cn.jjaw.ktg8.server.core.Client;

public abstract class ClientEvent extends Event {
    private final Client client;

    public ClientEvent(Client client) {
        this.client = client;
    }

    /**
     *  获取发生事件的客户端
     */
    public Client getClient() {
        return client;
    }
}
