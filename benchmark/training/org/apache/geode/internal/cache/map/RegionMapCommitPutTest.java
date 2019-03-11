/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.geode.internal.cache.map;


import EnumListenerEvent.AFTER_UPDATE;
import Operation.CREATE;
import Operation.SEARCH_CREATE;
import Operation.UPDATE;
import Token.INVALID;
import Token.LOCAL_INVALID;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.geode.cache.Operation;
import org.apache.geode.cache.TransactionId;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.DistributedRegion;
import org.apache.geode.internal.cache.EntryEventImpl;
import org.apache.geode.internal.cache.InternalRegion;
import org.apache.geode.internal.cache.LocalRegion;
import org.apache.geode.internal.cache.RegionClearedException;
import org.apache.geode.internal.cache.RegionEntry;
import org.apache.geode.internal.cache.TXEntryState;
import org.apache.geode.internal.cache.TXRmtEvent;
import org.apache.geode.internal.cache.versions.VersionTag;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class RegionMapCommitPutTest {
    private final InternalRegion internalRegion = Mockito.mock(InternalRegion.class);

    private final FocusedRegionMap focusedRegionMap = Mockito.mock(FocusedRegionMap.class);

    @SuppressWarnings("rawtypes")
    private final Map entryMap = Mockito.mock(Map.class);

    private final EntryEventImpl event = Mockito.mock(EntryEventImpl.class);

    private final RegionEntry regionEntry = Mockito.mock(RegionEntry.class);

    private final TransactionId transactionId = Mockito.mock(TransactionId.class);

    private final TXRmtEvent txRmtEvent = Mockito.mock(TXRmtEvent.class);

    private final List<EntryEventImpl> pendingCallbacks = new ArrayList<>();

    private final TXEntryState localTxEntryState = Mockito.mock(TXEntryState.class);

    private final InternalDistributedMember myId = Mockito.mock(InternalDistributedMember.class);

    private final InternalDistributedMember remoteId = Mockito.mock(InternalDistributedMember.class);

    private final Object key = "theKey";

    private final Object newValue = "newValue";

    private RegionMapCommitPut instance;

    @Test
    public void localCreateIsNotRemoteOrigin() {
        createInstance(CREATE, false, null, localTxEntryState);
        assertThat(instance.isRemoteOrigin()).isFalse();
    }

    @Test
    public void remoteCreateIsRemoteOrigin() {
        Mockito.when(transactionId.getMemberId()).thenReturn(remoteId);
        createInstance(CREATE, false, txRmtEvent, null);
        assertThat(instance.isRemoteOrigin()).isTrue();
    }

    @Test
    public void localCreateIsNotOnlyExisting() {
        Mockito.when(transactionId.getMemberId()).thenReturn(myId);
        createInstance(CREATE, false, null, localTxEntryState);
        assertThat(instance.isOnlyExisting()).isFalse();
    }

    @Test
    public void remoteCreateWithNotAllEventsIsOnlyExisting() {
        Mockito.when(transactionId.getMemberId()).thenReturn(remoteId);
        Mockito.when(internalRegion.isAllEvents()).thenReturn(false);
        createInstance(CREATE, false, txRmtEvent, null);
        assertThat(instance.isOnlyExisting()).isTrue();
    }

    @Test
    public void remoteCreateWithLocalTxEntryStateIsNotOnlyExisting() {
        Mockito.when(transactionId.getMemberId()).thenReturn(remoteId);
        createInstance(CREATE, false, txRmtEvent, localTxEntryState);
        assertThat(instance.isOnlyExisting()).isFalse();
    }

    @Test
    public void remoteCreateWithAllEventsIsNotOnlyExisting() {
        Mockito.when(transactionId.getMemberId()).thenReturn(remoteId);
        Mockito.when(internalRegion.isAllEvents()).thenReturn(true);
        createInstance(CREATE, false, txRmtEvent, null);
        assertThat(instance.isOnlyExisting()).isFalse();
    }

    @Test
    public void remoteUpdateWithAllEventsIsNotOnlyExisting() {
        Mockito.when(transactionId.getMemberId()).thenReturn(remoteId);
        Mockito.when(internalRegion.isAllEvents()).thenReturn(true);
        createInstance(UPDATE, false, txRmtEvent, null);
        assertThat(instance.isOnlyExisting()).isFalse();
    }

    @Test
    public void uninitializedNonPartitionedRegionDoesNotInvokeCallbacks() {
        Mockito.when(internalRegion.shouldDispatchListenerEvent()).thenReturn(true);
        Mockito.when(internalRegion.shouldNotifyBridgeClients()).thenReturn(true);
        Mockito.when(internalRegion.getConcurrencyChecksEnabled()).thenReturn(true);
        createInstance(CREATE, false, null, localTxEntryState);
        assertThat(instance.isInvokeCallbacks()).isFalse();
    }

    // 
    @Test
    public void uninitializedPartitionedRegionDoesNotInvokeCallbacks() {
        Mockito.when(internalRegion.isUsedForPartitionedRegionBucket()).thenReturn(true);
        Mockito.when(internalRegion.getPartitionedRegion()).thenReturn(Mockito.mock(LocalRegion.class));
        createInstance(CREATE, false, null, localTxEntryState);
        assertThat(instance.isInvokeCallbacks()).isFalse();
    }

    @Test
    public void initializedNonPartitionedRegionWithFalseAttributesDoesNotInvokeCallbacks() {
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        createInstance(CREATE, false, null, localTxEntryState);
        assertThat(instance.isInvokeCallbacks()).isFalse();
    }

    @Test
    public void initializedNonPartitionedRegionWithShouldDispatchListenerEventDoesInvokeCallbacks() {
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        Mockito.when(internalRegion.shouldDispatchListenerEvent()).thenReturn(true);
        createInstance(CREATE, false, null, localTxEntryState);
        assertThat(instance.isInvokeCallbacks()).isTrue();
    }

    @Test
    public void initializedNonPartitionedRegionWithShouldNotifyBridgeClientsDoesInvokeCallbacks() {
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        Mockito.when(internalRegion.shouldNotifyBridgeClients()).thenReturn(true);
        createInstance(CREATE, false, null, localTxEntryState);
        assertThat(instance.isInvokeCallbacks()).isTrue();
    }

    @Test
    public void initializedNonPartitionedRegionWithConcurrencyChecksEnabledDoesInvokeCallbacks() {
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        Mockito.when(internalRegion.getConcurrencyChecksEnabled()).thenReturn(true);
        createInstance(CREATE, false, null, localTxEntryState);
        assertThat(instance.isInvokeCallbacks()).isTrue();
    }

    @Test
    public void uninitializedPartitionedRegionWithFalseAttributesDoesNotInvokeCallbacks() {
        Mockito.when(internalRegion.isUsedForPartitionedRegionBucket()).thenReturn(true);
        Mockito.when(internalRegion.getPartitionedRegion()).thenReturn(Mockito.mock(LocalRegion.class));
        createInstance(CREATE, false, null, localTxEntryState);
        assertThat(instance.isInvokeCallbacks()).isFalse();
    }

    @Test
    public void uninitializedPartitionedRegionWithShouldDispatchListenerEventDoesInvokeCallbacks() {
        Mockito.when(internalRegion.isUsedForPartitionedRegionBucket()).thenReturn(true);
        LocalRegion partitionedRegion = Mockito.mock(LocalRegion.class);
        Mockito.when(internalRegion.getPartitionedRegion()).thenReturn(partitionedRegion);
        Mockito.when(partitionedRegion.shouldDispatchListenerEvent()).thenReturn(true);
        createInstance(CREATE, false, null, localTxEntryState);
        assertThat(instance.isInvokeCallbacks()).isTrue();
    }

    @Test
    public void uninitializedPartitionedRegionWithShouldNotifyBridgeClientsDoesInvokeCallbacks() {
        Mockito.when(internalRegion.isUsedForPartitionedRegionBucket()).thenReturn(true);
        LocalRegion partitionedRegion = Mockito.mock(LocalRegion.class);
        Mockito.when(internalRegion.getPartitionedRegion()).thenReturn(partitionedRegion);
        Mockito.when(partitionedRegion.shouldNotifyBridgeClients()).thenReturn(true);
        createInstance(CREATE, false, null, localTxEntryState);
        assertThat(instance.isInvokeCallbacks()).isTrue();
    }

    @Test
    public void uninitializedPartitionedRegionWithConcurrencyChecksEnabledDoesInvokeCallbacks() {
        Mockito.when(internalRegion.isUsedForPartitionedRegionBucket()).thenReturn(true);
        LocalRegion partitionedRegion = Mockito.mock(LocalRegion.class);
        Mockito.when(internalRegion.getPartitionedRegion()).thenReturn(partitionedRegion);
        Mockito.when(partitionedRegion.getConcurrencyChecksEnabled()).thenReturn(true);
        createInstance(CREATE, false, null, localTxEntryState);
        assertThat(instance.isInvokeCallbacks()).isTrue();
    }

    @Test
    public void remoteUpdateWithAllEventsAndInitializedIsOnlyExisting() {
        Mockito.when(transactionId.getMemberId()).thenReturn(remoteId);
        Mockito.when(internalRegion.isAllEvents()).thenReturn(true);
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        createInstance(UPDATE, false, txRmtEvent, null);
        assertThat(instance.isOnlyExisting()).isTrue();
    }

    @Test
    public void successfulPutCallsUpdateStatsForPut() {
        createInstance(CREATE, false, null, localTxEntryState);
        RegionEntry result = instance.put();
        assertThat(result).isSameAs(regionEntry);
        final long lastModifiedTime = instance.getLastModifiedTime();
        Mockito.verify(regionEntry, Mockito.times(1)).updateStatsForPut(ArgumentMatchers.eq(lastModifiedTime), ArgumentMatchers.eq(lastModifiedTime));
    }

    @Test
    public void doBeforeCompletionActionsCallsTxApplyPutPart2() {
        final boolean didDestroy = true;
        createInstance(CREATE, didDestroy, null, localTxEntryState);
        instance.put();
        final long lastModifiedTime = instance.getLastModifiedTime();
        Mockito.verify(internalRegion, Mockito.times(1)).txApplyPutPart2(ArgumentMatchers.eq(regionEntry), ArgumentMatchers.eq(key), ArgumentMatchers.eq(lastModifiedTime), ArgumentMatchers.eq(true), ArgumentMatchers.eq(didDestroy), ArgumentMatchers.eq(false));
    }

    @Test
    public void putThatDoesNotAddEventToPendingCallbacksCallsEventRelease() {
        createInstance(UPDATE, false, txRmtEvent, null);
        instance.put();
        assertThat(instance.isCallbackEventInPending()).isFalse();
        assertThat(pendingCallbacks).doesNotContain(event);
        Mockito.verify(event, Mockito.never()).changeRegionToBucketsOwner();
        Mockito.verify(event, Mockito.never()).setOriginRemote(ArgumentMatchers.anyBoolean());
        Mockito.verify(event, Mockito.times(1)).release();
    }

    @Test
    public void putThatDoesAddEventToPendingCallbacksDoesNotCallEventRelease() {
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        Mockito.when(internalRegion.getConcurrencyChecksEnabled()).thenReturn(true);
        RegionEntry existingEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(focusedRegionMap.getEntry(ArgumentMatchers.eq(event))).thenReturn(existingEntry);
        createInstance(UPDATE, false, txRmtEvent, null);
        RegionEntry result = instance.put();
        assertThat(result).isNotNull();
        Mockito.verify(event, Mockito.never()).release();
    }

    @Test
    public void putThatDoesNotInvokesCallbacksDoesNotAddToPendingCallbacks() {
        createInstance(UPDATE, false, txRmtEvent, null);
        instance.put();
        assertThat(instance.isCallbackEventInPending()).isFalse();
        assertThat(pendingCallbacks).doesNotContain(event);
        Mockito.verify(event, Mockito.never()).changeRegionToBucketsOwner();
        Mockito.verify(event, Mockito.never()).setOriginRemote(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void putThatInvokesCallbacksAddsToPendingCallbacks() {
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        Mockito.when(internalRegion.getConcurrencyChecksEnabled()).thenReturn(true);
        RegionEntry existingEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(focusedRegionMap.getEntry(ArgumentMatchers.eq(event))).thenReturn(existingEntry);
        createInstance(UPDATE, false, txRmtEvent, null);
        RegionEntry result = instance.put();
        assertThat(result).isNotNull();
        assertThat(instance.isCallbackEventInPending()).isTrue();
        assertThat(pendingCallbacks).contains(event);
        Mockito.verify(event, Mockito.times(1)).changeRegionToBucketsOwner();
        Mockito.verify(event, Mockito.times(1)).setOriginRemote(instance.isRemoteOrigin());
    }

    @Test
    public void updateThatDoesNotSeeClearCallsLruEntryUpdate() {
        RegionEntry existingEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(focusedRegionMap.getEntry(ArgumentMatchers.eq(event))).thenReturn(existingEntry);
        createInstance(UPDATE, false, txRmtEvent, null);
        instance.put();
        Mockito.verify(focusedRegionMap, Mockito.times(1)).lruEntryUpdate(existingEntry);
    }

    @Test
    public void createThatDoesNotSeeClearCallsLruEntryCreate() {
        createInstance(CREATE, false, null, localTxEntryState);
        instance.put();
        Mockito.verify(focusedRegionMap, Mockito.times(1)).lruEntryCreate(regionEntry);
        Mockito.verify(focusedRegionMap, Mockito.times(1)).incEntryCount(1);
    }

    @Test
    public void createCallsUpdateSizeOnCreate() {
        final int newSize = 79;
        Mockito.when(internalRegion.calculateRegionEntryValueSize(ArgumentMatchers.eq(regionEntry))).thenReturn(newSize);
        createInstance(CREATE, false, null, localTxEntryState);
        instance.put();
        Mockito.verify(internalRegion, Mockito.times(1)).updateSizeOnCreate(ArgumentMatchers.eq(key), ArgumentMatchers.eq(newSize));
    }

    @Test
    public void updateCallsUpdateSizeOnPut() {
        final int oldSize = 12;
        final int newSize = 79;
        RegionEntry existingEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(focusedRegionMap.getEntry(ArgumentMatchers.eq(event))).thenReturn(existingEntry);
        Mockito.when(internalRegion.calculateRegionEntryValueSize(ArgumentMatchers.eq(existingEntry))).thenReturn(oldSize).thenReturn(newSize);
        createInstance(UPDATE, false, null, localTxEntryState);
        instance.put();
        Mockito.verify(internalRegion, Mockito.times(1)).updateSizeOnPut(ArgumentMatchers.eq(key), ArgumentMatchers.eq(oldSize), ArgumentMatchers.eq(newSize));
    }

    @Test
    public void createThatDoesSeeClearDoesNotMakeLruCalls() throws RegionClearedException {
        Mockito.doThrow(RegionClearedException.class).when(regionEntry).setValue(ArgumentMatchers.any(), ArgumentMatchers.any());
        createInstance(CREATE, false, null, localTxEntryState);
        instance.put();
        assertThat(instance.isClearOccurred()).isTrue();
        Mockito.verify(focusedRegionMap, Mockito.never()).lruEntryUpdate(ArgumentMatchers.any());
        Mockito.verify(focusedRegionMap, Mockito.never()).lruEntryCreate(ArgumentMatchers.any());
        Mockito.verify(focusedRegionMap, Mockito.never()).incEntryCount(1);
    }

    @Test
    public void putWithoutConcurrencyChecksEnabledDoesNotCallSetVersionTag() {
        createInstance(UPDATE, false, null, localTxEntryState);
        instance.put();
        Mockito.verify(localTxEntryState, Mockito.never()).setVersionTag(ArgumentMatchers.any());
    }

    @Test
    public void putWithConcurrencyChecksEnabledDoesCallSetVersionTag() {
        Mockito.when(internalRegion.getConcurrencyChecksEnabled()).thenReturn(true);
        VersionTag versionTag = Mockito.mock(VersionTag.class);
        Mockito.when(event.getVersionTag()).thenReturn(versionTag);
        createInstance(UPDATE, false, null, localTxEntryState);
        instance.put();
        Mockito.verify(localTxEntryState, Mockito.times(1)).setVersionTag(ArgumentMatchers.eq(versionTag));
    }

    @Test
    public void failedUpdateWithDidDestroyDoesCallTxApplyPutHandleDidDestroy() {
        Mockito.when(transactionId.getMemberId()).thenReturn(remoteId);
        Mockito.when(internalRegion.getConcurrencyChecksEnabled()).thenReturn(true);
        createInstance(UPDATE, true, txRmtEvent, null);
        RegionEntry result = instance.put();
        assertThat(result).isNull();
        assertThat(instance.isOnlyExisting()).isTrue();
        Mockito.verify(internalRegion, Mockito.times(1)).txApplyPutHandleDidDestroy(ArgumentMatchers.eq(key));
    }

    @Test
    public void successfulUpdateWithDidDestroyDoesNotCallTxApplyPutHandleDidDestroy() {
        Mockito.when(transactionId.getMemberId()).thenReturn(remoteId);
        Mockito.when(internalRegion.getConcurrencyChecksEnabled()).thenReturn(true);
        RegionEntry existingEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(focusedRegionMap.getEntry(ArgumentMatchers.eq(event))).thenReturn(existingEntry);
        createInstance(UPDATE, true, txRmtEvent, null);
        RegionEntry result = instance.put();
        assertThat(result).isNotNull();
        assertThat(instance.isOnlyExisting()).isTrue();
        Mockito.verify(internalRegion, Mockito.never()).txApplyPutHandleDidDestroy(ArgumentMatchers.any());
    }

    @Test
    public void failedUpdateDoesCallInvokeTXCallbacks() {
        Mockito.when(transactionId.getMemberId()).thenReturn(remoteId);
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        Mockito.when(internalRegion.getConcurrencyChecksEnabled()).thenReturn(true);
        createInstance(UPDATE, false, txRmtEvent, null);
        RegionEntry result = instance.put();
        assertThat(result).isNull();
        assertThat(instance.isOnlyExisting()).isTrue();
        assertThat(instance.isInvokeCallbacks()).isTrue();
        Mockito.verify(event, Mockito.times(1)).makeUpdate();
        Mockito.verify(internalRegion, Mockito.times(1)).invokeTXCallbacks(ArgumentMatchers.eq(AFTER_UPDATE), ArgumentMatchers.eq(event), ArgumentMatchers.eq(false));
    }

    @Test
    public void successfulUpdateDoesNotCallInvokeTXCallbacks() {
        Mockito.when(transactionId.getMemberId()).thenReturn(remoteId);
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        Mockito.when(internalRegion.getConcurrencyChecksEnabled()).thenReturn(true);
        RegionEntry existingEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(focusedRegionMap.getEntry(ArgumentMatchers.eq(event))).thenReturn(existingEntry);
        createInstance(UPDATE, false, txRmtEvent, null);
        RegionEntry result = instance.put();
        assertThat(result).isNotNull();
        assertThat(instance.isOnlyExisting()).isTrue();
        assertThat(instance.isInvokeCallbacks()).isTrue();
        Mockito.verify(internalRegion, Mockito.never()).invokeTXCallbacks(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void localUpdateSetsOldValueOnEvent() {
        Object oldValue = new Object();
        RegionEntry existingEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(existingEntry.getValueInVM(ArgumentMatchers.any())).thenReturn(oldValue);
        Mockito.when(focusedRegionMap.getEntry(ArgumentMatchers.eq(event))).thenReturn(existingEntry);
        createInstance(UPDATE, false, null, localTxEntryState);
        RegionEntry result = instance.put();
        assertThat(result).isSameAs(existingEntry);
        Mockito.verify(event, Mockito.times(1)).setOldValue(oldValue);
        Mockito.verify(existingEntry, Mockito.never()).txDidDestroy(ArgumentMatchers.anyLong());
    }

    @Test
    public void localUpdateThatAlsoDidDestroyCallsTxDidDestroy() {
        Object oldValue = new Object();
        RegionEntry existingEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(existingEntry.getValueInVM(ArgumentMatchers.any())).thenReturn(oldValue);
        Mockito.when(focusedRegionMap.getEntry(ArgumentMatchers.eq(event))).thenReturn(existingEntry);
        final long lastModifiedTime = 123L;
        Mockito.when(internalRegion.cacheTimeMillis()).thenReturn(lastModifiedTime);
        createInstance(UPDATE, true, null, localTxEntryState);
        RegionEntry result = instance.put();
        assertThat(result).isSameAs(existingEntry);
        Mockito.verify(event, Mockito.times(1)).setOldValue(oldValue);
        Mockito.verify(existingEntry, Mockito.times(1)).txDidDestroy(lastModifiedTime);
    }

    @Test
    public void localCreateDoesSetsOldValueToNullOnEvent() {
        createInstance(CREATE, false, null, localTxEntryState);
        RegionEntry result = instance.put();
        assertThat(result).isSameAs(regionEntry);
        Mockito.verify(event, Mockito.times(1)).setOldValue(null);
    }

    @Test
    public void localCreateCallsProcessAndGenerateTXVersionTag() {
        createInstance(SEARCH_CREATE, false, null, localTxEntryState);
        RegionEntry result = instance.put();
        assertThat(result).isSameAs(regionEntry);
        Mockito.verify(focusedRegionMap, Mockito.times(1)).processAndGenerateTXVersionTag(ArgumentMatchers.eq(event), ArgumentMatchers.eq(regionEntry), ArgumentMatchers.eq(localTxEntryState));
    }

    @Test
    public void localSearchCreateCallsSetValueResultOfSearchWithTrue() {
        createInstance(SEARCH_CREATE, false, null, localTxEntryState);
        RegionEntry result = instance.put();
        assertThat(result).isSameAs(regionEntry);
        Mockito.verify(regionEntry, Mockito.times(1)).setValueResultOfSearch(true);
    }

    @Test
    public void remoteUpdateWithOnlyExistingSucceeds() throws Exception {
        Mockito.when(internalRegion.isAllEvents()).thenReturn(true);
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        Object oldValue = new Object();
        RegionEntry existingEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(existingEntry.getValueInVM(ArgumentMatchers.any())).thenReturn(oldValue);
        Mockito.when(focusedRegionMap.getEntry(ArgumentMatchers.eq(event))).thenReturn(existingEntry);
        Mockito.when(existingEntry.prepareValueForCache(ArgumentMatchers.any(), ArgumentMatchers.eq(newValue), ArgumentMatchers.eq(event), ArgumentMatchers.eq(true))).thenReturn(newValue);
        createInstance(UPDATE, false, txRmtEvent, null);
        RegionEntry result = instance.put();
        assertThat(result).isSameAs(existingEntry);
        Mockito.verify(existingEntry, Mockito.times(1)).setValue(ArgumentMatchers.eq(internalRegion), ArgumentMatchers.eq(newValue));
    }

    @Test
    public void remoteUpdateWithOnlyExistingCallsAddPut() throws Exception {
        Mockito.when(internalRegion.isAllEvents()).thenReturn(true);
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        Object oldValue = new Object();
        RegionEntry existingEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(existingEntry.getValueInVM(ArgumentMatchers.any())).thenReturn(oldValue);
        Mockito.when(focusedRegionMap.getEntry(ArgumentMatchers.eq(event))).thenReturn(existingEntry);
        Mockito.when(existingEntry.prepareValueForCache(ArgumentMatchers.any(), ArgumentMatchers.eq(newValue), ArgumentMatchers.eq(event), ArgumentMatchers.eq(true))).thenReturn(newValue);
        final Object callbackArgument = "callbackArgument";
        Mockito.when(event.getCallbackArgument()).thenReturn(callbackArgument);
        createInstance(UPDATE, false, txRmtEvent, null);
        RegionEntry result = instance.put();
        assertThat(result).isSameAs(existingEntry);
        Mockito.verify(txRmtEvent, Mockito.times(1)).addPut(ArgumentMatchers.eq(UPDATE), ArgumentMatchers.eq(internalRegion), ArgumentMatchers.eq(existingEntry), ArgumentMatchers.eq(key), ArgumentMatchers.eq(newValue), ArgumentMatchers.eq(callbackArgument));
    }

    @Test
    public void remoteUpdateWithInvalidateWithOnlyExistingSucceeds() throws Exception {
        Mockito.when(internalRegion.isAllEvents()).thenReturn(true);
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        Object oldValue = new Object();
        RegionEntry existingEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(existingEntry.getValueInVM(ArgumentMatchers.any())).thenReturn(oldValue);
        Mockito.when(focusedRegionMap.getEntry(ArgumentMatchers.eq(event))).thenReturn(existingEntry);
        Mockito.when(event.getRawNewValueAsHeapObject()).thenReturn(null);
        Mockito.when(existingEntry.prepareValueForCache(ArgumentMatchers.any(), ArgumentMatchers.eq(INVALID), ArgumentMatchers.eq(event), ArgumentMatchers.eq(true))).thenReturn(INVALID);
        createInstance(UPDATE, false, txRmtEvent, null);
        RegionEntry result = instance.put();
        assertThat(result).isSameAs(existingEntry);
        Mockito.verify(existingEntry, Mockito.times(1)).setValue(ArgumentMatchers.eq(internalRegion), ArgumentMatchers.eq(INVALID));
    }

    @Test
    public void remoteUpdateWithLocalInvalidateWithOnlyExistingSucceeds() throws Exception {
        Mockito.when(internalRegion.isAllEvents()).thenReturn(true);
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        Object oldValue = new Object();
        RegionEntry existingEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(existingEntry.getValueInVM(ArgumentMatchers.any())).thenReturn(oldValue);
        Mockito.when(focusedRegionMap.getEntry(ArgumentMatchers.eq(event))).thenReturn(existingEntry);
        Mockito.when(event.getRawNewValueAsHeapObject()).thenReturn(null);
        Mockito.when(event.isLocalInvalid()).thenReturn(true);
        Mockito.when(existingEntry.prepareValueForCache(ArgumentMatchers.any(), ArgumentMatchers.eq(LOCAL_INVALID), ArgumentMatchers.eq(event), ArgumentMatchers.eq(true))).thenReturn(LOCAL_INVALID);
        createInstance(UPDATE, false, txRmtEvent, null);
        RegionEntry result = instance.put();
        assertThat(result).isSameAs(existingEntry);
        Mockito.verify(existingEntry, Mockito.times(1)).setValue(ArgumentMatchers.eq(internalRegion), ArgumentMatchers.eq(LOCAL_INVALID));
    }

    @Test
    public void remoteUpdateWithOnlyExistingOnRemovedEntryFails() {
        Mockito.when(transactionId.getMemberId()).thenReturn(remoteId);
        Mockito.when(internalRegion.isAllEvents()).thenReturn(true);
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        Object oldValue = new Object();
        RegionEntry existingEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(existingEntry.getValueInVM(ArgumentMatchers.any())).thenReturn(oldValue);
        Mockito.when(existingEntry.isDestroyedOrRemoved()).thenReturn(false).thenReturn(true);
        Mockito.when(focusedRegionMap.getEntry(ArgumentMatchers.eq(event))).thenReturn(existingEntry);
        createInstance(UPDATE, false, txRmtEvent, null);
        RegionEntry result = instance.put();
        assertThat(result).isNull();
    }

    @Test
    public void putThatUpdatesTombstoneCallsUnscheduleTombstone() {
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        Mockito.when(internalRegion.getConcurrencyChecksEnabled()).thenReturn(true);
        RegionEntry existingEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(existingEntry.isTombstone()).thenReturn(true);
        Mockito.when(focusedRegionMap.getEntry(ArgumentMatchers.eq(event))).thenReturn(existingEntry);
        createInstance(UPDATE, false, txRmtEvent, null);
        RegionEntry result = instance.put();
        assertThat(result).isNotNull();
        Mockito.verify(internalRegion, Mockito.times(1)).unscheduleTombstone(ArgumentMatchers.eq(existingEntry));
    }

    @Test
    public void entryExistsWithNullReturnsFalse() {
        createInstance(UPDATE, false, txRmtEvent, null);
        boolean result = instance.entryExists(null);
        assertThat(result).isFalse();
    }

    @Test
    public void entryExistsWithRemovedEntryReturnsFalse() {
        createInstance(UPDATE, false, txRmtEvent, null);
        RegionEntry regionEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(regionEntry.isDestroyedOrRemoved()).thenReturn(true);
        boolean result = instance.entryExists(regionEntry);
        assertThat(result).isFalse();
    }

    @Test
    public void entryExistsWithExistingEntryReturnsTrue() {
        createInstance(UPDATE, false, txRmtEvent, null);
        RegionEntry regionEntry = Mockito.mock(RegionEntry.class);
        boolean result = instance.entryExists(regionEntry);
        assertThat(result).isTrue();
    }

    @Test
    public void runWileLockedForCacheModificationDoesNotLockGIIClearLockWhenRegionIsInitialized() throws Exception {
        DistributedRegion region = Mockito.mock(DistributedRegion.class);
        Mockito.when(region.isInitialized()).thenReturn(true);
        Mockito.when(region.lockWhenRegionIsInitializing()).thenCallRealMethod();
        RegionMapCommitPut regionMapCommitPut = new RegionMapCommitPut(focusedRegionMap, region, event, Operation.UPDATE, false, transactionId, txRmtEvent, pendingCallbacks, null);
        regionMapCommitPut.runWhileLockedForCacheModification(() -> {
        });
        Mockito.verify(region).lockWhenRegionIsInitializing();
        assertThat(region.lockWhenRegionIsInitializing()).isFalse();
        Mockito.verify(region, Mockito.never()).unlockWhenRegionIsInitializing();
    }

    @Test
    public void runWileLockedForCacheModificationLockGIIClearLockWhenRegionIsInitializing() {
        DistributedRegion region = Mockito.mock(DistributedRegion.class);
        Mockito.when(region.isInitialized()).thenReturn(false);
        Mockito.when(region.lockWhenRegionIsInitializing()).thenCallRealMethod();
        RegionMapCommitPut regionMapCommitPut = new RegionMapCommitPut(focusedRegionMap, region, event, Operation.UPDATE, false, transactionId, txRmtEvent, pendingCallbacks, null);
        regionMapCommitPut.runWhileLockedForCacheModification(() -> {
        });
        Mockito.verify(region).lockWhenRegionIsInitializing();
        assertThat(region.lockWhenRegionIsInitializing()).isTrue();
        Mockito.verify(region).unlockWhenRegionIsInitializing();
    }
}

