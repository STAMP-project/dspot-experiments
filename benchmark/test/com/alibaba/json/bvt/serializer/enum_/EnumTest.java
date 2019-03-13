package com.alibaba.json.bvt.serializer.enum_;


import SerializerFeature.UseSingleQuotes;
import SerializerFeature.WriteEnumUsingToString;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class EnumTest extends TestCase {
    public static enum Type {

        Big,
        Medium,
        Small;}

    public void test_enum() throws Exception {
        Assert.assertEquals("0", JSON.toJSONStringZ(EnumTest.Type.Big, SerializeConfig.getGlobalInstance()));// 0

        Assert.assertEquals("1", JSON.toJSONStringZ(EnumTest.Type.Medium, SerializeConfig.getGlobalInstance()));// 1

        Assert.assertEquals("2", JSON.toJSONStringZ(EnumTest.Type.Small, SerializeConfig.getGlobalInstance()));// 2

        Assert.assertEquals("\"Big\"", JSON.toJSONString(EnumTest.Type.Big, WriteEnumUsingToString));// "Big"

        Assert.assertEquals("\"Medium\"", JSON.toJSONString(EnumTest.Type.Medium, WriteEnumUsingToString));// "Medium"

        Assert.assertEquals("\"Small\"", JSON.toJSONString(EnumTest.Type.Small, WriteEnumUsingToString));// "Small"

        Assert.assertEquals("'Small'", JSON.toJSONString(EnumTest.Type.Small, UseSingleQuotes));// "Small"

    }

    public void test_empty() throws Exception {
        EnumTest.Model model = JSON.parseObject("{\"type\":\"\"}", EnumTest.Model.class);
        TestCase.assertNull(model.type);
    }

    public static class Model {
        public EnumTest.Type type;
    }
}

