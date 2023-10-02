package cn.jjaw.ktg8.client.util;

import cn.jjaw.ktg8.client.core.ClientMessageListenerManager;
import cn.jjaw.ktg8.client.core.KTG8Client;
import cn.jjaw.ktg8.client.core.KTG8ClientPlugin;
import com.alibaba.fastjson2.JSONObject;

/**
 *  一个请求接发送器对象，一个简化通信的实现
 */
public class RecordRequestSend<Send extends Record,R extends Record> {
    ClientRequestSend requestSend;
    Class<Send> sendClass;
    Class<R> returnClass;
    public RecordRequestSend(KTG8Client ktg8Client, KTG8ClientPlugin ktg8Plugin, String listenerID, Class<Send> sendClass, Class<R> returnClass) {
        requestSend = new ClientRequestSend(ktg8Client,ktg8Plugin,listenerID);
        this.sendClass = sendClass;
        this.returnClass = returnClass;
    }
    public RecordRequestSend(ClientMessageListenerManager messageListenerManager, KTG8ClientPlugin ktg8Plugin, String listenerID, Class<Send> sendClass, Class<R> returnClass) {
        requestSend = new ClientRequestSend(messageListenerManager,ktg8Plugin,listenerID);
        this.sendClass = sendClass;
        this.returnClass = returnClass;
    }


    /**
     * 发送请求，此方法不会柱塞线程
     * @param requestData 请求数据
     * @param onResponse 当成功响应，不关心可设置为null
     * @param onError 当对方发生错误，一般是对方Worker方法抛出异常时调用。如果不关心可设置为null
     */
    public RecordRequestSend<Send,R> sendRequest(Send requestData, AcceptResponse<R> onResponse, ClientRequestSend.AcceptError onError){
        var send = JSONObject.from(requestData);
        ClientRequestSend.AcceptResponse acceptResponse = onResponse==null?null:(r)->{
            R re = r.to(returnClass);
            onResponse.accept(re);
        };
        requestSend.sendRequest(send,acceptResponse,onError);
        return this;
    }

    /**
     * 设置请求超时时间，对方多久不回复为超时
     * @param timeOut 超时时间，单位秒
     */
    public RecordRequestSend<Send,R> setTimeOut(long timeOut) {
        requestSend.setTimeOut(timeOut);
        return this;
    }

    /**
     * 开始发送
     */
    public RecordRequestSend<Send,R> start(){
        requestSend.start();
        return this;
    }

    /**
     * 停止发送
     */
    public RecordRequestSend<Send,R> stop(){
        requestSend.stop();
        return this;
    }



    public interface AcceptResponse<R extends Record>{
        /**
         * @param response 响应结果
         */
        void accept(R response);
    }
}
