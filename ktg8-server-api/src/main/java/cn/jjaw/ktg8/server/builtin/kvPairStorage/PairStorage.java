package cn.jjaw.ktg8.server.builtin.kvPairStorage;

import java.util.Set;

public interface PairStorage {
    /**
     * 设置键值对，如果key已经存在则覆盖
     */
    void put(String key,String value);

    /**
     * 获取valve
     * @return null key不存在
     */
    String get(String key);

    /**
     * 删除
     * @return valve 如果为null key不存在
     */
    String remove(String key);
    /**
     * 清空全部数据
     */
    void clear();

    /**
     * 获取全部key列表
     */
    Set<String> keySet();
}
