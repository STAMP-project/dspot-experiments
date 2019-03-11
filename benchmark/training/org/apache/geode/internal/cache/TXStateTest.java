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


import Status.STATUS_COMMITTED;
import Status.STATUS_ROLLEDBACK;
import org.apache.geode.cache.CommitConflictException;
import org.apache.geode.cache.EntryNotFoundException;
import org.apache.geode.cache.FailedSynchronizationException;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.SynchronizationCommitConflictException;
import org.apache.geode.cache.TransactionDataNodeHasDepartedException;
import org.apache.geode.cache.TransactionException;
import org.junit.Test;
import org.mockito.Mockito;


public class TXStateTest {
    private TXStateProxyImpl txStateProxy;

    private CommitConflictException exception;

    private TransactionDataNodeHasDepartedException transactionDataNodeHasDepartedException;

    private SingleThreadJTAExecutor executor;

    @Test
    public void doBeforeCompletionThrowsIfReserveAndCheckFails() {
        TXState txState = Mockito.spy(new TXState(txStateProxy, true));
        Mockito.doThrow(exception).when(txState).reserveAndCheck();
        assertThatThrownBy(() -> txState.doBeforeCompletion()).isInstanceOf(SynchronizationCommitConflictException.class);
    }

    @Test
    public void doAfterCompletionThrowsIfCommitFails() {
        TXState txState = Mockito.spy(new TXState(txStateProxy, true));
        txState.reserveAndCheck();
        Mockito.doThrow(transactionDataNodeHasDepartedException).when(txState).commit();
        assertThatThrownBy(() -> txState.doAfterCompletionCommit()).isSameAs(transactionDataNodeHasDepartedException);
    }

    @Test
    public void doAfterCompletionCanCommitJTA() {
        TXState txState = Mockito.spy(new TXState(txStateProxy, false));
        txState.reserveAndCheck();
        txState.closed = true;
        txState.doAfterCompletionCommit();
        assertThat(txState.locks).isNull();
        Mockito.verify(txState, Mockito.times(1)).saveTXCommitMessageForClientFailover();
    }

    @Test(expected = FailedSynchronizationException.class)
    public void afterCompletionThrowsExceptionIfBeforeCompletionNotCalled() {
        TXState txState = new TXState(txStateProxy, true);
        txState.afterCompletion(STATUS_COMMITTED);
    }

    @Test
    public void afterCompletionInvokesExecuteAfterCompletionCommitIfBeforeCompletionCalled() {
        TXState txState = Mockito.spy(new TXState(txStateProxy, true, executor));
        Mockito.doReturn(true).when(txState).wasBeforeCompletionCalled();
        txState.afterCompletion(STATUS_COMMITTED);
        Mockito.verify(executor, Mockito.times(1)).executeAfterCompletionCommit();
    }

    @Test
    public void afterCompletionThrowsWithUnexpectedStatusIfBeforeCompletionCalled() {
        TXState txState = Mockito.spy(new TXState(txStateProxy, true, executor));
        Mockito.doReturn(true).when(txState).wasBeforeCompletionCalled();
        Throwable thrown = catchThrowable(() -> txState.afterCompletion(Status.STATUS_NO_TRANSACTION));
        assertThat(thrown).isInstanceOf(TransactionException.class);
    }

    @Test
    public void afterCompletionInvokesExecuteAfterCompletionRollbackIfBeforeCompletionCalled() {
        TXState txState = Mockito.spy(new TXState(txStateProxy, true, executor));
        Mockito.doReturn(true).when(txState).wasBeforeCompletionCalled();
        txState.afterCompletion(STATUS_ROLLEDBACK);
        Mockito.verify(executor, Mockito.times(1)).executeAfterCompletionRollback();
    }

    @Test
    public void afterCompletionCanRollbackJTA() {
        TXState txState = Mockito.spy(new TXState(txStateProxy, true));
        txState.afterCompletion(STATUS_ROLLEDBACK);
        Mockito.verify(txState, Mockito.times(1)).rollback();
        Mockito.verify(txState, Mockito.times(1)).saveTXCommitMessageForClientFailover();
    }

    @Test
    public void closeWillCleanupIfLocksObtained() {
        TXState txState = Mockito.spy(new TXState(txStateProxy, false));
        txState.closed = false;
        txState.locks = Mockito.mock(TXLockRequest.class);
        TXRegionState regionState1 = Mockito.mock(TXRegionState.class);
        TXRegionState regionState2 = Mockito.mock(TXRegionState.class);
        InternalRegion region1 = Mockito.mock(InternalRegion.class);
        InternalRegion region2 = Mockito.mock(InternalRegion.class);
        txState.regions.put(region1, regionState1);
        txState.regions.put(region2, regionState2);
        Mockito.doReturn(Mockito.mock(InternalCache.class)).when(txState).getCache();
        txState.close();
        assertThat(txState.closed).isEqualTo(true);
        Mockito.verify(txState, Mockito.times(1)).cleanup();
        Mockito.verify(regionState1, Mockito.times(1)).cleanup(region1);
        Mockito.verify(regionState2, Mockito.times(1)).cleanup(region2);
    }

