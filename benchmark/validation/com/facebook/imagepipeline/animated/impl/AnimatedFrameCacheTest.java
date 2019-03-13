/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.animated.impl;


import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.Supplier;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.image.CloseableImage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertFalse;


@RunWith(RobolectricTestRunner.class)
public class AnimatedFrameCacheTest {
    @Mock
    public MemoryTrimmableRegistry mMemoryTrimmableRegistry;

    @Mock
    public Supplier<MemoryCacheParams> mMemoryCacheParamsSupplier;

    @Mock
    public PlatformBitmapFactory mPlatformBitmapFactory;

    private CacheKey mCacheKey;

    private AnimatedFrameCache mAnimatedFrameCache;

    private CloseableReference<CloseableImage> mFrame1;

    private CloseableReference<CloseableImage> mFrame2;

    @Test
    public void testBasic() {
        CloseableReference<CloseableImage> ret = mAnimatedFrameCache.cache(1, mFrame1);
        Assert.assertSame(ret.get(), mFrame1.get());
    }

    @Test
    public void testMultipleFrames() {
        mAnimatedFrameCache.cache(1, mFrame1);
        mAnimatedFrameCache.cache(2, mFrame2);
        Assert.assertSame(mFrame1.get(), mAnimatedFrameCache.get(1).get());
        Assert.assertSame(mFrame2.get(), mAnimatedFrameCache.get(2).get());
    }

    @Test
    public void testReplace() {
        mAnimatedFrameCache.cache(1, mFrame1);
        mAnimatedFrameCache.cache(1, mFrame2);
        Assert.assertNotSame(mFrame1.get(), mAnimatedFrameCache.get(1).get());
        Assert.assertSame(mFrame2.get(), mAnimatedFrameCache.get(1).get());
    }

    @Test
    public void testReuse() {
        CloseableReference<CloseableImage> ret = mAnimatedFrameCache.cache(1, mFrame1);
        ret.close();
        CloseableReference<CloseableImage> free = mAnimatedFrameCache.getForReuse();
        Assert.assertNotNull(free);
    }

    @Test
    public void testCantReuseIfNotClosed() {
        CloseableReference<CloseableImage> ret = mAnimatedFrameCache.cache(1, mFrame1);
        CloseableReference<CloseableImage> free = mAnimatedFrameCache.getForReuse();
        Assert.assertNull(free);
    }

    @Test
    public void testStillThereIfClosed() {
        CloseableReference<CloseableImage> ret = mAnimatedFrameCache.cache(1, mFrame1);
        ret.close();
        Assert.assertNotNull(mAnimatedFrameCache.get(1));
    }

    @Test
    public void testContains() {
        assertFalse(mAnimatedFrameCache.contains(1));
        CloseableReference<CloseableImage> ret = mAnimatedFrameCache.cache(1, mFrame1);
        Assert.assertTrue(mAnimatedFrameCache.contains(1));
        assertFalse(mAnimatedFrameCache.contains(2));
        ret.close();
        Assert.assertTrue(mAnimatedFrameCache.contains(1));
        assertFalse(mAnimatedFrameCache.contains(2));
    }

    @Test
    public void testContainsWhenReused() {
        CloseableReference<CloseableImage> ret = mAnimatedFrameCache.cache(1, mFrame1);
        ret.close();
        Assert.assertTrue(mAnimatedFrameCache.contains(1));
        assertFalse(mAnimatedFrameCache.contains(2));
        CloseableReference<CloseableImage> free = mAnimatedFrameCache.getForReuse();
        free.close();
        assertFalse(mAnimatedFrameCache.contains(1));
        assertFalse(mAnimatedFrameCache.contains(2));
    }

    @Test
    public void testContainsFullReuseFlowWithMultipleItems() {
        assertFalse(mAnimatedFrameCache.contains(1));
        assertFalse(mAnimatedFrameCache.contains(2));
        CloseableReference<CloseableImage> ret = mAnimatedFrameCache.cache(1, mFrame1);
        CloseableReference<CloseableImage> ret2 = mAnimatedFrameCache.cache(2, mFrame2);
        Assert.assertTrue(mAnimatedFrameCache.contains(1));
        Assert.assertTrue(mAnimatedFrameCache.contains(2));
        ret.close();
        ret2.close();
        Assert.assertTrue(mAnimatedFrameCache.contains(1));
        Assert.assertTrue(mAnimatedFrameCache.contains(2));
        CloseableReference<CloseableImage> free = mAnimatedFrameCache.getForReuse();
        free.close();
        assertFalse(mAnimatedFrameCache.contains(1));
        Assert.assertTrue(mAnimatedFrameCache.contains(2));
        free = mAnimatedFrameCache.getForReuse();
        free.close();
        assertFalse(mAnimatedFrameCache.contains(1));
        assertFalse(mAnimatedFrameCache.contains(2));
    }
}

