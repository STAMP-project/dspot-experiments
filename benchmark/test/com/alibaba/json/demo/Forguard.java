package com.alibaba.json.demo;


import junit.framework.TestCase;


/**
 * Created by wenshao on 10/03/2017.
 */
public class Forguard extends TestCase {
    public void test_0() throws Exception {
        String json = "{\"id\":\"a123\", \"name\":\"wxf\"}";
        String value = Forguard.javaGet(json, "id");
        System.out.println(value);
    }

    public static class Parser {
        public char[] json_chars;

        public int json_len;

        public char[] str_chars;

        public int str_chars_len;

        public char ch;

        public int pos;

        public Forguard.Token token;
    }

    public static enum Token {

        STRING,
        // 
        EOF,
        // 
        LBRACE,
        RBRACE,
        COMMA,
        COLON;}
}

