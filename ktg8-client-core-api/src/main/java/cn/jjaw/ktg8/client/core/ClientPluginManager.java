package cn.jjaw.ktg8.client.core;

public interface ClientPluginManager {
    /**
     * 创建一个指定名称的插件
     */
    KTG8ClientPlugin createPlugin(String name);


    /**
     * 获取ktg8客户端
     */
    KTG8Client getKTG8Client();
}
