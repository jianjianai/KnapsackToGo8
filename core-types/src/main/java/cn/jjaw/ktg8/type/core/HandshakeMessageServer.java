package cn.jjaw.ktg8.type.core;

/**
 * 服务端握手
 * @param version 版本
 * @param serverID 服务器id
 * @param serverType 服务器类型
 */
public record HandshakeMessageServer (
    long version,
    String serverID,
    ServerType serverType
){}
