package cn.jjaw.ktg8.server.builtin.kvPairStorage.cilentApi;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.jjaw.ktg8.type.builtin.kvPairStorage.cilentApi.GetPairStorageC;
import cn.jjaw.ktg8.type.builtin.kvPairStorage.cilentApi.GetPairStorageS;
import cn.jjaw.ktg8.type.builtin.kvPairStorage.cilentApi.PairStoragesC;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import cn.jjaw.ktg8.server.core.Client;
import cn.jjaw.ktg8.server.core.RequestAccept;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.KvPairStorage;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.PairStorage;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.PairStorageManager;

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


	/**
	 * 获取其库存列表
	 */
	private JSONObject pairStorages(Client client,JSONObject requestData){
		ArrayList<String> list = new ArrayList<>(pairStorageManager.pairStorages());
		return JSONObject.from(new PairStoragesC(list));
	}

	/**
	 * 加载指定库存
	 */
	private JSONObject getPairStorage(Client client,JSONObject requestData){
		GetPairStorageS request = requestData.to(GetPairStorageS.class);
		PairStorage pairStorage = storageKeyMap.computeIfAbsent(request.storageName(), pairStorageManager::getPairStorage);
		boolean ok = pairStorage!=null;
		return JSONObject.from(new GetPairStorageC(ok));
	}
    
    
}
