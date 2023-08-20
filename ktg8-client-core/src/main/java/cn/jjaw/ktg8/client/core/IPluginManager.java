package cn.jjaw.ktg8.client.core;

import cn.jjaw.ktg8.client.api.ClientPluginManager;
import cn.jjaw.ktg8.client.api.KTG8ClientPlugin;

import java.util.HashMap;
import java.util.Map;


public class IPluginManager implements ClientPluginManager {
    private final IKTG8Client ktg8Client;
    private final Map<String, KTG8ClientPlugin> pluginMap = new HashMap<>();

    public IPluginManager(IKTG8Client ktg8Client) {
        this.ktg8Client = ktg8Client;
    }

    /**
     * 添加一个插件
     */
     public void addPlugin(KTG8ClientPlugin ktg8Plugin){
         if(pluginMap.containsKey(ktg8Plugin.getName())){
             throw new Error(ktg8Plugin.getName()+" 有一个同名的插件已注册！");
         }
         pluginMap.put(ktg8Plugin.getName(),ktg8Plugin);
     }

    @Override
    public KTG8ClientPlugin createPlugin(String name) {
        return new KTG8ClientPlugin(this,name);
    }

    @Override
    public IKTG8Client getKTG8Client() {
        return ktg8Client;
    }
}
