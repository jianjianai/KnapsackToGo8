import cn.jjaw.ktg8.server.Main;
import cn.jjaw.ktg8.server.api.Client;
import cn.jjaw.ktg8.server.api.KTG8;
import cn.jjaw.ktg8.server.api.KTG8Plugin;
import cn.jjaw.ktg8.server.api.RequestAccept;
import cn.jjaw.ktg8.server.builtin.Builtin;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

public class serverTest {
    public static class A{
        public String cc = "asdasd";
    }
    public static void main(String[] args) {
        Builtin.runnableList.add(() -> new KTG8Plugin("my") {
            @Override
            public void onEnable() {
                new RequestAccept(KTG8.getKTG8Server().getMessageListenerManager(), this,"a")
                        .setWorker((client, requestData) -> {
                            System.out.println(client);
                            System.out.println(requestData);
                            return JSONObject.from(new A());
                        }).start();
            }
        });
        Main.main(args);
    }
}
