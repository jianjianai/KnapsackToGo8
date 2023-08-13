package cn.jjaw.ktg8.server;


import cn.jja8.config.tool.YamlConfig;
import cn.jjaw.ktg8.server.core.communication.CommunicationConfig;
import cn.jjaw.ktg8.server.core.communication.IKTG8Server;
import cn.jjaw.ktg8.server.core.plugin.IPluginManager;

import java.io.File;

import static cn.jjaw.ktg8.server.Logger.logger;

public class Main {
    private static IKTG8Server ktg8Server;
    private static IPluginManager PluginManager;

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
        logger.info("正在加载配置文件 server.yaml ..");
        new YamlConfig()
                .load(new File("server.yaml"))
                .as(CommunicationConfig.class)
                .save(new File("server.yaml"));
        logger.info("正在加载服务器核心..");
        ktg8Server = new IKTG8Server();
        logger.info("正在加载插件管理器 ..");
        PluginManager = new IPluginManager();
        logger.info("正在加载插件包 ..");
        PluginManager.initialization();
        logger.info("正在初始化插件 ..");
        PluginManager.load();
        logger.info("正在启动插件..");
        PluginManager.enable();
        logger.info("正在启动 KTG8Server ..");
        ktg8Server.start();
    }


    public static IKTG8Server getKTG8Server() {
        return ktg8Server;
    }

    public static IPluginManager getPluginManager() {
        return PluginManager;
    }
}