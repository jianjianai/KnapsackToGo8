package cn.jjaw.ktg8.server.builtin.kvPairStorage.cilentApi;

import cn.jjaw.ktg8.server.builtin.kvPairStorage.KvPairStorage;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.KvPairStorageApi;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.PairStorage;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.PairStorageManager;
import cn.jjaw.ktg8.server.core.Client;
import cn.jjaw.ktg8.server.util.RequestAccept;
import cn.jjaw.ktg8.type.builtin.kvPairStorage.cilentApi.*;
import com.alibaba.fastjson2.JSONObject;
import static cn.jjaw.ktg8.type.builtin.kvPairStorage.cilentApi.RequestName.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class kvPairStorageAccept{
	private final PairStorageManager pairStorageManager;
	private final KvPairStorage kvPairStorage;

	private final RequestAccept pairStorages;
	private final RequestAccept getPairStorage;
	private final RequestAccept createPairStorage;

	private final Map<String,PairStorage> storageKeyMap = new ConcurrentHashMap<>();
	public kvPairStorageAccept(KvPairStorage ktg8Plugin) {
		kvPairStorage = ktg8Plugin;
		pairStorageManager = KvPairStorageApi.getPairStorageManager();
		pairStorages = new RequestAccept(ktg8Plugin, PairStorages).setWorker(this::pairStorages).start();
		getPairStorage = new RequestAccept(ktg8Plugin, GetPairStorage).setWorker(this::getPairStorage).start();
		createPairStorage = new RequestAccept(ktg8Plugin,CreatePairStorage).setWorker(this::createPairStorage).start();
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

	/**
	 * 创建指定库存，如果已经存在则加载
	 */
	private JSONObject createPairStorage(Client client,JSONObject requestData){
		CreatePairStorageS request = requestData.to(CreatePairStorageS.class);
		PairStorage pairStorage = storageKeyMap.computeIfAbsent(request.storageName(), pairStorageManager::createPairStorage);
		boolean ok = pairStorage!=null;
		return JSONObject.from(new CreatePairStorageC(ok));
	}
    
    
}
