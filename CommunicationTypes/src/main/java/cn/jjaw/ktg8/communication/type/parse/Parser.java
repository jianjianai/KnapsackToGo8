package cn.jjaw.ktg8.communication.type.parse;

import cn.jjaw.ktg8.communication.type.censor.Check;
import cn.jjaw.ktg8.communication.type.message.BaseMessage;
import cn.jjaw.ktg8.communication.type.message.data.DataMessage;
import cn.jjaw.ktg8.communication.type.message.handshake.client.HandshakeMessageClient;
import cn.jjaw.ktg8.communication.type.message.handshake.server.HandshakeMessageServer;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;

import static cn.jjaw.ktg8.communication.type.Logger.logger;

public class Parser {
    /**
     * 将json字符串解析为BaseMessage并检查合法性
     * @return null 发生错误或字符串为空
     */
    public static BaseMessage ofBaseMessage(String json){
        JSONObject jsonObject = null;
        try {
            jsonObject = JSON.parseObject(json);
        }catch (JSONException exception){
            exception.printStackTrace();
        }
        if(jsonObject==null){
            logger.error("json解析错误！");
            return null;
        }
        return Check.check(jsonObject.to(BaseMessage.class));
    }
    /**
     * 将json对象解析为HandshakeMessageServer并检查合法性
     * @return null 发生错误或字符串为空
     */
    public static  HandshakeMessageServer ofHandshakeMessageServer(JSONObject jsonObject){
        return Check.check(jsonObject.to(HandshakeMessageServer.class));
    }
    /**
     * 将json对象解析为HandshakeMessageServer并检查合法性
     * @return null 发生错误或字符串为空
     */
    public static  DataMessage ofDataMessage(JSONObject jsonObject){
        return Check.check(jsonObject.to(DataMessage.class));
    }
    /**
     * 将json对象解析为HandshakeMessageServer并检查合法性
     * @return null 发生错误或字符串为空
     */
    public static HandshakeMessageClient ofHandshakeMessageClient(JSONObject jsonObject){
        return Check.check(jsonObject.to(HandshakeMessageClient.class));
    }
}
