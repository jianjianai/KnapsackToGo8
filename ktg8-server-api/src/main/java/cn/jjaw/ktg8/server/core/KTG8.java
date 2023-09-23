package cn.jjaw.ktg8.server.core;

import cn.jjaw.easyevent.EventManager;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class KTG8 {
    protected static KTG8Server ktg8Server;
    protected static PluginManager pluginManager;
    protected static ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    protected static EventManager eventManager;

    public static EventManager getEventManager() {
        return eventManager;
    }

    public static KTG8Server getKTG8Server(){
        return ktg8Server;
    }
    public static PluginManager getPluginManager(){
        return pluginManager;
    }

    /**
     * 获得线程池
     */
    public static ScheduledThreadPoolExecutor getExecutor(){
        return scheduledThreadPoolExecutor;
    }
}
