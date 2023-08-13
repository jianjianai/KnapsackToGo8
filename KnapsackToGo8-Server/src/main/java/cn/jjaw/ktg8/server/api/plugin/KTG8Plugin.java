package cn.jjaw.ktg8.server.api.plugin;

public interface KTG8Plugin {
    /**
     * 获取插件名称，必须是唯一的，不可变的
     */
    String getName();

    /**
     * 获取依赖列表，在列表中的插件会先加载
     */
    default String[] getDepends(){return new String[]{};}

    /**
     * 加载时，此时 KTG8Server 还未加载
     */
    default void onLoad(){}

    /**
     * 启动时，此时 KTG8Server 已加载完毕，但还没打开端口
     */
    default void onEnable(){}
}
