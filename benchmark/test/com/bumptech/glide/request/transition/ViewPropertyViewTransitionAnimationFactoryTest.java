package com.bumptech.glide.request.transition;


import DataSource.DATA_DISK_CACHE;
import DataSource.MEMORY_CACHE;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ViewPropertyViewTransitionAnimationFactoryTest {
    private ViewPropertyAnimationFactory<Object> factory;

    @Test
    public void testReturnsNoAnimationIfFromMemoryCache() {
        Assert.assertEquals(NoTransition.get(), /* isFirstResource */
        factory.build(MEMORY_CACHE, true));
    }

    @Test
    public void testReturnsNoAnimationIfNotFirstResource() {
        Assert.assertEquals(NoTransition.get(), /* isFirstResource */
        factory.build(DATA_DISK_CACHE, false));
    }

    @Test
    public void testReturnsAnimationIfNotFromMemoryCacheAndFirstResource() {
        Assert.assertNotEquals(NoTransition.get(), /* isFirstResource */
        factory.build(DATA_DISK_CACHE, true));
    }
}

