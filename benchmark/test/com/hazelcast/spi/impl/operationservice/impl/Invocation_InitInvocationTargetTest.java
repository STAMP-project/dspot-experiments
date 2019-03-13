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
package com.hazelcast.spi.impl.operationservice.impl;


import PartitionDataSerializerHook.F_ID;
import PartitionDataSerializerHook.PARTITION_STATE_OP;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.PacketFiltersUtil;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class Invocation_InitInvocationTargetTest extends HazelcastTestSupport {
    @Test
    public void testPartitionTableIsFetchedLazilyOnPartitionInvocation() throws InterruptedException, ExecutionException {
        Config config = new Config();
        config.setProperty(GroupProperty.PARTITION_TABLE_SEND_INTERVAL.getName(), String.valueOf(Integer.MAX_VALUE));
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance[] instances = factory.newInstances(config);
        PacketFiltersUtil.dropOperationsBetween(instances[0], instances[1], F_ID, Collections.singletonList(PARTITION_STATE_OP));
        InternalOperationService operationService = HazelcastTestSupport.getNodeEngineImpl(instances[1]).getOperationService();
        Future<Object> future = operationService.invokeOnPartition(null, new DummyOperation(), 0);
        PacketFiltersUtil.resetPacketFiltersFrom(instances[0]);
        future.get();
    }
}

