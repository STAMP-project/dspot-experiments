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
package com.hazelcast.map.impl.mapstore.writebehind;


import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MapStoreAdapter;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.NightlyTest;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category(NightlyTest.class)
public class WriteBehindWriteDelaySecondsTest extends HazelcastTestSupport {
    @Test
    public void testUpdatesInWriteDelayWindowDone_withStoreAllMethod() throws Exception {
        final WriteBehindWriteDelaySecondsTest.TestMapStore store = new WriteBehindWriteDelaySecondsTest.TestMapStore();
        Config config = newMapStoredConfig(store, 20);
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<Integer, Integer> map = instance.getMap("default");
        int numberOfPuts = 2;
        HazelcastTestSupport.sleepSeconds(2);
        for (int i = 0; i < numberOfPuts; i++) {
            map.put(i, i);
            HazelcastTestSupport.sleepSeconds(4);
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(store.toString(), 1, store.getStoreAllMethodCallCount());
            }
        });
    }

    private static class TestMapStore extends MapStoreAdapter {
        private final AtomicInteger storeMethodCallCount = new AtomicInteger();

        private final AtomicInteger storeAllMethodCallCount = new AtomicInteger();

        @Override
        public void store(Object key, Object value) {
            storeMethodCallCount.incrementAndGet();
        }

        @Override
        public void storeAll(Map map) {
            storeAllMethodCallCount.incrementAndGet();
        }

        int getStoreAllMethodCallCount() {
            return storeAllMethodCallCount.get();
        }

        int getStoreMethodCallCount() {
            return storeMethodCallCount.get();
        }

        @Override
        public String toString() {
            return (((("TestMapStore{" + "storeMethodCallCount=") + (getStoreMethodCallCount())) + ", storeAllMethodCallCount=") + (getStoreAllMethodCallCount())) + '}';
        }
    }

    /**
     * Updates on same key should not shift store time.
     */
    @Test
    public void continuouslyUpdatedKey_shouldBeStored_inEveryWriteDelayTimeWindow() throws Exception {
        final MapStoreWithCounter<Integer, Integer> mapStore = new MapStoreWithCounter<Integer, Integer>();
        final IMap<Integer, Integer> map = TestMapUsingMapStoreBuilder.<Integer, Integer>create().withMapStore(mapStore).withNodeCount(1).withNodeFactory(createHazelcastInstanceFactory(1)).withWriteDelaySeconds(6).withPartitionCount(1).build();
        for (int i = 1; i <= 60; i++) {
            map.put(1, i);
            HazelcastTestSupport.sleepMillis(500);
        }
        // min expected 2 --> Expect to observe at least 2 store operations in every write-delay-seconds window.
        // Current write-delay-seconds is 6 seconds. We don't want to see 1 store operation in the course of this 60 * 500 millis test period.
        // It should be bigger than 1.
        assertMinMaxStoreOperationsCount(2, mapStore);
        mapStore.countStore.get();
    }
}

