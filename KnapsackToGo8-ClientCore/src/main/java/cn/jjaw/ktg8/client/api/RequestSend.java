package cn.jjaw.ktg8.client.api;

import cn.jjaw.ktg8.type.core.RADataRequestAccept;
import cn.jjaw.ktg8.type.core.RSRequestError;
import cn.jjaw.ktg8.type.core.RSRequestSend;
import com.alibaba.fastjson2.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cn.jjaw.ktg8.client.core.Logger.logger;

/**
 *  一个请求接发送器对象，一个简化通信的实现
 */
public class RequestSend {
    private final KTG8ClientPlugin ktg8Plugin;
    private final String listenerID;
    private final ClientMessageListenerManager listenerManager;
    private boolean isStart = false;
    private final Map<Long,Accepts> acceptsMap = new ConcurrentHashMap<>();
    private long nextID = 0;

    public RequestSend(ClientMessageListenerManager messageListenerManager,KTG8ClientPlugin ktg8Plugin,String listenerID) {
        this.ktg8Plugin = ktg8Plugin;
        this.listenerID = listenerID;
        this.listenerManager = messageListenerManager;
    }

    private void onMessage(JSONObject data, ClientMessageListenWorker messageListenWorker) {
        RSRequestSend requestSend = data.to(RSRequestSend.class);
        Accepts accepts = acceptsMap.remove(requestSend.id());
        if(accepts==null){
            logger.warn("RequestSend 收到 "+ktg8Plugin.getName()+":"+listenerID+"消息 ID:"+requestSend.id()+" 不存在或已被处理:"+requestSend);
            return;
        }
        if(requestSend.type()==null){
            logger.warn("RequestSend 收到 "+ktg8Plugin.getName()+":"+listenerID+"消息 ID:"+requestSend.id()+" 不规范，type=null :"+requestSend);
            return;
        }
        try {
            switch (requestSend.type()){
                case ok -> {
                    accepts.acceptResponse().accept(requestSend.data());
                }
                case error -> {
                    RSRequestError error = requestSend.data().to(RSRequestError.class);
                    accepts.acceptError().accept(error.code(),error.reason());
                }
                case noOne -> {}
            }
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }
    }

    /**
     * 发送请求
     * @param requestData 请求数据
     * @param onResponse 当成功响应，不关心可设置为null
     * @param onError 当对方发生错误，一般是对方Worker方法抛出异常时调用。如果不关心可设置为null
     */
    public RequestSend sendRequest(JSONObject requestData,AcceptResponse onResponse,AcceptError onError){
        if(!isStart){
            throw new Error("RequestSend在启动之前不能发送消息，需要先使用start()方法启动后再发送消息。");
        }
        nextID++;
        boolean acceptResponse = onResponse!=null;
        boolean acceptError = onError!=null;
        if (acceptResponse || acceptError){
            acceptsMap.put(nextID,new Accepts(onResponse,onError));
        }
        ktg8Plugin.sendMessage(listenerID, JSONObject.from(
                new RADataRequestAccept(nextID,acceptResponse,acceptError,requestData))
        );
        return this;
    }

    /**
     * 开始发送
     */
    public RequestSend start(){
        listenerManager.regListener(ktg8Plugin,listenerID,this::onMessage);
        isStart = true;
        return this;
    }

    /**
     * 停止发送
     */
    public RequestSend stop(){
        listenerManager.removeListener(ktg8Plugin,listenerID);
        isStart = false;
        return this;
    }

    record Accepts(AcceptResponse acceptResponse,AcceptError acceptError){}

    public interface AcceptResponse{
        /**
         * @param response 响应结果
         */
        void accept(JSONObject response);
    }

    public interface AcceptError{
        /**
         * @param code 错误代码
         * @param reason 原因
         */
        void accept(String code,String reason);
    }
}
