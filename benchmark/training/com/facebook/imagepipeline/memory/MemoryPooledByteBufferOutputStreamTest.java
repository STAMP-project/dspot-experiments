/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.memory;


import MemoryPooledByteBufferOutputStream.InvalidStreamException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Tests for {@link MemoryPooledByteBufferOutputStream}
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MemoryPooledByteBufferOutputStreamTest extends TestUsingNativeMemoryChunk {
    private NativeMemoryChunkPool mNativePool;

    private BufferMemoryChunkPool mBufferPool;

    private byte[] mData;

    private PoolStats<byte[]> mNativeStats;

    private PoolStats<byte[]> mBufferStats;

    @Test
    public void testBasic_1() throws Exception {
        testBasic_1(mNativePool, mNativeStats);
        testBasic_1(mBufferPool, mBufferStats);
    }

    @Test
    public void testBasic_2() throws Exception {
        testBasic_2(mNativePool, mNativeStats);
        testBasic_2(mBufferPool, mBufferStats);
    }

    @Test
    public void testBasic_3() throws Exception {
        testBasic_3(mNativePool, mNativeStats);
        testBasic_3(mBufferPool, mBufferStats);
    }

    @Test
    public void testBasic_4() throws Exception {
        testBasic_4(mNativePool, mNativeStats);
        testBasic_4(mBufferPool, mBufferStats);
    }

    @Test
    public void testClose() {
        MemoryPooledByteBufferOutputStreamTest.testClose(mNativePool, mNativeStats);
        MemoryPooledByteBufferOutputStreamTest.testClose(mBufferPool, mBufferStats);
    }

    @Test(expected = InvalidStreamException.class)
    public void testToByteBufExceptionUsingNativePool() {
        MemoryPooledByteBufferOutputStreamTest.testToByteBufException(mNativePool);
    }

    @Test(expected = InvalidStreamException.class)
    public void testToByteBufExceptionUsingBufferPool() {
        MemoryPooledByteBufferOutputStreamTest.testToByteBufException(mBufferPool);
    }

    @Test
    public void testWriteAfterToByteBuf() throws Exception {
        testWriteAfterToByteBuf(mNativePool);
        testWriteAfterToByteBuf(mBufferPool);
    }
}

