package com.alibaba.json.bvt.serializer.features;


import SerializerFeature.WriteNonStringValueAsString;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteNonStringValueAsStringTestByteField extends TestCase {
    public void test_0() throws Exception {
        WriteNonStringValueAsStringTestByteField.VO vo = new WriteNonStringValueAsStringTestByteField.VO();
        vo.id = 100;
        String text = JSON.toJSONString(vo, WriteNonStringValueAsString);
        Assert.assertEquals("{\"id\":\"100\"}", text);
    }

    public void test_1() throws Exception {
        WriteNonStringValueAsStringTestByteField.V1 vo = new WriteNonStringValueAsStringTestByteField.V1();
        vo.id = 100;
        String text = JSON.toJSONString(vo, WriteNonStringValueAsString);
        Assert.assertEquals("{\"id\":\"100\"}", text);
    }

    public static class VO {
        public byte id;
    }

    private static class V1 {
        public byte id;
    }
}

