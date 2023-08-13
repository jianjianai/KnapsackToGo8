package cn.jjaw.ktg8.communication.type.message.handshake.server;

import cn.jjaw.ktg8.communication.type.censor.checker.AllNotNull;

/**
 * 服务端握手
 */
@AllNotNull
public class HandshakeMessageServer {
    /**
     * 版本
     */
    public long version;
    /**
     * 服务器id
     */
    public String serverID;
    /**
     * 服务器类型
     */
    public ServerType serverType;

}
