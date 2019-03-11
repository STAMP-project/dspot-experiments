package com.alibaba.json.demo;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONFeidDemo extends TestCase {
    public static class User {
        private int id;

        private String name;

        @JSONField(name = "uid")
        public int getId() {
            return id;
        }

        @JSONField(name = "uid")
        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public void test_0() throws Exception {
        JSONFeidDemo.User user = new JSONFeidDemo.User();
        user.setId(123);
        user.setName("??");
        String text = JSON.toJSONString(user);
        Assert.assertEquals("{\"name\":\"\u6bdb\u5934\",\"uid\":123}", text);
        System.out.println(text);
    }
}

