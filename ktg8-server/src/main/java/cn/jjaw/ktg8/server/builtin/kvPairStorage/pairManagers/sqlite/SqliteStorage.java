package cn.jjaw.ktg8.server.builtin.kvPairStorage.pairManagers.sqlite;

import cn.jjaw.ktg8.server.builtin.kvPairStorage.PairStorage;
import org.apache.ibatis.session.SqlSession;

import java.util.Set;

public class SqliteStorage implements PairStorage {

    static SqliteStorage create(SqliteStorageManager manager, String name){
        try (SqlSession session = manager.getSql().openSession()){
            SqlExecute execute = session.getMapper(SqlExecute.class);
            execute.createTable(name);
            session.commit();
        }
        return new SqliteStorage(manager,name);
    }

    static SqliteStorage get(SqliteStorageManager manager, String name){
        try (SqlSession session = manager.getSql().openSession()){
            SqlExecute execute = session.getMapper(SqlExecute.class);
            String theName = execute.isTableExist(name);
            if(theName==null){
                return null;
            }
        }
        return new SqliteStorage(manager,name);
    }

    private final SqliteStorageManager manager;
    private final String name;

    private SqliteStorage(SqliteStorageManager manager, String name) {
        this.manager = manager;
        this.name = name;
    }

    @Override
    public void put(String key, String value) {
        try (SqlSession session = manager.getSql().openSession()){
            SqlExecute execute = session.getMapper(SqlExecute.class);
            if(execute.update(name,key,value)<=0){
                execute.insert(name,key,value);
            }
            session.commit();
        }
    }

    @Override
    public String get(String key) {
        try (SqlSession session = manager.getSql().openSession()){
            SqlExecute execute = session.getMapper(SqlExecute.class);
            return execute.select(name,key);
        }
    }

    @Override
    public String remove(String key) {
        try (SqlSession session = manager.getSql().openSession()){
            SqlExecute execute = session.getMapper(SqlExecute.class);
            String s = execute.select(name,key);
            if (s!=null){
                execute.delete(name,key);
            }
            session.commit();
            return s;
        }
    }

    @Override
    public void clear() {
        try (SqlSession session = manager.getSql().openSession()){
            SqlExecute execute = session.getMapper(SqlExecute.class);
            execute.clear(name);
            session.commit();
        }
    }

    @Override
    public Set<String> keySet() {
        try (SqlSession session = manager.getSql().openSession()){
            SqlExecute execute = session.getMapper(SqlExecute.class);
            return execute.keySet(name);
        }
    }
}
