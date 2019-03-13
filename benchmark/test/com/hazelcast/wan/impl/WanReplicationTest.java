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
package com.hazelcast.wan.impl;


import MapService.SERVICE_NAME;
import com.hazelcast.config.Config;
import com.hazelcast.config.WanPublisherConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Tests for WAN replication API.
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class WanReplicationTest extends HazelcastTestSupport {
    private TestHazelcastInstanceFactory factory;

    private HazelcastInstance instance1;

    private HazelcastInstance instance2;

    private IMap<Object, Object> map;

    private DummyWanReplication impl1;

    private DummyWanReplication impl2;

    @Test
    public void mapPutReplaceRemoveTest() {
        initInstancesAndMap("wan-replication-test-put-replace-remove");
        for (int i = 0; i < 10; i++) {
            map.put(i, i);
            map.replace(i, (i * 2));
            map.remove(i);
        }
        assertTotalQueueSize(30);
    }

    @Test
    public void mapSetTtlTest() {
        initInstancesAndMap("wan-replication-test-setTtl");
        for (int i = 0; i < 10; i++) {
            map.put(i, i);
            map.setTtl(i, 1, TimeUnit.MINUTES);
            map.remove(i);
        }
        assertTotalQueueSize(30);
    }

    @Test
    public void mapSetReplaceRemoveIfSameTest() {
        initInstancesAndMap("wan-replication-test-set-replace-remove-if-same");
        for (int i = 0; i < 10; i++) {
            map.set(i, i);
            map.replace(i, i, (i * 2));
            map.remove(i, (i * 2));
        }
        assertTotalQueueSize(30);
    }

    @Test
    public void mapTryPutRemoveTest() {
        initInstancesAndMap("wan-replication-test-try-put-remove");
        for (int i = 0; i < 10; i++) {
            Assert.assertTrue(map.tryPut(i, i, 10, TimeUnit.SECONDS));
            Assert.assertTrue(map.tryRemove(i, 10, TimeUnit.SECONDS));
        }
        assertTotalQueueSize(20);
    }

    @Test
    public void mapPutIfAbsentDeleteTest() {
        initInstancesAndMap("wan-replication-test-put-if-absent-delete");
        for (int i = 0; i < 10; i++) {
            Assert.assertNull(map.putIfAbsent(i, i));
            map.delete(i);
        }
        assertTotalQueueSize(20);
    }

    @Test
    public void mapPutTransientTest() {
        initInstancesAndMap("wan-replication-test-put-transient");
        for (int i = 0; i < 10; i++) {
            map.putTransient(i, i, 1, TimeUnit.SECONDS);
        }
        assertTotalQueueSize(10);
    }

    @Test
    public void mapPutAllTest() {
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        for (int i = 0; i < 10; i++) {
            userMap.put(i, i);
        }
        initInstancesAndMap("wan-replication-test-put-all");
        map.putAll(userMap);
        assertTotalQueueSize(10);
    }

    @Test
    public void entryProcessorTest() throws Exception {
        initInstancesAndMap("wan-replication-test-entry-processor");
        for (int i = 0; i < 10; i++) {
            map.put(i, i);
        }
        assertTotalQueueSize(10);
        // clean event queues
        impl1.eventQueue.clear();
        impl2.eventQueue.clear();
        InternalSerializationService serializationService = HazelcastTestSupport.getSerializationService(instance1);
        Set<Data> keySet = new HashSet<Data>();
        for (int i = 0; i < 10; i++) {
            keySet.add(serializationService.toData(i));
        }
        // multiple entry operations (update)
        OperationFactory operationFactory = getOperationProvider(map).createMultipleEntryOperationFactory(map.getName(), keySet, new WanReplicationTest.UpdatingEntryProcessor());
        InternalOperationService operationService = HazelcastTestSupport.getOperationService(instance1);
        operationService.invokeOnAllPartitions(SERVICE_NAME, operationFactory);
        // there should be 10 events since all entries should be processed
        assertTotalQueueSize(10);
        // multiple entry operations (remove)
        OperationFactory deletingOperationFactory = getOperationProvider(map).createMultipleEntryOperationFactory(map.getName(), keySet, new WanReplicationTest.DeletingEntryProcessor());
        operationService.invokeOnAllPartitions(SERVICE_NAME, deletingOperationFactory);
        // 10 more event should be published
        assertTotalQueueSize(20);
    }

    @Test
    public void programmaticImplCreationTest() {
        Config config = getConfig();
        WanPublisherConfig publisherConfig = config.getWanReplicationConfig("dummyWan").getWanPublisherConfigs().get(0);
        DummyWanReplication dummyWanReplication = new DummyWanReplication();
        publisherConfig.setImplementation(dummyWanReplication);
        instance1 = factory.newHazelcastInstance(config);
        Assert.assertEquals(dummyWanReplication, getWanReplicationImpl(instance1));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void mergeOperationGeneratesWanReplicationEvent_withLegacyMergePolicy() {
        boolean enableWANReplicationEvent = true;
        boolean useLegacyMergePolicy = true;
        runMergeOpForWAN(enableWANReplicationEvent, useLegacyMergePolicy);
        assertTotalQueueSize(1);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void mergeOperationGeneratesWanReplicationEvent() {
        boolean enableWANReplicationEvent = true;
        boolean useLegacyMergePolicy = false;
        runMergeOpForWAN(enableWANReplicationEvent, useLegacyMergePolicy);
        assertTotalQueueSize(1);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void mergeOperationDoesNotGenerateWanReplicationEventWhenDisabled_withLegacyMergePolicy() {
        boolean enableWANReplicationEvent = false;
        boolean useLegacyMergePolicy = true;
        runMergeOpForWAN(enableWANReplicationEvent, useLegacyMergePolicy);
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                assertTotalQueueSize(0);
            }
        }, 3);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void mergeOperationDoesNotGenerateWanReplicationEventWhenDisabled() {
        boolean enableWANReplicationEvent = false;
        boolean useLegacyMergePolicy = false;
        runMergeOpForWAN(enableWANReplicationEvent, useLegacyMergePolicy);
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                assertTotalQueueSize(0);
            }
        }, 3);
    }

    private static class UpdatingEntryProcessor implements EntryBackupProcessor<Object, Object> , EntryProcessor<Object, Object> {
        @Override
        public Object process(Map.Entry<Object, Object> entry) {
            entry.setValue(("EP" + (entry.getValue())));
            return "done";
        }

        @Override
        public EntryBackupProcessor<Object, Object> getBackupProcessor() {
            return this;
        }

        @Override
        public void processBackup(Map.Entry<Object, Object> entry) {
            process(entry);
        }
    }

    private static class DeletingEntryProcessor implements EntryBackupProcessor<Object, Object> , EntryProcessor<Object, Object> {
        @Override
        public Object process(Map.Entry<Object, Object> entry) {
            entry.setValue(null);
            return "done";
        }

        @Override
        public EntryBackupProcessor<Object, Object> getBackupProcessor() {
            return this;
        }

        @Override
        public void processBackup(Map.Entry<Object, Object> entry) {
            process(entry);
        }
    }
}