    @Test
    public void closeWillCloseTXRegionStatesIfLocksNotObtained() {
        TXState txState = Mockito.spy(new TXState(txStateProxy, false));
        txState.closed = false;
        // txState.locks = mock(TXLockRequest.class);
        TXRegionState regionState1 = Mockito.mock(TXRegionState.class);
        TXRegionState regionState2 = Mockito.mock(TXRegionState.class);
        InternalRegion region1 = Mockito.mock(InternalRegion.class);
        InternalRegion region2 = Mockito.mock(InternalRegion.class);
        txState.regions.put(region1, regionState1);
        txState.regions.put(region2, regionState2);
        Mockito.doReturn(Mockito.mock(InternalCache.class)).when(txState).getCache();
        txState.close();
        assertThat(txState.closed).isEqualTo(true);
        Mockito.verify(txState, Mockito.never()).cleanup();
        Mockito.verify(regionState1, Mockito.times(1)).close();
        Mockito.verify(regionState2, Mockito.times(1)).close();
    }

    @Test
    public void getOriginatingMemberReturnsNullIfNotOriginatedFromClient() {
        TXState txState = Mockito.spy(new TXState(txStateProxy, false));
        assertThat(txState.getOriginatingMember()).isSameAs(txStateProxy.getOnBehalfOfClientMember());
    }

    @Test
    public void txReadEntryDoesNotCleanupTXEntriesIfRegionCreateReadEntryReturnsNull() {
        TXState txState = Mockito.spy(new TXState(txStateProxy, true));
        KeyInfo keyInfo = Mockito.mock(KeyInfo.class);
        Object key = new Object();
        InternalRegion internalRegion = Mockito.mock(InternalRegion.class);
        InternalRegion dataRegion = Mockito.mock(InternalRegion.class);
        TXRegionState txRegionState = Mockito.mock(TXRegionState.class);
        Mockito.when(internalRegion.getDataRegionForWrite(keyInfo)).thenReturn(dataRegion);
        Mockito.when(txState.txReadRegion(dataRegion)).thenReturn(txRegionState);
        Mockito.when(keyInfo.getKey()).thenReturn(key);
        Mockito.when(txRegionState.readEntry(key)).thenReturn(null);
        Mockito.when(dataRegion.createReadEntry(txRegionState, keyInfo, false)).thenReturn(null);
        assertThat(txState.txReadEntry(keyInfo, internalRegion, true, null, false)).isNull();
        Mockito.verify(txRegionState, Mockito.never()).cleanupNonDirtyEntries(dataRegion);
    }

    @Test
    public void txReadEntryDoesNotCleanupTXEntriesIfEntryNotFound() {
        TXState txState = Mockito.spy(new TXState(txStateProxy, true));
        KeyInfo keyInfo = Mockito.mock(KeyInfo.class);
        Object key = new Object();
        Object expectedValue = new Object();
        InternalRegion internalRegion = Mockito.mock(InternalRegion.class);
        InternalRegion dataRegion = Mockito.mock(InternalRegion.class);
        TXRegionState txRegionState = Mockito.mock(TXRegionState.class);
        TXEntryState txEntryState = Mockito.mock(TXEntryState.class);
        Mockito.when(internalRegion.getDataRegionForWrite(keyInfo)).thenReturn(dataRegion);
        Mockito.when(internalRegion.getAttributes()).thenReturn(Mockito.mock(RegionAttributes.class));
        Mockito.when(internalRegion.getCache()).thenReturn(Mockito.mock(InternalCache.class));
        Mockito.when(txState.txReadRegion(dataRegion)).thenReturn(txRegionState);
        Mockito.when(keyInfo.getKey()).thenReturn(key);
        Mockito.when(txRegionState.readEntry(key)).thenReturn(txEntryState);
        Mockito.when(txEntryState.getNearSidePendingValue()).thenReturn("currentVal");
        assertThatThrownBy(() -> txState.txReadEntry(keyInfo, internalRegion, true, expectedValue, false)).isInstanceOf(EntryNotFoundException.class);
        Mockito.verify(txRegionState, Mockito.never()).cleanupNonDirtyEntries(internalRegion);
    }
}

