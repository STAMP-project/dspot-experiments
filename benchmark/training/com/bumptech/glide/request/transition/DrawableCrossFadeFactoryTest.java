package com.bumptech.glide.request.transition;


import DataSource.DATA_DISK_CACHE;
import DataSource.MEMORY_CACHE;
import android.graphics.drawable.Drawable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class DrawableCrossFadeFactoryTest {
    private DrawableCrossFadeFactory factory;

    @Test
    public void testReturnsNoAnimationIfFromMemoryCache() {
        Assert.assertEquals(NoTransition.<Drawable>get(), /* isFirstResource */
        factory.build(MEMORY_CACHE, true));
    }

    @Test
    public void testReturnsReturnsAnimationIfNotFromMemoryCacheAndIsFirstResource() {
        Assert.assertNotEquals(NoTransition.<Drawable>get(), /* isFirstResource */
        factory.build(DATA_DISK_CACHE, true));
    }

    @Test
    public void testReturnsAnimationIfNotFromMemoryCacheAndNotIsFirstResource() {
        Assert.assertNotEquals(NoTransition.<Drawable>get(), /* isFirstResource */
        factory.build(DATA_DISK_CACHE, false));
    }
}

