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


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.monitor.LocalReplicatedMapStats;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.Clock;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ReplicatedMapStatsTest extends HazelcastTestSupport {
    private static final int OPERATION_COUNT = 10;

    private static final int DEFAULT_PARTITION_COUNT = Integer.valueOf(GroupProperty.PARTITION_COUNT.getDefaultValue());

    private HazelcastInstance instance;

    private String replicatedMapName = "replicatedMap";

    @Test
    public void testGetOperationCount() {
        ReplicatedMap<Integer, Integer> replicatedMap = getReplicatedMap();
        replicatedMap.put(1, 1);
        int count = ReplicatedMapStatsTest.OPERATION_COUNT;
        for (int i = 0; i < count; i++) {
            replicatedMap.get(1);
        }
        LocalReplicatedMapStats stats = getReplicatedMapStats();
        Assert.assertEquals(count, stats.getGetOperationCount());
    }

    @Test
    public void testPutOperationCount() {
        ReplicatedMap<Integer, Integer> replicatedMap = getReplicatedMap();
        int count = ReplicatedMapStatsTest.OPERATION_COUNT;
        for (int i = 0; i < count; i++) {
            replicatedMap.put(i, i);
        }
        LocalReplicatedMapStats stats = getReplicatedMapStats();
        Assert.assertEquals(count, stats.getPutOperationCount());
    }

    @Test
    public void testRemoveOperationCount() {
        ReplicatedMap<Integer, Integer> replicatedMap = getReplicatedMap();
        int count = ReplicatedMapStatsTest.OPERATION_COUNT;
        for (int i = 0; i < count; i++) {
            replicatedMap.put(i, i);
            replicatedMap.remove(i);
        }
        LocalReplicatedMapStats stats = getReplicatedMapStats();
        Assert.assertEquals(count, stats.getRemoveOperationCount());
    }

    @Test
    public void testHitsGenerated() {
        ReplicatedMap<Integer, Integer> replicatedMap = getReplicatedMap();
        for (int i = 0; i < (ReplicatedMapStatsTest.OPERATION_COUNT); i++) {
            replicatedMap.put(i, i);
            replicatedMap.get(i);
        }
        LocalReplicatedMapStats stats = getReplicatedMapStats();
        Assert.assertEquals(ReplicatedMapStatsTest.OPERATION_COUNT, stats.getHits());
    }

    @Test
    public void testPutAndHitsGenerated() {
        ReplicatedMap<Integer, Integer> replicatedMap = getReplicatedMap();
        for (int i = 0; i < (ReplicatedMapStatsTest.OPERATION_COUNT); i++) {
            replicatedMap.put(i, i);
            replicatedMap.get(i);
        }
        LocalReplicatedMapStats stats = getReplicatedMapStats();
        Assert.assertEquals(ReplicatedMapStatsTest.OPERATION_COUNT, stats.getHits());
    }

    @Test
    public void testGetAndHitsGenerated() {
        ReplicatedMap<Integer, Integer> replicatedMap = getReplicatedMap();
        for (int i = 0; i < (ReplicatedMapStatsTest.OPERATION_COUNT); i++) {
            replicatedMap.put(i, i);
            replicatedMap.get(i);
        }
        LocalReplicatedMapStats stats = getReplicatedMapStats();
        Assert.assertEquals(ReplicatedMapStatsTest.OPERATION_COUNT, stats.getHits());
    }

    @Test
    public void testHitsGenerated_updatedConcurrently() {
        final ReplicatedMap<Integer, Integer> replicatedMap = getReplicatedMap();
        final int actionCount = ReplicatedMapStatsTest.OPERATION_COUNT;
        for (int i = 0; i < actionCount; i++) {
            replicatedMap.put(i, i);
            replicatedMap.get(i);
        }
        final LocalReplicatedMapStats stats = getReplicatedMapStats();
        final long initialHits = stats.getHits();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < actionCount; i++) {
                    replicatedMap.get(i);
                }
                getReplicatedMapStats();// causes the local stats object to update

            }
        }).start();
        Assert.assertEquals(actionCount, initialHits);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals((actionCount * 2), stats.getHits());
            }
        });
    }

    @Test
    public void testLastAccessTime() {
        final long startTime = Clock.currentTimeMillis();
        ReplicatedMap<String, String> replicatedMap = getReplicatedMap();
        String key = "key";
        replicatedMap.put(key, "value");
        replicatedMap.get(key);
        long lastAccessTime = getReplicatedMapStats().getLastAccessTime();
        Assert.assertTrue((lastAccessTime >= startTime));
    }

    @Test
    public void testLastAccessTime_updatedConcurrently() {
        final long startTime = Clock.currentTimeMillis();
        final ReplicatedMap<String, String> map = getReplicatedMap();
        final String key = "key";
        map.put(key, "value");
        map.get(key);
        final LocalReplicatedMapStats stats = getReplicatedMapStats();
        final long lastAccessTime = stats.getLastAccessTime();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HazelcastTestSupport.sleepAtLeastMillis(1);
                map.get(key);
                map.getReplicatedMapStats();// causes the local stats object to update

            }
        }).start();
        Assert.assertTrue((lastAccessTime >= startTime));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertTrue(((stats.getLastAccessTime()) >= lastAccessTime));
            }
        });
    }

    @Test
    public void testLastUpdateTime() {
        final long startTime = Clock.currentTimeMillis();
        ReplicatedMap<String, String> replicatedMap = getReplicatedMap();
        String key = "key";
        replicatedMap.put(key, "value");
        long lastUpdateTime = getReplicatedMapStats().getLastUpdateTime();
        Assert.assertTrue((lastUpdateTime >= startTime));
        HazelcastTestSupport.sleepAtLeastMillis(5);
        replicatedMap.put(key, "value2");
        long lastUpdateTime2 = getReplicatedMapStats().getLastUpdateTime();
        Assert.assertTrue((lastUpdateTime2 >= lastUpdateTime));
    }

    @Test
    public void testLastUpdateTime_updatedConcurrently() {
        final long startTime = Clock.currentTimeMillis();
        final ReplicatedMap<String, String> map = getReplicatedMap();
        final String key = "key";
        map.put(key, "value");
        final LocalReplicatedMapStats stats = getReplicatedMapStats();
        final long lastUpdateTime = stats.getLastUpdateTime();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HazelcastTestSupport.sleepAtLeastMillis(1);
                map.put(key, "value2");
                getReplicatedMapStats();// causes the local stats object to update

            }
        }).start();
        Assert.assertTrue((lastUpdateTime >= startTime));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertTrue(((stats.getLastUpdateTime()) >= lastUpdateTime));
            }
        });
    }

    @Test
    public void testPutOperationCount_afterPutAll() {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int i = 1; i <= (ReplicatedMapStatsTest.OPERATION_COUNT); i++) {
            map.put(i, i);
        }
        ReplicatedMap<Integer, Integer> replicatedMap = getReplicatedMap();
        replicatedMap.putAll(map);
        final LocalReplicatedMapStats stats = getReplicatedMapStats();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(ReplicatedMapStatsTest.OPERATION_COUNT, stats.getPutOperationCount());
            }
        });
    }

    @Test
    public void testOtherOperationCount_containsKey() {
        ReplicatedMap<Integer, Integer> map = getReplicatedMap();
        for (int i = 0; i < (ReplicatedMapStatsTest.OPERATION_COUNT); i++) {
            map.containsKey(i);
        }
        LocalReplicatedMapStats stats = getReplicatedMapStats();
        Assert.assertEquals(ReplicatedMapStatsTest.OPERATION_COUNT, stats.getOtherOperationCount());
    }

    @Test
    public void testOtherOperationCount_entrySet() {
        ReplicatedMap<Integer, Integer> map = getReplicatedMap();
        for (int i = 0; i < (ReplicatedMapStatsTest.OPERATION_COUNT); i++) {
            map.entrySet();
        }
        LocalReplicatedMapStats stats = getReplicatedMapStats();
        Assert.assertEquals(((ReplicatedMapStatsTest.OPERATION_COUNT) * (ReplicatedMapStatsTest.DEFAULT_PARTITION_COUNT)), stats.getOtherOperationCount());
    }

    @Test
    public void testOtherOperationCount_keySet() {
        ReplicatedMap<Integer, Integer> map = getReplicatedMap();
        for (int i = 0; i < (ReplicatedMapStatsTest.OPERATION_COUNT); i++) {
            map.keySet();
        }
        LocalReplicatedMapStats stats = getReplicatedMapStats();
        Assert.assertEquals(((ReplicatedMapStatsTest.OPERATION_COUNT) * (ReplicatedMapStatsTest.DEFAULT_PARTITION_COUNT)), stats.getOtherOperationCount());
    }

    @Test
    public void testOtherOperationCount_values() {
        ReplicatedMap<Integer, Integer> map = getReplicatedMap();
        for (int i = 0; i < (ReplicatedMapStatsTest.OPERATION_COUNT); i++) {
            map.values();
        }
        LocalReplicatedMapStats stats = getReplicatedMapStats();
        Assert.assertEquals(((ReplicatedMapStatsTest.OPERATION_COUNT) * (ReplicatedMapStatsTest.DEFAULT_PARTITION_COUNT)), stats.getOtherOperationCount());
    }

    @Test
    public void testOtherOperationCount_clear() {
        ReplicatedMap<Integer, Integer> map = getReplicatedMap();
        for (int i = 0; i < (ReplicatedMapStatsTest.OPERATION_COUNT); i++) {
            map.clear();
        }
        LocalReplicatedMapStats stats = getReplicatedMapStats();
        Assert.assertEquals(((ReplicatedMapStatsTest.OPERATION_COUNT) * (ReplicatedMapStatsTest.DEFAULT_PARTITION_COUNT)), stats.getOtherOperationCount());
    }

    @Test
    public void testOtherOperationCount_containsValue() {
        ReplicatedMap<Integer, Integer> map = getReplicatedMap();
        for (int i = 0; i < (ReplicatedMapStatsTest.OPERATION_COUNT); i++) {
            map.containsValue(1);
        }
        LocalReplicatedMapStats stats = getReplicatedMapStats();
        Assert.assertEquals(((ReplicatedMapStatsTest.OPERATION_COUNT) * (ReplicatedMapStatsTest.DEFAULT_PARTITION_COUNT)), stats.getOtherOperationCount());
    }

    @Test
    public void testOtherOperationCount_isEmpty() {
        ReplicatedMap<Integer, Integer> map = getReplicatedMap();
        for (int i = 0; i < (ReplicatedMapStatsTest.OPERATION_COUNT); i++) {
            map.isEmpty();
        }
        LocalReplicatedMapStats stats = getReplicatedMapStats();
        Assert.assertEquals(((ReplicatedMapStatsTest.OPERATION_COUNT) * (ReplicatedMapStatsTest.DEFAULT_PARTITION_COUNT)), stats.getOtherOperationCount());
    }

    @Test
    public void testOtherOperationCount_size() {
        ReplicatedMap<Integer, Integer> map = getReplicatedMap();
        for (int i = 0; i < (ReplicatedMapStatsTest.OPERATION_COUNT); i++) {
            map.size();
        }
        LocalReplicatedMapStats stats = getReplicatedMapStats();
        Assert.assertEquals(((ReplicatedMapStatsTest.OPERATION_COUNT) * (ReplicatedMapStatsTest.DEFAULT_PARTITION_COUNT)), stats.getOtherOperationCount());
    }
}

