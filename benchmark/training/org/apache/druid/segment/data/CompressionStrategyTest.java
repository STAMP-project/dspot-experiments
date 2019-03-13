/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.segment.data;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.druid.java.util.common.io.Closer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class CompressionStrategyTest {
    protected final CompressionStrategy compressionStrategy;

    public CompressionStrategyTest(CompressionStrategy compressionStrategy) {
        this.compressionStrategy = compressionStrategy;
    }

    // MUST be smaller than CompressedPools.BUFFER_SIZE
    private static final int DATA_SIZER = 65535;

    private static byte[] originalData;

    private Closer closer;

    @Test
    public void testBasicOperations() {
        ByteBuffer compressionOut = compressionStrategy.getCompressor().allocateOutBuffer(CompressionStrategyTest.originalData.length, closer);
        ByteBuffer compressed = compressionStrategy.getCompressor().compress(ByteBuffer.wrap(CompressionStrategyTest.originalData), compressionOut);
        ByteBuffer output = ByteBuffer.allocate(CompressionStrategyTest.originalData.length);
        compressionStrategy.getDecompressor().decompress(compressed, compressed.remaining(), output);
        byte[] checkArray = new byte[CompressionStrategyTest.DATA_SIZER];
        output.get(checkArray);
        Assert.assertArrayEquals("Uncompressed data does not match", CompressionStrategyTest.originalData, checkArray);
    }

    @Test
    public void testDirectMemoryOperations() {
        ByteBuffer compressionOut = compressionStrategy.getCompressor().allocateOutBuffer(CompressionStrategyTest.originalData.length, closer);
        ByteBuffer compressed = compressionStrategy.getCompressor().compress(ByteBuffer.wrap(CompressionStrategyTest.originalData), compressionOut);
        ByteBuffer output = ByteBuffer.allocateDirect(CompressionStrategyTest.originalData.length);
        compressionStrategy.getDecompressor().decompress(compressed, compressed.remaining(), output);
        byte[] checkArray = new byte[CompressionStrategyTest.DATA_SIZER];
        output.get(checkArray);
        Assert.assertArrayEquals("Uncompressed data does not match", CompressionStrategyTest.originalData, checkArray);
    }

    @Test(timeout = 60000L)
    public void testConcurrency() throws Exception {
        final int numThreads = 20;
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(numThreads);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(numThreads, numThreads, 100, TimeUnit.MILLISECONDS, queue);
        Collection<Future<Boolean>> results = new ArrayList<>();
        for (int i = 0; i < numThreads; ++i) {
            results.add(threadPoolExecutor.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    ByteBuffer compressionOut = compressionStrategy.getCompressor().allocateOutBuffer(CompressionStrategyTest.originalData.length, closer);
                    ByteBuffer compressed = compressionStrategy.getCompressor().compress(ByteBuffer.wrap(CompressionStrategyTest.originalData), compressionOut);
                    ByteBuffer output = ByteBuffer.allocate(CompressionStrategyTest.originalData.length);
                    compressionStrategy.getDecompressor().decompress(compressed, compressed.remaining(), output);
                    byte[] checkArray = new byte[CompressionStrategyTest.DATA_SIZER];
                    output.get(checkArray);
                    Assert.assertArrayEquals("Uncompressed data does not match", CompressionStrategyTest.originalData, checkArray);
                    return true;
                }
            }));
        }
        threadPoolExecutor.shutdown();
        for (Future result : results) {
            Assert.assertTrue(((Boolean) (result.get())));
        }
    }
}

