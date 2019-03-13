package com.bumptech.glide.load.resource.bitmap;


import Bitmap.Config.ARGB_8888;
import Bitmap.Config.RGB_565;
import android.graphics.Bitmap;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.tests.Util;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


// TODO: add a test for bitmap size using getAllocationByteSize when robolectric supports kitkat.
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class BitmapResourceTest {
    private int currentBuildVersion;

    private BitmapResourceTest.BitmapResourceHarness harness;

    @Test
    public void testCanGetBitmap() {
        Assert.assertEquals(harness.bitmap, harness.resource.get());
    }

    @Test
    public void testSizeIsBasedOnDimensPreKitKat() {
        Util.setSdkVersionInt(18);
        Assert.assertEquals((((harness.bitmap.getWidth()) * (harness.bitmap.getHeight())) * 4), harness.resource.getSize());
    }

    @Test
    public void testPutsBitmapInPoolOnRecycle() {
        harness.resource.recycle();
        Mockito.verify(harness.bitmapPool).put(ArgumentMatchers.eq(harness.bitmap));
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsIfBitmapIsNull() {
        new BitmapResource(null, Mockito.mock(BitmapPool.class));
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsIfBitmapPoolIsNull() {
        new BitmapResource(Bitmap.createBitmap(100, 100, RGB_565), null);
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsIfBitmapAndBitmapPoolAreNull() {
        new BitmapResource(null, null);
    }

    private static class BitmapResourceHarness {
        final Bitmap bitmap = Bitmap.createBitmap(100, 100, ARGB_8888);

        final BitmapPool bitmapPool = Mockito.mock(BitmapPool.class);

        final BitmapResource resource = new BitmapResource(bitmap, bitmapPool);
    }
}

