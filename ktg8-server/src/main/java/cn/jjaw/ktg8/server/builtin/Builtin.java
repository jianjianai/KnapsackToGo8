package cn.jjaw.ktg8.server.builtin;


import cn.jja8.config.tool.Conf;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.KvPairStorage;
import cn.jjaw.ktg8.server.builtin.webAdmin.WebAdmin;

import java.util.ArrayList;
import java.util.List;

public class Builtin {
    public static List<Runnable> runnableList = new ArrayList<>();

    @Conf public static boolean webAdmin = true;
    @Conf public static boolean kvPairStorage = true;
    public static void initializationAll(){
        if (webAdmin){new WebAdmin();}
        if (kvPairStorage){new KvPairStorage();}
        for (Runnable runnable : runnableList) {try {runnable.run();}catch (Throwable throwable){throwable.printStackTrace();}}
    }
}
