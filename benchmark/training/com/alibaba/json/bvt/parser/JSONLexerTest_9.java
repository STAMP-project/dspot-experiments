package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class JSONLexerTest_9 extends TestCase {
    public void test_ident() throws Exception {
        JSON.parseObject("\"AAA\"", JSONLexerTest_9.Type.class);
    }

    public void test_a() throws Exception {
        JSON.parseObject("{\"type\":\"AAA\"}", JSONLexerTest_9.VO.class);
    }

    public void test_b() throws Exception {
        JSON.parseObject("{\"tt\":\"AA\"}", JSONLexerTest_9.VO.class);
    }

    public void test_value() throws Exception {
        JSON.parseObject("{\"type\":\'AAA\'}", JSONLexerTest_9.VO.class);
    }

    public void test_value2() throws Exception {
        JSON.parseObject("{\"type\":\"AAA\",id:0}", JSONLexerTest_9.VO.class);
    }

    public static class VO {
        public VO() {
        }

        private JSONLexerTest_9.Type type;

        public JSONLexerTest_9.Type getType() {
            return type;
        }

        public void setType(JSONLexerTest_9.Type type) {
            this.type = type;
        }
    }

    public static enum Type {

        AAA,
        BBB,
        CCC;}
}

