package com.alibaba.json.test;


import junit.framework.TestCase;


/**
 * Created by wenshao on 27/12/2016.
 */
public class JsonIteratorTest extends TestCase {
    public void test_for_iterator() throws Exception {
        String text = "{\"id\":1001,\"name\":\"wenshao\",\"type\":\"Small\"}";
        fastjson(text);
        jsoniterator(text);
        for (int i = 0; i < 5; ++i) {
            long startMillis = System.currentTimeMillis();
            fastjson(text);
            long millis = (System.currentTimeMillis()) - startMillis;
            System.out.println(("fastjson : " + millis));
        }
        for (int i = 0; i < 5; ++i) {
            long startMillis = System.currentTimeMillis();
            jsoniterator(text);
            long millis = (System.currentTimeMillis()) - startMillis;
            System.out.println(("jsoniterator : " + millis));
        }
    }

    public static class Model {
        public int id;

        public String name;

        public JsonIteratorTest.Type type;
    }

    public static enum Type {

        Big,
        Small;}
}

