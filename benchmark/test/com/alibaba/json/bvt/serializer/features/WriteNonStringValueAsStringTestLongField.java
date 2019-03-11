package com.alibaba.json.bvt.serializer.features;


import SerializerFeature.WriteNonStringValueAsString;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteNonStringValueAsStringTestLongField extends TestCase {
    public void test_0() throws Exception {
        WriteNonStringValueAsStringTestLongField.VO vo = new WriteNonStringValueAsStringTestLongField.VO();
        vo.id = 100;
        String text = JSON.toJSONString(vo, WriteNonStringValueAsString);
        Assert.assertEquals("{\"id\":\"100\"}", text);
    }

    public static class VO {
        public long id;
    }
}

