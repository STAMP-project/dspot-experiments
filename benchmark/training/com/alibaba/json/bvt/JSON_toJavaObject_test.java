package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.TypeUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSON_toJavaObject_test extends TestCase {
    public void test_0() throws Exception {
        JSON_toJavaObject_test.A a = ((JSON_toJavaObject_test.A) (JSON.toJavaObject(new JSONObject(), JSON_toJavaObject_test.A.class)));
        Assert.assertNotNull(a);
    }

    public void test_1() throws Exception {
        JSON_toJavaObject_test.A a = ((JSON_toJavaObject_test.A) (TypeUtils.cast(new JSON_toJavaObject_test.B(), JSON_toJavaObject_test.A.class, ParserConfig.getGlobalInstance())));
        Assert.assertNotNull(a);
    }

    public static class A {}

    public static interface IB {}

    public static class B extends JSON_toJavaObject_test.A implements JSON_toJavaObject_test.IB {}
}

