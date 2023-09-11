import cn.jjaw.ktg8.server.Main;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.KvPairStorage;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.api.PairStorage;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.pairManagers.sqlite.SqliteStorageManager;

public class sqllite测试 {
    public static void main(String[] args) {
        Main.main(args);

        KvPairStorage kvPairStorage = KvPairStorage.getKvPairStorage();
        SqliteStorageManager manager = (SqliteStorageManager) kvPairStorage.getPairStorageManager();
        PairStorage qwe = manager.createPairStorage("qwe");
        System.out.println(manager.pairStorages());

        qwe.put("qwe","asd");
        qwe.put("qweq","asd");
        qwe.put("qwea","asd");

        System.out.println(qwe.get("asd"));
        System.out.println(qwe.get("qwe"));
        System.out.println(qwe.keySet());

        qwe.put("qwe","qwe");
        System.out.println(qwe.get("qwe"));

        qwe.remove("qwe");
        System.out.println(qwe.get("qwe"));

        qwe.clear();
    }
}
