package cn.jjaw.ktg8.client.core;

import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KTG8ClientPlugin {
    private final ClientPluginManager pluginManager;
    private final String name;
    private final Logger logger;

    protected KTG8ClientPlugin(ClientPluginManager iPluginManager, String name) {
        this.name = name;
        this.pluginManager = iPluginManager;
        this.logger = LoggerFactory.getLogger(name);
    }

    /**
     * 服务器发送消息，会阻塞线程
     */
    public void sendMessage(String id, JSONObject jsonObject){
        pluginManager.getKTG8Client().sendMessage(this,id,jsonObject);
    }

    /**
     * 获取插件名称，必须是唯一的，不可变的
     */
    public String getName() {
        return name;
    }

    /**
     * 获取插件管理器
     */
    public ClientPluginManager getPluginManager() {
        return pluginManager;
    }

    public Logger getLogger() {
        return logger;
    }
}
