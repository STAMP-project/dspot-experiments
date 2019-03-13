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


import TXManagerImpl.NOTX;
import org.apache.geode.distributed.internal.DistributionManager;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.cache.PartitionedRegion;
import org.apache.geode.internal.cache.TXId;
import org.apache.geode.internal.cache.TXManagerImpl;
import org.apache.geode.internal.cache.TXStateProxy;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;


public class FetchKeysMessageTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private InternalCache cache;

    @Mock
    private DistributionManager distributionManager;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private InternalDistributedSystem distributedSystem;

    @Mock
    private InternalDistributedMember recipient;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private PartitionedRegion region;

    @Mock
    private TXStateProxy txStateProxy;

    @Captor
    private ArgumentCaptor<FetchKeysMessage> sentMessage;

    private TXManagerImpl originalTxManager;

    private TXManagerImpl txManager;

    @Test
    public void sendsWithTransactionPaused_ifTransactionIsHostedLocally() throws Exception {
        // Transaction is locally hosted
        Mockito.when(txStateProxy.isRealDealLocal()).thenReturn(true);
        Mockito.when(txStateProxy.isDistTx()).thenReturn(false);
        FetchKeysMessage.send(recipient, region, 1, false);
        InOrder inOrder = Mockito.inOrder(txManager, distributionManager);
        inOrder.verify(txManager, Mockito.times(1)).pauseTransaction();
        inOrder.verify(distributionManager, Mockito.times(1)).putOutgoing(sentMessage.capture());
        inOrder.verify(txManager, Mockito.times(1)).unpauseTransaction(ArgumentMatchers.same(txStateProxy));
        assertThat(sentMessage.getValue().getTXUniqId()).isEqualTo(NOTX);
    }

    @Test
    public void sendsWithoutPausingTransaction_ifTransactionIsNotHostedLocally() throws Exception {
        // Transaction is not locally hosted
        Mockito.when(txStateProxy.isRealDealLocal()).thenReturn(false);
        int uniqueId = 99;
        TXId txID = new TXId(recipient, uniqueId);
        Mockito.when(txStateProxy.getTxId()).thenReturn(txID);
        FetchKeysMessage.send(recipient, region, 1, false);
        Mockito.verify(distributionManager, Mockito.times(1)).putOutgoing(sentMessage.capture());
        assertThat(sentMessage.getValue().getTXUniqId()).isEqualTo(uniqueId);
        Mockito.verify(txManager, Mockito.never()).pauseTransaction();
        Mockito.verify(txManager, Mockito.never()).unpauseTransaction(ArgumentMatchers.any());
    }
}

