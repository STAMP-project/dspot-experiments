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


import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MigrationTest extends HazelcastTestSupport {
    @Test
    public void testMigration_whenAddingInstances_withStatisticsEnabled() {
        int size = 1000;
        String name = HazelcastTestSupport.randomString();
        Config config = getConfig();
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(3);
        HazelcastInstance instance1 = nodeFactory.newHazelcastInstance(config);
        Map<Integer, Integer> map = instance1.getMap(name);
        for (int i = 0; i < size; i++) {
            map.put(i, i);
        }
        HazelcastInstance instance2 = nodeFactory.newHazelcastInstance(config);
        HazelcastTestSupport.waitAllForSafeState(instance1, instance2);
        Assert.assertEquals("Some records have been lost.", size, map.values().size());
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(i, map.get(i).intValue());
        }
        HazelcastInstance instance3 = nodeFactory.newHazelcastInstance(config);
        HazelcastTestSupport.waitAllForSafeState(instance1, instance2, instance3);
        Assert.assertEquals("Some records have been lost.", size, map.values().size());
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(i, map.get(i).intValue());
        }
        List<HazelcastInstance> list = new ArrayList<HazelcastInstance>(3);
        list.add(instance1);
        list.add(instance2);
        list.add(instance3);
        MigrationTest.assertThatMigrationIsDoneAndReplicasAreIntact(list);
    }

    @Test
    public void testMigration_whenRemovingInstances_withStatisticsDisabled() {
        int size = 100;
        String name = HazelcastTestSupport.randomString();
        Config config = getConfig();
        MapConfig mapConfig = config.getMapConfig(name);
        mapConfig.setStatisticsEnabled(false);
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(3);
        HazelcastInstance instance1 = nodeFactory.newHazelcastInstance(config);
        HazelcastInstance instance2 = nodeFactory.newHazelcastInstance(config);
        HazelcastInstance instance3 = nodeFactory.newHazelcastInstance(config);
        IMap<Integer, Integer> map = instance1.getMap(name);
        for (int i = 0; i < size; i++) {
            map.put(i, i);
        }
        instance2.shutdown();
        instance3.shutdown();
        HazelcastTestSupport.waitAllForSafeState(instance1);
        Assert.assertEquals("Some records have been lost.", size, map.values().size());
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(i, map.get(i).intValue());
        }
        MigrationTest.assertThatMigrationIsDoneAndReplicasAreIntact(Collections.singletonList(instance1));
    }
}

