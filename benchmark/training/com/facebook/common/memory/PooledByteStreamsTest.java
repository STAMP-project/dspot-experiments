/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.common.memory;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class PooledByteStreamsTest {
    private static final int POOLED_ARRAY_SIZE = 4;

    private ByteArrayPool mByteArrayPool;

    private byte[] mPooledArray;

    private byte[] mData;

    private InputStream mIs;

    private ByteArrayOutputStream mOs;

    private PooledByteStreams mPooledByteStreams;

    @Test
    public void testUsesPool() throws IOException {
        mPooledByteStreams.copy(mIs, mOs);
        Mockito.verify(mByteArrayPool).get(PooledByteStreamsTest.POOLED_ARRAY_SIZE);
        Mockito.verify(mByteArrayPool).release(mPooledArray);
    }

    @Test
    public void testReleasesOnException() throws IOException {
        try {
            mPooledByteStreams.copy(mIs, new OutputStream() {
                @Override
                public void write(int oneByte) throws IOException {
                    throw new IOException();
                }
            });
            Assert.fail();
        } catch (IOException ioe) {
            // expected
        }
        Mockito.verify(mByteArrayPool).release(mPooledArray);
    }

    @Test
    public void testCopiesData() throws IOException {
        mPooledByteStreams.copy(mIs, mOs);
        Assert.assertArrayEquals(mData, mOs.toByteArray());
    }

    @Test
    public void testReleasesOnExceptionWithSize() throws IOException {
        try {
            mPooledByteStreams.copy(mIs, new OutputStream() {
                @Override
                public void write(int oneByte) throws IOException {
                    throw new IOException();
                }
            }, 3);
            Assert.fail();
        } catch (IOException ioe) {
            // expected
        }
        Mockito.verify(mByteArrayPool).release(mPooledArray);
    }

    @Test
    public void testCopiesDataWithSize() throws IOException {
        mPooledByteStreams.copy(mIs, mOs, 3);
        Assert.assertArrayEquals(Arrays.copyOf(mData, 3), mOs.toByteArray());
    }
}

