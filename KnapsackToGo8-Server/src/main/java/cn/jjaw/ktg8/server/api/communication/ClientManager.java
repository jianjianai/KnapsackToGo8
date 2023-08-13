package cn.jjaw.ktg8.server.api.communication;


import java.util.Collection;

public interface ClientManager {
    /**
     * 获取指定名称的客户端
     */
    Client getClient(String name);
    /**
     * 获取全部客户端
     */
    Collection<? extends Client> getAllClient();
}
