package com.alibaba.json.test.epubview;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import junit.framework.TestCase;


public class TestKlutz extends TestCase {
    private EpubViewBook book;

    ObjectMapper mapper = new ObjectMapper();

    Gson gson = new Gson();

    private EpubViewBook book_jackson;

    private EpubViewBook book_fastjson;

    private int LOOP_COUNT = 1000 * 1;

    public void test_0() throws Exception {
        for (int j = 0; j < 5; j++) {
            fastjson();
            // gson();
            jackson();
            System.out.println("=======================");
        }
    }
}

