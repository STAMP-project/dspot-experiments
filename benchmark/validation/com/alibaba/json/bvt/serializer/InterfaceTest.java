package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;
import org.junit.Assert;


public class InterfaceTest extends TestCase {
    public void test_interface() throws Exception {
        InterfaceTest.A a = new InterfaceTest.A();
        a.setId(123);
        a.setName("xasdf");
        String text = JSON.toJSONString(a);
        Assert.assertEquals("{\"ID\":123,\"Name\":\"xasdf\"}", text);
    }

    public static class A implements InterfaceTest.IA , InterfaceTest.IB {
        private int id;

        private String name;

        public int getId() {
            return id;
        }

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

    public static interface IA {
        @JSONField(name = "ID")
        int getId();
    }

    public static interface IB {
        @JSONField(name = "Name")
        String getName();
    }
}

