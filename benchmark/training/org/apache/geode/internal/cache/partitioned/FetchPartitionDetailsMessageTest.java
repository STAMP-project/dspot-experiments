/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache.partitioned;


import org.apache.geode.distributed.internal.ClusterDistributionManager;
import org.apache.geode.internal.cache.PartitionedRegion;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class FetchPartitionDetailsMessageTest {
    @Test
    public void shouldBeMockable() throws Exception {
        FetchPartitionDetailsMessage mockFetchPartitionDetailsMessage = Mockito.mock(FetchPartitionDetailsMessage.class);
        ClusterDistributionManager mockDistributionManager = Mockito.mock(ClusterDistributionManager.class);
        PartitionedRegion mockPartitionedRegion = Mockito.mock(PartitionedRegion.class);
        long startTime = System.currentTimeMillis();
        Object key = new Object();
        Mockito.when(mockFetchPartitionDetailsMessage.operateOnPartitionedRegion(ArgumentMatchers.eq(mockDistributionManager), ArgumentMatchers.eq(mockPartitionedRegion), ArgumentMatchers.eq(startTime))).thenReturn(true);
        assertThat(mockFetchPartitionDetailsMessage.operateOnPartitionedRegion(mockDistributionManager, mockPartitionedRegion, startTime)).isTrue();
    }
}

