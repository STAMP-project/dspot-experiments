package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSONPath;
import java.math.BigInteger;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONPath_containsValue_biginteger extends TestCase {
    public void test_root() throws Exception {
        JSONPath_containsValue_biginteger.Model model = new JSONPath_containsValue_biginteger.Model();
        model.value = new BigInteger("1001");
        Assert.assertTrue(JSONPath.containsValue(model, "/value", 1001));
        Assert.assertTrue(JSONPath.containsValue(model, "/value", 1001L));
        Assert.assertTrue(JSONPath.containsValue(model, "/value", ((short) (1001))));
        Assert.assertFalse(JSONPath.containsValue(model, "/value", 1002));
        Assert.assertFalse(JSONPath.containsValue(model, "/value", 1002L));
        Assert.assertFalse(JSONPath.containsValue(model, "/value", ((short) (1002))));
    }

    public static class Model {
        public BigInteger value;
    }
}

