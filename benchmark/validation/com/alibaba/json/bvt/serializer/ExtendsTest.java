package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;
import org.junit.Assert;


public class ExtendsTest extends TestCase {
    public void test_extends() throws Exception {
        ExtendsTest.B b = new ExtendsTest.B();
        b.setId(123);
        b.setName("??");
        JSONObject json = JSON.parseObject(JSON.toJSONString(b));
        Assert.assertEquals(b.getId(), json.get("id"));
        Assert.assertEquals(b.getName(), json.get("name"));
    }

    public static class A {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class B extends ExtendsTest.A {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

