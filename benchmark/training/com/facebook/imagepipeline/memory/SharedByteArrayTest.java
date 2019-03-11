/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.memory;


import MemoryTrimType.OnCloseToDalvikHeapLimit;
import com.facebook.common.references.CloseableReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for {@link SharedByteArray}
 */
@RunWith(RobolectricTestRunner.class)
public class SharedByteArrayTest {
    private SharedByteArray mArray;

    @Test
    public void testBasic() throws Exception {
        Assert.assertEquals(4, mArray.mMinByteArraySize);
        Assert.assertEquals(16, mArray.mMaxByteArraySize);
        Assert.assertNull(mArray.mByteArraySoftRef.get());
        Assert.assertEquals(1, mArray.mSemaphore.availablePermits());
    }

    @Test
    public void testGet() throws Exception {
        CloseableReference<byte[]> arrayRef = mArray.get(1);
        Assert.assertSame(mArray.mByteArraySoftRef.get(), arrayRef.get());
        Assert.assertEquals(4, arrayRef.get().length);
        Assert.assertEquals(0, mArray.mSemaphore.availablePermits());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTooBigArray() {
        mArray.get(32);
    }

    @Test
    public void testRelease() throws Exception {
        mArray.get(4).close();
        Assert.assertEquals(1, mArray.mSemaphore.availablePermits());
    }

    @Test
    public void testGet_Realloc() {
        CloseableReference<byte[]> arrayRef = mArray.get(1);
        final byte[] smallArray = arrayRef.get();
        arrayRef.close();
        arrayRef = mArray.get(7);
        Assert.assertEquals(8, arrayRef.get().length);
        Assert.assertSame(mArray.mByteArraySoftRef.get(), arrayRef.get());
        Assert.assertNotSame(smallArray, arrayRef.get());
    }

    @Test
    public void testTrim() {
        mArray.get(7).close();
        Assert.assertEquals(8, mArray.mByteArraySoftRef.get().length);
        // now trim, and verify again
        mArray.trim(OnCloseToDalvikHeapLimit);
        Assert.assertNull(mArray.mByteArraySoftRef.get());
        Assert.assertEquals(1, mArray.mSemaphore.availablePermits());
    }

    @Test
    public void testTrimUnsuccessful() {
        CloseableReference<byte[]> arrayRef = mArray.get(7);
        mArray.trim(OnCloseToDalvikHeapLimit);
        Assert.assertSame(arrayRef.get(), mArray.mByteArraySoftRef.get());
        Assert.assertEquals(0, mArray.mSemaphore.availablePermits());
    }

    @Test
    public void testGetBucketedSize() throws Exception {
        Assert.assertEquals(4, mArray.getBucketedSize(1));
        Assert.assertEquals(4, mArray.getBucketedSize(2));
        Assert.assertEquals(4, mArray.getBucketedSize(3));
        Assert.assertEquals(4, mArray.getBucketedSize(4));
        Assert.assertEquals(8, mArray.getBucketedSize(5));
        Assert.assertEquals(8, mArray.getBucketedSize(6));
        Assert.assertEquals(8, mArray.getBucketedSize(7));
        Assert.assertEquals(8, mArray.getBucketedSize(8));
        Assert.assertEquals(16, mArray.getBucketedSize(9));
    }
}

