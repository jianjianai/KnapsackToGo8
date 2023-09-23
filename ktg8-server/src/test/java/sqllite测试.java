import cn.jjaw.ktg8.server.core.Main;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.KvPairStorageApi;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.PairStorage;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.PairStorageManager;

public class sqllite测试 {
    public static void main(String[] args) {
        Main.main(args);

        PairStorageManager manager =  KvPairStorageApi.getPairStorageManager();
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
