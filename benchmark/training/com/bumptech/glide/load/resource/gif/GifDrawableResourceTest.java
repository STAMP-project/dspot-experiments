package com.bumptech.glide.load.resource.gif;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class GifDrawableResourceTest {
    private GifDrawable drawable;

    private GifDrawableResource resource;

    @Test
    public void testReturnsSizeFromDrawable() {
        final int size = 2134;
        Mockito.when(drawable.getSize()).thenReturn(size);
        Assert.assertEquals(size, resource.getSize());
    }

    @Test
    public void testStopsAndThenRecyclesDrawableWhenRecycled() {
        resource.recycle();
        InOrder inOrder = Mockito.inOrder(drawable);
        inOrder.verify(drawable).stop();
        inOrder.verify(drawable).recycle();
    }
}

