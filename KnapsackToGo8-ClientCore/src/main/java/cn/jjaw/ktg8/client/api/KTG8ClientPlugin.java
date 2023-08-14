package cn.jjaw.ktg8.client.api;

import cn.jjaw.ktg8.client.core.IPluginManager;
import com.alibaba.fastjson2.JSONObject;

public class KTG8ClientPlugin {
    private final IPluginManager pluginManager;
    private final String name;

    public KTG8ClientPlugin(IPluginManager iPluginManager, String name) {
        this.name = name;
        this.pluginManager = iPluginManager;
        iPluginManager.addPlugin(this);
    }

    /**
     * 服务器发送消息，会阻塞线程
     */
    public void sendMessage(String id, JSONObject jsonObject){
        pluginManager.getKtg8Client().sendMessage(this,id,jsonObject);
    }

    /**
     * 获取插件名称，必须是唯一的，不可变的
     */
    public String getName() {
        return name;
    }
}
