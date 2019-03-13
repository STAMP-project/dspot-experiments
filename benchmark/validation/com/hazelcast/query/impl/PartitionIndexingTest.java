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
package com.hazelcast.query.impl;


import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.spi.MigrationAwareService;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionMigrationEvent;
import com.hazelcast.spi.PartitionReplicationEvent;
import com.hazelcast.test.HazelcastParallelParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParallelParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class PartitionIndexingTest extends HazelcastTestSupport {
    private static final int ENTRIES = 10000;

    private static final String MAP_NAME = "map";

    @Parameterized.Parameter
    public InMemoryFormat inMemoryFormat;

    protected TestHazelcastInstanceFactory factory;

    private PartitionIndexingTest.MigrationFailingService migrationFailingService = new PartitionIndexingTest.MigrationFailingService();

    @Test
    public void testOnPreConfiguredIndexes() {
        Config config = getConfig();
        config.getMapConfig(PartitionIndexingTest.MAP_NAME).addMapIndexConfig(new MapIndexConfig("this", false));
        config.getMapConfig(PartitionIndexingTest.MAP_NAME).addMapIndexConfig(new MapIndexConfig("__key", true));
        HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        int expectedPartitions = HazelcastTestSupport.getPartitionService(instance1).getPartitionCount();
        IMap<Integer, Integer> map1 = instance1.getMap(PartitionIndexingTest.MAP_NAME);
        PartitionIndexingTest.assertPartitionsIndexedCorrectly(expectedPartitions, map1);
        IMap<Integer, Integer> client1 = createClientFor(map1);
        for (int i = 0; i < (PartitionIndexingTest.ENTRIES); ++i) {
            client1.put(i, i);
        }
        PartitionIndexingTest.assertPartitionsIndexedCorrectly(expectedPartitions, map1);
        HazelcastInstance instance2 = factory.newHazelcastInstance(config);
        IMap<Integer, Integer> map2 = instance2.getMap(PartitionIndexingTest.MAP_NAME);
        HazelcastTestSupport.waitAllForSafeState(instance1, instance2);
        PartitionIndexingTest.assertPartitionsIndexedCorrectly(expectedPartitions, map1, map2);
        HazelcastInstance instance3 = factory.newHazelcastInstance(config);
        IMap<Integer, Integer> map3 = instance3.getMap(PartitionIndexingTest.MAP_NAME);
        HazelcastTestSupport.waitAllForSafeState(instance1, instance2, instance3);
        PartitionIndexingTest.assertPartitionsIndexedCorrectly(expectedPartitions, map1, map2, map3);
        instance2.shutdown();
        HazelcastTestSupport.waitAllForSafeState(instance1, instance3);
        PartitionIndexingTest.assertPartitionsIndexedCorrectly(expectedPartitions, map1, map3);
        migrationFailingService.fail = true;
        HazelcastInstance instance4 = factory.newHazelcastInstance(config);
        IMap<Integer, Integer> map4 = instance4.getMap(PartitionIndexingTest.MAP_NAME);
        HazelcastTestSupport.waitAllForSafeState(instance1, instance3, instance4);
        PartitionIndexingTest.assertPartitionsIndexedCorrectly(expectedPartitions, map1, map3, map4);
        Assert.assertTrue(migrationFailingService.rolledBack);
    }

    @Test
    public void testOnProgrammaticallyAddedIndexes() {
        Config config = getConfig();
        HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        int expectedPartitions = HazelcastTestSupport.getPartitionService(instance1).getPartitionCount();
        IMap<Integer, Integer> map1 = instance1.getMap(PartitionIndexingTest.MAP_NAME);
        PartitionIndexingTest.assertPartitionsIndexedCorrectly(expectedPartitions, map1);
        IMap<Integer, Integer> client1 = createClientFor(map1);
        for (int i = 0; i < (PartitionIndexingTest.ENTRIES); ++i) {
            client1.put(i, i);
        }
        client1.addIndex("this", false);
        PartitionIndexingTest.assertPartitionsIndexedCorrectly(expectedPartitions, map1);
        HazelcastInstance instance2 = factory.newHazelcastInstance(config);
        IMap<Integer, Integer> map2 = instance2.getMap(PartitionIndexingTest.MAP_NAME);
        HazelcastTestSupport.waitAllForSafeState(instance1, instance2);
        PartitionIndexingTest.assertPartitionsIndexedCorrectly(expectedPartitions, map1, map2);
        HazelcastInstance instance3 = factory.newHazelcastInstance(config);
        IMap<Integer, Integer> map3 = instance3.getMap(PartitionIndexingTest.MAP_NAME);
        HazelcastTestSupport.waitAllForSafeState(instance1, instance2, instance3);
        PartitionIndexingTest.assertPartitionsIndexedCorrectly(expectedPartitions, map1, map2, map3);
        instance2.shutdown();
        HazelcastTestSupport.waitAllForSafeState(instance1, instance3);
        PartitionIndexingTest.assertPartitionsIndexedCorrectly(expectedPartitions, map1, map3);
        IMap<Integer, Integer> client3 = createClientFor(map3);
        client3.addIndex("__key", true);
        PartitionIndexingTest.assertPartitionsIndexedCorrectly(expectedPartitions, map1, map3);
        migrationFailingService.fail = true;
        HazelcastInstance instance4 = factory.newHazelcastInstance(config);
        IMap<Integer, Integer> map4 = instance4.getMap(PartitionIndexingTest.MAP_NAME);
        HazelcastTestSupport.waitAllForSafeState(instance1, instance3, instance4);
        PartitionIndexingTest.assertPartitionsIndexedCorrectly(expectedPartitions, map1, map3, map4);
        Assert.assertTrue(migrationFailingService.rolledBack);
    }

    private static class MigrationFailingService implements MigrationAwareService {
        public volatile boolean fail = false;

        public volatile boolean rolledBack = false;

        @Override
        public Operation prepareReplicationOperation(PartitionReplicationEvent event) {
            return null;
        }

        @Override
        public void beforeMigration(PartitionMigrationEvent event) {
            if ((fail) && (!(rolledBack))) {
                throw new RuntimeException("migration intentionally failed");
            }
        }

        @Override
        public void commitMigration(PartitionMigrationEvent event) {
        }

        @Override
        public void rollbackMigration(PartitionMigrationEvent event) {
            rolledBack = true;
        }
    }
}

