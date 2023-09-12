package cn.jjaw.ktg8.type.core;

/**
 * 服务端握手
 * @param version 版本
 * @param serverID 服务器id
 */
public record HandshakeMessageServer (
    long version,
    String serverID
){}
