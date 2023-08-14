package cn.jjaw.ktg8.server.builtin;

import cn.jjaw.ktg8.server.builtin.clientCommunication.KTG8ClientCommunication;

public class Builtin {
    static private KTG8ClientCommunication ktg8ClientCommunication;
    public static KTG8ClientCommunication getKtg8ClientCommunication() {
        return ktg8ClientCommunication;
    }

    public static void initializationAll(){
        ktg8ClientCommunication = new KTG8ClientCommunication();
    }
}
