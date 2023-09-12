package cn.jjaw.ktg8.server.builtin.kvPairStorage.pairManagers.sqlite;

import cn.jjaw.ktg8.server.builtin.kvPairStorage.KvPairStorage;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.PairStorage;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.PairStorageManager;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.slf4j.Logger;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SqliteStorageManager implements PairStorageManager {
    static class NameIllegal extends Error{
        public NameIllegal(String message) {
            super(message);
        }
    }
    private final Logger logger;
    private final SqlSessionFactory sql;
    private final Map<String,SqliteStorage> storageMap = new HashMap<>();

    public SqliteStorageManager(KvPairStorage kvPairStorage) {
        logger = kvPairStorage.getLogger();
        SQLiteDataSource dataSource = new SQLiteDataSource();
        String path = "jdbc:sqlite:"+new File(kvPairStorage.getDataFolder(),"PairStorage.db").getAbsolutePath();
        dataSource.setUrl(path);
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(SqlExecute.class);
        sql = new SqlSessionFactoryBuilder().build(configuration);
        logger.info("已连接到数据库:"+path);
    }

    public SqlSessionFactory getSql() {
        return sql;
    }

    /**
     * 检查名称是否合法，不合法则抛出异常
     */
    private void nameCheck(String name){
        if(name==null){
            throw new NullPointerException("name is null");
        }
        if(!name.matches("^[A-Za-z_]+$")){
            throw new NameIllegal("名称只能包含大小写字母和下划线");
        }
    }

    @Override
    public PairStorage createPairStorage(String name) {
        nameCheck(name);
        synchronized(storageMap){
            return storageMap.computeIfAbsent(name,key->SqliteStorage.create(this, name));
        }
       
    }

    @Override
    public PairStorage getPairStorage(String name) {
        nameCheck(name);
        synchronized(storageMap){
            return storageMap.computeIfAbsent(name,key->SqliteStorage.get(this, name));
        }
    }

    @Override
    public void deletePairStorage(String name) {
        nameCheck(name);
        throw new Error("sqlite目前还没实现删除功能");
    }


    @Override
    public Collection<String> pairStorages() {
        try (SqlSession session = sql.openSession()){
            SqlExecute execute = session.getMapper(SqlExecute.class);
            return execute.getTableList();
        }
    }


}
