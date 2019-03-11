package com.alibaba.json.bvt.serializer.features;


import SerializerFeature.WriteNonStringValueAsString;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteNonStringValueAsStringTestFloatField2 extends TestCase {
    public void test_0() throws Exception {
        WriteNonStringValueAsStringTestFloatField2.VO vo = new WriteNonStringValueAsStringTestFloatField2.VO();
        vo.id = 100;
        String text = JSON.toJSONString(vo, WriteNonStringValueAsString);
        Assert.assertEquals("{\"id\":\"100.00\"}", text);
    }

    public static class VO {
        @JSONField(format = "0.00")
        public float id;
    }
}

