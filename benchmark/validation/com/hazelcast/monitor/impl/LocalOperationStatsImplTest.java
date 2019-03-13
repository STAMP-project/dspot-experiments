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
package com.hazelcast.monitor.impl;


import GroupProperty.MC_MAX_VISIBLE_SLOW_OPERATION_COUNT;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.management.dto.SlowOperationDTO;
import com.hazelcast.internal.management.dto.SlowOperationInvocationDTO;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LocalOperationStatsImplTest extends HazelcastTestSupport {
    @Test
    public void testDefaultConstructor() {
        LocalOperationStatsImpl localOperationStats = new LocalOperationStatsImpl();
        Assert.assertEquals(Long.MAX_VALUE, localOperationStats.getMaxVisibleSlowOperationCount());
        Assert.assertEquals(0, localOperationStats.getSlowOperations().size());
        Assert.assertTrue(((localOperationStats.getCreationTime()) > 0));
        Assert.assertNotNull(localOperationStats.toString());
    }

    @Test
    public void testNodeConstructor() {
        Config config = new Config();
        config.setProperty(MC_MAX_VISIBLE_SLOW_OPERATION_COUNT.getName(), "139");
        HazelcastInstance hazelcastInstance = createHazelcastInstance(config);
        Node node = HazelcastTestSupport.getNode(hazelcastInstance);
        LocalOperationStatsImpl localOperationStats = new LocalOperationStatsImpl(node);
        Assert.assertEquals(139, localOperationStats.getMaxVisibleSlowOperationCount());
        Assert.assertEquals(0, localOperationStats.getSlowOperations().size());
        Assert.assertTrue(((localOperationStats.getCreationTime()) > 0));
        Assert.assertNotNull(localOperationStats.toString());
    }

    @Test
    public void testSerialization() {
        Config config = new Config();
        config.setProperty(MC_MAX_VISIBLE_SLOW_OPERATION_COUNT.getName(), "127");
        SlowOperationInvocationDTO slowOperationInvocationDTO = new SlowOperationInvocationDTO();
        slowOperationInvocationDTO.id = 12345;
        slowOperationInvocationDTO.durationMs = 15000;
        slowOperationInvocationDTO.startedAt = 12381912;
        slowOperationInvocationDTO.operationDetails = "TestOperationDetails";
        List<SlowOperationInvocationDTO> invocationList = new ArrayList<SlowOperationInvocationDTO>();
        invocationList.add(slowOperationInvocationDTO);
        SlowOperationDTO slowOperationDTO = new SlowOperationDTO();
        slowOperationDTO.operation = "TestOperation";
        slowOperationDTO.stackTrace = "stackTrace";
        slowOperationDTO.totalInvocations = 4;
        slowOperationDTO.invocations = invocationList;
        HazelcastInstance hazelcastInstance = createHazelcastInstance(config);
        Node node = HazelcastTestSupport.getNode(hazelcastInstance);
        LocalOperationStatsImpl localOperationStats = new LocalOperationStatsImpl(node);
        localOperationStats.getSlowOperations().add(slowOperationDTO);
        LocalOperationStatsImpl deserialized = new LocalOperationStatsImpl();
        deserialized.fromJson(localOperationStats.toJson());
        Assert.assertEquals(localOperationStats.getCreationTime(), deserialized.getCreationTime());
        Assert.assertEquals(localOperationStats.getMaxVisibleSlowOperationCount(), deserialized.getMaxVisibleSlowOperationCount());
        LocalOperationStatsImplTest.assertEqualsSlowOperationDTOs(localOperationStats.getSlowOperations(), deserialized.getSlowOperations());
    }
}

