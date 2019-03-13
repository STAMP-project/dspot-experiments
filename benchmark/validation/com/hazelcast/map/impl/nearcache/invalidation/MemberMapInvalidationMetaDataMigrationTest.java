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
package com.hazelcast.map.impl.nearcache.invalidation;


import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MemberMapInvalidationMetaDataMigrationTest extends HazelcastTestSupport {
    private static final int MAP_SIZE = 10000;

    private static final String MAP_NAME = "MapInvalidationMetaDataMigrationTest";

    private TestHazelcastInstanceFactory factory = new TestHazelcastInstanceFactory();

    private Config config;

    @Test
    public void sequences_migrated_whenNewlyJoinedNodesShutdown() {
        HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        IMap<Object, Object> map = instance1.getMap(MemberMapInvalidationMetaDataMigrationTest.MAP_NAME);
        for (int i = 0; i < (MemberMapInvalidationMetaDataMigrationTest.MAP_SIZE); i++) {
            map.put(i, i);
        }
        assertInvalidationCountEventually(MemberMapInvalidationMetaDataMigrationTest.MAP_NAME, MemberMapInvalidationMetaDataMigrationTest.MAP_SIZE, instance1);
        Map<Integer, Long> source = MemberMapInvalidationMetaDataMigrationTest.getPartitionToSequenceMap(MemberMapInvalidationMetaDataMigrationTest.MAP_NAME, instance1);
        HazelcastInstance instance2 = factory.newHazelcastInstance(config);
        HazelcastTestSupport.waitAllForSafeState(instance2);
        instance1.shutdown();
        HazelcastInstance instance3 = factory.newHazelcastInstance(config);
        HazelcastTestSupport.waitAllForSafeState(instance3);
        instance2.shutdown();
        HazelcastTestSupport.waitAllForSafeState(instance3);
        Map<Integer, Long> destination = MemberMapInvalidationMetaDataMigrationTest.getPartitionToSequenceMap(MemberMapInvalidationMetaDataMigrationTest.MAP_NAME, instance3);
        MemberMapInvalidationMetaDataMigrationTest.assertEqualsSequenceNumbers(source, destination);
    }

    @Test
    public void sequences_migrated_whenSourceNodeShutdown() {
        HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        IMap<Object, Object> map = instance1.getMap(MemberMapInvalidationMetaDataMigrationTest.MAP_NAME);
        for (int i = 0; i < (MemberMapInvalidationMetaDataMigrationTest.MAP_SIZE); i++) {
            map.put(i, i);
        }
        assertInvalidationCountEventually(MemberMapInvalidationMetaDataMigrationTest.MAP_NAME, MemberMapInvalidationMetaDataMigrationTest.MAP_SIZE, instance1);
        Map<Integer, Long> source = MemberMapInvalidationMetaDataMigrationTest.getPartitionToSequenceMap(MemberMapInvalidationMetaDataMigrationTest.MAP_NAME, instance1);
        HazelcastInstance instance2 = factory.newHazelcastInstance(config);
        HazelcastInstance instance3 = factory.newHazelcastInstance(config);
        HazelcastTestSupport.waitAllForSafeState(instance2, instance3);
        instance1.shutdown();
        Map<Integer, Long> destination2 = MemberMapInvalidationMetaDataMigrationTest.getPartitionToSequenceMap(MemberMapInvalidationMetaDataMigrationTest.MAP_NAME, instance2);
        Map<Integer, Long> destination3 = MemberMapInvalidationMetaDataMigrationTest.getPartitionToSequenceMap(MemberMapInvalidationMetaDataMigrationTest.MAP_NAME, instance3);
        for (Map.Entry<Integer, Long> entry : destination2.entrySet()) {
            Integer key = entry.getKey();
            Long value = entry.getValue();
            if (value != 0) {
                destination3.put(key, value);
            }
        }
        MemberMapInvalidationMetaDataMigrationTest.assertEqualsSequenceNumbers(source, destination3);
    }

    @Test
    public void sequences_migrated_whenOneNodeContinuouslyStartsAndStops() {
        HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        IMap<Object, Object> map = instance1.getMap(MemberMapInvalidationMetaDataMigrationTest.MAP_NAME);
        for (int i = 0; i < (MemberMapInvalidationMetaDataMigrationTest.MAP_SIZE); i++) {
            map.put(i, i);
        }
        assertInvalidationCountEventually(MemberMapInvalidationMetaDataMigrationTest.MAP_NAME, MemberMapInvalidationMetaDataMigrationTest.MAP_SIZE, instance1);
        Map<Integer, Long> source = MemberMapInvalidationMetaDataMigrationTest.getPartitionToSequenceMap(MemberMapInvalidationMetaDataMigrationTest.MAP_NAME, instance1);
        HazelcastInstance instance2 = factory.newHazelcastInstance(config);
        final AtomicBoolean stop = new AtomicBoolean();
        Thread shadow = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(stop.get())) {
                    HazelcastInstance instance = factory.newHazelcastInstance(config);
                    HazelcastTestSupport.waitAllForSafeState(instance);
                    HazelcastTestSupport.sleepSeconds(5);
                    instance.shutdown();
                } 
            }
        });
        shadow.start();
        HazelcastTestSupport.sleepSeconds(20);
        stop.set(true);
        HazelcastTestSupport.assertJoinable(shadow);
        instance2.shutdown();
        Map<Integer, Long> destination = MemberMapInvalidationMetaDataMigrationTest.getPartitionToSequenceMap(MemberMapInvalidationMetaDataMigrationTest.MAP_NAME, instance1);
        MemberMapInvalidationMetaDataMigrationTest.assertEqualsSequenceNumbers(source, destination);
    }

    @Test
    public void uuids_migrated_whenNewlyJoinedNodesShutdown() {
        HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        IMap<Object, Object> map = instance1.getMap(MemberMapInvalidationMetaDataMigrationTest.MAP_NAME);
        for (int i = 0; i < (MemberMapInvalidationMetaDataMigrationTest.MAP_SIZE); i++) {
            map.put(i, i);
        }
        assertInvalidationCountEventually(MemberMapInvalidationMetaDataMigrationTest.MAP_NAME, MemberMapInvalidationMetaDataMigrationTest.MAP_SIZE, instance1);
        Map<Integer, UUID> source = MemberMapInvalidationMetaDataMigrationTest.getPartitionToUuidMap(instance1);
        HazelcastInstance instance2 = factory.newHazelcastInstance(config);
        HazelcastTestSupport.waitAllForSafeState(instance2);
        instance1.shutdown();
        HazelcastInstance instance3 = factory.newHazelcastInstance(config);
        HazelcastTestSupport.waitAllForSafeState(instance3);
        instance2.shutdown();
        Map<Integer, UUID> destination = MemberMapInvalidationMetaDataMigrationTest.getPartitionToUuidMap(instance3);
        MemberMapInvalidationMetaDataMigrationTest.assertEqualsPartitionUUIDs(source, destination);
    }

    @Test
    public void uuids_migrated_whenSourceNodeShutdown() {
        final HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        IMap<Object, Object> map = instance1.getMap(MemberMapInvalidationMetaDataMigrationTest.MAP_NAME);
        for (int i = 0; i < (MemberMapInvalidationMetaDataMigrationTest.MAP_SIZE); i++) {
            map.put(i, i);
        }
        assertInvalidationCountEventually(MemberMapInvalidationMetaDataMigrationTest.MAP_NAME, MemberMapInvalidationMetaDataMigrationTest.MAP_SIZE, instance1);
        Map<Integer, UUID> source = MemberMapInvalidationMetaDataMigrationTest.getPartitionToUuidMap(instance1);
        HazelcastInstance instance2 = factory.newHazelcastInstance(config);
        HazelcastInstance instance3 = factory.newHazelcastInstance(config);
        HazelcastTestSupport.waitAllForSafeState(instance1, instance2, instance3);
        instance1.shutdown();
        Map<Integer, UUID> destination2 = MemberMapInvalidationMetaDataMigrationTest.getPartitionToUuidMap(instance2);
        Map<Integer, UUID> destination3 = MemberMapInvalidationMetaDataMigrationTest.getPartitionToUuidMap(instance3);
        InternalPartitionService partitionService2 = HazelcastTestSupport.getNodeEngineImpl(instance2).getPartitionService();
        Map<Integer, UUID> merged = MemberMapInvalidationMetaDataMigrationTest.mergeOwnedPartitionUuids(partitionService2, destination2, destination3);
        MemberMapInvalidationMetaDataMigrationTest.assertEqualsPartitionUUIDs(source, merged);
    }
}

