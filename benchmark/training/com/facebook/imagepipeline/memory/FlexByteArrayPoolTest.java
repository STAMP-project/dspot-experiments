/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.memory;


import FlexByteArrayPool.SoftRefByteArrayPool;
import MemoryTrimType.OnCloseToDalvikHeapLimit;
import com.facebook.common.references.CloseableReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for {@link FlexByteArrayPool}
 */
@RunWith(RobolectricTestRunner.class)
public class FlexByteArrayPoolTest {
    private static final int MIN_BUFFER_SIZE = 4;

    private static final int MAX_BUFFER_SIZE = 16;

    private FlexByteArrayPool mPool;

    private SoftRefByteArrayPool mDelegatePool;

    @Test
    public void testBasic() throws Exception {
        Assert.assertEquals(FlexByteArrayPoolTest.MIN_BUFFER_SIZE, mPool.getMinBufferSize());
        Assert.assertEquals(FlexByteArrayPoolTest.MAX_BUFFER_SIZE, mDelegatePool.mPoolParams.maxBucketSize);
        Assert.assertEquals(0, mDelegatePool.mFree.mNumBytes);
    }

    @Test
    public void testGet() throws Exception {
        CloseableReference<byte[]> arrayRef = mPool.get(1);
        Assert.assertEquals(0, mDelegatePool.mFree.mNumBytes);
        Assert.assertEquals(FlexByteArrayPoolTest.MIN_BUFFER_SIZE, arrayRef.get().length);
    }

    @Test
    public void testGetTooBigArray() {
        Assert.assertEquals((2 * (FlexByteArrayPoolTest.MAX_BUFFER_SIZE)), mPool.get((2 * (FlexByteArrayPoolTest.MAX_BUFFER_SIZE))).get().length);
    }

    @Test
    public void testRelease() throws Exception {
        mPool.get(FlexByteArrayPoolTest.MIN_BUFFER_SIZE).close();
        Assert.assertEquals(FlexByteArrayPoolTest.MIN_BUFFER_SIZE, mDelegatePool.mFree.mNumBytes);
    }

    @Test
    public void testGet_Realloc() {
        CloseableReference<byte[]> arrayRef = mPool.get(1);
        final byte[] smallArray = arrayRef.get();
        arrayRef.close();
        arrayRef = mPool.get(7);
        Assert.assertEquals(8, arrayRef.get().length);
        Assert.assertNotSame(smallArray, arrayRef.get());
    }

    @Test
    public void testTrim() {
        mPool.get(7).close();
        Assert.assertEquals(1, mDelegatePool.getBucket(8).getFreeListSize());
        // now trim, and verify again
        mDelegatePool.trim(OnCloseToDalvikHeapLimit);
        Assert.assertEquals(0, mDelegatePool.getBucket(8).getFreeListSize());
    }

    @Test
    public void testTrimUnsuccessful() {
        CloseableReference<byte[]> arrayRef = mPool.get(7);
        mDelegatePool.trim(OnCloseToDalvikHeapLimit);
        Assert.assertNotNull(arrayRef.get());
    }

    @Test
    public void testGetBucketedSize() throws Exception {
        Assert.assertEquals(FlexByteArrayPoolTest.MIN_BUFFER_SIZE, mDelegatePool.getBucketedSize(1));
        Assert.assertEquals(FlexByteArrayPoolTest.MIN_BUFFER_SIZE, mDelegatePool.getBucketedSize(2));
        Assert.assertEquals(FlexByteArrayPoolTest.MIN_BUFFER_SIZE, mDelegatePool.getBucketedSize(3));
        Assert.assertEquals(FlexByteArrayPoolTest.MIN_BUFFER_SIZE, mDelegatePool.getBucketedSize(4));
        Assert.assertEquals(8, mDelegatePool.getBucketedSize(5));
        Assert.assertEquals(8, mDelegatePool.getBucketedSize(6));
        Assert.assertEquals(8, mDelegatePool.getBucketedSize(7));
        Assert.assertEquals(8, mDelegatePool.getBucketedSize(8));
        Assert.assertEquals(16, mDelegatePool.getBucketedSize(9));
    }
}

