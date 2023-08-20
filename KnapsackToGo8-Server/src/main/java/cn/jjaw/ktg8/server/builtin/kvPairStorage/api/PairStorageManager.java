package cn.jjaw.ktg8.server.builtin.kvPairStorage.api;

import java.util.List;

public interface PairStorageManager {
    /**
     * 创建一个键值对库，如果已经有了就获取，建议使用 插件名称_库存名称命名
     * @param name 建议使用 插件名称_库存名称命名
     */
    PairStorage createPairStorage(String name);

    /**
     * 删除一个键值对库，包括全部数据
     */
    void deletePairStorage(String name);

    /**
     * 获取键值对库列表
     */
    List<String> getPairStorageList();

}
