package cn.jjaw.ktg8.server.util;

import cn.jjaw.ktg8.server.core.*;
import cn.jjaw.ktg8.type.util.RADataRequestAccept;
import cn.jjaw.ktg8.type.util.RSRequestError;
import cn.jjaw.ktg8.type.util.RSRequestSend;
import com.alibaba.fastjson2.JSONObject;

/**
 * 一个请求接收器对象，一个简化通信的实现
 */
public class RequestAccept{
    private final KTG8Plugin ktg8Plugin;
    private final String listenerID;
    private final MessageListenerManager listenerManager;
    private Worker worker;

    public RequestAccept(KTG8Plugin ktg8Plugin, String listenerID) {
        this(KTG8.getKTG8Server().getMessageListenerManager(), ktg8Plugin,listenerID);
    }

    public RequestAccept(MessageListenerManager messageListenerManager, KTG8Plugin ktg8Plugin, String listenerID) {
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
    }

    private void onMessage(Client client, JSONObject data, MessageListenWorker messageListenWorker) {
        RADataRequestAccept requestAccept = data.to(RADataRequestAccept.class);
        JSONObject res = null;
        Throwable error = null;
        try {
            res =  worker.onRequest(client,requestAccept.data());
        }catch (Throwable throwable){
            error = throwable;
            throwable.printStackTrace();
        }
        //如果错误就返回错误
        a:if(error!=null){
            //如果接收错误就返回错误
            if (requestAccept.acceptError()){
                client.sendMessage(ktg8Plugin,listenerID,JSONObject.from(new RSRequestSend(
                        requestAccept.id(),
                        RSRequestSend.RSRequestSendType.error,
                        JSONObject.from(new RSRequestError(
                                RSRequestError.ErrorType.acceptThrowable,
                                error.toString()
                        ))
                )));
                break a;
            }
            //如果是错误但是不接收错误但接收结果就返回空
            if (requestAccept.acceptResponse()){
                client.sendMessage(ktg8Plugin,listenerID,JSONObject.from(new RSRequestSend(
                        requestAccept.id(),
                        RSRequestSend.RSRequestSendType.noOne,
                        null
                )));
                break a;
            }
        }else {
            //如果没错误就返回结果,接收结果就返回结果
            if (requestAccept.acceptResponse()){
                client.sendMessage(ktg8Plugin,listenerID,JSONObject.from(new RSRequestSend(
                        requestAccept.id(),
                        RSRequestSend.RSRequestSendType.ok,
                        res
                )));
                break a;
            }
            //如果不接收结果，但接收错误就返回空
            if (requestAccept.acceptError()){
                client.sendMessage(ktg8Plugin,listenerID,JSONObject.from(new RSRequestSend(
                        requestAccept.id(),
                        RSRequestSend.RSRequestSendType.noOne,
                        null
                )));
                break a;
            }
        }
    }

    /**
     * 设置请求处理器
     * @param worker 建议使用lambda表达式或者方法引用
     */
    public RequestAccept setWorker(Worker worker){
        if (worker==null){
            throw new NullPointerException("worker is null");
        }
        this.worker = worker;
        return this;
    }
    /**
     * 开始接收请求
     */
    public RequestAccept start(){
        if (worker==null){
            throw new Error("在接收请求之前需要先使用setWorker方法设置请求处理器");
        }
        listenerManager.regListener(ktg8Plugin,listenerID,this::onMessage);
        return this;
    }
    /**
     * 停止接收请求
     */
    public RequestAccept stop(){
        listenerManager.removeListener(ktg8Plugin,listenerID);
        return this;
    }

    public interface Worker{
        /**
         * 当收到请求
         * @param client 发起请求的客户端
         * @param requestData 请求的数据
         * @return 返回请求结恶
         */
        JSONObject onRequest(Client client,JSONObject requestData);
    }
}
