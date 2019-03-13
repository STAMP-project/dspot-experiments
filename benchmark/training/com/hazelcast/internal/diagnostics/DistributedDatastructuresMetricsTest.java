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
package com.hazelcast.internal.diagnostics;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.internal.metrics.renderers.ProbeRenderer;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DistributedDatastructuresMetricsTest extends HazelcastTestSupport {
    private static final int EVENT_COUNTER = 1000;

    private static final String MAP_NAME = "myMap";

    private static final String EXECUTOR_NAME = "myExecutor";

    private static final String QUEUE_NAME = "myQueue";

    private static final String REPLICATED_MAP_NAME = "myReplicatedMap";

    private static final String TOPIC_NAME = "myTopic";

    private static final String NEAR_CACHE_MAP_NAME = "nearCacheMap";

    private HazelcastInstance hz;

    @Test
    public void testMap() {
        final IMap<Integer, Integer> map = hz.getMap(DistributedDatastructuresMetricsTest.MAP_NAME);
        Random random = new Random();
        for (int i = 0; i < (DistributedDatastructuresMetricsTest.EVENT_COUNTER); i++) {
            int key = random.nextInt(Integer.MAX_VALUE);
            map.putAsync(key, 23);
            map.removeAsync(key);
        }
        assertHasStatsEventually((("map[" + (DistributedDatastructuresMetricsTest.MAP_NAME)) + "]"));
    }

    @Test
    public void testExecutor() throws InterruptedException {
        final IExecutorService executor = hz.getExecutorService(DistributedDatastructuresMetricsTest.EXECUTOR_NAME);
        final CountDownLatch latch = new CountDownLatch(DistributedDatastructuresMetricsTest.EVENT_COUNTER);
        for (int i = 0; i < (DistributedDatastructuresMetricsTest.EVENT_COUNTER); i++) {
            executor.submit(new DistributedDatastructuresMetricsTest.EmptyRunnable(), new com.hazelcast.core.ExecutionCallback<Object>() {
                @Override
                public void onResponse(Object response) {
                    latch.countDown();
                }

                @Override
                public void onFailure(Throwable t) {
                }
            });
        }
        latch.await();
        assertHasStatsEventually((("executor[" + (DistributedDatastructuresMetricsTest.EXECUTOR_NAME)) + "]"));
    }

    @Test
    public void testQueue() {
        final IQueue<Object> q = hz.getQueue(DistributedDatastructuresMetricsTest.QUEUE_NAME);
        for (int i = 0; i < (DistributedDatastructuresMetricsTest.EVENT_COUNTER); i++) {
            q.offer(i);
        }
        q.poll();
        assertHasStatsEventually((("queue[" + (DistributedDatastructuresMetricsTest.QUEUE_NAME)) + "]"));
    }

    @Test
    public void testReplicatedMap() {
        final ReplicatedMap<Object, Object> replicatedMap = hz.getReplicatedMap(DistributedDatastructuresMetricsTest.REPLICATED_MAP_NAME);
        for (int i = 0; i < (DistributedDatastructuresMetricsTest.EVENT_COUNTER); i++) {
            replicatedMap.put(i, i);
        }
        replicatedMap.remove(0);
        assertHasStatsEventually((("replicatedMap[" + (DistributedDatastructuresMetricsTest.REPLICATED_MAP_NAME)) + "]"));
    }

    @Test
    public void testTopic() {
        final ITopic<Object> topic = hz.getTopic(DistributedDatastructuresMetricsTest.TOPIC_NAME);
        for (int i = 0; i < (DistributedDatastructuresMetricsTest.EVENT_COUNTER); i++) {
            topic.publish(i);
        }
        assertHasStatsEventually((("topic[" + (DistributedDatastructuresMetricsTest.TOPIC_NAME)) + "]"));
    }

    @Test
    public void testNearCache() {
        final IMap<Object, Object> map = hz.getMap(DistributedDatastructuresMetricsTest.NEAR_CACHE_MAP_NAME);
        for (int i = 0; i < (DistributedDatastructuresMetricsTest.EVENT_COUNTER); i++) {
            map.put(i, i);
            map.get(i);
        }
        assertHasStatsEventually((("map[" + (DistributedDatastructuresMetricsTest.NEAR_CACHE_MAP_NAME)) + "].nearcache"));
    }

    static class EmptyRunnable implements Serializable , Runnable {
        @Override
        public void run() {
        }
    }

    static class StringProbeRenderer implements ProbeRenderer {
        final HashMap<String, Object> probes = new HashMap<String, Object>();

        private final String prefix;

        StringProbeRenderer(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public void renderLong(String name, long value) {
            if (name.startsWith(prefix)) {
                probes.put(name, value);
            }
        }

        @Override
        public void renderDouble(String name, double value) {
            if (name.startsWith(prefix)) {
                probes.put(name, value);
            }
        }

        @Override
        public void renderException(String name, Exception e) {
            if (name.startsWith(prefix)) {
                probes.put(name, e);
            }
        }

        @Override
        public void renderNoValue(String name) {
            if (name.startsWith(prefix)) {
                probes.put(name, null);
            }
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Object> probe : probes.entrySet()) {
                sb.append(probe.getKey()).append(" - ").append(probe.getValue()).append("\n");
            }
            return sb.toString();
        }
    }
}

