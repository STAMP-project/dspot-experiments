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
package org.apache.geode.internal.cache;


import org.apache.geode.distributed.internal.ClusterDistributionManager;
import org.apache.geode.internal.cache.DistributedCacheOperation.CacheOperationMessage;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CacheOperationMessageTest {
    @Test
    public void shouldBeMockable() throws Exception {
        CacheOperationMessage mockCacheOperationMessage = Mockito.mock(CacheOperationMessage.class);
        ClusterDistributionManager mockDistributionManager = Mockito.mock(ClusterDistributionManager.class);
        Mockito.when(mockCacheOperationMessage.supportsDirectAck()).thenReturn(true);
        Mockito.when(mockCacheOperationMessage._mayAddToMultipleSerialGateways(ArgumentMatchers.eq(mockDistributionManager))).thenReturn(true);
        mockCacheOperationMessage.process(mockDistributionManager);
        Mockito.verify(mockCacheOperationMessage, Mockito.times(1)).process(mockDistributionManager);
        assertThat(mockCacheOperationMessage.supportsDirectAck()).isTrue();
        assertThat(mockCacheOperationMessage._mayAddToMultipleSerialGateways(mockDistributionManager)).isTrue();
    }
}

