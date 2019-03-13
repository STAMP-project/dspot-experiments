package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.TreeSet;
import junit.framework.TestCase;


public class Issue87_treeset extends TestCase {
    public void test_for_issue() throws Exception {
        Issue87_treeset.TestObject to = new Issue87_treeset.TestObject();
        to.add("test1");
        to.add("test2");
        String text = JSON.toJSONString(to);
        System.out.println(text);
        JSONObject jo = JSON.parseObject(text);
        to = JSON.toJavaObject(jo, Issue87_treeset.TestObject.class);
    }

    public static class TestObject {
        private TreeSet<String> set = new TreeSet<String>();

        public TreeSet<String> getSet() {
            return set;
        }

        public void setSet(TreeSet<String> set) {
            this.set = set;
        }

        public void add(String str) {
            set.add(str);
        }
    }
}

