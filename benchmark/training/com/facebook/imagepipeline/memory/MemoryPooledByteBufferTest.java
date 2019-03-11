/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.memory;


import PooledByteBuffer.ClosedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Basic tests for {@link MemoryPooledByteBuffer}
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MemoryPooledByteBufferTest extends TestUsingNativeMemoryChunk {
    private static final byte[] BYTES = new byte[]{ 1, 4, 5, 0, 100, 34, 0, 1, -1, -1 };

    private static final int BUFFER_LENGTH = (MemoryPooledByteBufferTest.BYTES.length) - 2;

    @Mock
    private NativeMemoryChunkPool mNativePool;

    private NativeMemoryChunk mNativeChunk;

    private MemoryPooledByteBuffer mNativePooledByteBuffer;

    @Mock
    private BufferMemoryChunkPool mBufferPool;

    private BufferMemoryChunk mBufferChunk;

    private MemoryPooledByteBuffer mBufferPooledByteBuffer;

    @Test
    public void testBasic() {
        MemoryPooledByteBufferTest.testBasic(mNativePooledByteBuffer, mNativeChunk);
        MemoryPooledByteBufferTest.testBasic(mBufferPooledByteBuffer, mBufferChunk);
    }

    @Test
    public void testSimpleRead() {
        MemoryPooledByteBufferTest.testSimpleRead(mNativePooledByteBuffer);
        MemoryPooledByteBufferTest.testSimpleRead(mBufferPooledByteBuffer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSimpleReadOutOfBoundsUsingNativePool() {
        mNativePooledByteBuffer.read(MemoryPooledByteBufferTest.BUFFER_LENGTH);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSimpleReadOutOfBoundsUsingBufferPool() {
        mBufferPooledByteBuffer.read(MemoryPooledByteBufferTest.BUFFER_LENGTH);
    }

    @Test
    public void testRangeRead() {
        MemoryPooledByteBufferTest.testRangeRead(mNativePooledByteBuffer);
        MemoryPooledByteBufferTest.testRangeRead(mBufferPooledByteBuffer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRangeReadOutOfBoundsUsingNativePool() {
        MemoryPooledByteBufferTest.testRangeReadOutOfBounds(mNativePooledByteBuffer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRangeReadOutOfBoundsUsingBufferPool() {
        MemoryPooledByteBufferTest.testRangeReadOutOfBounds(mBufferPooledByteBuffer);
    }

    @Test
    public void testReadFromStream() throws Exception {
        MemoryPooledByteBufferTest.testReadFromStream(mNativePooledByteBuffer);
        MemoryPooledByteBufferTest.testReadFromStream(mBufferPooledByteBuffer);
    }

    @Test
    public void testClose() {
        MemoryPooledByteBufferTest.testClose(mNativePooledByteBuffer, mNativeChunk, mNativePool);
        MemoryPooledByteBufferTest.testClose(mBufferPooledByteBuffer, mBufferChunk, mBufferPool);
    }

    @Test(expected = ClosedException.class)
    public void testGettingSizeAfterCloseUsingNativePool() {
        mNativePooledByteBuffer.close();
        mNativePooledByteBuffer.size();
    }

    @Test(expected = ClosedException.class)
    public void testGettingSizeAfterCloseUsingBufferPool() {
        mBufferPooledByteBuffer.close();
        mBufferPooledByteBuffer.size();
    }
}

