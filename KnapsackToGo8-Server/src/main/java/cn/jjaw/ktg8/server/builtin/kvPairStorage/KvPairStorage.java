package cn.jjaw.ktg8.server.builtin.kvPairStorage;

import cn.jja8.config.tool.YamlConfig;
import cn.jjaw.ktg8.server.api.KTG8Plugin;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.api.PairStorageManager;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.pairManagers.sqlite.SqliteStorageManager;

import java.io.File;

/**
 * 键值对存储，持久化键值对
 */
public class KvPairStorage extends KTG8Plugin {
    private static KvPairStorage kvPairStorage;

    public static KvPairStorage getKvPairStorage() {
        return kvPairStorage;
    }

    private boolean ok = false;
    private PairStorageManager pairStorageManager;

    public KvPairStorage() {
        super("kvPairStorage");
        kvPairStorage = this;
    }

    public PairStorageManager getPairStorageManager() {
        if (!ok){
            throw new Error("数据库没有正确加载,请检查 KvPairStorageConfig.yaml 配置文件！ 可用列表: sqlite");
        }
        return pairStorageManager;
    }

    @Override
    public void onLoad() {
        getLogger().info("正在加载配置文件 KvPairStorageConfig.yaml ..");
        getDataFolder().mkdirs();
        File webServerConfig = new File(getDataFolder(),"KvPairStorageConfig.yaml");
        new YamlConfig().load(webServerConfig).as(KvPairStorageConfig.class).save(webServerConfig);
        getLogger().info("正在初始化数据管理器..");
        switch (KvPairStorageConfig.dataBase){
            case "sqlite" -> pairStorageManager = new SqliteStorageManager(this);
            default -> getLogger().error("数据库名称'"+KvPairStorageConfig.dataBase+"'不正确！可用列表: sqlite");
        }
        if (pairStorageManager!=null){
            ok = true;
        }
    }

    @Override
    public void onEnable() {
        if (!ok){return;}

    }
}
