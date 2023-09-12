package cn.jjaw.ktg8.server.builtin.kvPairLookStorage;

import java.util.HashMap;

import cn.jjaw.ktg8.server.builtin.kvPairStorage.PairStorageManager;

public class PairLookStorageManager {

    PairStorageManager pairStorageManager;
    HashMap<String,PairLookStorage> LookedPairLookStorageMap = new HashMap<>();

    PairLookStorageManager(PairStorageManager pairStorageManager){
        this.pairStorageManager = pairStorageManager;
    }
}
