package cn.jjaw.ktg8.client.util;

import cn.jjaw.ktg8.client.core.ClientMessageListenWorker;
import cn.jjaw.ktg8.client.core.ClientMessageListenerManager;
import cn.jjaw.ktg8.client.core.KTG8Client;
import cn.jjaw.ktg8.client.core.KTG8ClientPlugin;
import cn.jjaw.ktg8.type.util.RADataRequestAccept;
import cn.jjaw.ktg8.type.util.RSRequestError;
import cn.jjaw.ktg8.type.util.RSRequestSend;
import com.alibaba.fastjson2.JSONObject;

/**
 * 一个请求接收器对象，一个简化通信的实现
 */
public class ClientRequestAccept {
    private final KTG8ClientPlugin ktg8Plugin;
    private final String listenerID;
    private final ClientMessageListenerManager listenerManager;
    private Worker worker;

    public ClientRequestAccept(KTG8Client ktg8Client, KTG8ClientPlugin ktg8Plugin, String listenerID) {
        this(ktg8Client.getMessageListenerManager(),ktg8Plugin,listenerID);
    }
    public ClientRequestAccept(ClientMessageListenerManager messageListenerManager, KTG8ClientPlugin ktg8Plugin, String listenerID) {
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

    private void onMessage(JSONObject data, ClientMessageListenWorker messageListenWorker) {
        RADataRequestAccept requestAccept = data.to(RADataRequestAccept.class);
        JSONObject res = null;
        Throwable error = null;
        try {
            res =  worker.onRequest(requestAccept.data());
        }catch (Throwable throwable){
            error = throwable;
            throwable.printStackTrace();
        }
        //如果错误就返回错误
        a:if(error!=null){
            //如果接收错误就返回错误
            if (requestAccept.acceptError()){
                ktg8Plugin.sendMessage(listenerID,JSONObject.from(new RSRequestSend(
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
                ktg8Plugin.sendMessage(listenerID,JSONObject.from(new RSRequestSend(
                        requestAccept.id(),
                        RSRequestSend.RSRequestSendType.noOne,
                        null
                )));
                break a;
            }
        }else {
            //如果没错误就返回结果,接收结果就返回结果
            if (requestAccept.acceptResponse()){
                ktg8Plugin.sendMessage(listenerID,JSONObject.from(new RSRequestSend(
                        requestAccept.id(),
                        RSRequestSend.RSRequestSendType.ok,
                        res
                )));
                break a;
            }
            //如果不接收结果，但接收错误就返回空
            if (requestAccept.acceptError()){
                ktg8Plugin.sendMessage(listenerID,JSONObject.from(new RSRequestSend(
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
    public ClientRequestAccept setWorker(Worker worker){
        if (worker==null){
            throw new NullPointerException("worker is null");
        }
        this.worker = worker;
        return this;
    }
    /**
     * 开始接收请求
     */
    public ClientRequestAccept start(){
        if (worker==null){
            throw new Error("在接收请求之前需要先使用setWorker方法设置请求处理器");
        }
        listenerManager.regListener(ktg8Plugin,listenerID,this::onMessage);
        return this;
    }
    /**
     * 停止接收请求
     */
    public ClientRequestAccept stop(){
        listenerManager.removeListener(ktg8Plugin,listenerID);
        return this;
    }

    public interface Worker{
        /**
         * 当收到请求
         * @param requestData 请求的数据
         * @return 返回请求结恶
         */
        JSONObject onRequest(JSONObject requestData);
    }
}
