package cn.jjaw.ktg8.server.core;

import cn.jjaw.ktg8.server.api.KTG8Plugin;
import cn.jjaw.ktg8.server.api.PluginManager;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import static cn.jjaw.ktg8.server.core.Logger.logger;

public class IPluginManager implements PluginManager {
     private final Map<String, KTG8Plugin> pluginMap = new HashMap<>();
     private boolean isCanAdd = true;

    /**
     * 添加一个插件
     */
     public void addPlugin(KTG8Plugin ktg8Plugin){
         if(!isCanAdd){
             throw new Error("加载阶段已结束，无法继续添加插件！必须load前触发添加插件。");
         }
         if(pluginMap.containsKey(ktg8Plugin.getName())){
             throw new Error(ktg8Plugin.getName()+" 有一个同名的插件已注册！");
         }
         pluginMap.put(ktg8Plugin.getName(),ktg8Plugin);
     }
    /**
     * 初始化插件列表
     */
    public void initialization(){
        File plugins = new File("plugins");
        plugins.mkdirs();
        File[] fs = plugins.listFiles();
        if(fs==null){
            return;
        }
        File[] jars = Arrays.stream(fs)
                .filter(file -> file.isFile() && file.getName().toLowerCase().endsWith(".jar"))
                .toArray(File[]::new);

        for (File jar : jars) {
            logger.info("正在加载插件包 "+jar);
            URL url = null;
            try {
                url = jar.toURI().toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if(url!=null){
                ClassLoader classLoader = new URLClassLoader(new URL[]{url},this.getClass().getClassLoader());
                InputStream inputStream = classLoader.getResourceAsStream("KTG8Plugin.json");
                if(inputStream==null){
                    logger.warn("在 "+url+" 中找不到 KTG8Plugin.json 文件，这是个KTG8插件吗？");
                    continue;
                }
                JSONObject pluginConfig;
                try {
                    pluginConfig = JSON.parseObject(inputStream);
                }catch (JSONException e){
                    logger.warn("在 "+url+" 中找不到 KTG8Plugin.json 文件解析错误！");
                    e.printStackTrace();
                    continue;
                }
                if(pluginConfig==null){
                    logger.warn("在 "+url+" 中找不到 KTG8Plugin.json 文件为空。json提示: {\"main\":\"your.KTG8Plugin.class\"}");
                    continue;
                }
                String main = pluginConfig.getString("main");
                if(main==null){
                    logger.warn("在 "+url+" 中找不到 KTG8Plugin.json 中找不到main属性。json提示: {\"main\":\"your.KTG8Plugin.class\"}");
                    continue;
                }
                Class<?> theClass;
                try {
                    theClass = classLoader.loadClass(main);
                } catch (ClassNotFoundException e) {
                    logger.warn("在 "+url+" 中找不到 "+main+" 类!");
                    e.printStackTrace();
                    continue;
                }
                if (!theClass.isNestmateOf(KTG8Plugin.class)) {
                    logger.warn("在 "+url+" 中 "+main+" 类,需要实现 KTG8Plugin 接口才能作为插件被加载。");
                    continue;
                }
                Constructor<?> constructor;
                try {
                    constructor = theClass.getConstructor();
                }catch (NoSuchMethodException e){
                    logger.warn("在 "+url+" 中 "+main+" 类,需要有一个无参构造方法才能作为插件加载。");
                    continue;
                }
                try {
                    constructor.newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    logger.warn("在 "+url+" 中 "+main+" 类,的无参构造方法必须为public");
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }

    /**
     * 运行load
     */
    public void load(){
        isCanAdd = false;
        dependRun(ktg8Plugin -> {
            try {
                logger.info("初始化插件 "+ktg8Plugin.getName());
                ktg8Plugin.onLoad();
            }catch (Throwable e){
                e.printStackTrace();
            }
        });
    }

    /**
     * 运行enable
     */
    public void enable(){
        isCanAdd = false;
        dependRun(ktg8Plugin -> {
            try {
                logger.info("启动插件 "+ktg8Plugin.getName());
                ktg8Plugin.onEnable();
            }catch (Throwable e){
                e.printStackTrace();
            }
        });
    }


    /**
     * 按照依赖顺序有序执行run方法
     */
    void dependRun(KTG8PluginRun run){
        HashMap<String,KTG8Plugin> map = new HashMap<>(pluginMap);
        ArrayList<String> runed = new ArrayList<>();
        while (true){
            Iterator<Map.Entry<String, KTG8Plugin>> iterator = map.entrySet().iterator();
            if(!iterator.hasNext()){
                break;
            }
            KTG8Plugin ktg8Plugin = iterator.next().getValue();
            iterator.remove();
            runed.add(ktg8Plugin.getName());
            dependRun(run,map,runed,ktg8Plugin);
        }
    }

    void dependRun(KTG8PluginRun run, HashMap<String,KTG8Plugin> noRun, List<String> runed, KTG8Plugin ktg8Plugin){
        String[] depends = ktg8Plugin.getDepends();
        if(depends==null){
            run.run(ktg8Plugin);
            return;
        }
        for (String depend : depends) {
            KTG8Plugin ktg8PluginDepend = noRun.remove(depend);
            if(ktg8PluginDepend==null){
                if(!runed.contains(depend)){
                    logger.warn(ktg8Plugin.getName()+"插件要求的依赖"+depend+"不存在！可能导致插件无法正常运行。");
                }
                continue;
            }
            runed.add(ktg8PluginDepend.getName());
            dependRun(run,noRun,runed,ktg8PluginDepend);
        }
        run.run(ktg8Plugin);
    }

    interface KTG8PluginRun{
        void run(KTG8Plugin ktg8Plugin);
    }

    @Override
    public KTG8Plugin getKTG8Plugin(String name) {
        return pluginMap.get(name);
    }


}
