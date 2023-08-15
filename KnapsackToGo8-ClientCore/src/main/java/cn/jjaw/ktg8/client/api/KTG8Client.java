package cn.jjaw.ktg8.client.api;

import cn.jjaw.ktg8.type.core.ServerType;
import com.alibaba.fastjson2.JSONObject;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public interface KTG8Client {

    /**
     * 获取服务器类型
     */
    ServerType getServerType();

    /**
     * 获取服务器ID
     */
    String getServerID();

    /**
     * 服务器发送消息，会阻塞线程
     */
    void sendMessage(KTG8ClientPlugin plugin, String id, JSONObject jsonObject);

    /**
     * 获取消息管理器
     */
    ClientMessageListenerManager getMessageListenerManager();

    /**
     * 获取插件管理器
     */
    ClientPluginManager getPluginManager();

    /***
     * 获取线程池，用于定时任务等
     */
    ScheduledThreadPoolExecutor getExecutor();
}
