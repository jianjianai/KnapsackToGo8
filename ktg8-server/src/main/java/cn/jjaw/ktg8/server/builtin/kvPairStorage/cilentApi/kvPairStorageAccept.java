package cn.jjaw.ktg8.server.builtin.kvPairStorage.cilentApi;

import java.util.ArrayList;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import cn.jjaw.ktg8.server.api.Client;
import cn.jjaw.ktg8.server.api.KTG8Plugin;
import cn.jjaw.ktg8.server.api.RequestAccept;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.KvPairStorage;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.api.PairStorageManager;

public class kvPairStorageAccept{
	private final PairStorageManager pairStorageManager;
	private final KvPairStorage kvPairStorage;
	private final RequestAccept getStorageList;
	public kvPairStorageAccept(KvPairStorage ktg8Plugin) {
		kvPairStorage = ktg8Plugin;
		pairStorageManager = ktg8Plugin.getPairStorageManager();
		getStorageList = new RequestAccept(ktg8Plugin, "getStorageList").setWorker(this::onGetStorageList).start();
	}


	private JSONObject onGetStorageList(Client client,JSONObject requestData){
		ArrayList list = new ArrayList<>(pairStorageManager.pairStorages());
		return JSONObject.from(list);
	}
    
    
}
