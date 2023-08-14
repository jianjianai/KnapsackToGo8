package cn.jjaw.ktg8.type.core;

import com.alibaba.fastjson2.JSONObject;

/**
 * @param id 消息编号
 * @param acceptResponse 是否关心返回值
 * @param acceptError 是否关心错误
 * @param data 请求数据
 */
public record RADataRequestAccept(
        long id,
        boolean acceptResponse,
        boolean acceptError,
        JSONObject data
) {
}
