package com.alibaba.json.bvt.serializer.features;


import SerializerFeature.WriteNonStringValueAsString;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteNonStringValueAsStringTestIntegerField extends TestCase {
    public void test_0() throws Exception {
        WriteNonStringValueAsStringTestIntegerField.VO vo = new WriteNonStringValueAsStringTestIntegerField.VO();
        vo.id = 100;
        String text = JSON.toJSONString(vo, WriteNonStringValueAsString);
        Assert.assertEquals("{\"id\":\"100\"}", text);
    }

    public void test_1() throws Exception {
        WriteNonStringValueAsStringTestIntegerField.V1 vo = new WriteNonStringValueAsStringTestIntegerField.V1();
        vo.id = 100;
        String text = JSON.toJSONString(vo, WriteNonStringValueAsString);
        Assert.assertEquals("{\"id\":\"100\"}", text);
    }

    public static class VO {
        public Integer id;
    }

    private static class V1 {
        public Integer id;
    }
}

