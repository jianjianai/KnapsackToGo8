import java.io.File;

public class serverTest {
    public static class A{
        public String cc = "asdasd";
    }
    public static void main(String[] args) {
        System.out.println(new File(new File("qwe"),"PairStorage.db").getAbsolutePath());
//        Builtin.runnableList.add(() -> new KTG8Plugin("my") {
//            @Override
//            public void onEnable() {
//                new RequestAccept(KTG8.getKTG8Server().getMessageListenerManager(), this,"a")
//                        .setWorker((client, requestData) -> {
//                            System.out.println(client);
//                            System.out.println(requestData);
//                            return JSONObject.from(new A());
//                        }).start();
//            }
//        });
//        Main.main(args);
    }
}
