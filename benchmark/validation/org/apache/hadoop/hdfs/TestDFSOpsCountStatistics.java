/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hadoop.fs.StorageStatistics.LongStatistic;
import org.apache.hadoop.hdfs.DFSOpsCountStatistics.OpType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This tests basic operations of {@link DFSOpsCountStatistics} class.
 */
public class TestDFSOpsCountStatistics {
    private static final Logger LOG = LoggerFactory.getLogger(TestDFSOpsCountStatistics.class);

    private static final String NO_SUCH_OP = "no-such-dfs-operation-dude";

    private final DFSOpsCountStatistics statistics = new DFSOpsCountStatistics();

    private final Map<OpType, AtomicLong> expectedOpsCountMap = new HashMap<>();

    @Rule
    public final Timeout globalTimeout = new Timeout((10 * 1000));

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    /**
     * This is to test the the {@link OpType} symbols are unique.
     */
    @Test
    public void testOpTypeSymbolsAreUnique() {
        final Set<String> opTypeSymbols = new HashSet<>();
        for (OpType opType : OpType.values()) {
            Assert.assertFalse(opTypeSymbols.contains(opType.getSymbol()));
            opTypeSymbols.add(opType.getSymbol());
        }
        Assert.assertEquals(OpType.values().length, opTypeSymbols.size());
    }

    @Test
    public void testGetLongStatistics() {
        short iterations = 0;// number of the iter.hasNext()

        final Iterator<LongStatistic> iter = statistics.getLongStatistics();
        while (iter.hasNext()) {
            final LongStatistic longStat = iter.next();
            Assert.assertNotNull(longStat);
            final OpType opType = OpType.fromSymbol(longStat.getName());
            Assert.assertNotNull(opType);
            Assert.assertTrue(expectedOpsCountMap.containsKey(opType));
            Assert.assertEquals(expectedOpsCountMap.get(opType).longValue(), longStat.getValue());
            iterations++;
        } 
        // check that all the OpType enum entries are iterated via iter
        Assert.assertEquals(OpType.values().length, iterations);
    }

    @Test
    public void testGetLong() {
        Assert.assertNull(statistics.getLong(null));
        Assert.assertNull(statistics.getLong(TestDFSOpsCountStatistics.NO_SUCH_OP));
        verifyStatistics();
    }

    @Test
    public void testIsTracked() {
        Assert.assertFalse(statistics.isTracked(null));
        Assert.assertFalse(statistics.isTracked(TestDFSOpsCountStatistics.NO_SUCH_OP));
        final Iterator<LongStatistic> iter = statistics.getLongStatistics();
        while (iter.hasNext()) {
            final LongStatistic longStatistic = iter.next();
            Assert.assertTrue(statistics.isTracked(longStatistic.getName()));
        } 
    }

    @Test
    public void testReset() {
        statistics.reset();
        for (OpType opType : OpType.values()) {
            expectedOpsCountMap.get(opType).set(0);
        }
        final Iterator<LongStatistic> iter = statistics.getLongStatistics();
        while (iter.hasNext()) {
            final LongStatistic longStat = iter.next();
            Assert.assertEquals(0, longStat.getValue());
        } 
        incrementOpsCountByRandomNumbers();
        verifyStatistics();
    }

    @Test
    public void testCurrentAccess() throws InterruptedException {
        final int numThreads = 10;
        final ExecutorService threadPool = newFixedThreadPool(numThreads);
        try {
            final CountDownLatch allReady = new CountDownLatch(numThreads);
            final CountDownLatch startBlocker = new CountDownLatch(1);
            final CountDownLatch allDone = new CountDownLatch(numThreads);
            final AtomicReference<Throwable> childError = new AtomicReference<>();
            for (int i = 0; i < numThreads; i++) {
                threadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        allReady.countDown();
                        try {
                            startBlocker.await();
                            incrementOpsCountByRandomNumbers();
                        } catch (Throwable t) {
                            TestDFSOpsCountStatistics.LOG.error("Child failed when calling mkdir", t);
                            childError.compareAndSet(null, t);
                        } finally {
                            allDone.countDown();
                        }
                    }
                });
            }
            allReady.await();// wait until all threads are ready

            startBlocker.countDown();// all threads start making directories

            allDone.await();// wait until all threads are done

            Assert.assertNull("Child failed with exception.", childError.get());
            verifyStatistics();
        } finally {
            threadPool.shutdownNow();
        }
    }
}

