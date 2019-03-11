package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSONPath;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONPath_containsValue_double extends TestCase {
    public void test_root() throws Exception {
        JSONPath_containsValue_double.Model model = new JSONPath_containsValue_double.Model();
        model.value = 1001.0;
        Assert.assertTrue(JSONPath.containsValue(model, "/value", 1001));
        Assert.assertTrue(JSONPath.containsValue(model, "/value", 1001L));
        Assert.assertTrue(JSONPath.containsValue(model, "/value", ((short) (1001))));
        Assert.assertTrue(JSONPath.containsValue(model, "/value", 1001.0F));
        Assert.assertTrue(JSONPath.containsValue(model, "/value", 1001.0));
        Assert.assertFalse(JSONPath.containsValue(model, "/value", 1002));
        Assert.assertFalse(JSONPath.containsValue(model, "/value", 1002L));
        Assert.assertFalse(JSONPath.containsValue(model, "/value", ((short) (1002))));
        Assert.assertFalse(JSONPath.containsValue(model, "/value", 1002.0F));
        Assert.assertFalse(JSONPath.containsValue(model, "/value", 1002.0));
    }

    public static class Model {
        public double value;
    }
}

