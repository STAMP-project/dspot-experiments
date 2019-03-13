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
package com.hazelcast.map.impl.nearcache;


import com.hazelcast.core.IMap;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.RandomPicker;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapNearCacheStalenessTest extends HazelcastTestSupport {
    private static final int ENTRY_COUNT = 10;

    private static final int NEAR_CACHE_INVALIDATOR_THREAD_COUNT = 3;

    private static final int NEAR_CACHE_PUTTER_THREAD_COUNT = 10;

    private static final int NEAR_CACHE_REMOVER_THREAD_COUNT = 3;

    private final AtomicBoolean stop = new AtomicBoolean(false);

    private IMap<Integer, Integer> map1;

    private IMap<Integer, Integer> map2;

    @Test
    public void testNearCache_notContainsStaleValue_whenUpdatedByMultipleThreads() {
        List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < (MapNearCacheStalenessTest.NEAR_CACHE_INVALIDATOR_THREAD_COUNT); i++) {
            Thread putter = new MapNearCacheStalenessTest.NearCacheInvalidator(stop, map1, MapNearCacheStalenessTest.ENTRY_COUNT);
            threads.add(putter);
        }
        for (int i = 0; i < (MapNearCacheStalenessTest.NEAR_CACHE_PUTTER_THREAD_COUNT); i++) {
            Thread getter = new MapNearCacheStalenessTest.NearCachePutter(stop, map2, MapNearCacheStalenessTest.ENTRY_COUNT);
            threads.add(getter);
        }
        for (int i = 0; i < (MapNearCacheStalenessTest.NEAR_CACHE_REMOVER_THREAD_COUNT); i++) {
            Thread remover = new MapNearCacheStalenessTest.NearCacheRemover(stop, map2, MapNearCacheStalenessTest.ENTRY_COUNT);
            threads.add(remover);
        }
        for (Thread thread : threads) {
            thread.start();
        }
        HazelcastTestSupport.sleepSeconds(5);
        stop.set(true);
        for (Thread thread : threads) {
            HazelcastTestSupport.assertJoinable(thread);
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                MapNearCacheStalenessTest.assertNoStaleDataExistInNearCache(map1);
                MapNearCacheStalenessTest.assertNoStaleDataExistInNearCache(map2);
            }
        });
    }

    private abstract static class NearCacheThread extends Thread {
        final AtomicBoolean stop;

        final IMap<Integer, Integer> map;

        final int entryCount;

        NearCacheThread(AtomicBoolean stop, IMap<Integer, Integer> map, int entryCount) {
            this.stop = stop;
            this.map = map;
            this.entryCount = entryCount;
        }
    }

    public static class NearCacheInvalidator extends MapNearCacheStalenessTest.NearCacheThread {
        public NearCacheInvalidator(AtomicBoolean stop, IMap<Integer, Integer> map, int entryCount) {
            super(stop, map, entryCount);
        }

        @Override
        public void run() {
            while (!(stop.get())) {
                for (int i = 0; i < (MapNearCacheStalenessTest.ENTRY_COUNT); i++) {
                    map.put(i, RandomPicker.getInt(MapNearCacheStalenessTest.ENTRY_COUNT));
                }
            } 
        }
    }

    public static class NearCachePutter extends MapNearCacheStalenessTest.NearCacheThread {
        public NearCachePutter(AtomicBoolean stop, IMap<Integer, Integer> map, int entryCount) {
            super(stop, map, entryCount);
        }

        @Override
        public void run() {
            while (!(stop.get())) {
                for (int i = 0; i < (MapNearCacheStalenessTest.ENTRY_COUNT); i++) {
                    map.get(i);
                }
            } 
        }
    }

    public static class NearCacheRemover extends MapNearCacheStalenessTest.NearCacheThread {
        public NearCacheRemover(AtomicBoolean stop, IMap<Integer, Integer> map, int entryCount) {
            super(stop, map, entryCount);
        }

        @Override
        public void run() {
            while (!(stop.get())) {
                for (int i = 0; i < (MapNearCacheStalenessTest.ENTRY_COUNT); i++) {
                    map.remove(i);
                }
            } 
        }
    }
}

