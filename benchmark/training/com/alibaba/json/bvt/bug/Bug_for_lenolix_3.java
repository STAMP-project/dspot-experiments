package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSONObject;
import java.util.Map;
import junit.framework.TestCase;


public class Bug_for_lenolix_3 extends TestCase {
    public void test_0() throws Exception {
        System.out.println("{}");
        JSONObject.parseObject("{\"id\":{}}", new com.alibaba.fastjson.TypeReference<Map<String, Map<String, Bug_for_lenolix_3.User>>>() {});
    }

    public static class User {}
}

