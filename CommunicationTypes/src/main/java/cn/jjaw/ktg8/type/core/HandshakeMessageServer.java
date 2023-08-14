package cn.jjaw.ktg8.type.core;

import cn.jjaw.ktg8.censor.checker.AllNotNull;

/**
 * 服务端握手
 * @param version 版本
 * @param serverID 服务器id
 * @param serverType 服务器类型
 */
@AllNotNull
public record HandshakeMessageServer (
    long version,
    String serverID,
    ServerType serverType
){}
