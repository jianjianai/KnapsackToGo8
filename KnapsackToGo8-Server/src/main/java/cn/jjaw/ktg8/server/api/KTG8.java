package cn.jjaw.ktg8.server.api;

import cn.jjaw.ktg8.server.Main;

public class KTG8 {
    public static KTG8Server getKTG8Server(){
        return Main.getKTG8Server();
    }
    public static PluginManager getPluginManager(){
        return Main.getPluginManager();
    }
}
