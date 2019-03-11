package com.alibaba.json.bvt.serializer.features;


import SerializerFeature.WriteNonStringValueAsString;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteNonStringValueAsStringTestBooleanField extends TestCase {
    public void test_0() throws Exception {
        WriteNonStringValueAsStringTestBooleanField.VO vo = new WriteNonStringValueAsStringTestBooleanField.VO();
        vo.id = true;
        Assert.assertEquals("{\"id\":\"true\"}", JSON.toJSONString(vo, WriteNonStringValueAsString));
        Assert.assertEquals("{\"id\":true}", JSON.toJSONString(vo));
    }

    public static class VO {
        public boolean id;
    }
}

