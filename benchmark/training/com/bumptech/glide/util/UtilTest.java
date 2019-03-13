package com.bumptech.glide.util;


import Bitmap.Config.ALPHA_8;
import Bitmap.Config.ARGB_4444;
import Bitmap.Config.ARGB_8888;
import Bitmap.Config.RGBA_F16;
import Bitmap.Config.RGB_565;
import android.graphics.Bitmap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 27)
public class UtilTest {
    @Test
    public void testReturnsCorrectBitmapSizeForDifferentDimensions() {
        int width = 100;
        int height = 100;
        Bitmap.Config config = Config.ARGB_8888;
        int initialSize = Util.getBitmapByteSize(width, height, config);
        int sizeOne = Util.getBitmapByteSize((width * 2), height, config);
        int sizeTwo = Util.getBitmapByteSize(width, (height * 2), config);
        Assert.assertEquals(((4 * width) * height), initialSize);
        Assert.assertEquals((2 * initialSize), sizeOne);
        Assert.assertEquals((2 * initialSize), sizeTwo);
    }

    @Test
    public void testReturnsCorrectBitmapSizeForAlpha8Bitmap() {
        int width = 110;
        int height = 43;
        int size = Util.getBitmapByteSize(width, height, ALPHA_8);
        Assert.assertEquals((width * height), size);
    }

    @Test
    public void testReturnsCorrectBitmapSizeForRgb565() {
        int width = 34;
        int height = 1444;
        int size = Util.getBitmapByteSize(width, height, RGB_565);
        Assert.assertEquals(((width * height) * 2), size);
    }

    @Test
    public void testReturnsCorrectBitmapSizeForARGB4444() {
        int width = 4454;
        int height = 1235;
        int size = Util.getBitmapByteSize(width, height, ARGB_4444);
        Assert.assertEquals(((width * height) * 2), size);
    }

    @Test
    public void testReturnsCorrectBitmapSizeForARGB8888() {
        int width = 943;
        int height = 3584;
        int size = Util.getBitmapByteSize(width, height, ARGB_8888);
        Assert.assertEquals(((width * height) * 4), size);
    }

    @Test
    public void testReturnsLargestSizeForNullConfig() {
        int width = 999;
        int height = 41324;
        int size = Util.getBitmapByteSize(width, height, null);
        Assert.assertEquals(((width * height) * 4), size);
    }

    @Test
    public void getBitmapByteSize_withRGBA_F16_returnsCorrectSize() {
        int width = 100;
        int height = 200;
        assertThat(Util.getBitmapByteSize(width, height, RGBA_F16)).isEqualTo(((width * height) * 8));
    }
}

