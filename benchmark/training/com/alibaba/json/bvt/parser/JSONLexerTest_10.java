package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONLexerTest_10 extends TestCase {
    public void test_a() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"type\":\"AAA", JSONLexerTest_10.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class VO {
        public VO() {
        }

        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}

