package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class ParserConfigTest extends TestCase {
    public void test_0() throws Exception {
        ParserConfig config = new ParserConfig();
        config.getDeserializers();
    }

    public void test_1() throws Exception {
        ParserConfig config = new ParserConfig(Thread.currentThread().getContextClassLoader());
        ParserConfigTest.Model model = JSON.parseObject("{\"value\":123}", ParserConfigTest.Model.class, config);
        Assert.assertEquals(123, model.value);
    }

    public static class Model {
        public int value;
    }
}

