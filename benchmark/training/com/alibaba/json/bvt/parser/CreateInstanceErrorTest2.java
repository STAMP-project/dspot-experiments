package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import junit.framework.TestCase;
import org.junit.Assert;


public class CreateInstanceErrorTest2 extends TestCase {
    public void test_ordered_field() throws Exception {
        Exception error = null;
        try {
            CreateInstanceErrorTest2.Model model = JSON.parseObject("{\"value\":{\"@type\":\"com.alibaba.json.bvt.parser.CreateInstanceErrorTest2$MyMap\"}}", CreateInstanceErrorTest2.Model.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class Model {
        public Object value;
    }

    public static class MyMap extends HashMap {
        public MyMap() {
            throw new UnsupportedOperationException();
        }
    }
}

