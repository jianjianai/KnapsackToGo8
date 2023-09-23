package cn.jjaw.ktg8.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public abstract class KTG8Plugin {
    private final Logger logger;
    private final PluginManager pluginManager;
    private final String name;
    private final String[] depends;
    private final File dataFolder;

    public KTG8Plugin(String name) {
        this(name,null);
    }
    public KTG8Plugin(String name,String[] depends){
        if(name==null){
            throw new NullPointerException("name is null");
        }
        pluginManager = KTG8.getPluginManager();
        if (pluginManager==null){
            throw new Error("插件必须在PluginManager实例化之后才能实例化！");
        }
        this.name = name;
        this.depends = depends;
        pluginManager.addPlugin(this);
        logger = LoggerFactory.getLogger(name);
        dataFolder = new File(new File("plugins"),name);
    }

    /**
     * 获取插件名称，必须是唯一的，不可变的
     */
    public final String getName(){
        return name;
    }

    /**
     * 获取依赖列表，在列表中的插件会先加载
     */
    public final String[] getDepends(){
        return depends;
    }

    /**
     * 获取数据文件夹
     */
    public File getDataFolder(){
        return dataFolder;
    }


    @Override
    public final int hashCode() {
        return name.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return name.equals(obj);
    }


    public Logger getLogger() {
        return logger;
    }

    /**
     * 加载时，此时 KTG8Server 还未加载
     */
    public void onLoad(){}

    /**
     * 启动时，此时 KTG8Server 已加载完毕，但还没打开端口
     */
    public abstract void onEnable();
}
