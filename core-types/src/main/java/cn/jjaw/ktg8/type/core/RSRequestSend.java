package cn.jjaw.ktg8.type.core;

import com.alibaba.fastjson2.JSONObject;

public record RSRequestSend(
        long id,
        RSRequestSendType type,
        JSONObject data
) {
    public enum RSRequestSendType{
        ok,
        error,
        /**
         * 啥也不返回，但是要返回一个表示结果已处理
         */
        noOne
    }
}


