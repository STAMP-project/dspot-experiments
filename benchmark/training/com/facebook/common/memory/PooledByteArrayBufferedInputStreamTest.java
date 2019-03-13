/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.common.memory;


import com.facebook.common.references.ResourceReleaser;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class PooledByteArrayBufferedInputStreamTest {
    private ResourceReleaser mResourceReleaser;

    private byte[] mBuffer;

    private PooledByteArrayBufferedInputStream mPooledByteArrayBufferedInputStream;

    @Test
    public void testSingleByteRead() throws IOException {
        for (int i = 0; i < 256; ++i) {
            Assert.assertEquals(i, mPooledByteArrayBufferedInputStream.read());
        }
        Assert.assertEquals((-1), mPooledByteArrayBufferedInputStream.read());
    }

    @Test
    public void testReleaseOnClose() throws IOException {
        mPooledByteArrayBufferedInputStream.close();
        Mockito.verify(mResourceReleaser).release(mBuffer);
        mPooledByteArrayBufferedInputStream.close();
        // we do not expect second close to release resource again,
        // the one checked bellow is the one that happened when close was called for the first time
        Mockito.verify(mResourceReleaser).release(ArgumentMatchers.any(byte[].class));
    }

    @Test
    public void testSkip() throws IOException {
        // buffer some data
        mPooledByteArrayBufferedInputStream.read();
        Assert.assertEquals(99, mPooledByteArrayBufferedInputStream.skip(99));
        Assert.assertEquals(100, mPooledByteArrayBufferedInputStream.read());
    }

    @Test
    public void testSkip2() throws IOException {
        int i = 0;
        while (i < 256) {
            Assert.assertEquals(i, mPooledByteArrayBufferedInputStream.read());
            i += (mPooledByteArrayBufferedInputStream.skip(7)) + 1;
        } 
    }

    @Test
    public void testMark() {
        Assert.assertFalse(mPooledByteArrayBufferedInputStream.markSupported());
    }

    @Test
    public void testReadWithByteArray() throws IOException {
        byte[] readBuffer = new byte[5];
        Assert.assertEquals(5, mPooledByteArrayBufferedInputStream.read(readBuffer));
        PooledByteArrayBufferedInputStreamTest.assertFilledWithConsecutiveBytes(readBuffer, 0, 5, 0);
    }

    @Test
    public void testNonFullRead() throws IOException {
        byte[] readBuffer = new byte[200];
        Assert.assertEquals(10, mPooledByteArrayBufferedInputStream.read(readBuffer));
        PooledByteArrayBufferedInputStreamTest.assertFilledWithConsecutiveBytes(readBuffer, 0, 10, 0);
        PooledByteArrayBufferedInputStreamTest.assertFilledWithZeros(readBuffer, 10, 200);
    }

    @Test
    public void testNonFullReadWithOffset() throws IOException {
        byte[] readBuffer = new byte[200];
        Assert.assertEquals(10, mPooledByteArrayBufferedInputStream.read(readBuffer, 45, 75));
        PooledByteArrayBufferedInputStreamTest.assertFilledWithZeros(readBuffer, 0, 45);
        PooledByteArrayBufferedInputStreamTest.assertFilledWithConsecutiveBytes(readBuffer, 45, 55, 0);
        PooledByteArrayBufferedInputStreamTest.assertFilledWithZeros(readBuffer, 55, 200);
    }

    @Test
    public void testReadsCombined() throws IOException {
        byte[] readBuffer = new byte[5];
        int i = 0;
        while (i <= 245) {
            Assert.assertEquals(i, mPooledByteArrayBufferedInputStream.read());
            Assert.assertEquals(5, mPooledByteArrayBufferedInputStream.read(readBuffer));
            PooledByteArrayBufferedInputStreamTest.assertFilledWithConsecutiveBytes(readBuffer, 0, readBuffer.length, (i + 1));
            Assert.assertEquals(3, mPooledByteArrayBufferedInputStream.read(readBuffer, 1, 3));
            Assert.assertEquals(((byte) (i + 1)), readBuffer[0]);
            PooledByteArrayBufferedInputStreamTest.assertFilledWithConsecutiveBytes(readBuffer, 1, 4, (i + 6));
            Assert.assertEquals(((byte) (i + 5)), readBuffer[4]);
            Assert.assertEquals(2, mPooledByteArrayBufferedInputStream.skip(2));
            i += 11;
        } 
        Assert.assertEquals((256 - i), mPooledByteArrayBufferedInputStream.available());
    }
}

