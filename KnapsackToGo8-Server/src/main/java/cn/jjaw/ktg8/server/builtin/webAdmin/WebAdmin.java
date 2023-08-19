package cn.jjaw.ktg8.server.builtin.webAdmin;

import cn.jja8.config.tool.YamlConfig;
import cn.jjaw.ktg8.server.api.KTG8;
import cn.jjaw.ktg8.server.api.KTG8Plugin;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * 内置插件，网页后台管理
 */
public class WebAdmin extends KTG8Plugin {
    static WebAdmin webAdmin;
    public static WebAdmin getWebAdmin() {
        return webAdmin;
    }

    HttpServer httpServer;
    public WebAdmin() {
        super("webAdmin");
        webAdmin = this;
    }

    @Override
    public void onLoad() {
        getLogger().info("正在初始化..");
        getDataFolder().mkdirs();
        File webServerConfig = new File(getDataFolder(),"WebServerConfig.yaml");
        new YamlConfig().load(webServerConfig).as(WebServerConfig.class).save(webServerConfig);
        try {
            httpServer = HttpServer.create(new InetSocketAddress(WebServerConfig.hostname,WebServerConfig.port),0);
            httpServer.setExecutor(KTG8.getExecutor());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public void onEnable() {
        if (httpServer==null){
            return;
        }
        getLogger().info("正在启动web管理器..");
        httpServer.start();
        httpServer.createContext("/",(exchange)->{
            exchange.getResponseHeaders().set("Content-Type","application/json; charset=utf-8");
            exchange.sendResponseHeaders(200,0);
            exchange.getResponseBody().write("网页管理还未正式开始开发，尽情期待。".getBytes(StandardCharsets.UTF_8));
            exchange.close();
        });
        getLogger().info("web管理器已在: http://"+WebServerConfig.hostname+":"+WebServerConfig.port+" 启动。");
    }
}
