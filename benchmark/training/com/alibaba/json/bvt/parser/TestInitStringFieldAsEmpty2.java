package com.alibaba.json.bvt.parser;


import Feature.InitStringFieldAsEmpty;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestInitStringFieldAsEmpty2 extends TestCase {
    public void test_public() throws Exception {
        TestInitStringFieldAsEmpty2.VO1 vo1 = JSON.parseObject("{\"id\":0,\"value\":33, \"o\":{}}", TestInitStringFieldAsEmpty2.VO1.class, InitStringFieldAsEmpty);
        Assert.assertEquals("", vo1.getName());
        Assert.assertEquals("", vo1.getO().getValue());
    }

    public static class VO1 {
        private int id;

        private String name;

        private int value;

        private TestInitStringFieldAsEmpty2.VO2 o;

        public VO1() {
        }

        public TestInitStringFieldAsEmpty2.VO2 getO() {
            return o;
        }

        public void setO(TestInitStringFieldAsEmpty2.VO2 o) {
            this.o = o;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class VO2 {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

