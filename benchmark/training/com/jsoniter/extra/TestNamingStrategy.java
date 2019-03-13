package com.jsoniter.extra;


import NamingStrategySupport.SNAKE_CASE;
import com.jsoniter.output.JsonStream;
import junit.framework.TestCase;


public class TestNamingStrategy extends TestCase {
    public static class TestObject1 {
        public String helloWorld;
    }

    public void test() {
        NamingStrategySupport.enable(SNAKE_CASE);
        TestNamingStrategy.TestObject1 obj = new TestNamingStrategy.TestObject1();
        obj.helloWorld = "!!!";
        TestCase.assertEquals("{\"hello_world\":\"!!!\"}", JsonStream.serialize(obj));
    }
}

