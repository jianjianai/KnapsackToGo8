package cn.jjaw.ktg8.communication.type.message;

import cn.jjaw.ktg8.communication.type.censor.checker.AllNotNull;
import com.alibaba.fastjson2.JSONObject;

/**
 * 基础消息
 */
@AllNotNull
public class BaseMessage {
    public BaseType type;
    public JSONObject data;
}
