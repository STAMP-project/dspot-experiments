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


import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.partition.service.TestGetOperation;
import com.hazelcast.internal.partition.service.TestPutOperation;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.OperationService;
import com.hazelcast.test.HazelcastTestSupport;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.Parameterized;


public abstract class AbstractGracefulShutdownCorrectnessTest extends PartitionCorrectnessTestSupport {
    @Parameterized.Parameter(2)
    public int shutdownNodeCount;

    @Test(timeout = (6000 * 10) * 10)
    public void testPartitionData_whenNodesShutdown() throws InterruptedException {
        Config config = getConfig(true, false);
        HazelcastInstance hz = factory.newHazelcastInstance(config);
        startNodes(config, nodeCount);
        HazelcastTestSupport.warmUpPartitions(factory.getAllHazelcastInstances());
        fillData(hz);
        assertSizeAndDataEventually();
        shutdownNodes(shutdownNodeCount);
        assertSizeAndData();
    }

    @Test(timeout = (6000 * 10) * 10)
    public void testPartitionData_whenNodesStartedShutdown() throws InterruptedException {
        Config config = getConfig(true, false);
        HazelcastInstance hz = factory.newHazelcastInstance(config);
        fillData(hz);
        assertSizeAndDataEventually();
        int size = 1;
        while (size < ((nodeCount) + 1)) {
            startNodes(config, ((shutdownNodeCount) + 1));
            size += (shutdownNodeCount) + 1;
            assertSizeAndDataEventually();
            shutdownNodes(shutdownNodeCount);
            size -= shutdownNodeCount;
            assertSizeAndDataEventually();
        } 
    }

    @Test(timeout = (6000 * 10) * 10)
    public void testPartitionData_whenNodesStartedShutdown_withRestart() throws InterruptedException {
        Config config = getConfig(true, false);
        HazelcastInstance hz = factory.newHazelcastInstance(config);
        fillData(hz);
        assertSizeAndDataEventually();
        Collection<Address> addresses = Collections.emptySet();
        int size = 1;
        while (size < ((nodeCount) + 1)) {
            int startCount = ((shutdownNodeCount) + 1) - (addresses.size());
            startNodes(config, addresses);
            startNodes(config, startCount);
            size += (shutdownNodeCount) + 1;
            assertSizeAndDataEventually();
            addresses = shutdownNodes(shutdownNodeCount);
            size -= shutdownNodeCount;
            assertSizeAndDataEventually();
        } 
    }

    @Test(timeout = (6000 * 10) * 10)
    public void testPartitionData_whenNodesStartedShutdown_whileOperationsOngoing() throws InterruptedException {
        final Config config = getConfig(true, false);
        Future future = HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                LinkedList<HazelcastInstance> instances = new LinkedList<HazelcastInstance>(Arrays.asList(factory.newInstances(config, nodeCount)));
                try {
                    for (int i = 0; i < 3; i++) {
                        shutdownNodes(instances, shutdownNodeCount);
                        Collection<HazelcastInstance> startedInstances = startNodes(config, shutdownNodeCount);
                        instances.addAll(startedInstances);
                    }
                    shutdownNodes(instances, shutdownNodeCount);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        HazelcastInstance hz = factory.newHazelcastInstance(config);
        NodeEngine nodeEngine = HazelcastTestSupport.getNodeEngineImpl(hz);
        OperationService operationService = nodeEngine.getOperationService();
        int partitionCount = nodeEngine.getPartitionService().getPartitionCount();
        int value = 0;
        while (!(future.isDone())) {
            value++;
            for (int p = 0; p < partitionCount; p++) {
                operationService.invokeOnPartition(null, new TestPutOperation(value), p).join();
            }
        } 
        for (int p = 0; p < partitionCount; p++) {
            Integer actual = ((Integer) (operationService.invokeOnPartition(null, new TestGetOperation(), p).join()));
            Assert.assertEquals(value, actual.intValue());
        }
    }
}

