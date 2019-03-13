package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONLexerTest_4 extends TestCase {
    public void test_scanFieldString() throws Exception {
        JSONLexerTest_4.VO vo = JSON.parseObject("{\"value\":\"abc\"}", JSONLexerTest_4.VO.class);
        Assert.assertEquals("abc", vo.getValue());
    }

    public static class VO {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

