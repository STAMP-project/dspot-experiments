package com.alibaba.json.test;


import junit.framework.TestCase;


/**
 * Created by wenshao on 27/12/2016.
 */
public class JsonIteratorByteArrayTest extends TestCase {
    public void test_for_iterator() throws Exception {
        String text = "{\"id\":1001,\"name\":\"wenshao\",\"type\":\"Small\"}";
        byte[] bytes = text.getBytes();
        fastjson(bytes);
        for (int i = 0; i < 10; ++i) {
            long startMillis = System.currentTimeMillis();
            fastjson(bytes);
            long millis = (System.currentTimeMillis()) - startMillis;
            System.out.println(("fastjson : " + millis));
        }
        // jsoniterator(bytes);
        // for (int i = 0; i < 10; ++i) {
        // long startMillis = System.currentTimeMillis();
        // jsoniterator(bytes);
        // long millis = System.currentTimeMillis() - startMillis;
        // System.out.println("jsoniterator : " + millis);
        // }
    }

    // public Type type;
    public static class Model {
        public int id;

        public String name;
    }

    public static enum Type {

        Big,
        Small;}
}

