package com.alibaba.json.bvt.parser;


import Feature.InitStringFieldAsEmpty;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestInitStringFieldAsEmpty extends TestCase {
    public void test_private() throws Exception {
        TestInitStringFieldAsEmpty.VO1 vo1 = JSON.parseObject("{}", TestInitStringFieldAsEmpty.VO1.class, InitStringFieldAsEmpty);
        Assert.assertEquals("", vo1.getValue());
    }

    public void test_public() throws Exception {
        TestInitStringFieldAsEmpty.VO2 vo2 = JSON.parseObject("{}", TestInitStringFieldAsEmpty.VO2.class, InitStringFieldAsEmpty);
        Assert.assertEquals("", vo2.getValue());
    }

    private static class VO1 {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class VO2 {
        private String value;

        public VO2() {
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

