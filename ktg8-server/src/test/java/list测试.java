import java.util.ArrayList;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

class list测试{
    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("qweasd");
        list.add("qweasdasd");
        JSONObject json = new JSONObject();
        json.put("data",list);

        System.out.println(JSON.toJSONString(json));
    }
}