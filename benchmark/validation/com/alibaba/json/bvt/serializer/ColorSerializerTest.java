package com.alibaba.json.bvt.serializer;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.AwtCodec;
import com.alibaba.fastjson.serializer.JSONSerializer;
import java.awt.Color;
import junit.framework.TestCase;
import org.junit.Assert;


public class ColorSerializerTest extends TestCase {
    public void test_null() throws Exception {
        JSONSerializer serializer = new JSONSerializer();
        Assert.assertEquals(AwtCodec.class, serializer.getObjectWriter(Color.class).getClass());
        ColorSerializerTest.VO vo = new ColorSerializerTest.VO();
        Assert.assertEquals("{\"value\":null}", JSON.toJSONString(vo, WriteMapNullValue));
    }

    public void test_rgb() throws Exception {
        JSONSerializer serializer = new JSONSerializer();
        Assert.assertEquals(AwtCodec.class, serializer.getObjectWriter(Color.class).getClass());
        ColorSerializerTest.VO vo = new ColorSerializerTest.VO();
        vo.setValue(new Color(1, 1, 1, 0));
        Assert.assertEquals("{\"value\":{\"r\":1,\"g\":1,\"b\":1}}", JSON.toJSONString(vo, WriteMapNullValue));
    }

    private static class VO {
        private Color value;

        public Color getValue() {
            return value;
        }

        public void setValue(Color value) {
            this.value = value;
        }
    }
}

