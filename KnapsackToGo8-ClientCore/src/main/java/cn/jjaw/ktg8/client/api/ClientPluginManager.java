package cn.jjaw.ktg8.client.api;

public interface ClientPluginManager {
    /**
     * 创建一个指定名称的插件
     */
    KTG8ClientPlugin createPlugin(String name);
}
