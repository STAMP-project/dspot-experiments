package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import junit.framework.TestCase;
import org.junit.Assert;


public class NotWriteRootClassNameTest extends TestCase {
    public void test_NotWriteRootClassName() throws Exception {
        SerializerFeature[] features = new SerializerFeature[]{ SerializerFeature.WriteClassName, SerializerFeature.NotWriteRootClassName };
        Assert.assertEquals("{}", JSON.toJSONString(new NotWriteRootClassNameTest.VO(), features));
        Assert.assertEquals("{}", JSON.toJSONString(new NotWriteRootClassNameTest.V1(), features));
    }

    public static class VO {}

    private static class V1 {}
}

