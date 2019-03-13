package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;
import org.junit.Assert;


public class AnnotationTest3 extends TestCase {
    public void test_supperField() throws Exception {
        AnnotationTest3.C c = new AnnotationTest3.C();
        c.setId(123);
        c.setName("jobs");
        String str = JSON.toJSONString(c);
        Assert.assertEquals("{\"ID\":123,\"name\":\"jobs\"}", str);
    }

    public static class S {
        @JSONField(name = "ID")
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class C extends AnnotationTest3.S {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

