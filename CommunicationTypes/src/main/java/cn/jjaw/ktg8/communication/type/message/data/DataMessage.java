package cn.jjaw.ktg8.communication.type.message.data;

import cn.jjaw.ktg8.communication.type.censor.checker.NotNull;
import com.alibaba.fastjson2.JSONObject;

/**
 * 数据消息
 */
public class DataMessage {
    @NotNull public String plugin;
    @NotNull public String id;
    public JSONObject data;
}
