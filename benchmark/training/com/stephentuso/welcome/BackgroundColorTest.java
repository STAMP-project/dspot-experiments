package com.stephentuso.welcome;


import Color.BLACK;
import Color.RED;
import Color.WHITE;
import android.graphics.Color;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by stephentuso on 10/6/16.
 */
public class BackgroundColorTest {
    @Test
    public void testBackgroundColor() {
        BackgroundColor color = new BackgroundColor(Color.RED);
        Assert.assertEquals(RED, color.value());
        color = new BackgroundColor(Color.BLACK, Color.WHITE);
        Assert.assertEquals(BLACK, color.value());
        color = new BackgroundColor(null, Color.WHITE);
        Assert.assertEquals(WHITE, color.value());
    }

    @Test
    public void testEquals() {
        BackgroundColor color = new BackgroundColor(Color.RED);
        Assert.assertFalse(color.equals(null));
        Assert.assertFalse(color.equals(RED));
        Assert.assertFalse(color.equals(new BackgroundColor(Color.BLACK)));
        Assert.assertTrue(color.equals(new BackgroundColor(Color.RED)));
        Assert.assertTrue(color.equals(color));
    }
}

