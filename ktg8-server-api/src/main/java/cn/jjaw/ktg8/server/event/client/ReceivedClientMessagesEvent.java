package cn.jjaw.ktg8.server.event.client;

import cn.jjaw.ktg8.server.core.Client;
import cn.jjaw.ktg8.server.event.CancelEvent;
import com.alibaba.fastjson2.JSONObject;

/**
 * 收到客户端消息事件
 */
public class ReceivedClientMessagesEvent extends ClientEvent implements CancelEvent {
    private String plugin;
    private String id;
    private JSONObject data;
    private boolean cancel;

    public ReceivedClientMessagesEvent(Client client, String plugin, String id, JSONObject data) {
        super(client);
        this.plugin = plugin;
        this.id = id;
        this.data = data;
    }

    /**
     * 获取接收此消息插件的名称
     */
    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    /**
     * 获取消息的id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取此消息的数据
     */
    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    @Override
    public boolean isCancel() {
        return cancel;
    }

    @Override
    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }
}
