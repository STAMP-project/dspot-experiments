package com.bumptech.glide.load.engine.bitmap_recycle;


import Bitmap.Config.ALPHA_8;
import Bitmap.Config.ARGB_4444;
import Bitmap.Config.ARGB_8888;
import Bitmap.Config.RGB_565;
import android.graphics.Bitmap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class AttributeStrategyTest {
    private AttributeStrategy strategy;

    @Test
    public void testIGetNullIfNoMatchingBitmapExists() {
        Assert.assertNull(strategy.get(100, 100, ARGB_8888));
    }

    @Test
    public void testICanAddAndGetABitmapOfTheSameSizeAndDimensions() {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, ARGB_8888);
        strategy.put(bitmap);
        Assert.assertEquals(bitmap, strategy.get(bitmap.getWidth(), bitmap.getHeight(), ARGB_8888));
    }

    @Test
    public void testICantGetABitmapOfTheSameDimensionsButDifferentConfigs() {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, ARGB_8888);
        strategy.put(bitmap);
        Assert.assertNull(strategy.get(100, 100, RGB_565));
    }

    @Test
    public void testICantGetABitmapOfTheSameDimensionsAndSizeButDifferentConfigs() {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, ARGB_4444);
        strategy.put(bitmap);
        Assert.assertNull(strategy.get(100, 100, RGB_565));
    }

    @Test
    public void testICantGetABitmapOfDifferentWidths() {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, ARGB_8888);
        strategy.put(bitmap);
        Assert.assertNull(strategy.get(99, 100, ARGB_8888));
    }

    @Test
    public void testICantGetABitmapOfDifferentHeights() {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, ARGB_8888);
        strategy.put(bitmap);
        Assert.assertNull(strategy.get(100, 99, ARGB_8888));
    }

    @Test
    public void testICantGetABitmapOfDifferentDimensionsButTheSameSize() {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, ARGB_8888);
        strategy.put(bitmap);
        Assert.assertNull(strategy.get(50, 200, ARGB_8888));
    }

    @Test
    public void testMultipleBitmapsOfDifferentAttributesCanBeAddedAtOnce() {
        Bitmap first = Bitmap.createBitmap(100, 100, RGB_565);
        Bitmap second = Bitmap.createBitmap(100, 100, ARGB_8888);
        Bitmap third = Bitmap.createBitmap(120, 120, RGB_565);
        strategy.put(first);
        strategy.put(second);
        strategy.put(third);
        Assert.assertEquals(first, strategy.get(100, 100, RGB_565));
        Assert.assertEquals(second, strategy.get(100, 100, ARGB_8888));
        Assert.assertEquals(third, strategy.get(120, 120, RGB_565));
    }

    @Test
    public void testLeastRecentlyUsedAttributeSetIsRemovedFirst() {
        final Bitmap leastRecentlyUsed = Bitmap.createBitmap(100, 100, ALPHA_8);
        final Bitmap other = Bitmap.createBitmap(1000, 1000, RGB_565);
        final Bitmap mostRecentlyUsed = Bitmap.createBitmap(100, 100, ARGB_8888);
        strategy.get(100, 100, ALPHA_8);
        strategy.get(1000, 1000, RGB_565);
        strategy.get(100, 100, ARGB_8888);
        strategy.put(other);
        strategy.put(leastRecentlyUsed);
        strategy.put(mostRecentlyUsed);
        Bitmap removed = strategy.removeLast();
        Assert.assertEquals(((("Expected=" + (strategy.logBitmap(leastRecentlyUsed))) + " got=") + (strategy.logBitmap(removed))), leastRecentlyUsed, removed);
    }
}

