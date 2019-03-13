package com.alibaba.json.bvt.awt;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import junit.framework.TestCase;
import org.junit.Assert;


public class FontTest2 extends TestCase {
    public void test_color() throws Exception {
        Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        for (Font font : fonts) {
            String text = JSON.toJSONString(font, WriteClassName);
            Font font2 = ((Font) (JSON.parse(text)));
            Assert.assertEquals(font, font2);
        }
    }
}

