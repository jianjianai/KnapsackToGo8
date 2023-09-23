package cn.jjaw.ktg8.server.core;

import cn.jjaw.ktg8.type.core.RADataRequestAccept;
import cn.jjaw.ktg8.type.core.RSRequestError;
import cn.jjaw.ktg8.type.core.RSRequestError.ErrorType;
import cn.jjaw.ktg8.type.core.RSRequestSend;
import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *  一个请求接发送器对象，一个简化通信的实现
 */
public class RequestSend {
    private final Logger logger;
    private final ScheduledThreadPoolExecutor executor = KTG8.getExecutor();
    private final KTG8Plugin ktg8Plugin;
    private final String listenerID;
    private final MessageListenerManager listenerManager;
    private boolean isStart = false;
    private final Map<Long,Accepts> acceptsMap = new ConcurrentHashMap<>();
    private long nextID = 0;
    private long timeOut = 30;//超时时间，30秒

    public RequestSend(KTG8Plugin ktg8Plugin, String listenerID) {
        this(KTG8.getKTG8Server().getMessageListenerManager(), ktg8Plugin,listenerID);
    }
    public RequestSend(MessageListenerManager messageListenerManager,KTG8Plugin ktg8Plugin,String listenerID) {
        if (messageListenerManager==null){
            throw new NullPointerException("messageListenerManager is null");
        }
        if (ktg8Plugin==null){
            throw new NullPointerException("ktg8Plugin is null");
        }
        if (listenerID==null){
            throw new NullPointerException("listenerID is null");
        }
        this.ktg8Plugin = ktg8Plugin;
        this.listenerID = listenerID;
        this.listenerManager = messageListenerManager;
        this.logger = ktg8Plugin.getLogger();
    }

    private void onMessage(Client client, JSONObject data, MessageListenWorker messageListenWorker) {
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
     * 发送请求，此方法不会柱塞线程
     * @param requestData 请求数据
     * @param onResponse 当成功响应，不关心可设置为null
     * @param onError 当对方发生错误，一般是对方Worker方法抛出异常时调用。如果不关心可设置为null
     */
    public RequestSend sendRequest(Client client,JSONObject requestData,AcceptResponse onResponse,AcceptError onError){
        if(!isStart){
            throw new Error("RequestSend在启动之前不能发送消息，需要先使用start()方法启动后再发送消息。");
        }
        final long theID;
        synchronized (this){
            nextID++;
            theID = nextID;
        }
        boolean acceptResponse = onResponse!=null;
        boolean acceptError = onError!=null;
        if (acceptResponse || acceptError){
            acceptsMap.put(theID,new Accepts(onResponse,onError));
            //设置超时处理
            executor.schedule(()->{
                Accepts accepts = acceptsMap.remove(theID);
                if (accepts==null || accepts.acceptError()==null){
                    return;
                }
                try {
                    accepts.acceptError().accept(ErrorType.timeOut,"请求超时,"+timeOut+"秒没有收到回复，对方正常处理了请求吗？");
                }catch (Throwable throwable){
                    throwable.printStackTrace();
                }
            },timeOut, TimeUnit.SECONDS);
        }
        executor.execute(()->{
            try {
                client.sendMessage(ktg8Plugin,listenerID, JSONObject.from(
                        new RADataRequestAccept(nextID,acceptResponse,acceptError,requestData))
                );
            }catch (Throwable throwable){
                throwable.printStackTrace();
                Accepts accepts = acceptsMap.remove(theID);
                if (accepts==null || accepts.acceptError()==null){
                    return;
                }
                try {
                    accepts.acceptError().accept(ErrorType.sendError,throwable.toString());
                }catch (Throwable throwable1){
                    throwable1.printStackTrace();
                }

            }
        });

        return this;
    }

    /**
     * 设置请求超时时间，对方多久不回复为超时
     * @param timeOut 超时时间，单位秒
     */
    public RequestSend setTimeOut(long timeOut) {
        this.timeOut = timeOut;
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
        void accept(ErrorType code,String reason);
    }
}
