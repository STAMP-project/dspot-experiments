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
package com.hazelcast.internal.eviction;


import com.hazelcast.config.Config;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.instance.LifecycleServiceImpl;
import com.hazelcast.map.impl.eviction.MapClearExpiredRecordsTask;
import com.hazelcast.map.listener.EntryExpiredListener;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapExpirationManagerTest extends AbstractExpirationManagerTest {
    @Test
    public void restarts_running_backgroundClearTask_when_lifecycleState_turns_to_MERGED() {
        Config config = getConfig();
        config.setProperty(taskPeriodSecondsPropName(), "1");
        HazelcastInstance node = createHazelcastInstance(config);
        final AtomicInteger expirationCounter = new AtomicInteger();
        IMap<Integer, Integer> map = node.getMap("test");
        map.addEntryListener(new EntryExpiredListener() {
            @Override
            public void entryExpired(EntryEvent event) {
                expirationCounter.incrementAndGet();
            }
        }, true);
        map.put(1, 1, 3, TimeUnit.SECONDS);
        ((LifecycleServiceImpl) (node.getLifecycleService())).fireLifecycleEvent(MERGING);
        ((LifecycleServiceImpl) (node.getLifecycleService())).fireLifecycleEvent(MERGED);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                int expirationCount = expirationCounter.get();
                Assert.assertEquals(String.format("Expecting 1 expiration but found:%d", expirationCount), 1, expirationCount);
            }
        });
    }

    @Test
    public void clearExpiredRecordsTask_should_not_be_started_if_map_has_no_expirable_records() {
        Config config = getConfig();
        config.setProperty(taskPeriodSecondsPropName(), "1");
        final HazelcastInstance node = createHazelcastInstance(config);
        IMap<Integer, Integer> map = node.getMap("test");
        map.put(1, 1);
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse("There should be zero ClearExpiredRecordsTask", hasClearExpiredRecordsTaskStarted(node));
            }
        }, 3);
    }

    @Test
    public void testPrimaryDrivesEvictions_set_viaSystemProperty() {
        String previous = System.getProperty(MapClearExpiredRecordsTask.PROP_PRIMARY_DRIVES_BACKUP);
        try {
            System.setProperty(MapClearExpiredRecordsTask.PROP_PRIMARY_DRIVES_BACKUP, "False");
            MapClearExpiredRecordsTask task = ((MapClearExpiredRecordsTask) (newExpirationManager(createHazelcastInstance()).getTask()));
            boolean primaryDrivesEviction = task.canPrimaryDriveExpiration();
            Assert.assertFalse(primaryDrivesEviction);
        } finally {
            restoreProperty(MapClearExpiredRecordsTask.PROP_PRIMARY_DRIVES_BACKUP, previous);
        }
    }

    @Test
    public void clearExpiredRecordsTask_should_not_be_started_if_member_is_lite() {
        Config liteMemberConfig = getConfig();
        liteMemberConfig.setLiteMember(true);
        liteMemberConfig.setProperty(taskPeriodSecondsPropName(), "1");
        Config dataMemberConfig = getConfig();
        dataMemberConfig.setProperty(taskPeriodSecondsPropName(), "1");
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        final HazelcastInstance liteMember = factory.newHazelcastInstance(liteMemberConfig);
        factory.newHazelcastInstance(dataMemberConfig);
        IMap<Integer, Integer> map = liteMember.getMap("test");
        map.put(1, 1, 3, TimeUnit.SECONDS);
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse("There should be zero ClearExpiredRecordsTask", hasClearExpiredRecordsTaskStarted(liteMember));
            }
        }, 3);
    }

    @Test
    public void clearExpiredRecordsTask_should_be_started_when_mapConfig_ttl_is_configured() {
        String mapName = "test";
        Config config = getConfig();
        config.setProperty(taskPeriodSecondsPropName(), "1");
        config.getMapConfig(mapName).setTimeToLiveSeconds(2);
        HazelcastInstance node = createHazelcastInstance(config);
        IMap<Integer, Integer> map = node.getMap(mapName);
        map.put(1, 1);
        Assert.assertTrue("There should be one ClearExpiredRecordsTask", hasClearExpiredRecordsTaskStarted(node));
    }

    @Test
    public void clearExpiredRecordsTask_should_be_started_when_mapConfig_has_idle_configured() {
        String mapName = "test";
        Config config = getConfig();
        config.setProperty(taskPeriodSecondsPropName(), "1");
        config.getMapConfig(mapName).setMaxIdleSeconds(2);
        HazelcastInstance node = createHazelcastInstance(config);
        IMap<Integer, Integer> map = node.getMap(mapName);
        map.put(1, 1);
        Assert.assertTrue("There should be one ClearExpiredRecordsTask", hasClearExpiredRecordsTaskStarted(node));
    }

    @Test
    public void stops_running_backgroundClearTask_when_lifecycleState_SHUTTING_DOWN() {
        backgroundClearTaskStops_whenLifecycleState(SHUTTING_DOWN);
    }

    @Test
    public void stops_running_backgroundClearTask_when_lifecycleState_MERGING() {
        backgroundClearTaskStops_whenLifecycleState(MERGING);
    }

    @Test
    public void no_expiration_task_starts_on_new_node_after_migration_when_there_is_no_expirable_entry() {
        Config config = getConfig();
        config.setProperty(taskPeriodSecondsPropName(), "1");
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        final HazelcastInstance node1 = factory.newHazelcastInstance(config);
        IMap<Integer, Integer> map = node1.getMap("test");
        map.put(1, 1);
        final HazelcastInstance node2 = factory.newHazelcastInstance(config);
        node1.shutdown();
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse("There should be zero ClearExpiredRecordsTask", hasClearExpiredRecordsTaskStarted(node2));
            }
        }, 3);
    }

    @Test
    public void expiration_task_starts_on_new_node_after_migration_when_there_is_expirable_entry() {
        Config config = getConfig();
        config.setProperty(taskPeriodSecondsPropName(), "1");
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        final HazelcastInstance node1 = factory.newHazelcastInstance(config);
        IMap<Integer, Integer> map = node1.getMap("test");
        map.put(1, 1, 100, TimeUnit.SECONDS);
        final HazelcastInstance node2 = factory.newHazelcastInstance(config);
        node1.shutdown();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue("There should be a ClearExpiredRecordsTask", hasClearExpiredRecordsTaskStarted(node2));
            }
        });
    }
}

