package cn.jjaw.ktg8.server.builtin.kvPairStorage.pairManagers.sqlite;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Set;

public interface SqlExecute {
    @Update("create table if not exists '${name}'(k varchar(255) primary key,v text)")
    void createTable(@Param("name") String name);

    @Select("select name from sqlite_master where type='table'")
    List<String> getTableList();

    @Update("insert into '${table}'(k,v) values (#{key},#{valve})")
    int insert(@Param("table")String table,
               @Param("key")String key,
               @Param("valve")String valve);

    @Update("update '${table}' set v=#{valve} where k=#{key}")
    int update(@Param("table")String table,
               @Param("key")String key,
               @Param("valve")String valve);

    @Select("select v from '${table}' where k=#{key}")
    String select(@Param("table")String table,
                  @Param("key")String key);


    @Update("delete from '${table}' where k=#{key}")
    int delete(@Param("table")String table,
               @Param("key")String key);

    @Update("delete from '${table}'")
    int clear(@Param("table")String table);

    @Select("select k from '${table}'")
    Set<String> keySet(@Param("table")String table);
}
