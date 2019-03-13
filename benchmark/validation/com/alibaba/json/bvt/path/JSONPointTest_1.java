package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSONPath;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONPointTest_1 extends TestCase {
    private Object json;

    public void test_key_1() throws Exception {
        Object val = JSONPath.eval(json, "/0/name");
        Assert.assertEquals("ljw", val);
    }
}

