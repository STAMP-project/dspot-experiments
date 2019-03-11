package com.alibaba.json.bvt.feature;


import Feature.InitStringFieldAsEmpty;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import junit.framework.TestCase;
import org.junit.Assert;


public class InitStringFieldAsEmptyTest extends TestCase {
    public void test_0() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser("{}");
        parser.config(InitStringFieldAsEmpty, true);
        InitStringFieldAsEmptyTest.Model model = parser.parseObject(InitStringFieldAsEmptyTest.Model.class);
        Assert.assertEquals("", model.value);
        parser.close();
    }

    public void test_1() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser("{}");
        parser.config(InitStringFieldAsEmpty, false);
        InitStringFieldAsEmptyTest.Model model = parser.parseObject(InitStringFieldAsEmptyTest.Model.class);
        Assert.assertNull(null, model.value);
        parser.close();
    }

    public static class Model {
        public Model() {
        }

        public String value;
    }
}

