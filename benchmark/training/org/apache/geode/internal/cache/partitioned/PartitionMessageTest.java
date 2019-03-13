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


import org.apache.geode.cache.TransactionException;
import org.apache.geode.distributed.internal.ClusterDistributionManager;
import org.apache.geode.distributed.internal.DistributionAdvisor;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.GemFireCacheImpl;
import org.apache.geode.internal.cache.PartitionedRegion;
import org.apache.geode.internal.cache.TXManagerImpl;
import org.apache.geode.internal.cache.TXStateProxy;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class PartitionMessageTest {
    private GemFireCacheImpl cache;

    private PartitionMessage msg;

    private ClusterDistributionManager dm;

    private PartitionedRegion pr;

    private TXManagerImpl txMgr;

    private long startTime = 1;

    private TXStateProxy tx;

    private DistributionAdvisor advisor;

    @Test
    public void shouldBeMockable() throws Exception {
        PartitionMessage mockPartitionMessage = Mockito.mock(PartitionMessage.class);
        InternalDistributedMember mockInternalDistributedMember = Mockito.mock(InternalDistributedMember.class);
        Mockito.when(mockPartitionMessage.getMemberToMasqueradeAs()).thenReturn(mockInternalDistributedMember);
        assertThat(mockPartitionMessage.getMemberToMasqueradeAs()).isSameAs(mockInternalDistributedMember);
    }

    @Test
    public void messageWithNoTXPerformsOnRegion() throws Exception {
        Mockito.when(txMgr.masqueradeAs(msg)).thenReturn(null);
        msg.process(dm);
        Mockito.verify(msg, Mockito.times(1)).operateOnPartitionedRegion(dm, pr, startTime);
    }

    @Test
    public void messageForNotFinishedTXPerformsOnRegion() throws Exception {
        Mockito.when(txMgr.masqueradeAs(msg)).thenReturn(tx);
        Mockito.when(tx.isInProgress()).thenReturn(true);
        msg.process(dm);
        Mockito.verify(msg, Mockito.times(1)).operateOnPartitionedRegion(dm, pr, startTime);
    }

    @Test
    public void messageForFinishedTXDoesNotPerformOnRegion() throws Exception {
        Mockito.when(txMgr.masqueradeAs(msg)).thenReturn(tx);
        Mockito.when(tx.isInProgress()).thenReturn(false);
        msg.process(dm);
        Mockito.verify(msg, Mockito.times(0)).operateOnPartitionedRegion(dm, pr, startTime);
    }

    @Test
    public void messageForFinishedTXRepliesWithException() throws Exception {
        Mockito.when(txMgr.masqueradeAs(msg)).thenReturn(tx);
        Mockito.when(tx.isInProgress()).thenReturn(false);
        msg.process(dm);
        Mockito.verify(msg, Mockito.times(1)).sendReply(ArgumentMatchers.isNull(), ArgumentMatchers.eq(0), ArgumentMatchers.eq(dm), ArgumentMatchers.argThat(( ex) -> (ex != null) && ((ex.getCause()) instanceof TransactionException)), ArgumentMatchers.eq(pr), ArgumentMatchers.eq(startTime));
    }

    @Test
    public void noNewTxProcessingAfterTXManagerImplClosed() throws Exception {
        txMgr = new TXManagerImpl(null, cache);
        Mockito.when(msg.getPartitionedRegion()).thenReturn(pr);
        Mockito.when(msg.getStartPartitionMessageProcessingTime(pr)).thenReturn(startTime);
        Mockito.when(msg.getTXManagerImpl(cache)).thenReturn(txMgr);
        Mockito.when(msg.canParticipateInTransaction()).thenReturn(true);
        Mockito.when(msg.canStartRemoteTransaction()).thenReturn(true);
        msg.process(dm);
        txMgr.close();
        msg.process(dm);
        Mockito.verify(msg, Mockito.times(1)).operateOnPartitionedRegion(dm, pr, startTime);
    }
}

