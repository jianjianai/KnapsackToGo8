package cn.jjaw.ktg8.server.builtin.kvPairStorage.pairManagers.sqlite;

import cn.jjaw.ktg8.server.builtin.kvPairStorage.KvPairStorage;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.api.PairStorageManager;
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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlitePairManager implements PairStorageManager {
    private Logger logger;
    private SqlSessionFactory sqlSessionFactory;

    public SqlitePairManager(KvPairStorage kvPairStorage) {
        logger = kvPairStorage.getLogger();
        SQLiteDataSource dataSource = new SQLiteDataSource();
        String path = "jdbc:sqlite:"+new File(kvPairStorage.getDataFolder(),"PairStorage.db").getAbsolutePath();
        dataSource.setUrl(path);
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(SqlExecute.class);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        try (SqlSession session = sqlSessionFactory.openSession()){
            try (Statement statement = session.getConnection().createStatement();){
                statement.execute("create table data(k varchar(255),v text)");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        logger.info("已连接到数据库:"+path);
    }
}
