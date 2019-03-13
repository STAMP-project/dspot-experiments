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
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.NightlyTest;
import com.hazelcast.util.RandomPicker;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(NightlyTest.class)
public class MapNearCacheRecordStateStressTest extends HazelcastTestSupport {
    private static final int KEY_SPACE = 100;

    private static final int TEST_RUN_SECONDS = 60;

    private static final int GET_ALL_THREAD_COUNT = 3;

    private static final int GET_THREAD_COUNT = 7;

    private static final int PUT_LOCAL_THREAD_COUNT = 2;

    private static final int PUT_THREAD_COUNT = 2;

    private static final int CLEAR_THREAD_COUNT = 2;

    private static final int REMOVE_THREAD_COUNT = 2;

    private final TestHazelcastInstanceFactory factory = new TestHazelcastInstanceFactory();

    private final AtomicBoolean stop = new AtomicBoolean();

    private IMap<Integer, Integer> memberMap;

    private IMap<Integer, Integer> nearCachedMap;

    @Test
    public void allRecordsAreInReadableStateInTheEnd() throws Exception {
        // initial population of IMap from member
        for (int i = 0; i < (MapNearCacheRecordStateStressTest.KEY_SPACE); i++) {
            memberMap.put(i, i);
        }
        List<Thread> threads = new ArrayList<Thread>();
        // member
        for (int i = 0; i < (MapNearCacheRecordStateStressTest.PUT_THREAD_COUNT); i++) {
            MapNearCacheRecordStateStressTest.Put put = new MapNearCacheRecordStateStressTest.Put(memberMap);
            threads.add(put);
        }
        // member with Near Cache
        for (int i = 0; i < (MapNearCacheRecordStateStressTest.PUT_LOCAL_THREAD_COUNT); i++) {
            MapNearCacheRecordStateStressTest.Put put = new MapNearCacheRecordStateStressTest.Put(nearCachedMap);
            threads.add(put);
        }
        for (int i = 0; i < (MapNearCacheRecordStateStressTest.GET_ALL_THREAD_COUNT); i++) {
            MapNearCacheRecordStateStressTest.GetAll getAll = new MapNearCacheRecordStateStressTest.GetAll(nearCachedMap);
            threads.add(getAll);
        }
        for (int i = 0; i < (MapNearCacheRecordStateStressTest.GET_THREAD_COUNT); i++) {
            MapNearCacheRecordStateStressTest.Get get = new MapNearCacheRecordStateStressTest.Get(nearCachedMap);
            threads.add(get);
        }
        for (int i = 0; i < (MapNearCacheRecordStateStressTest.REMOVE_THREAD_COUNT); i++) {
            MapNearCacheRecordStateStressTest.Remove remove = new MapNearCacheRecordStateStressTest.Remove(nearCachedMap);
            threads.add(remove);
        }
        for (int i = 0; i < (MapNearCacheRecordStateStressTest.CLEAR_THREAD_COUNT); i++) {
            MapNearCacheRecordStateStressTest.Clear clear = new MapNearCacheRecordStateStressTest.Clear(nearCachedMap);
            threads.add(clear);
        }
        // start threads
        for (Thread thread : threads) {
            thread.start();
        }
        // stress for a while
        HazelcastTestSupport.sleepSeconds(MapNearCacheRecordStateStressTest.TEST_RUN_SECONDS);
        // stop threads
        stop.set(true);
        for (Thread thread : threads) {
            thread.join();
        }
        MapNearCacheRecordStateStressTest.assertFinalRecordStateIsReadPermitted(nearCachedMap);
    }

    private class Put extends Thread {
        private final IMap<Integer, Integer> map;

        private Put(IMap<Integer, Integer> map) {
            this.map = map;
        }

        @Override
        public void run() {
            do {
                for (int i = 0; i < (MapNearCacheRecordStateStressTest.KEY_SPACE); i++) {
                    map.put(i, RandomPicker.getInt(MapNearCacheRecordStateStressTest.KEY_SPACE));
                }
                HazelcastTestSupport.sleepAtLeastMillis(100);
            } while (!(stop.get()) );
        }
    }

    private class Remove extends Thread {
        private final IMap<Integer, Integer> map;

        private Remove(IMap<Integer, Integer> map) {
            this.map = map;
        }

        @Override
        public void run() {
            do {
                for (int i = 0; i < (MapNearCacheRecordStateStressTest.KEY_SPACE); i++) {
                    map.remove(i);
                }
                HazelcastTestSupport.sleepAtLeastMillis(100);
            } while (!(stop.get()) );
        }
    }

    private class Clear extends Thread {
        private final IMap<Integer, Integer> map;

        private Clear(IMap<Integer, Integer> map) {
            this.map = map;
        }

        @Override
        public void run() {
            do {
                map.clear();
                HazelcastTestSupport.sleepAtLeastMillis(5000);
            } while (!(stop.get()) );
        }
    }

    private class GetAll extends Thread {
        private final IMap<Integer, Integer> map;

        private GetAll(IMap<Integer, Integer> map) {
            this.map = map;
        }

        @Override
        public void run() {
            HashSet<Integer> keys = new HashSet<Integer>();
            for (int i = 0; i < (MapNearCacheRecordStateStressTest.KEY_SPACE); i++) {
                keys.add(i);
            }
            do {
                map.getAll(keys);
                HazelcastTestSupport.sleepAtLeastMillis(2);
            } while (!(stop.get()) );
        }
    }

    private class Get extends Thread {
        private final IMap<Integer, Integer> map;

        private Get(IMap<Integer, Integer> map) {
            this.map = map;
        }

        @Override
        public void run() {
            do {
                for (int i = 0; i < (MapNearCacheRecordStateStressTest.KEY_SPACE); i++) {
                    map.get(i);
                }
            } while (!(stop.get()) );
        }
    }
}

