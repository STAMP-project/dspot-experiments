package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.HashSet;
import junit.framework.TestCase;


public class Issue87_hashset extends TestCase {
    public void test_for_issue() throws Exception {
        Issue87_hashset.TestObject to = new Issue87_hashset.TestObject();
        to.add("test1");
        to.add("test2");
        String text = JSON.toJSONString(to);
        System.out.println(text);
        JSONObject jo = JSON.parseObject(text);
        to = JSON.toJavaObject(jo, Issue87_hashset.TestObject.class);
    }

    public static class TestObject {
        private HashSet<String> set = new HashSet<String>(0);

        public HashSet<String> getSet() {
            return set;
        }

        public void setSet(HashSet<String> set) {
            this.set = set;
        }

        public void add(String str) {
            set.add(str);
        }
    }
}

