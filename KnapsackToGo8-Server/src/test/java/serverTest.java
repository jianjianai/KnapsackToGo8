import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

public class serverTest {
    public enum B{
        A,B,C,D
    }
    public static class A{
        public B eee = B.A;
        public B aaa = B.C;
        public byte[] DD = new byte[]{12,65,12,78,45,14,14,4,4,4,4};

        @Override
        public String toString() {
            return "A{" +
                    "eee=" + eee +
                    ", aaa=" + aaa +
                    '}';
        }
    }

    public static class SS{
        public String a;
        public String b;
        public String c;
        public String d;

        @Override
        public String toString() {
            return "SS{" +
                    "a='" + a + '\'' +
                    ", b='" + b + '\'' +
                    ", c='" + c + '\'' +
                    ", d='" + d + '\'' +
                    '}';
        }
    }
    public static void main(String[] args) {
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("my",new A());
            String s = JSON.toJSONString(jsonObject);
            System.out.println(s);

            JSONObject jsonObject1 = JSON.parseObject(s);
            A a = jsonObject1.getObject("my", A.class);
            System.out.println(a);
        }

        {
            String j = JSON.toJSONString(new SS(){{
                a="asdas";
                b="qweqwe";
                c="dfdf";
            }});
            System.out.println(j);

            JSONObject jsonObject1 = JSON.parseObject(j);
            SS a = jsonObject1.to(SS.class);
            System.out.println(a);
        }

    }
}
