package cn.jjaw.ktg8.server.builtin;


import java.util.ArrayList;
import java.util.List;

public class Builtin {
    public static List<Runnable> runnableList = new ArrayList<>();
    public static void initializationAll(){
        for (Runnable runnable : runnableList) {try {runnable.run();}catch (Throwable throwable){throwable.printStackTrace();}}
    }
}
