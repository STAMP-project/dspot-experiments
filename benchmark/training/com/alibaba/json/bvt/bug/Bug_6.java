package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.List;
import junit.framework.TestCase;


public class Bug_6 extends TestCase {
    public void test_bug6() throws Exception {
        Bug_6.Entity entity = new Bug_6.Entity();
        String jsonString = JSON.toJSONString(entity);
        System.out.println(jsonString);
        JSON.parseObject(jsonString, Bug_6.Entity.class);
    }

    public static class Entity {
        private List<HashMap<String, String>> list = null;

        public List<HashMap<String, String>> getList() {
            return list;
        }

        public void setList(List<HashMap<String, String>> list) {
            this.list = list;
        }
    }
}

