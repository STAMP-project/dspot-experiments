package com.alibaba.json.bvt;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.AwtCodec;
import com.alibaba.fastjson.serializer.JSONSerializer;
import java.awt.Rectangle;
import junit.framework.TestCase;
import org.junit.Assert;


public class RectangleTest extends TestCase {
    public void test_color() throws Exception {
        JSONSerializer serializer = new JSONSerializer();
        Assert.assertEquals(AwtCodec.class, serializer.getObjectWriter(Rectangle.class).getClass());
        Rectangle v = new Rectangle(3, 4, 100, 200);
        String text = JSON.toJSONString(v, WriteClassName);
        System.out.println(text);
        Rectangle v2 = ((Rectangle) (JSON.parse(text)));
        Assert.assertEquals(v, v2);
    }
}

