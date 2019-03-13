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


import com.google.common.primitives.Longs;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.java.util.common.guava.CloseQuietly;
import org.apache.druid.java.util.common.io.Closer;
import org.apache.druid.segment.CompressedPools;
import org.junit.Assert;
import org.junit.Test;


public class CompressedColumnarIntsSupplierTest extends CompressionStrategyTest {
    public CompressedColumnarIntsSupplierTest(CompressionStrategy compressionStrategy) {
        super(compressionStrategy);
    }

    private Closer closer;

    private ColumnarInts columnarInts;

    private CompressedColumnarIntsSupplier supplier;

    private int[] vals;

    @Test
    public void testSanity() {
        setupSimple(5);
        Assert.assertEquals(4, supplier.getBaseIntBuffers().size());
        assertIndexMatchesVals();
        // test powers of 2
        setupSimple(4);
        Assert.assertEquals(4, supplier.getBaseIntBuffers().size());
        assertIndexMatchesVals();
        setupSimple(32);
        Assert.assertEquals(1, supplier.getBaseIntBuffers().size());
        assertIndexMatchesVals();
    }

    @Test
    public void testLargeChunks() throws Exception {
        final int maxChunkSize = (CompressedPools.BUFFER_SIZE) / (Long.BYTES);
        setupLargeChunks(maxChunkSize, (10 * maxChunkSize));
        Assert.assertEquals(10, supplier.getBaseIntBuffers().size());
        assertIndexMatchesVals();
        setupLargeChunks(maxChunkSize, ((10 * maxChunkSize) + 1));
        Assert.assertEquals(11, supplier.getBaseIntBuffers().size());
        assertIndexMatchesVals();
        setupLargeChunks((maxChunkSize - 1), ((10 * (maxChunkSize - 1)) + 1));
        Assert.assertEquals(11, supplier.getBaseIntBuffers().size());
        assertIndexMatchesVals();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChunkTooBig() throws Exception {
        final int maxChunkSize = (CompressedPools.BUFFER_SIZE) / (Integer.BYTES);
        setupLargeChunks((maxChunkSize + 1), (10 * (maxChunkSize + 1)));
    }

    @Test
    public void testSanityWithSerde() throws Exception {
        setupSimpleWithSerde(5);
        Assert.assertEquals(4, supplier.getBaseIntBuffers().size());
        assertIndexMatchesVals();
    }

    // This test attempts to cause a race condition with the DirectByteBuffers, it's non-deterministic in causing it,
    // which sucks but I can't think of a way to deterministically cause it...
    @Test
    public void testConcurrentThreadReads() throws Exception {
        setupSimple(5);
        final AtomicReference<String> reason = new AtomicReference<String>("none");
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

