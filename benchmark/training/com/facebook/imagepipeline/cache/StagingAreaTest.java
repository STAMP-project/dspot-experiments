/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.cache;


import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.image.EncodedImage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class StagingAreaTest {
    private StagingArea mStagingArea;

    private CloseableReference<PooledByteBuffer> mCloseableReference;

    private CloseableReference<PooledByteBuffer> mCloseableReference2;

    private EncodedImage mEncodedImage;

    private EncodedImage mSecondEncodedImage;

    private CacheKey mCacheKey;

    @Test
    public void testContains() {
        Assert.assertTrue(mStagingArea.containsKey(mCacheKey));
        Assert.assertFalse(mStagingArea.containsKey(new SimpleCacheKey("http://this.is.not.uri")));
    }

    @Test
    public void testDoesntContainInvalid() {
        mEncodedImage.close();
        Assert.assertTrue(mStagingArea.containsKey(mCacheKey));
        Assert.assertTrue(EncodedImage.isValid(mStagingArea.get(mCacheKey)));
    }

    @Test
    public void testGetValue() {
        Assert.assertSame(mCloseableReference.getUnderlyingReferenceTestOnly(), mStagingArea.get(mCacheKey).getByteBufferRef().getUnderlyingReferenceTestOnly());
    }

    @Test
    public void testBumpsRefCountOnGet() {
        mStagingArea.get(mCacheKey);
        Assert.assertEquals(4, mCloseableReference.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
    }

    @Test
    public void testAnotherPut() {
        mStagingArea.put(mCacheKey, mSecondEncodedImage);
        Assert.assertEquals(2, mCloseableReference.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
        Assert.assertSame(mCloseableReference2.getUnderlyingReferenceTestOnly(), mStagingArea.get(mCacheKey).getByteBufferRef().getUnderlyingReferenceTestOnly());
    }

    @Test
    public void testSamePut() {
        Assert.assertEquals(3, mCloseableReference.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
        mStagingArea.put(mCacheKey, mEncodedImage);
        Assert.assertEquals(3, mCloseableReference.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
        Assert.assertSame(mCloseableReference.getUnderlyingReferenceTestOnly(), mStagingArea.get(mCacheKey).getByteBufferRef().getUnderlyingReferenceTestOnly());
    }

    @Test
    public void testRemove() {
        Assert.assertTrue(mStagingArea.remove(mCacheKey, mEncodedImage));
        Assert.assertEquals(2, mCloseableReference.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
        Assert.assertFalse(mStagingArea.remove(mCacheKey, mEncodedImage));
    }

    @Test
    public void testRemoveWithBadRef() {
        Assert.assertFalse(mStagingArea.remove(mCacheKey, mSecondEncodedImage));
        Assert.assertTrue(CloseableReference.isValid(mCloseableReference));
        Assert.assertTrue(CloseableReference.isValid(mCloseableReference2));
    }

    @Test
    public void testRemoveWithoutValueCheck() {
        Assert.assertTrue(mStagingArea.remove(mCacheKey));
        Assert.assertEquals(2, mCloseableReference.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
        Assert.assertFalse(mStagingArea.remove(mCacheKey));
    }

    @Test
    public void testClearAll() {
        mStagingArea.put(new SimpleCacheKey("second"), mSecondEncodedImage);
        mStagingArea.clearAll();
        Assert.assertEquals(2, mCloseableReference.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
        Assert.assertEquals(2, mCloseableReference2.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
        Assert.assertFalse(mStagingArea.remove(mCacheKey));
    }
}

