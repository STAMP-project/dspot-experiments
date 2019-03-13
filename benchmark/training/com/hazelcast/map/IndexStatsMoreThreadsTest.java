/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.map;


import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicates;
import com.hazelcast.test.HazelcastParallelParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParallelParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class IndexStatsMoreThreadsTest extends HazelcastTestSupport {
    @Parameterized.Parameter
    public InMemoryFormat inMemoryFormat;

    private static final int THREADS_NUMBER = 10;

    private static final int THREADS_ITERATION = 100;

    private String mapName;

    private HazelcastInstance instance;

    private IMap<Integer, Integer> map;

    @Test
    public void testIndexStatsWithMoreThreads() throws InterruptedException {
        int entryCount = 100;
        final int lessEqualCount = 25;
        double expectedEqual = 1.0 - (1.0 / entryCount);
        double expectedGreaterEqual = 1.0 - (((double) (lessEqualCount)) / entryCount);
        map.addIndex("this", false);
        for (int i = 0; i < entryCount; ++i) {
            map.put(i, i);
        }
        Assert.assertEquals(0, stats().getQueryCount());
        Assert.assertEquals(0, stats().getIndexedQueryCount());
        Assert.assertEquals(0, valueStats().getQueryCount());
        Assert.assertEquals(0, valueStats().getHitCount());
        final CountDownLatch startGate = new CountDownLatch(1);
        final CountDownLatch endGate = new CountDownLatch(IndexStatsMoreThreadsTest.THREADS_NUMBER);
        for (int i = 0; i < (IndexStatsMoreThreadsTest.THREADS_NUMBER); i++) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        startGate.await();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    try {
                        for (int j = 0; j < (IndexStatsMoreThreadsTest.THREADS_ITERATION); j++) {
                            map.entrySet(Predicates.alwaysTrue());
                            map.entrySet(Predicates.equal("this", 10));
                            map.entrySet(Predicates.lessEqual("this", lessEqualCount));
                        }
                    } finally {
                        endGate.countDown();
                    }
                }
            }.start();
        }
        startGate.countDown();
        endGate.await();
        Assert.assertEquals((((IndexStatsMoreThreadsTest.THREADS_NUMBER) * (IndexStatsMoreThreadsTest.THREADS_ITERATION)) * 3), stats().getQueryCount());
        Assert.assertEquals((((IndexStatsMoreThreadsTest.THREADS_NUMBER) * (IndexStatsMoreThreadsTest.THREADS_ITERATION)) * 2), stats().getIndexedQueryCount());
        Assert.assertEquals((((IndexStatsMoreThreadsTest.THREADS_NUMBER) * (IndexStatsMoreThreadsTest.THREADS_ITERATION)) * 2), valueStats().getQueryCount());
        Assert.assertEquals((((IndexStatsMoreThreadsTest.THREADS_NUMBER) * (IndexStatsMoreThreadsTest.THREADS_ITERATION)) * 2), valueStats().getHitCount());
        Assert.assertEquals(((expectedEqual + expectedGreaterEqual) / 2), valueStats().getAverageHitSelectivity(), 0.015);
    }

    @Test
    public void testIndexMapStatsWithMoreThreads() throws InterruptedException {
        map.addIndex("this", false);
        Assert.assertEquals(0, valueStats().getInsertCount());
        Assert.assertEquals(0, valueStats().getUpdateCount());
        Assert.assertEquals(0, valueStats().getRemoveCount());
        final CountDownLatch startGate = new CountDownLatch(1);
        final CountDownLatch endGate = new CountDownLatch(IndexStatsMoreThreadsTest.THREADS_NUMBER);
        for (int i = 0; i < (IndexStatsMoreThreadsTest.THREADS_NUMBER); i++) {
            final int threadOrder = i;
            new Thread() {
                @Override
                public void run() {
                    try {
                        startGate.await();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    try {
                        for (int j = 0; j < (IndexStatsMoreThreadsTest.THREADS_ITERATION); j++) {
                            map.put(((threadOrder * (IndexStatsMoreThreadsTest.THREADS_ITERATION)) + j), j);
                            map.put((((threadOrder + (IndexStatsMoreThreadsTest.THREADS_NUMBER)) * (IndexStatsMoreThreadsTest.THREADS_ITERATION)) + j), j);
                            map.remove(((threadOrder * (IndexStatsMoreThreadsTest.THREADS_ITERATION)) + j));
                            map.put((((threadOrder + (IndexStatsMoreThreadsTest.THREADS_NUMBER)) * (IndexStatsMoreThreadsTest.THREADS_ITERATION)) + j), (j * j));
                        }
                    } finally {
                        endGate.countDown();
                    }
                }
            }.start();
        }
        startGate.countDown();
        endGate.await();
        Assert.assertEquals((((IndexStatsMoreThreadsTest.THREADS_NUMBER) * (IndexStatsMoreThreadsTest.THREADS_ITERATION)) * 2), valueStats().getInsertCount());
        Assert.assertEquals(((IndexStatsMoreThreadsTest.THREADS_NUMBER) * (IndexStatsMoreThreadsTest.THREADS_ITERATION)), valueStats().getUpdateCount());
        Assert.assertEquals(((IndexStatsMoreThreadsTest.THREADS_NUMBER) * (IndexStatsMoreThreadsTest.THREADS_ITERATION)), valueStats().getRemoveCount());
    }
}

