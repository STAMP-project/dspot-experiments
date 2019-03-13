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
package com.hazelcast.internal.partition;


import MapService.SERVICE_NAME;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
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
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AntiEntropyCleanupTest extends HazelcastTestSupport {
    @Parameterized.Parameter
    public int nodeCount;

    @Test
    public void testCleanup() {
        Config config = new Config().setProperty(GroupProperty.PARTITION_BACKUP_SYNC_INTERVAL.getName(), "1");
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(nodeCount);
        HazelcastInstance[] instances = factory.newInstances(config, nodeCount);
        HazelcastTestSupport.warmUpPartitions(instances);
        String mapName = HazelcastTestSupport.randomMapName();
        HazelcastInstance instance1 = instances[0];
        for (int partitionId = 0; partitionId < (HazelcastTestSupport.getPartitionService(instance1).getPartitionCount()); partitionId++) {
            String key = HazelcastTestSupport.generateKeyForPartition(instance1, partitionId);
            instance1.getMap(mapName).put(key, key);
        }
        instance1.getMap(mapName).destroy();
        for (final HazelcastInstance instance : instances) {
            final InternalPartitionService partitionService = HazelcastTestSupport.getPartitionService(instance);
            final PartitionReplicaVersionManager replicaVersionManager = partitionService.getPartitionReplicaVersionManager();
            HazelcastTestSupport.assertTrueEventually(new AssertTask() {
                @Override
                public void run() throws Exception {
                    for (int partitionId = 0; partitionId < (partitionService.getPartitionCount()); partitionId++) {
                        for (ServiceNamespace namespace : replicaVersionManager.getNamespaces(partitionId)) {
                            Assert.assertFalse(namespace.getServiceName().equals(SERVICE_NAME));
                        }
                    }
                }
            });
        }
    }
}

