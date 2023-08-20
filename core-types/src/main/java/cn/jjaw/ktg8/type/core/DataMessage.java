package cn.jjaw.ktg8.type.core;

import com.alibaba.fastjson2.JSONObject;

/**
 * 数据消息
 */
public record DataMessage (
        String plugin,
        String id,
        JSONObject data
){}
