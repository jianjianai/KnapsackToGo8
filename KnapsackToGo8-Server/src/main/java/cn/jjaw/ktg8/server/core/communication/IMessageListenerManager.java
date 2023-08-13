package cn.jjaw.ktg8.server.core.communication;

import cn.jjaw.ktg8.server.api.communication.MessageListenWorker;
import cn.jjaw.ktg8.server.api.plugin.KTG8Plugin;
import cn.jjaw.ktg8.server.api.communication.MessageListener;
import cn.jjaw.ktg8.server.api.communication.MessageListenerManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 监听器管理器
 */
class IMessageListenerManager implements MessageListenerManager {
    private final Map<String, Map<String, IMessageListenWorker>> listenerMap = new ConcurrentHashMap<>();

    /**
     * 注册一个监听器
     */
    @Override
    public IMessageListenWorker regListener(KTG8Plugin plugin, String id, MessageListener messageListener){
        Map<String, IMessageListenWorker> map = listenerMap.computeIfAbsent(plugin.getName(), k -> new HashMap<>());
        if(map.containsKey(id)){
            throw new Error(plugin.getName()+":"+id+" 的消息监听器被重复注册！");
        }
        IMessageListenWorker messageListenWorker = new IMessageListenWorker(plugin,id, messageListener);
        map.put(id,messageListenWorker);
        return messageListenWorker;
    }

    @Override
    public MessageListenWorker removeListener(KTG8Plugin plugin, String id) {
        Map<String, IMessageListenWorker> map = listenerMap.get(plugin.getName());
        if(map==null){
            return null;
        }
        return map.remove(id);
    }

    /**
     * 获取一个监听器工作类
     */
    IMessageListenWorker getListenWorker(String pluginName, String id){
        Map<String, IMessageListenWorker> map = listenerMap.get(pluginName);
        if(map==null){
            return null;
        }
        return map.get(id);
    }

    /**
     * 获取一个监听器工作类
     */
    @Override
    public IMessageListenWorker getListenWorker(KTG8Plugin plugin, String id){
        return getListenWorker(plugin.getName(),id);
    }
}