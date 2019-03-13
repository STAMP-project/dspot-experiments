package com.alibaba.json.bvt.awt;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.AwtCodec;
import com.alibaba.fastjson.serializer.JSONSerializer;
import java.awt.Color;
import junit.framework.TestCase;
import org.junit.Assert;


public class ColorTest2 extends TestCase {
    public void test_color() throws Exception {
        JSONSerializer serializer = new JSONSerializer();
        Assert.assertEquals(AwtCodec.class, serializer.getObjectWriter(Color.class).getClass());
        Color color = Color.RED;
        String text = JSON.toJSONString(color, WriteClassName);
        System.out.println(text);
        Color color2 = ((Color) (JSON.parse(text)));
        Assert.assertEquals(color, color2);
    }
}

