package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class BooleanFieldTest extends TestCase {
    public void test_model() throws Exception {
        BooleanFieldTest.Model model = new BooleanFieldTest.Model();
        model.value = true;
        String text = JSON.toJSONString(model);
        Assert.assertEquals("{\"value\":true}", text);
    }

    public void test_model_max() throws Exception {
        BooleanFieldTest.Model model = new BooleanFieldTest.Model();
        model.value = false;
        String text = JSON.toJSONString(model);
        Assert.assertEquals("{\"value\":false}", text);
    }

    public static class Model {
        public boolean value;
    }
}

