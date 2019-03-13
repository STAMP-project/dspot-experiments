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
package com.hazelcast.partition;


import com.hazelcast.config.Config;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.PartitionService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class PartitionLostListenerRegistrationTest extends HazelcastTestSupport {
    @Test(expected = NullPointerException.class)
    public void test_addPartitionLostListener_whenNullListener() {
        HazelcastInstance hz = createHazelcastInstance();
        PartitionService partitionService = hz.getPartitionService();
        partitionService.addPartitionLostListener(null);
    }

    @Test
    public void test_addPartitionLostListener_whenListenerRegisteredProgrammatically() {
        final HazelcastInstance instance = createHazelcastInstance();
        final String id = instance.getPartitionService().addPartitionLostListener(Mockito.mock(PartitionLostListener.class));
        Assert.assertNotNull(id);
        // Expected = 4 -> 1 added + 1 from {@link com.hazelcast.scheduledexecutor.impl.DistributedScheduledExecutorService}
        // + 2 from map and cache ExpirationManagers
        PartitionLostListenerRegistrationTest.assertRegistrationsSizeEventually(instance, 4);
    }

    @Test
    public void test_partitionLostListener_whenListenerRegisteredViaConfiguration() {
        Config config = new Config();
        config.addListenerConfig(new ListenerConfig(Mockito.mock(PartitionLostListener.class)));
        HazelcastInstance instance = createHazelcastInstance(config);
        // Expected = 4 -> 1 added + 1 from {@link com.hazelcast.scheduledexecutor.impl.DistributedScheduledExecutorService}
        // + 2 from map and cache ExpirationManagers
        PartitionLostListenerRegistrationTest.assertRegistrationsSizeEventually(instance, 4);
    }

    @Test
    public void test_addPartitionLostListener_whenListenerRegisteredTwice() {
        HazelcastInstance instance = createHazelcastInstance();
        PartitionService partitionService = instance.getPartitionService();
        PartitionLostListener listener = Mockito.mock(PartitionLostListener.class);
        String id1 = partitionService.addPartitionLostListener(listener);
        String id2 = partitionService.addPartitionLostListener(listener);
        Assert.assertNotEquals(id1, id2);
        // Expected = 5 -> 2 added + 1 from {@link com.hazelcast.scheduledexecutor.impl.DistributedScheduledExecutorService}
        // + 2 from map and cache ExpirationManagers
        PartitionLostListenerRegistrationTest.assertRegistrationsSizeEventually(instance, 5);
    }

    @Test
    public void test_removePartitionLostListener_whenRegisteredListenerRemovedSuccessfully() {
        HazelcastInstance instance = createHazelcastInstance();
        PartitionService partitionService = instance.getPartitionService();
        PartitionLostListener listener = Mockito.mock(PartitionLostListener.class);
        String id1 = partitionService.addPartitionLostListener(listener);
        boolean result = partitionService.removePartitionLostListener(id1);
        Assert.assertTrue(result);
        // Expected = 1 -> see {@link com.hazelcast.scheduledexecutor.impl.DistributedScheduledExecutorService}
        // + 2 from map and cache ExpirationManagers
        PartitionLostListenerRegistrationTest.assertRegistrationsSizeEventually(instance, 3);
    }

    @Test
    public void test_removePartitionLostListener_whenNonExistingRegistrationIdRemoved() {
        HazelcastInstance instance = createHazelcastInstance();
        PartitionService partitionService = instance.getPartitionService();
        boolean result = partitionService.removePartitionLostListener("notExist");
        Assert.assertFalse(result);
    }

    @Test(expected = NullPointerException.class)
    public void test_removePartitionLostListener_whenNullRegistrationIdRemoved() {
        HazelcastInstance instance = createHazelcastInstance();
        PartitionService partitionService = instance.getPartitionService();
        partitionService.removePartitionLostListener(null);
    }
}

