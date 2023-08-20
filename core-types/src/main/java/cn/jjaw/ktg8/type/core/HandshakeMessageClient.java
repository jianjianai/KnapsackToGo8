package cn.jjaw.ktg8.type.core;

/**
 * 客户端握手
 */
public record HandshakeMessageClient(
        boolean ok,
        String reason
) {}
