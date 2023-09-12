package cn.jjaw.ktg8.server.builtin.kvPairLookStorage;

import cn.jjaw.ktg8.server.core.KTG8;
import cn.jjaw.ktg8.server.core.KTG8Plugin;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.KvPairStorage;

public class KvPairLookStorage extends KTG8Plugin{
    PairLookStorageManager pairLookStorageManager;
    

    public KvPairLookStorage() {
        super("kvPairLookStorage", new String[]{"kvPairStorage"});
    }

    @Override
    public void onEnable() {
        KvPairStorage kvPairStorage = (KvPairStorage)KTG8.getPluginManager().getKTG8Plugin("kvPairStorage");
        pairLookStorageManager = new PairLookStorageManager(kvPairStorage.getPairStorageManager());
    }

    public PairLookStorageManager getPairLookStorageManager() {
        return pairLookStorageManager;
    }
    
}
