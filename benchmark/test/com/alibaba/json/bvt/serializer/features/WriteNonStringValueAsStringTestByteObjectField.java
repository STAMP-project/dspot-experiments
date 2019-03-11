package com.alibaba.json.bvt.serializer.features;


import SerializerFeature.WriteNonStringValueAsString;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteNonStringValueAsStringTestByteObjectField extends TestCase {
    public void test_0() throws Exception {
        WriteNonStringValueAsStringTestByteObjectField.VO vo = new WriteNonStringValueAsStringTestByteObjectField.VO();
        vo.id = 100;
        String text = JSON.toJSONString(vo, WriteNonStringValueAsString);
        Assert.assertEquals("{\"id\":\"100\"}", text);
    }

    public void test_1() throws Exception {
        WriteNonStringValueAsStringTestByteObjectField.V1 vo = new WriteNonStringValueAsStringTestByteObjectField.V1();
        vo.id = 100;
        String text = JSON.toJSONString(vo, WriteNonStringValueAsString);
        Assert.assertEquals("{\"id\":\"100\"}", text);
    }

    public static class VO {
        public Byte id;
    }

    private static class V1 {
        public Byte id;
    }
}

