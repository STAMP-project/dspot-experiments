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
import org.apache.geode.distributed.internal.ReplyProcessor21;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.junit.Test;
import org.mockito.Mockito;


public class JtaBeforeCompletionMessageTest {
    @Test
    public void testBeforeCompletionNotInvokedIfJTACompleted() throws Exception {
        InternalCache cache = Mockito.mock(InternalCache.class);
        TXManagerImpl txMgr = Mockito.mock(TXManagerImpl.class);
        ClusterDistributionManager distributionManager = Mockito.mock(ClusterDistributionManager.class);
        TXId txId = Mockito.mock(TXId.class);
        Mockito.when(distributionManager.getCache()).thenReturn(cache);
        Mockito.when(cache.getTXMgr()).thenReturn(txMgr);
        Mockito.when(txMgr.isHostedTxRecentlyCompleted(txId)).thenReturn(true);
        Mockito.when(txMgr.getTXState()).thenReturn(Mockito.mock(TXStateProxyImpl.class));
        JtaBeforeCompletionMessage message = new JtaBeforeCompletionMessage(1, Mockito.mock(InternalDistributedMember.class), Mockito.mock(ReplyProcessor21.class));
        message.operateOnTx(txId, distributionManager);
        Mockito.verify(txMgr, Mockito.never()).getTXState();
    }
}

