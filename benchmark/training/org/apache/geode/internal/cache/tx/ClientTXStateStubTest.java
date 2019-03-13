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
package org.apache.geode.internal.cache.tx;


import org.apache.geode.CancelCriterion;
import org.apache.geode.CancelException;
import org.apache.geode.InternalGemFireError;
import org.apache.geode.cache.client.internal.InternalPool;
import org.apache.geode.cache.client.internal.ServerRegionProxy;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.internal.DistributionManager;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.cache.LocalRegion;
import org.apache.geode.internal.cache.TXCommitMessage;
import org.apache.geode.internal.cache.TXLockRequest;
import org.apache.geode.internal.cache.TXManagerImpl;
import org.apache.geode.internal.cache.TXRegionLockRequestImpl;
import org.apache.geode.internal.cache.TXStateProxy;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ClientTXStateStubTest {
    private InternalCache cache;

    private DistributionManager dm;

    private TXStateProxy stateProxy;

    private DistributedMember target;

    private LocalRegion region;

    private ServerRegionProxy serverRegionProxy;

    private CancelCriterion cancelCriterion;

    private InternalPool internalPool;

    @Test
    public void commitThrowsCancelExceptionIfCacheIsClosed() {
        ClientTXStateStub stub = Mockito.spy(new ClientTXStateStub(cache, dm, stateProxy, target, region));
        Mockito.when(stub.createTXLockRequest()).thenReturn(Mockito.mock(TXLockRequest.class));
        Mockito.when(stub.createTXRegionLockRequestImpl(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mockito.mock(TXRegionLockRequestImpl.class));
        assertThatThrownBy(() -> stub.commit()).isInstanceOf(CancelException.class);
    }

    @Test
    public void commitReleasesServerAffinityAfterCommit() {
        TXCommitMessage txCommitMessage = Mockito.mock(TXCommitMessage.class);
        TXManagerImpl txManager = Mockito.mock(TXManagerImpl.class);
        Mockito.when(cache.getTxManager()).thenReturn(txManager);
        Mockito.when(serverRegionProxy.commit(ArgumentMatchers.anyInt())).thenReturn(txCommitMessage);
        Mockito.doNothing().when(cancelCriterion).checkCancelInProgress(null);
        Mockito.doNothing().when(txManager).setTXState(null);
        ClientTXStateStub stub = Mockito.spy(new ClientTXStateStub(cache, dm, stateProxy, target, region));
        InOrder order = Mockito.inOrder(serverRegionProxy, internalPool, cancelCriterion);
        stub.commit();
        order.verify(serverRegionProxy).commit(ArgumentMatchers.anyInt());
        order.verify(internalPool).releaseServerAffinity();
        order.verify(cancelCriterion).checkCancelInProgress(null);
    }

    @Test
    public void commitReleasesServerAffinity_whenCommitThrowsAnException() {
        TXManagerImpl txManager = Mockito.mock(TXManagerImpl.class);
        Mockito.when(cache.getTxManager()).thenReturn(txManager);
        Mockito.when(serverRegionProxy.commit(ArgumentMatchers.anyInt())).thenThrow(new InternalGemFireError());
        Mockito.doNothing().when(cancelCriterion).checkCancelInProgress(null);
        Mockito.doNothing().when(txManager).setTXState(null);
        ClientTXStateStub stub = Mockito.spy(new ClientTXStateStub(cache, dm, stateProxy, target, region));
        InOrder order = Mockito.inOrder(serverRegionProxy, internalPool);
        assertThatThrownBy(() -> stub.commit()).isInstanceOf(InternalGemFireError.class);
        order.verify(serverRegionProxy).commit(ArgumentMatchers.anyInt());
        order.verify(internalPool).releaseServerAffinity();
    }
}

