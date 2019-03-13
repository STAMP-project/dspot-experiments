package com.bumptech.glide.load.resource.bitmap;


import Bitmap.Config.ARGB_8888;
import RuntimeEnvironment.application;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class BitmapDrawableResourceTest {
    private BitmapDrawableResourceTest.BitmapDrawableResourceHarness harness;

    @Test
    public void testReturnsGivenBitmapFromGet() {
        Assert.assertEquals(harness.bitmap, harness.create().get().getBitmap());
    }

    @Test
    public void testReturnsDifferentDrawableEachTime() {
        BitmapDrawableResource resource = harness.create();
        BitmapDrawable first = resource.get();
        BitmapDrawable second = resource.get();
        Assert.assertNotSame(first, second);
    }

    @Test
    public void testReturnsSizeFromGivenBitmap() {
        Assert.assertEquals(((harness.bitmap.getHeight()) * (harness.bitmap.getRowBytes())), harness.create().getSize());
    }

    @Test
    public void testBitmapIsReturnedToPoolOnRecycle() {
        harness.create().recycle();
        Mockito.verify(harness.bitmapPool).put(ArgumentMatchers.eq(harness.bitmap));
    }

    private static class BitmapDrawableResourceHarness {
        final BitmapPool bitmapPool = Mockito.mock(BitmapPool.class);

        final Bitmap bitmap = Bitmap.createBitmap(100, 100, ARGB_8888);

        BitmapDrawableResource create() {
            return new BitmapDrawableResource(new BitmapDrawable(application.getResources(), bitmap), bitmapPool);
        }
    }
}

