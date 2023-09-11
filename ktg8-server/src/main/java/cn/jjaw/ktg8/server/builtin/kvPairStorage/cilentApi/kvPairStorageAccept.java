package cn.jjaw.ktg8.server.builtin.kvPairStorage.cilentApi;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import cn.jjaw.ktg8.server.api.Client;
import cn.jjaw.ktg8.server.api.RequestAccept;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.KvPairStorage;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.api.PairStorage;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.api.PairStorageManager;

public class kvPairStorageAccept{
	private final PairStorageManager pairStorageManager;
	private final KvPairStorage kvPairStorage;

	private final RequestAccept pairStorages;
	private final RequestAccept getPairStorage;

	private final Map<String,PairStorage> storageKeyMap = new ConcurrentHashMap<>();
	public kvPairStorageAccept(KvPairStorage ktg8Plugin) {
		kvPairStorage = ktg8Plugin;
		pairStorageManager = ktg8Plugin.getPairStorageManager();
		pairStorages = new RequestAccept(ktg8Plugin, "pairStorages").setWorker(this::pairStorages).start();
		getPairStorage = new RequestAccept(ktg8Plugin, "getPairStorage").setWorker(this::getPairStorage).start();
	}


	private JSONObject pairStorages(Client client,JSONObject requestData){
		// TODO 没有测试
		ArrayList list = new ArrayList<>(pairStorageManager.pairStorages());
		return JSONObject.from(list);
	}

	private JSONObject getPairStorage(Client client,JSONObject requestData){
		String storageName = requestData.getString("storageName");
		PairStorage pairStorage = storageKeyMap.computeIfAbsent(storageName, key->pairStorageManager.getPairStorage(key));
		boolean ok = false;
		if(pairStorage!=null){
			ok = true;
		}
		JSONObject json = new JSONObject();
		json.put("ok",ok);
		return json;
	}
    
    
}
