package com.bumptech.glide.load.engine.prefill;


import android.graphics.Bitmap;
import com.google.common.testing.EqualsTester;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class PreFillTypeTest {
    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfSizeIsZero() {
        new PreFillType.Builder(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfWidthIsZero() {
        new PreFillType.Builder(0, 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfHeightIsZero() {
        new PreFillType.Builder(100, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfWeightIsZero() {
        new PreFillType.Builder(100).setWeight(0);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorThrowsIfConfigIsNull() {
        new PreFillType(100, 100, null, 1);
    }

    @Test
    public void testGetWidthReturnsGivenWidth() {
        int width = 500;
        Assert.assertEquals(width, getWidth());
    }

    @Test
    public void testGetHeightReturnsGivenHeight() {
        int height = 123;
        Assert.assertEquals(height, getHeight());
    }

    @Test
    public void testGetConfigReturnsGivenConfig() {
        Bitmap.Config config = Config.ARGB_8888;
        Assert.assertEquals(config, getConfig());
    }

    @Test
    public void testGetWeightReturnsGivenWeight() {
        int weight = 400;
        Assert.assertEquals(weight, getWeight());
    }

    @Test
    public void testEquality() {
        new EqualsTester().addEqualityGroup(new PreFillType(100, 100, Config.ARGB_4444, 1), new PreFillType(100, 100, Config.ARGB_4444, 1)).addEqualityGroup(new PreFillType(200, 100, Config.ARGB_4444, 1)).addEqualityGroup(new PreFillType(100, 200, Config.ARGB_4444, 1)).addEqualityGroup(new PreFillType(100, 100, Config.ARGB_8888, 1)).addEqualityGroup(new PreFillType(100, 100, Config.ARGB_4444, 2)).testEquals();
    }
}

