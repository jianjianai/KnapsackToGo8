package cn.jjaw.ktg8.server.core;


import cn.jja8.config.tool.YamlConfig;
import cn.jjaw.easyevent.EventManager;
import cn.jjaw.ktg8.server.builtin.Builtin;

import java.io.File;

import static cn.jjaw.ktg8.server.core.Logger.logger;

public class Main extends KTG8{
    public static void main(String[] args) {
        logger.info("""
                
                -----------------------------------
                ██╗  ██╗████████╗ ██████╗  █████╗
                ██║ ██╔╝╚══██╔══╝██╔════╝ ██╔══██╗
                █████╔╝    ██║   ██║  ███╗╚█████╔╝
                ██╔═██╗    ██║   ██║   ██║██╔══██╗
                ██║  ██╗   ██║   ╚██████╔╝╚█████╔╝
                ╚═╝  ╚═╝   ╚═╝    ╚═════╝  ╚════╝
                KnapsackToGo8 - 强大的通信数据中心
                -----------------------------------
                """);
        logger.info("正在启动..");
        eventManager = new EventManager();
        logger.info("正在加载配置文件 server.yaml ..");
        new YamlConfig()
                .load(new File("server.yaml"))
                .as(CommunicationConfig.class)
                .save(new File("server.yaml"));
        logger.info("正在加载服务器核心..");
        ktg8Server = new IKTG8Server();
        logger.info("正在加载插件管理器 ..");
        pluginManager = new IPluginManager();
        logger.info("正在加载配置文件 builtinPlugins.yaml ..");
        new YamlConfig()
                .load(new File("builtinPlugins.yaml"))
                .as(Builtin.class)
                .save(new File("builtinPlugins.yaml"));
        logger.info("正在加载插件包 ..");
        Builtin.initializationAll();
        ((IPluginManager)pluginManager).initialization();
        logger.info("正在初始化插件 ..");
        ((IPluginManager)pluginManager).load();
        logger.info("正在启动插件..");
        ((IPluginManager)pluginManager).enable();
        logger.info("正在启动 KTG8Server ..");
        ((IKTG8Server)ktg8Server).start();
    }


}