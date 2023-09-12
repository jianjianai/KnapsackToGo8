package cn.jjaw.ktg8.client.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 监听器管理器
 */
class IMessageListenerManager implements ClientMessageListenerManager {
    private final Map<String, Map<String, IMessageListenWorker>> listenerMap = new ConcurrentHashMap<>();

    /**
     * 注册一个监听器
     */
    @Override
    public IMessageListenWorker regListener(KTG8ClientPlugin plugin, String id, ClientMessageListener messageListener){
        Map<String, IMessageListenWorker> map = listenerMap.computeIfAbsent(plugin.getName(), k -> new HashMap<>());
        if(map.containsKey(id)){
            throw new Error(plugin.getName()+":"+id+" 的消息监听器被重复注册！");
        }
        IMessageListenWorker messageListenWorker = new IMessageListenWorker(plugin,id, messageListener);
        map.put(id,messageListenWorker);
        return messageListenWorker;
    }

    @Override
    public ClientMessageListenWorker removeListener(KTG8ClientPlugin plugin, String id) {
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
    public IMessageListenWorker getListenWorker(KTG8ClientPlugin plugin, String id){
        return getListenWorker(plugin.getName(),id);
    }
}
