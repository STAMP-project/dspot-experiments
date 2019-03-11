package com.alibaba.json.bvt.serializer;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.AwtCodec;
import com.alibaba.fastjson.serializer.JSONSerializer;
import java.awt.Font;
import junit.framework.TestCase;
import org.junit.Assert;


public class FontSerializerTest extends TestCase {
    public void test_null() throws Exception {
        JSONSerializer serializer = new JSONSerializer();
        Assert.assertEquals(AwtCodec.class, serializer.getObjectWriter(Font.class).getClass());
        FontSerializerTest.VO vo = new FontSerializerTest.VO();
        Assert.assertEquals("{\"value\":null}", JSON.toJSONString(vo, WriteMapNullValue));
    }

    private static class VO {
        private Font value;

        public Font getValue() {
            return value;
        }

        public void setValue(Font value) {
            this.value = value;
        }
    }
}

