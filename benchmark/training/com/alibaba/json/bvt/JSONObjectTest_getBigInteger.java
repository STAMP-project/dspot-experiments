package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSONObject;
import java.math.BigInteger;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONObjectTest_getBigInteger extends TestCase {
    public void test_get_float() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("value", 123.45F);
        Assert.assertTrue((123.45F == (((Float) (obj.get("value"))).floatValue())));
        Assert.assertEquals(new BigInteger("123"), obj.getBigInteger("value"));
    }

    public void test_get_double() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("value", 123.45);
        Assert.assertTrue((123.45 == (((Double) (obj.get("value"))).doubleValue())));
        Assert.assertEquals(new BigInteger("123"), obj.getBigInteger("value"));
    }

    public void test_get_empty() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("value", "");
        Assert.assertEquals("", obj.get("value"));
        Assert.assertNull(obj.getBigInteger("value"));
    }
}

