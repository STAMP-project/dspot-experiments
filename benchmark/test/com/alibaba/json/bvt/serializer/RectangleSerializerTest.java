package com.alibaba.json.bvt.serializer;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.AwtCodec;
import com.alibaba.fastjson.serializer.JSONSerializer;
import java.awt.Rectangle;
import junit.framework.TestCase;
import org.junit.Assert;


public class RectangleSerializerTest extends TestCase {
    public void test_null() throws Exception {
        JSONSerializer serializer = new JSONSerializer();
        Assert.assertEquals(AwtCodec.class, serializer.getObjectWriter(Rectangle.class).getClass());
        RectangleSerializerTest.VO vo = new RectangleSerializerTest.VO();
        Assert.assertEquals("{\"value\":null}", JSON.toJSONString(vo, WriteMapNullValue));
    }

    private static class VO {
        private Rectangle value;

        public Rectangle getValue() {
            return value;
        }

        public void setValue(Rectangle value) {
            this.value = value;
        }
    }
}

