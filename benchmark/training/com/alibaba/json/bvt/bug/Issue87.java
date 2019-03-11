package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;


public class Issue87 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue87.TestObject to = new Issue87.TestObject();
        to.add("test1");
        to.add("test2");
        String text = JSON.toJSONString(to);
        System.out.println(text);
        JSONObject jo = JSON.parseObject(text);
        to = JSON.toJavaObject(jo, Issue87.TestObject.class);
    }

    public static class TestObject {
        private Set<String> set = new HashSet<String>(0);

        public Set<String> getSet() {
            return set;
        }

        public void setSet(Set<String> set) {
            this.set = set;
        }

        public void add(String str) {
            set.add(str);
        }
    }
}

