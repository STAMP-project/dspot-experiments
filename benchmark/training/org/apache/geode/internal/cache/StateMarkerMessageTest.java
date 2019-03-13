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


import org.apache.geode.GemFireIOException;
import org.apache.geode.distributed.internal.ClusterDistributionManager;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.StateFlushOperation.StateMarkerMessage;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class StateMarkerMessageTest {
    @Test
    public void shouldBeMockable() throws Exception {
        StateMarkerMessage mockStateMarkerMessage = Mockito.mock(StateMarkerMessage.class);
        Mockito.when(mockStateMarkerMessage.getProcessorType()).thenReturn(1);
        assertThat(mockStateMarkerMessage.getProcessorType()).isEqualTo(1);
    }

    @Test
    public void testProcessWithWaitForCurrentOperationsThatTimesOut() {
        InternalDistributedMember relayRecipient = Mockito.mock(InternalDistributedMember.class);
        ClusterDistributionManager dm = Mockito.mock(ClusterDistributionManager.class);
        InternalCache gfc = Mockito.mock(InternalCache.class);
        DistributedRegion region = Mockito.mock(DistributedRegion.class);
        CacheDistributionAdvisor distributionAdvisor = Mockito.mock(CacheDistributionAdvisor.class);
        Mockito.when(dm.getDistributionManagerId()).thenReturn(relayRecipient);
        Mockito.when(dm.getExistingCache()).thenReturn(gfc);
        Mockito.when(region.isInitialized()).thenReturn(true);
        Mockito.when(region.getDistributionAdvisor()).thenReturn(distributionAdvisor);
        Mockito.when(gfc.getRegionByPathForProcessing(ArgumentMatchers.any())).thenReturn(region);
        Mockito.doThrow(new GemFireIOException("expected in fatal log message")).when(distributionAdvisor).waitForCurrentOperations();
        StateMarkerMessage message = new StateMarkerMessage();
        message.relayRecipient = relayRecipient;
        message.process(dm);
        Mockito.verify(dm, Mockito.times(1)).putOutgoing(ArgumentMatchers.any());
    }
}

