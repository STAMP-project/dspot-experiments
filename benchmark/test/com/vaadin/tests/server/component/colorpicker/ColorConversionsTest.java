package com.vaadin.tests.server.component.colorpicker;


import com.vaadin.shared.ui.colorpicker.Color;
import org.junit.Assert;
import org.junit.Test;


public class ColorConversionsTest {
    @Test
    public void convertHSL2RGB() {
        int rgb = Color.HSLtoRGB(100, 50, 50);
        Color c = new Color(rgb);
        Assert.assertEquals(106, c.getRed());
        Assert.assertEquals(191, c.getGreen());
        Assert.assertEquals(64, c.getBlue());
        Assert.assertEquals("#6abf40", c.getCSS());
        rgb = Color.HSLtoRGB(0, 50, 50);
        c = new Color(rgb);
        Assert.assertEquals(191, c.getRed());
        Assert.assertEquals(64, c.getGreen());
        Assert.assertEquals(64, c.getBlue());
        Assert.assertEquals("#bf4040", c.getCSS());
        rgb = Color.HSLtoRGB(50, 0, 50);
        c = new Color(rgb);
        Assert.assertEquals(128, c.getRed());
        Assert.assertEquals(128, c.getGreen());
        Assert.assertEquals(128, c.getBlue());
        Assert.assertEquals("#808080", c.getCSS());
        rgb = Color.HSLtoRGB(50, 100, 0);
        c = new Color(rgb);
        Assert.assertEquals(0, c.getRed(), 0);
        Assert.assertEquals(0, c.getGreen(), 0);
        Assert.assertEquals(0, c.getBlue(), 0);
        Assert.assertEquals("#000000", c.getCSS());
    }
}

