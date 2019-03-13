package com.airbnb.lottie;


import junit.framework.Assert;
import org.junit.Test;


public class LottieDrawableTest extends BaseTest {
    @Test
    public void testMinFrame() {
        LottieComposition composition = createComposition(31, 391);
        LottieDrawable drawable = new LottieDrawable();
        drawable.setComposition(composition);
        drawable.setMinProgress(0.42F);
        Assert.assertEquals(182.0F, drawable.getMinFrame());
    }

    @Test
    public void testMinWithStartFrameFrame() {
        LottieComposition composition = createComposition(100, 200);
        LottieDrawable drawable = new LottieDrawable();
        drawable.setComposition(composition);
        drawable.setMinProgress(0.5F);
        Assert.assertEquals(150.0F, drawable.getMinFrame());
    }

    @Test
    public void testMaxFrame() {
        LottieComposition composition = createComposition(31, 391);
        LottieDrawable drawable = new LottieDrawable();
        drawable.setComposition(composition);
        drawable.setMaxProgress(0.25F);
        Assert.assertEquals(121.99F, drawable.getMaxFrame());
    }

    @Test
    public void testMinMaxFrame() {
        LottieComposition composition = createComposition(31, 391);
        LottieDrawable drawable = new LottieDrawable();
        drawable.setComposition(composition);
        drawable.setMinAndMaxProgress(0.25F, 0.42F);
        Assert.assertEquals(121.0F, drawable.getMinFrame());
        Assert.assertEquals(182.99F, drawable.getMaxFrame());
    }
}

