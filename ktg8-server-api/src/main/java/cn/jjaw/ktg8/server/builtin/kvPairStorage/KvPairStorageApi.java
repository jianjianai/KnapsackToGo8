package cn.jjaw.ktg8.server.builtin.kvPairStorage;

public class KvPairStorageApi {
    /**
     * 获取键值对库管理器
     */
    public PairStorageManager getPairStorageManager() {
        KvPairStorage kvPairStorage = KvPairStorage.getKvPairStorage();
        if (kvPairStorage==null) {
            throw new Error("KvPairStorage 内置插件还未启动，需要在内置插件启动后才可获取PairStorageManager");
        }
        return kvPairStorage.getPairStorageManager();
    }
}
