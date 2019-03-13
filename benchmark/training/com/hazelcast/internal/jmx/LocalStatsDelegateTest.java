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
package com.hazelcast.internal.jmx;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.internal.jmx.suppliers.StatsSupplier;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.NightlyTest;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.UuidUtil;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LocalStatsDelegateTest extends HazelcastTestSupport {
    private AtomicBoolean done;

    private HazelcastInstance hz;

    @Test
    public void testMapStats_Created_OnlyOnce_InGivenInternal() throws InterruptedException {
        final IMap<Object, Object> trial = hz.getMap("trial");
        final StatsSupplier<LocalMapStats> statsSupplier = new com.hazelcast.internal.jmx.suppliers.LocalMapStatsSupplier(trial);
        final LocalStatsDelegate localStatsDelegate = new LocalStatsDelegate<LocalMapStats>(statsSupplier, 60);
        Thread mapStatsThread1 = new Thread(new LocalStatsDelegateTest.MapStatsThread(localStatsDelegate, done, 100));
        Thread mapStatsThread2 = new Thread(new LocalStatsDelegateTest.MapStatsThread(localStatsDelegate, done, 90));
        mapStatsThread1.start();
        mapStatsThread2.start();
        Thread mapPutThread = new Thread(new LocalStatsDelegateTest.MapPutThread(trial, done));
        mapPutThread.start();
        HazelcastTestSupport.sleepSeconds(30);
        done.set(true);
        mapStatsThread1.join();
        mapStatsThread2.join();
        mapPutThread.join();
    }

    /**
     * In this test, map entry count is incrementing steadily. Thus, we expect OwnedEntryCount
     * to increase as well. Since the interval value for generating new map stats is 5 seconds,
     * we assert for greater or equal.
     */
    @Test
    @Category(NightlyTest.class)
    public void stressTest() throws InterruptedException {
        final IMap<Object, Object> trial = hz.getMap("trial");
        final StatsSupplier<LocalMapStats> statsSupplier = new com.hazelcast.internal.jmx.suppliers.LocalMapStatsSupplier(trial);
        final LocalStatsDelegate localStatsDelegate = new LocalStatsDelegate<LocalMapStats>(statsSupplier, 5);
        Thread mapStatsThread1 = new Thread(new LocalStatsDelegateTest.MapStatsThread(localStatsDelegate, done, true, 100));
        Thread mapStatsThread2 = new Thread(new LocalStatsDelegateTest.MapStatsThread(localStatsDelegate, done, true, 80));
        mapStatsThread1.start();
        mapStatsThread2.start();
        Thread mapPutThread = new Thread(new LocalStatsDelegateTest.MapPutThread(trial, done));
        mapPutThread.start();
        HazelcastTestSupport.sleepSeconds(60);
        done.set(true);
        mapStatsThread1.join();
        mapStatsThread2.join();
        mapPutThread.join();
    }

    private class MapPutThread implements Runnable {
        private IMap map;

        private AtomicBoolean done;

        public MapPutThread(IMap map, AtomicBoolean done) {
            this.map = map;
            this.done = done;
        }

        @Override
        public void run() {
            while (!(done.get())) {
                String randomString = UuidUtil.newUnsecureUuidString();
                map.put(randomString, randomString);
                HazelcastTestSupport.sleepMillis(10);
            } 
        }
    }

    private class MapStatsThread implements Runnable {
        private LocalStatsDelegate localStatsDelegate;

        private AtomicBoolean done;

        private boolean stress;

        private int sleepMs;

        public MapStatsThread(LocalStatsDelegate localMapStatsDelegate, AtomicBoolean done, int sleepMs) {
            this.localStatsDelegate = localMapStatsDelegate;
            this.done = done;
            this.sleepMs = sleepMs;
        }

        public MapStatsThread(LocalStatsDelegate localMapStatsDelegate, AtomicBoolean done, boolean stress, int sleepMs) {
            this.localStatsDelegate = localMapStatsDelegate;
            this.done = done;
            this.stress = stress;
            this.sleepMs = sleepMs;
        }

        @Override
        public void run() {
            LocalMapStats localMapStats = ((LocalMapStats) (localStatsDelegate.getLocalStats()));
            long previous = localMapStats.getOwnedEntryCount();
            long current = previous;
            while (!(done.get())) {
                if (stress) {
                    assertGreaterThanOrEqualTo(current, previous);
                } else {
                    Assert.assertEquals(current, previous);
                }
                previous = current;
                localMapStats = ((LocalMapStats) (localStatsDelegate.getLocalStats()));
                current = localMapStats.getOwnedEntryCount();
                try {
                    TimeUnit.MILLISECONDS.sleep(sleepMs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } 
        }
    }
}

