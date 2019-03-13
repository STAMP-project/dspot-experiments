package com.alibaba.json.bvt;


import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullNumberAsZero;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class EmptyObjectTest extends TestCase {
    public void test_codec_null() throws Exception {
        EmptyObjectTest.V0 v = new EmptyObjectTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{}", text);
        JSON.parseObject(text, EmptyObjectTest.V0.class);
    }

    public void test_codec_null_1() throws Exception {
        EmptyObjectTest.V0 v = new EmptyObjectTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue, WriteNullNumberAsZero);
        Assert.assertEquals("{}", text);
    }

    public static class V0 {}
}

