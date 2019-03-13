package com.alibaba.json.test;


import com.jsoniter.JsonIterator;
import com.jsoniter.spi.TypeLiteral;
import junit.framework.TestCase;


/**
 * Created by wenshao on 27/12/2016.
 */
public class JsonIteratorImageTest extends TestCase {
    private String input = "{\"bitrate\":262144,\"duration\":18000000,\"format\":\"video/mpg4\",\"height\":480,\"persons\":[\"Bill Gates\",\"Steve Jobs\"],\"player\":\"JAVA\",\"size\":58982400,\"title\":\"Javaone Keynote\",\"uri\":\"http://javaone.com/keynote.mpg\",\"width\":640}";

    private byte[] inputBytes = input.getBytes();

    private TypeLiteral<JsonIteratorImageTest.Model> modelTypeLiteral;// this is thread-safe can reused


    private JsonIterator iter;

    private int COUNT = (1000 * 1000) * 1;

    public void test_for_iterator() throws Exception {
        iter.reset(inputBytes);
        JsonIteratorImageTest.Model m2 = iter.read(modelTypeLiteral);
        fastjson();
        for (int i = 0; i < 5; ++i) {
            long startMillis = System.currentTimeMillis();
            fastjson();
            long millis = (System.currentTimeMillis()) - startMillis;
            System.out.println(("fastjson : " + millis));
        }
        // jsoniterator();
        // for (int i = 0; i < 5; ++i) {
        // long startMillis = System.currentTimeMillis();
        // jsoniterator();
        // long millis = System.currentTimeMillis() - startMillis;
        // System.out.println("jsoniterator : " + millis);
        // }
    }

    public static class Model {
        public int id;

        public String name;
    }
}

