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
package com.hazelcast.replicatedmap;


import FalsePredicate.INSTANCE;
import com.hazelcast.config.Config;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.ReplicatedMapConfig;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MapEvent;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.query.impl.predicates.InstanceOfPredicate;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ReplicatedMapListenerTest extends HazelcastTestSupport {
    @Test
    public void testRegisterListenerViaConfiguration() {
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = new Config();
        ReplicatedMapConfig replicatedMapConfig = config.getReplicatedMapConfig(mapName);
        EntryListenerConfig listenerConfig = new EntryListenerConfig();
        final ReplicatedMapListenerTest.EventCountingListener listener = new ReplicatedMapListenerTest.EventCountingListener();
        listenerConfig.setImplementation(listener);
        replicatedMapConfig.addEntryListenerConfig(listenerConfig);
        HazelcastInstance instance = createHazelcastInstance(config);
        ReplicatedMap<Object, Object> replicatedMap = instance.getReplicatedMap(mapName);
        replicatedMap.put(3, 3);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(1, listener.addCount.get());
                Assert.assertEquals(3, listener.keys.peek());
            }
        }, 10);
    }

    @Test
    public void testEntryAdded() {
        ReplicatedMap<Object, Object> replicatedMap = createClusterAndGetRandomReplicatedMap();
        final ReplicatedMapListenerTest.EventCountingListener listener = new ReplicatedMapListenerTest.EventCountingListener();
        replicatedMap.addEntryListener(listener);
        replicatedMap.put(1, 1);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(1, listener.addCount.get());
            }
        });
    }

    @Test
    public void testEntryUpdated() {
        ReplicatedMap<Object, Object> replicatedMap = createClusterAndGetRandomReplicatedMap();
        final ReplicatedMapListenerTest.EventCountingListener listener = new ReplicatedMapListenerTest.EventCountingListener();
        replicatedMap.addEntryListener(listener);
        replicatedMap.put(1, 1);
        replicatedMap.put(1, 2);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(1, listener.updateCount.get());
            }
        });
    }

    @Test
    public void testEntryEvicted() {
        ReplicatedMap<Object, Object> replicatedMap = createClusterAndGetRandomReplicatedMap();
        final ReplicatedMapListenerTest.EventCountingListener listener = new ReplicatedMapListenerTest.EventCountingListener();
        replicatedMap.addEntryListener(listener);
        replicatedMap.put(1, 1, 1, TimeUnit.SECONDS);
        HazelcastTestSupport.sleepAtLeastSeconds(2);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(1, listener.evictCount.get());
            }
        });
    }

    @Test
    public void testEntryRemoved() {
        ReplicatedMap<Object, Object> replicatedMap = createClusterAndGetRandomReplicatedMap();
        final ReplicatedMapListenerTest.EventCountingListener listener = new ReplicatedMapListenerTest.EventCountingListener();
        replicatedMap.addEntryListener(listener);
        replicatedMap.put(1, 1);
        replicatedMap.remove(1);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(1, listener.removeCount.get());
            }
        });
    }

    @Test
    public void testMapClear() {
        ReplicatedMap<Object, Object> replicatedMap = createClusterAndGetRandomReplicatedMap();
        final ReplicatedMapListenerTest.EventCountingListener listener = new ReplicatedMapListenerTest.EventCountingListener();
        replicatedMap.addEntryListener(listener);
        replicatedMap.put(1, 1);
        replicatedMap.clear();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(1, listener.mapClearCount.get());
            }
        });
    }

    @Test
    public void testListenToKeyForEntryAdded() {
        ReplicatedMap<Object, Object> replicatedMap = createClusterAndGetRandomReplicatedMap();
        final ReplicatedMapListenerTest.EventCountingListener listener = new ReplicatedMapListenerTest.EventCountingListener();
        replicatedMap.addEntryListener(listener, 1);
        replicatedMap.put(1, 1);
        replicatedMap.put(2, 2);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(1, listener.keys.size());
                Assert.assertEquals(1, listener.keys.peek());
                Assert.assertEquals(1, listener.addCount.get());
            }
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testListenWithPredicate() {
        ReplicatedMap<Object, Object> replicatedMap = createClusterAndGetRandomReplicatedMap();
        final ReplicatedMapListenerTest.EventCountingListener listener = new ReplicatedMapListenerTest.EventCountingListener();
        replicatedMap.addEntryListener(listener, INSTANCE);
        replicatedMap.put(2, 2);
        HazelcastTestSupport.assertTrueFiveSeconds(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(0, listener.addCount.get());
            }
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testListenToKeyWithPredicate() {
        ReplicatedMap<Object, Object> replicatedMap = createClusterAndGetRandomReplicatedMap();
        final ReplicatedMapListenerTest.EventCountingListener listener = new ReplicatedMapListenerTest.EventCountingListener();
        replicatedMap.addEntryListener(listener, new InstanceOfPredicate(Integer.class), 2);
        replicatedMap.put(1, 1);
        replicatedMap.put(2, 2);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(1, listener.keys.size());
                Assert.assertEquals(2, listener.keys.peek());
                Assert.assertEquals(1, listener.addCount.get());
            }
        });
    }

    public class EventCountingListener implements EntryListener<Object, Object> {
        private final ConcurrentLinkedQueue<Object> keys = new ConcurrentLinkedQueue<Object>();

        private final AtomicLong addCount = new AtomicLong();

        private final AtomicLong removeCount = new AtomicLong();

        private final AtomicLong updateCount = new AtomicLong();

        private final AtomicLong evictCount = new AtomicLong();

        private final AtomicLong mapClearCount = new AtomicLong();

        private final AtomicLong mapEvictCount = new AtomicLong();

        public EventCountingListener() {
        }

        @Override
        public void entryAdded(EntryEvent<Object, Object> event) {
            keys.add(event.getKey());
            addCount.incrementAndGet();
        }

        @Override
        public void entryRemoved(EntryEvent<Object, Object> event) {
            keys.add(event.getKey());
            removeCount.incrementAndGet();
        }

        @Override
        public void entryUpdated(EntryEvent<Object, Object> event) {
            keys.add(event.getKey());
            updateCount.incrementAndGet();
        }

        @Override
        public void entryEvicted(EntryEvent<Object, Object> event) {
            keys.add(event.getKey());
            evictCount.incrementAndGet();
        }

        @Override
        public void mapEvicted(MapEvent event) {
            mapEvictCount.incrementAndGet();
        }

        @Override
        public void mapCleared(MapEvent event) {
            mapClearCount.incrementAndGet();
        }

        @Override
        public String toString() {
            return (((((((((((("EventCountingListener{" + "addCount=") + (addCount)) + ", removeCount=") + (removeCount)) + ", updateCount=") + (updateCount)) + ", evictCount=") + (evictCount)) + ", mapClearCount=") + (mapClearCount)) + ", mapEvictCount=") + (mapEvictCount)) + '}';
        }
    }
}

