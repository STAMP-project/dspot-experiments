package com.alibaba.json.test.codegen;


import com.alibaba.fastjson.codegen.DeserializerGen;
import data.media.MediaContent;
import junit.framework.TestCase;


public class GenTest extends TestCase {
    public void test_codegen() throws Exception {
        StringBuffer out = new StringBuffer();
        DeserializerGen generator = new DeserializerGen(MediaContent.class, out);
        generator.gen();
        System.out.println(out.toString());
    }
}

