package com.alibaba.json.bvt.parser.number;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;
import org.junit.Assert;


public class NumberValueTest2 extends TestCase {
    public void test_0() throws Exception {
        String text = "{\"value\":3F}";
        JSONObject obj = ((JSONObject) (JSON.parse(text)));
        Assert.assertTrue((3.0F == (((Float) (obj.get("value"))).floatValue())));
    }

    public void test_1() throws Exception {
        String text = "{\"value\":3.e3F}";
        JSONObject obj = ((JSONObject) (JSON.parse(text)));
        Assert.assertTrue((3000.0F == (((Float) (obj.get("value"))).floatValue())));
    }
}

