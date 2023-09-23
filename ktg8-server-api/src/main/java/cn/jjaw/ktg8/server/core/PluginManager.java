package cn.jjaw.ktg8.server.core;

public abstract class PluginManager {
    /**
     * 获取指定名字的差价
     * @param name 插件的名字
     * @return 这个插件的事例对象 null则插件不存在
     */
   public abstract KTG8Plugin getKTG8Plugin(String name);

   protected abstract void addPlugin(KTG8Plugin ktg8Plugin);
}
