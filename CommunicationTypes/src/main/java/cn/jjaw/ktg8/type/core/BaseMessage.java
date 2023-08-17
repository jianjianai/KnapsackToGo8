package cn.jjaw.ktg8.type.core;

import com.alibaba.fastjson2.JSONObject;

/**
 * 基础消息
 */
public record BaseMessage(
        BaseMessageType type,
        JSONObject data
) {
    /**
     * 消息类型
     */
    public enum BaseMessageType {
        /**
         * 握握手
         */
        handshake,
        /**
         * 数据
         */
        data
    }
}
