/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.memory;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Basic tests for {@link MemoryPooledByteBufferFactory}
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MemoryPooledByteBufferFactoryTest extends TestUsingNativeMemoryChunk {
    private MemoryPooledByteBufferFactory mNativeFactory;

    private MemoryPooledByteBufferFactory mBufferFactory;

    private PoolStats mNativeStats;

    private PoolStats mBufferStats;

    private byte[] mData;

    @Test
    public void testNewByteBuf_1() throws Exception {
        testNewByteBuf_1(mNativeFactory, mNativeStats);
        testNewByteBuf_1(mBufferFactory, mBufferStats);
    }

    @Test
    public void testNewByteBuf_2() throws Exception {
        testNewByteBuf_2(mNativeFactory, mNativeStats);
        testNewByteBuf_2(mBufferFactory, mBufferStats);
    }

    @Test
    public void testNewByteBuf_3() throws Exception {
        testNewByteBuf_3(mNativeFactory, mNativeStats);
        testNewByteBuf_3(mBufferFactory, mBufferStats);
    }

    @Test
    public void testNewByteBuf_4() throws Exception {
        testNewByteBuf_4(mNativeFactory, mNativeStats);
        testNewByteBuf_4(mBufferFactory, mBufferStats);
    }

    @Test
    public void testNewByteBuf_5() {
        MemoryPooledByteBufferFactoryTest.testNewByteBuf_5(mNativeFactory, mNativeStats);
        MemoryPooledByteBufferFactoryTest.testNewByteBuf_5(mBufferFactory, mBufferStats);
    }
}

