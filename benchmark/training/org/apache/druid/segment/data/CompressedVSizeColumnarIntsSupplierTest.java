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


import CompressedPools.BUFFER_SIZE;
import com.google.common.primitives.Longs;
import java.nio.ByteOrder;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.java.util.common.guava.CloseQuietly;
import org.apache.druid.java.util.common.io.Closer;
import org.apache.druid.segment.CompressedPools;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class CompressedVSizeColumnarIntsSupplierTest extends CompressionStrategyTest {
    private static final int[] MAX_VALUES = new int[]{ 255, 65535, 16777215, 268435455 };

    public CompressedVSizeColumnarIntsSupplierTest(CompressionStrategy compressionStrategy, ByteOrder byteOrder) {
        super(compressionStrategy);
        this.byteOrder = byteOrder;
    }

    private Closer closer;

    private ColumnarInts columnarInts;

    private CompressedVSizeColumnarIntsSupplier supplier;

    private int[] vals;

    private final ByteOrder byteOrder;

    @Test
    public void testSanity() {
        setupSimple(2);
        Assert.assertEquals(8, supplier.getBaseBuffers().size());
        assertIndexMatchesVals();
        setupSimple(4);
        Assert.assertEquals(4, supplier.getBaseBuffers().size());
        assertIndexMatchesVals();
        setupSimple(32);
        Assert.assertEquals(1, supplier.getBaseBuffers().size());
        assertIndexMatchesVals();
    }

    @Test
    public void testLargeChunks() throws Exception {
        for (int maxValue : CompressedVSizeColumnarIntsSupplierTest.MAX_VALUES) {
            final int maxChunkSize = CompressedVSizeColumnarIntsSupplier.maxIntsInBufferForValue(maxValue);
            setupLargeChunks(maxChunkSize, (10 * maxChunkSize), maxValue);
            Assert.assertEquals(10, supplier.getBaseBuffers().size());
            assertIndexMatchesVals();
            setupLargeChunks(maxChunkSize, ((10 * maxChunkSize) + 1), maxValue);
            Assert.assertEquals(11, supplier.getBaseBuffers().size());
            assertIndexMatchesVals();
            setupLargeChunks(1, 65535, maxValue);
            Assert.assertEquals(65535, supplier.getBaseBuffers().size());
            assertIndexMatchesVals();
            setupLargeChunks((maxChunkSize / 2), ((10 * (maxChunkSize / 2)) + 1), maxValue);
            Assert.assertEquals(11, supplier.getBaseBuffers().size());
            assertIndexMatchesVals();
        }
    }

    @Test
    public void testChunkTooBig() throws Exception {
        for (int maxValue : CompressedVSizeColumnarIntsSupplierTest.MAX_VALUES) {
            final int maxChunkSize = CompressedVSizeColumnarIntsSupplier.maxIntsInBufferForValue(maxValue);
            try {
                setupLargeChunks((maxChunkSize + 1), (10 * (maxChunkSize + 1)), maxValue);
                Assert.fail();
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(("chunk too big for maxValue " + maxValue), true);
            }
        }
    }

    @Test
    public void testmaxIntsInBuffer() {
        Assert.assertEquals(BUFFER_SIZE, CompressedVSizeColumnarIntsSupplier.maxIntsInBufferForBytes(1));
        Assert.assertEquals(((CompressedPools.BUFFER_SIZE) / 2), CompressedVSizeColumnarIntsSupplier.maxIntsInBufferForBytes(2));
        Assert.assertEquals(((CompressedPools.BUFFER_SIZE) / 4), CompressedVSizeColumnarIntsSupplier.maxIntsInBufferForBytes(4));
        Assert.assertEquals(BUFFER_SIZE, 65536);// nearest power of 2 is 2^14

        Assert.assertEquals((1 << 14), CompressedVSizeColumnarIntsSupplier.maxIntsInBufferForBytes(3));
    }

    @Test
    public void testSanityWithSerde() throws Exception {
        setupSimpleWithSerde(4);
        Assert.assertEquals(4, supplier.getBaseBuffers().size());
        assertIndexMatchesVals();
        setupSimpleWithSerde(2);
        Assert.assertEquals(8, supplier.getBaseBuffers().size());
        assertIndexMatchesVals();
    }

    // This test attempts to cause a race condition with the DirectByteBuffers, it's non-deterministic in causing it,
    // which sucks but I can't think of a way to deterministically cause it...
    @Test
    public void testConcurrentThreadReads() throws Exception {
        setupSimple(4);
        final AtomicReference<String> reason = new AtomicReference<>("none");
        final int numRuns = 1000;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch stopLatch = new CountDownLatch(2);
        final AtomicBoolean failureHappened = new AtomicBoolean(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startLatch.await();
                } catch (InterruptedException e) {
                    failureHappened.set(true);
                    reason.set("interrupt.");
                    stopLatch.countDown();
                    return;
                }
                try {
                    for (int i = 0; i < numRuns; ++i) {
                        for (int j = 0, size = columnarInts.size(); j < size; ++j) {
                            final long val = vals[j];
                            final long indexedVal = columnarInts.get(j);
                            if ((Longs.compare(val, indexedVal)) != 0) {
                                failureHappened.set(true);
                                reason.set(StringUtils.format("Thread1[%d]: %d != %d", j, val, indexedVal));
                                stopLatch.countDown();
                                return;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    failureHappened.set(true);
                    reason.set(e.getMessage());
                }
                stopLatch.countDown();
            }
        }).start();
        final ColumnarInts columnarInts2 = supplier.get();
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        startLatch.await();
                    } catch (InterruptedException e) {
                        stopLatch.countDown();
                        return;
                    }
                    try {
                        for (int i = 0; i < numRuns; ++i) {
                            for (int j = (columnarInts2.size()) - 1; j >= 0; --j) {
                                final long val = vals[j];
                                final long indexedVal = columnarInts2.get(j);
                                if ((Longs.compare(val, indexedVal)) != 0) {
                                    failureHappened.set(true);
                                    reason.set(StringUtils.format("Thread2[%d]: %d != %d", j, val, indexedVal));
                                    stopLatch.countDown();
                                    return;
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        reason.set(e.getMessage());
                        failureHappened.set(true);
                    }
                    stopLatch.countDown();
                }
            }).start();
            startLatch.countDown();
            stopLatch.await();
        } finally {
            CloseQuietly.close(columnarInts2);
        }
        if (failureHappened.get()) {
            Assert.fail(("Failure happened.  Reason: " + (reason.get())));
        }
    }
}

