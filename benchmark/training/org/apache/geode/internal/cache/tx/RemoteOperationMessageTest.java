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


import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.cache.RegionDestroyedException;
import org.apache.geode.cache.TransactionException;
import org.apache.geode.distributed.DistributedSystemDisconnectedException;
import org.apache.geode.distributed.internal.ClusterDistributionManager;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.apache.geode.distributed.internal.ReplyException;
import org.apache.geode.distributed.internal.ReplyProcessor21;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.GemFireCacheImpl;
import org.apache.geode.internal.cache.LocalRegion;
import org.apache.geode.internal.cache.RemoteOperationException;
import org.apache.geode.internal.cache.TXManagerImpl;
import org.apache.geode.internal.cache.TXStateProxy;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class RemoteOperationMessageTest {
    private RemoteOperationMessageTest.TestableRemoteOperationMessage msg;// the class under test


    private InternalDistributedMember recipient;

    private InternalDistributedMember sender;

    private final String regionPath = "regionPath";

    private ReplyProcessor21 processor;

    private GemFireCacheImpl cache;

    private InternalDistributedSystem system;

    private ClusterDistributionManager dm;

    private LocalRegion r;

    private TXManagerImpl txMgr;

    private long startTime = 0;

    private TXStateProxy tx;

    @Test
    public void messageWithNoTXPerformsOnRegion() throws Exception {
        Mockito.when(txMgr.masqueradeAs(msg)).thenReturn(null);
        msg.setSender(sender);
        msg.process(dm);
        Mockito.verify(msg, Mockito.times(1)).operateOnRegion(dm, r, startTime);
        Mockito.verify(dm, Mockito.times(1)).putOutgoing(ArgumentMatchers.any());
    }

    @Test
    public void messageForNotFinishedTXPerformsOnRegion() throws Exception {
        Mockito.when(txMgr.masqueradeAs(msg)).thenReturn(tx);
        Mockito.when(tx.isInProgress()).thenReturn(true);
        msg.setSender(sender);
        msg.process(dm);
        Mockito.verify(msg, Mockito.times(1)).operateOnRegion(dm, r, startTime);
        Mockito.verify(dm, Mockito.times(1)).putOutgoing(ArgumentMatchers.any());
    }

    @Test
    public void messageForFinishedTXDoesNotPerformOnRegion() throws Exception {
        Mockito.when(txMgr.masqueradeAs(msg)).thenReturn(tx);
        Mockito.when(tx.isInProgress()).thenReturn(false);
        msg.setSender(sender);
        msg.process(dm);
        Mockito.verify(msg, Mockito.times(0)).operateOnRegion(dm, r, startTime);
        // A reply is sent even though we do not call operationOnRegion
        Mockito.verify(dm, Mockito.times(1)).putOutgoing(ArgumentMatchers.any());
    }

    @Test
    public void messageForFinishedTXRepliesWithException() throws Exception {
        Mockito.when(txMgr.masqueradeAs(msg)).thenReturn(tx);
        Mockito.when(tx.isInProgress()).thenReturn(false);
        msg.setSender(sender);
        msg.process(dm);
        Mockito.verify(msg, Mockito.times(1)).sendReply(ArgumentMatchers.eq(sender), ArgumentMatchers.eq(0), ArgumentMatchers.eq(dm), ArgumentMatchers.argThat(( ex) -> (ex != null) && ((ex.getCause()) instanceof TransactionException)), ArgumentMatchers.eq(r), ArgumentMatchers.eq(startTime));
    }

    @Test
    public void noNewTxProcessingAfterTXManagerImplClosed() throws Exception {
        Mockito.when(txMgr.masqueradeAs(msg)).thenReturn(tx);
        Mockito.when(txMgr.isClosed()).thenReturn(true);
        msg.setSender(sender);
        msg.process(dm);
        Mockito.verify(msg, Mockito.times(0)).operateOnRegion(dm, r, startTime);
        // If we do not respond what prevents the sender from waiting forever?
        Mockito.verify(dm, Mockito.times(0)).putOutgoing(ArgumentMatchers.any());
    }

    @Test
    public void processWithNullCacheSendsReplyContainingCacheClosedException() throws Exception {
        Mockito.when(dm.getExistingCache()).thenReturn(null);
        msg.setSender(sender);
        msg.process(dm);
        Mockito.verify(msg, Mockito.times(0)).operateOnRegion(dm, r, startTime);
        Mockito.verify(dm, Mockito.times(1)).putOutgoing(ArgumentMatchers.any());
        ArgumentCaptor<ReplyException> captor = ArgumentCaptor.forClass(ReplyException.class);
        Mockito.verify(msg, Mockito.times(1)).sendReply(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(dm), captor.capture(), ArgumentMatchers.any(), ArgumentMatchers.eq(startTime));
        assertThat(captor.getValue().getCause()).isInstanceOf(CacheClosedException.class);
    }

    @Test
    public void processWithDisconnectingDSAndClosedCacheSendsReplyContainingCachesClosedException() throws Exception {
        CacheClosedException reasonCacheWasClosed = Mockito.mock(CacheClosedException.class);
        Mockito.when(system.isDisconnecting()).thenReturn(true);
        Mockito.when(cache.getCacheClosedException(ArgumentMatchers.any())).thenReturn(reasonCacheWasClosed);
        msg.setSender(sender);
        msg.process(dm);
        Mockito.verify(msg, Mockito.times(0)).operateOnRegion(dm, r, startTime);
        Mockito.verify(dm, Mockito.times(1)).putOutgoing(ArgumentMatchers.any());
        ArgumentCaptor<ReplyException> captor = ArgumentCaptor.forClass(ReplyException.class);
        Mockito.verify(msg, Mockito.times(1)).sendReply(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(dm), captor.capture(), ArgumentMatchers.any(), ArgumentMatchers.eq(startTime));
        assertThat(captor.getValue().getCause()).isSameAs(reasonCacheWasClosed);
    }

    @Test
    public void processWithNullPointerExceptionFromOperationOnRegionWithNoSystemFailureSendsReplyWithNPE() throws Exception {
        Mockito.when(msg.operateOnRegion(dm, r, startTime)).thenThrow(NullPointerException.class);
        checkForSystemFailure();
        msg.setSender(sender);
        msg.process(dm);
        Mockito.verify(dm, Mockito.times(1)).putOutgoing(ArgumentMatchers.any());
        ArgumentCaptor<ReplyException> captor = ArgumentCaptor.forClass(ReplyException.class);
        Mockito.verify(msg, Mockito.times(1)).sendReply(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(dm), captor.capture(), ArgumentMatchers.eq(r), ArgumentMatchers.eq(startTime));
        assertThat(captor.getValue().getCause()).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void processWithNullPointerExceptionFromOperationOnRegionWithNoSystemFailureAndIsDisconnectingSendsReplyWithRemoteOperationException() throws Exception {
        Mockito.when(msg.operateOnRegion(dm, r, startTime)).thenThrow(NullPointerException.class);
        checkForSystemFailure();
        Mockito.when(system.isDisconnecting()).thenReturn(false).thenReturn(true);
        msg.setSender(sender);
        msg.process(dm);
        Mockito.verify(dm, Mockito.times(1)).putOutgoing(ArgumentMatchers.any());
        ArgumentCaptor<ReplyException> captor = ArgumentCaptor.forClass(ReplyException.class);
        Mockito.verify(msg, Mockito.times(1)).sendReply(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(dm), captor.capture(), ArgumentMatchers.eq(r), ArgumentMatchers.eq(startTime));
        assertThat(captor.getValue().getCause()).isInstanceOf(RemoteOperationException.class);
    }

    @Test
    public void processWithRegionDestroyedExceptionFromOperationOnRegionSendsReplyWithSameRegionDestroyedException() throws Exception {
        RegionDestroyedException ex = Mockito.mock(RegionDestroyedException.class);
        Mockito.when(msg.operateOnRegion(dm, r, startTime)).thenThrow(ex);
        msg.setSender(sender);
        msg.process(dm);
        Mockito.verify(dm, Mockito.times(1)).putOutgoing(ArgumentMatchers.any());
        ArgumentCaptor<ReplyException> captor = ArgumentCaptor.forClass(ReplyException.class);
        Mockito.verify(msg, Mockito.times(1)).sendReply(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(dm), captor.capture(), ArgumentMatchers.eq(r), ArgumentMatchers.eq(startTime));
        assertThat(captor.getValue().getCause()).isSameAs(ex);
    }

    @Test
    public void processWithRegionDoesNotExistSendsReplyWithRegionDestroyedExceptionReply() throws Exception {
        Mockito.when(cache.getRegionByPathForProcessing(regionPath)).thenReturn(null);
        msg.setSender(sender);
        msg.process(dm);
        Mockito.verify(msg, Mockito.never()).operateOnRegion(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyLong());
        Mockito.verify(dm, Mockito.times(1)).putOutgoing(ArgumentMatchers.any());
        ArgumentCaptor<ReplyException> captor = ArgumentCaptor.forClass(ReplyException.class);
        Mockito.verify(msg, Mockito.times(1)).sendReply(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(dm), captor.capture(), ArgumentMatchers.eq(null), ArgumentMatchers.eq(startTime));
        assertThat(captor.getValue().getCause()).isInstanceOf(RegionDestroyedException.class);
    }

    @Test
    public void processWithDistributedSystemDisconnectedExceptionFromOperationOnRegionDoesNotSendReply() throws Exception {
        Mockito.when(msg.operateOnRegion(dm, r, startTime)).thenThrow(DistributedSystemDisconnectedException.class);
        msg.setSender(sender);
        msg.process(dm);
        Mockito.verify(dm, Mockito.never()).putOutgoing(ArgumentMatchers.any());
    }

    @Test
    public void processWithOperateOnRegionReturningFalseDoesNotSendReply() throws Exception {
        msg.setOperationOnRegionResult(false);
        msg.setSender(sender);
        msg.process(dm);
        Mockito.verify(dm, Mockito.never()).putOutgoing(ArgumentMatchers.any());
    }

    @Test
    public void processWithRemoteOperationExceptionFromOperationOnRegionSendsReplyWithSameRemoteOperationException() throws Exception {
        RemoteOperationException theException = Mockito.mock(RemoteOperationException.class);
        Mockito.when(msg.operateOnRegion(dm, r, startTime)).thenThrow(theException);
        msg.setSender(sender);
        msg.process(dm);
        Mockito.verify(dm, Mockito.times(1)).putOutgoing(ArgumentMatchers.any());
        ArgumentCaptor<ReplyException> captor = ArgumentCaptor.forClass(ReplyException.class);
        Mockito.verify(msg, Mockito.times(1)).sendReply(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(dm), captor.capture(), ArgumentMatchers.eq(r), ArgumentMatchers.eq(startTime));
        assertThat(captor.getValue().getCause()).isSameAs(theException);
    }

    @Test
    public void processWithNullPointerExceptionFromOperationOnRegionWithSystemFailureSendsReplyWithRemoteOperationException() throws Exception {
        Mockito.when(msg.operateOnRegion(dm, r, startTime)).thenThrow(NullPointerException.class);
        checkForSystemFailure();
        msg.setSender(sender);
        assertThatThrownBy(() -> msg.process(dm)).isInstanceOf(RuntimeException.class).hasMessage("SystemFailure");
        Mockito.verify(dm, Mockito.times(1)).putOutgoing(ArgumentMatchers.any());
        ArgumentCaptor<ReplyException> captor = ArgumentCaptor.forClass(ReplyException.class);
        Mockito.verify(msg, Mockito.times(1)).sendReply(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(dm), captor.capture(), ArgumentMatchers.eq(r), ArgumentMatchers.eq(startTime));
        assertThat(captor.getValue().getCause()).isInstanceOf(RemoteOperationException.class).hasMessageContaining("system failure");
    }

    private static class TestableRemoteOperationMessage extends RemoteOperationMessage {
        private boolean operationOnRegionResult = true;

        public TestableRemoteOperationMessage(InternalDistributedMember recipient, String regionPath, ReplyProcessor21 processor) {
            super(recipient, regionPath, processor);
        }

        @Override
        public int getDSFID() {
            return 0;
        }

        @Override
        protected boolean operateOnRegion(ClusterDistributionManager dm, LocalRegion r, long startTime) throws RemoteOperationException {
            return operationOnRegionResult;
        }

        public void setOperationOnRegionResult(boolean v) {
            this.operationOnRegionResult = v;
        }
    }
}

