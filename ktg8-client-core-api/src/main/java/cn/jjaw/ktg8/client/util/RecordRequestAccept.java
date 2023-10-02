package cn.jjaw.ktg8.client.util;

import cn.jjaw.ktg8.client.core.ClientMessageListenerManager;
import cn.jjaw.ktg8.client.core.KTG8Client;
import cn.jjaw.ktg8.client.core.KTG8ClientPlugin;
import com.alibaba.fastjson2.JSONObject;

/**
 * 一个请求接收器对象，一个简化通信的实现
 */
public class RecordRequestAccept<Accept extends Record,R extends Record> {
    private final ClientRequestAccept requestAccept;
    private final  Class<Accept> acceptClass;
    private final  Class<R> returnClass;

    public RecordRequestAccept(KTG8Client ktg8Client, KTG8ClientPlugin ktg8Plugin, String listenerID, Class<Accept> acceptClass, Class<R> returnClass) {
        this.requestAccept = new ClientRequestAccept(ktg8Client,ktg8Plugin,listenerID);
        this.acceptClass = acceptClass;
        this.returnClass = returnClass;
    }

    public RecordRequestAccept(ClientMessageListenerManager messageListenerManager,KTG8ClientPlugin ktg8Plugin, String listenerID, Class<Accept> acceptClass, Class<R> returnClass) {
        this.requestAccept = new ClientRequestAccept(messageListenerManager,ktg8Plugin,listenerID);
        this.acceptClass = acceptClass;
        this.returnClass = returnClass;
    }

    /**
     * 设置请求处理器
     * @param worker 建议使用lambda表达式或者方法引用
     */
    public RecordRequestAccept<Accept,R> setWorker(Worker<Accept,R> worker){
        if (worker==null){
            throw new NullPointerException("the worker is null");
        }
        requestAccept.setWorker((d)->{
            Accept theAccept = d.to(acceptClass);
            R theReturn = worker.onRequest(theAccept);
            return JSONObject.from(theReturn);
        });
        return this;
    }
    /**
     * 开始接收请求
     */
    public RecordRequestAccept<Accept,R> start(){
        requestAccept.start();
        return this;
    }
    /**
     * 停止接收请求
     */
    public RecordRequestAccept<Accept,R> stop(){
        requestAccept.stop();
        return this;
    }

    public interface Worker<A extends Record,R extends Record>{
        /**
         * 当收到请求
         * @param data 请求的数据
         * @return 返回请求结恶
         */
        R onRequest(A data);
    }
}
