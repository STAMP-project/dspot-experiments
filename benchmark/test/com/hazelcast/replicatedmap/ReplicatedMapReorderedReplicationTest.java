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


import ReplicatedMapService.SERVICE_NAME;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.operation.ReplicateUpdateOperation;
import com.hazelcast.replicatedmap.impl.operation.ReplicatedMapDataSerializerHook;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.spi.exception.RetryableHazelcastException;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.SlowTest;
import java.lang.reflect.Field;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(SlowTest.class)
public class ReplicatedMapReorderedReplicationTest extends HazelcastTestSupport {
    // if data serializable factory of ReplicatedMapDataSerializerHook is replaced by updateFactory()
    // during a test, this field stores its original value to be restored on test tear down
    private DataSerializableFactory replicatedMapDataSerializableFactory;

    private Field field;

    @Test
    public void testNonConvergingReplicatedMaps() throws Exception {
        final int nodeCount = 4;
        final int keyCount = 10000;
        final int threadCount = 2;
        updateFactory();
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        final Config config = new Config();
        final HazelcastInstance[] instances = factory.newInstances(config, nodeCount);
        HazelcastTestSupport.warmUpPartitions(instances);
        final int partitionId = HazelcastTestSupport.randomPartitionOwnedBy(instances[0]).getPartitionId();
        final String mapName = HazelcastTestSupport.randomMapName();
        final NodeEngineImpl nodeEngine = HazelcastTestSupport.getNodeEngineImpl(instances[0]);
        final Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            final int startIndex = i;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = startIndex; j < keyCount; j += threadCount) {
                        put(nodeEngine, mapName, partitionId, j, j);
                    }
                }
            });
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        final ReplicatedRecordStore[] stores = new ReplicatedRecordStore[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            ReplicatedMapService service = HazelcastTestSupport.getNodeEngineImpl(instances[i]).getService(SERVICE_NAME);
            service.triggerAntiEntropy();
            stores[i] = service.getReplicatedRecordStore(mapName, false, partitionId);
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                long version = stores[0].getVersion();
                for (ReplicatedRecordStore store : stores) {
                    Assert.assertEquals(version, store.getVersion());
                    Assert.assertFalse(store.isStale(version));
                }
            }
        });
        for (int i = 0; i < keyCount; i++) {
            for (ReplicatedRecordStore store : stores) {
                Assert.assertTrue(store.containsKey(i));
            }
        }
    }

    private static class TestReplicatedMapDataSerializerFactory implements DataSerializableFactory {
        private final DataSerializableFactory factory;

        TestReplicatedMapDataSerializerFactory(DataSerializableFactory factory) {
            this.factory = factory;
        }

        @Override
        public IdentifiedDataSerializable create(int typeId) {
            if (typeId == (ReplicatedMapDataSerializerHook.REPLICATE_UPDATE)) {
                return new ReplicatedMapReorderedReplicationTest.RetriedReplicateUpdateOperation();
            }
            return factory.create(typeId);
        }
    }

    private static class RetriedReplicateUpdateOperation extends ReplicateUpdateOperation {
        private final Random random = new Random();

        @Override
        public void run() throws Exception {
            if ((random.nextInt(10)) < 2) {
                throw new RetryableHazelcastException();
            }
            super.run();
        }
    }
}

