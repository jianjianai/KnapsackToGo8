package cn.jjaw.ktg8.server.builtin.kvPairStorage.cilentApi;

import cn.jjaw.ktg8.server.builtin.kvPairStorage.KvPairStorage;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.KvPairStorageApi;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.PairStorage;
import cn.jjaw.ktg8.server.builtin.kvPairStorage.PairStorageManager;
import cn.jjaw.ktg8.server.core.Client;
import cn.jjaw.ktg8.server.util.RecordRequestAccept;
import cn.jjaw.ktg8.type.builtin.kvPairStorage.cilentApi.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cn.jjaw.ktg8.type.builtin.kvPairStorage.cilentApi.RequestName.*;

public class kvPairStorageAccept{
	private final PairStorageManager pairStorageManager;
	private final KvPairStorage kvPairStorage;

	private final RecordRequestAccept<PairStoragesS,PairStoragesC> pairStorages;
	private final RecordRequestAccept<GetPairStorageS,GetPairStorageC> getPairStorage;
	private final RecordRequestAccept<CreatePairStorageS,CreatePairStorageC> createPairStorage;

	private final Map<String,PairStorage> storageKeyMap = new ConcurrentHashMap<>();
	public kvPairStorageAccept(KvPairStorage ktg8Plugin) {
		kvPairStorage = ktg8Plugin;
		pairStorageManager = KvPairStorageApi.getPairStorageManager();
		pairStorages = new RecordRequestAccept<>(ktg8Plugin, PairStorages, PairStoragesS.class,PairStoragesC.class)
				.setWorker(this::pairStorages).start();
		getPairStorage = new RecordRequestAccept<>(ktg8Plugin, GetPairStorage, GetPairStorageS.class, GetPairStorageC.class)
				.setWorker(this::getPairStorage).start();
		createPairStorage = new RecordRequestAccept<>(ktg8Plugin,CreatePairStorage, CreatePairStorageS.class, CreatePairStorageC.class)
				.setWorker(this::createPairStorage).start();
	}


	/**
	 * 获取其库存列表
	 */
	private PairStoragesC pairStorages(Client client,PairStoragesS data){
		ArrayList<String> list = new ArrayList<>(pairStorageManager.pairStorages());
		return new PairStoragesC(list);
	}

	/**
	 * 加载指定库存
	 */
	private GetPairStorageC getPairStorage(Client client,GetPairStorageS request){
		PairStorage pairStorage = storageKeyMap.computeIfAbsent(request.storageName(), pairStorageManager::getPairStorage);
		boolean ok = pairStorage!=null;
		return new GetPairStorageC(ok);
	}

	/**
	 * 创建指定库存，如果已经存在则加载
	 */
	private CreatePairStorageC createPairStorage(Client client,CreatePairStorageS request){
		PairStorage pairStorage = storageKeyMap.computeIfAbsent(request.storageName(), pairStorageManager::createPairStorage);
		boolean ok = pairStorage!=null;
		return new CreatePairStorageC(ok);
	}
    
    
}
