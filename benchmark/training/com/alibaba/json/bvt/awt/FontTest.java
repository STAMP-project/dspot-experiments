package com.alibaba.json.bvt.awt;


import com.alibaba.fastjson.JSON;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import junit.framework.TestCase;
import org.junit.Assert;


public class FontTest extends TestCase {
    public void test_color() throws Exception {
        Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        for (Font font : fonts) {
            String text = JSON.toJSONString(font);
            Font font2 = JSON.parseObject(text, Font.class);
            Assert.assertEquals(font, font2);
        }
    }
}

