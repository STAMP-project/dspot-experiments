package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class JSONLexerTest_3 extends TestCase {
    public void test_matchField() throws Exception {
        JSON.parseObject("{\"val\":{}}", JSONLexerTest_3.VO.class);
    }

    public static class VO {
        private JSONLexerTest_3.A value;

        public JSONLexerTest_3.A getValue() {
            return value;
        }

        public void setValue(JSONLexerTest_3.A value) {
            this.value = value;
        }
    }

    public static class A {}
}

