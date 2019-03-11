/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.ml.dataset.impl.cache.util;


import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.apache.ignite.cache.affinity.AffinityFunction;
import org.apache.ignite.cache.affinity.AffinityFunctionContext;
import org.apache.ignite.cluster.ClusterNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Tests for {@link DatasetAffinityFunctionWrapper}.
 */
@RunWith(MockitoJUnitRunner.class)
public class DatasetAffinityFunctionWrapperTest {
    /**
     * Mocked affinity function.
     */
    @Mock
    private AffinityFunction affinityFunction;

    /**
     * Wrapper.
     */
    private DatasetAffinityFunctionWrapper wrapper;

    /**
     * Tests {@code reset()} method.
     */
    @Test
    public void testReset() {
        wrapper.reset();
        Mockito.verify(affinityFunction, Mockito.times(1)).reset();
    }

    /**
     * Tests {@code partitions()} method.
     */
    @Test
    public void testPartitions() {
        Mockito.doReturn(42).when(affinityFunction).partitions();
        int partitions = wrapper.partitions();
        Assert.assertEquals(42, partitions);
        Mockito.verify(affinityFunction, Mockito.times(1)).partitions();
    }

    /**
     * Tests {@code partition} method.
     */
    @Test
    public void testPartition() {
        Mockito.doReturn(0).when(affinityFunction).partition(ArgumentMatchers.eq(42));
        int part = wrapper.partition(42);
        Assert.assertEquals(42, part);
        Mockito.verify(affinityFunction, Mockito.times(0)).partition(ArgumentMatchers.any());
    }

    /**
     * Tests {@code assignPartitions()} method.
     */
    @Test
    public void testAssignPartitions() {
        List<List<ClusterNode>> nodes = Collections.singletonList(Collections.singletonList(Mockito.mock(ClusterNode.class)));
        Mockito.doReturn(nodes).when(affinityFunction).assignPartitions(ArgumentMatchers.any());
        List<List<ClusterNode>> resNodes = wrapper.assignPartitions(Mockito.mock(AffinityFunctionContext.class));
        Assert.assertEquals(nodes, resNodes);
        Mockito.verify(affinityFunction, Mockito.times(1)).assignPartitions(ArgumentMatchers.any());
    }

    /**
     * Tests {@code removeNode()} method.
     */
    @Test
    public void testRemoveNode() {
        UUID nodeId = UUID.randomUUID();
        wrapper.removeNode(nodeId);
        Mockito.verify(affinityFunction, Mockito.times(1)).removeNode(ArgumentMatchers.eq(nodeId));
    }
}

