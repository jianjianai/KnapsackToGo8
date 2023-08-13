package cn.jjaw.ktg8.communication.type.message.handshake.client;

import cn.jjaw.ktg8.communication.type.censor.checker.NotNull;

/**
 * 客户端握手
 */
public class HandshakeMessageClient {
    @NotNull public boolean ok;
    public String reason;
}
