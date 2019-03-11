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


import Operation.CREATE;
import Scope.DISTRIBUTED_ACK;
import Token.DESTROYED;
import Token.NOT_AVAILABLE;
import Token.TOMBSTONE;
import java.util.Set;
import org.apache.geode.cache.CacheWriter;
import org.apache.geode.cache.DiskAccessException;
import org.apache.geode.internal.cache.DistributedRegion;
import org.apache.geode.internal.cache.EntryEventImpl;
import org.apache.geode.internal.cache.EntryEventSerialization;
import org.apache.geode.internal.cache.InternalRegion;
import org.apache.geode.internal.cache.RegionClearedException;
import org.apache.geode.internal.cache.RegionEntry;
import org.apache.geode.internal.cache.versions.ConcurrentCacheModificationException;
import org.apache.geode.internal.cache.versions.VersionStamp;
import org.apache.geode.internal.cache.versions.VersionTag;
import org.apache.geode.internal.cache.wan.GatewaySenderEventImpl;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class RegionMapPutTest {
    private final InternalRegion internalRegion = Mockito.mock(InternalRegion.class);

    private final FocusedRegionMap focusedRegionMap = Mockito.mock(FocusedRegionMap.class);

    private final CacheModificationLock cacheModificationLock = Mockito.mock(CacheModificationLock.class);

    private final EntryEventSerialization entryEventSerialization = Mockito.mock(EntryEventSerialization.class);

    private final RegionEntry createdRegionEntry = Mockito.mock(RegionEntry.class);

    private final EntryEventImpl event = Mockito.mock(EntryEventImpl.class);

    private boolean ifNew = false;

    private boolean ifOld = false;

    private boolean requireOldValue = false;

    private Object expectedOldValue = null;

    private boolean overwriteDestroyed = false;

    private RegionMapPut instance;

    private final RegionEntry existingRegionEntry = Mockito.mock(RegionEntry.class);

    @Test
    public void doesNotSetEventOldValueToExistingRegionEntryValue_ifNotRequired() {
        givenExistingRegionEntry();
        givenAnOperationThatDoesNotGuaranteeOldValue();
        givenPutDoesNotNeedToDoCacheWrite();
        this.requireOldValue = false;
        Object oldValue = new Object();
        Mockito.when(existingRegionEntry.getValue()).thenReturn(oldValue);
        doPut();
        Mockito.verify(event, Mockito.never()).setOldValue(ArgumentMatchers.any());
        Mockito.verify(event, Mockito.never()).setOldValue(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void setsEventOldValueToExistingRegionEntryValue_ifOldValueIsGatewaySenderEvent() {
        givenExistingRegionEntry();
        GatewaySenderEventImpl oldValue = new GatewaySenderEventImpl();
        Mockito.when(existingRegionEntry.getValue()).thenReturn(oldValue);
        doPut();
        Mockito.verify(event, Mockito.times(1)).setOldValue(ArgumentMatchers.same(oldValue), ArgumentMatchers.eq(true));
        Mockito.verify(event, Mockito.never()).setOldValue(AdditionalMatchers.not(ArgumentMatchers.same(oldValue)), ArgumentMatchers.eq(true));
    }

    @Test
    public void setsEventOldValueToExistingRegionEntryValue_ifOperationGuaranteesOldValue() {
        givenExistingRegionEntry();
        givenAnOperationThatGuaranteesOldValue();
        Object oldValue = new Object();
        Mockito.when(existingRegionEntry.getValueOffHeapOrDiskWithoutFaultIn(ArgumentMatchers.same(internalRegion))).thenReturn(oldValue);
        doPut();
        Mockito.verify(event, Mockito.times(1)).setOldValue(ArgumentMatchers.same(oldValue), ArgumentMatchers.eq(true));
        Mockito.verify(event, Mockito.never()).setOldValue(AdditionalMatchers.not(ArgumentMatchers.same(oldValue)), ArgumentMatchers.eq(true));
    }

    @Test
    public void eventPutExistingEntryGivenOldValue_ifRetrieveOldValueForDelta() throws RegionClearedException {
        givenThatRunWhileEvictionDisabledCallsItsRunnable();
        givenExistingRegionEntry();
        Object oldValue = new Object();
        Mockito.when(existingRegionEntry.getValue(ArgumentMatchers.any())).thenReturn(oldValue);
        Mockito.when(event.getDeltaBytes()).thenReturn(new byte[1]);
        doPut();
        Mockito.verify(event, Mockito.times(1)).putExistingEntry(ArgumentMatchers.same(internalRegion), ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.eq(false), ArgumentMatchers.same(oldValue));
    }

    @Test
    public void eventPutExistingEntryGivenNullOldValue_ifNotRetrieveOldValueForDelta() throws RegionClearedException {
        givenThatRunWhileEvictionDisabledCallsItsRunnable();
        givenExistingRegionEntry();
        Object oldValue = new Object();
        Mockito.when(existingRegionEntry.getValue(ArgumentMatchers.any())).thenReturn(oldValue);
        Mockito.when(event.getDeltaBytes()).thenReturn(null);
        doPut();
        Mockito.verify(event, Mockito.times(1)).putExistingEntry(ArgumentMatchers.same(internalRegion), ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.eq(false), ArgumentMatchers.eq(null));
    }

    @Test
    public void setsEventOldValueToExistingRegionEntryValue_ifIsRequiredOldValueAndOperationDoesNotGuaranteeOldValue() {
        this.requireOldValue = true;
        givenExistingRegionEntry();
        givenAnOperationThatDoesNotGuaranteeOldValue();
        Object oldValue = new Object();
        Mockito.when(existingRegionEntry.getValueRetain(ArgumentMatchers.same(internalRegion), ArgumentMatchers.eq(true))).thenReturn(oldValue);
        doPut();
        Mockito.verify(event, Mockito.times(1)).setOldValue(ArgumentMatchers.same(oldValue));
        Mockito.verify(event, Mockito.never()).setOldValue(AdditionalMatchers.not(ArgumentMatchers.same(oldValue)));
    }

    @Test
    public void setsEventOldValueToExistingRegionEntryValue_ifIsCacheWriteAndOperationDoesNotGuaranteeOldValue() {
        givenExistingRegionEntry();
        givenAnOperationThatDoesNotGuaranteeOldValue();
        givenPutNeedsToDoCacheWrite();
        Object oldValue = new Object();
        Mockito.when(existingRegionEntry.getValueRetain(ArgumentMatchers.same(internalRegion), ArgumentMatchers.eq(true))).thenReturn(oldValue);
        doPut();
        Mockito.verify(event, Mockito.times(1)).setOldValue(ArgumentMatchers.same(oldValue));
        Mockito.verify(event, Mockito.never()).setOldValue(AdditionalMatchers.not(ArgumentMatchers.same(oldValue)));
    }

    @Test
    public void setsEventOldValueToNotAvailable_ifIsCacheWriteAndOperationDoesNotGuaranteeOldValue_andExistingValueIsNull() {
        givenPutNeedsToDoCacheWrite();
        givenAnOperationThatDoesNotGuaranteeOldValue();
        givenExistingRegionEntry();
        Mockito.when(existingRegionEntry.getValueRetain(ArgumentMatchers.same(internalRegion), ArgumentMatchers.eq(true))).thenReturn(null);
        doPut();
        Mockito.verify(event, Mockito.times(1)).setOldValue(NOT_AVAILABLE);
    }

    @Test
    public void retrieveOldValueForDeltaDefaultToFalse() {
        createInstance();
        assertThat(instance.isRetrieveOldValueForDelta()).isFalse();
    }

    @Test
    public void eventOldValueNotAvailableCalled_ifCacheWriteNotNeededAndNotInitialized() {
        givenPutDoesNotNeedToDoCacheWrite();
        Mockito.when(internalRegion.isInitialized()).thenReturn(false);
        doPut();
        Mockito.verify(event, Mockito.times(1)).oldValueNotAvailable();
    }

    @Test
    public void eventOperationNotSet_ifCacheWriteNeededAndInitializedAndReplaceOnClient() {
        givenPutNeedsToDoCacheWrite();
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        givenReplaceOnClient();
        doPut();
        Mockito.verify(event, Mockito.never()).makeCreate();
        Mockito.verify(event, Mockito.never()).makeUpdate();
    }

    @Test
    public void putExistingEntryCalled_ifReplaceOnClient() throws RegionClearedException {
        givenPutDoesNotNeedToDoCacheWrite();
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        givenReplaceOnClient();
        doPut();
        Mockito.verify(event, Mockito.times(1)).putExistingEntry(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
    }

    @Test
    public void eventOperationMadeCreate_ifCacheWriteNeededAndInitializedAndNotReplaceOnClientAndEntryRemoved() {
        givenPutNeedsToDoCacheWrite();
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        givenReplaceOnPeer();
        Mockito.when(createdRegionEntry.isDestroyedOrRemoved()).thenReturn(true);
        doPut();
        Mockito.verify(event, Mockito.times(1)).makeCreate();
        Mockito.verify(event, Mockito.never()).makeUpdate();
    }

    @Test
    public void eventOperationMadeUpdate_ifCacheWriteNeededAndInitializedAndNotReplaceOnClientAndEntryExists() {
        givenPutNeedsToDoCacheWrite();
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        givenReplaceOnPeer();
        Mockito.when(createdRegionEntry.isDestroyedOrRemoved()).thenReturn(false);
        doPut();
        Mockito.verify(event, Mockito.never()).makeCreate();
        Mockito.verify(event, Mockito.times(1)).makeUpdate();
    }

    @Test
    public void cacheWriteBeforePutNotCalled_ifNotInitialized() {
        givenPutNeedsToDoCacheWrite();
        Mockito.when(internalRegion.isInitialized()).thenReturn(false);
        doPut();
        Mockito.verify(internalRegion, Mockito.never()).cacheWriteBeforePut(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
    }

    @Test
    public void cacheWriteBeforePutNotCalled_ifCacheWriteNotNeeded() {
        givenPutDoesNotNeedToDoCacheWrite();
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        doPut();
        Mockito.verify(internalRegion, Mockito.never()).cacheWriteBeforePut(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
    }

    @Test
    public void cacheWriteBeforePutCalledWithRequireOldValue_givenRequireOldValueTrue() {
        givenPutNeedsToDoCacheWrite();
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        this.requireOldValue = true;
        doPut();
        Mockito.verify(internalRegion, Mockito.times(1)).cacheWriteBeforePut(ArgumentMatchers.same(event), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq(this.requireOldValue), ArgumentMatchers.eq(null));
    }

    @Test
    public void cacheWriteBeforePutCalledWithExpectedOldValue_givenRequireOldValueTrue() {
        givenPutNeedsToDoCacheWrite();
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        givenAnOperationThatGuaranteesOldValue();
        givenExistingRegionEntry();
        Mockito.when(existingRegionEntry.getValueRetain(ArgumentMatchers.same(internalRegion), ArgumentMatchers.eq(true))).thenReturn(null);
        expectedOldValue = "expectedOldValue";
        Mockito.when(event.getRawOldValue()).thenReturn(expectedOldValue);
        doPut();
        Mockito.verify(internalRegion, Mockito.times(1)).cacheWriteBeforePut(ArgumentMatchers.same(event), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.same(expectedOldValue));
    }

    @Test
    public void putWithExpectedOldValueReturnsNull_ifExistingValueIsNotExpected() {
        givenAnOperationThatGuaranteesOldValue();
        expectedOldValue = "expectedOldValue";
        Mockito.when(event.getRawOldValue()).thenReturn("unexpectedValue");
        RegionEntry result = doPut();
        assertThat(result).isNull();
    }

    @Test
    public void putWithExpectedOldValueReturnsRegionEntry_ifExistingValueIsExpected() {
        givenAnOperationThatGuaranteesOldValue();
        expectedOldValue = "expectedOldValue";
        Mockito.when(event.getRawOldValue()).thenReturn(expectedOldValue);
        RegionEntry result = doPut();
        assertThat(result).isSameAs(createdRegionEntry);
    }

    @Test
    public void putWithExpectedOldValueReturnsRegionEntry_ifExistingValueIsNotExpectedButIsReplaceOnClient() {
        givenAnOperationThatGuaranteesOldValue();
        expectedOldValue = "expectedOldValue";
        Mockito.when(event.getRawOldValue()).thenReturn("unexpectedValue");
        givenReplaceOnClient();
        RegionEntry result = doPut();
        assertThat(result).isSameAs(createdRegionEntry);
    }

    @Test
    public void cacheWriteBeforePutCalledWithCacheWriter_givenACacheWriter() {
        givenPutNeedsToDoCacheWrite();
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        CacheWriter cacheWriter = Mockito.mock(CacheWriter.class);
        Mockito.when(internalRegion.basicGetWriter()).thenReturn(cacheWriter);
        doPut();
        Mockito.verify(internalRegion, Mockito.times(1)).cacheWriteBeforePut(ArgumentMatchers.same(event), ArgumentMatchers.eq(null), ArgumentMatchers.same(cacheWriter), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(null));
    }

    @Test
    public void cacheWriteBeforePutCalledWithNetWriteRecipients_ifAdviseNetWrite() {
        givenPutNeedsToDoCacheWrite();
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        Mockito.when(internalRegion.basicGetWriter()).thenReturn(null);
        Set netWriteRecipients = Mockito.mock(Set.class);
        Mockito.when(internalRegion.adviseNetWrite()).thenReturn(netWriteRecipients);
        doPut();
        Mockito.verify(internalRegion, Mockito.times(1)).cacheWriteBeforePut(ArgumentMatchers.same(event), ArgumentMatchers.eq(netWriteRecipients), ArgumentMatchers.eq(null), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(null));
    }

    @Test
    public void retrieveOldValueForDeltaTrueIfEventHasDeltaBytes() {
        Mockito.when(event.getDeltaBytes()).thenReturn(new byte[1]);
        createInstance();
        assertThat(instance.isRetrieveOldValueForDelta()).isTrue();
    }

    @Test
    public void retrieveOldValueForDeltaFalseIfEventHasDeltaBytesAndRawNewValue() {
        Mockito.when(event.getDeltaBytes()).thenReturn(new byte[1]);
        Mockito.when(event.getRawNewValue()).thenReturn(new Object());
        createInstance();
        assertThat(instance.isRetrieveOldValueForDelta()).isFalse();
    }

    @Test
    public void replaceOnClientDefaultsToFalse() {
        createInstance();
        assertThat(instance.isReplaceOnClient()).isFalse();
    }

    @Test
    public void replaceOnClientIsTrueIfOperationIsReplaceAndOwnerIsClient() {
        givenReplaceOnClient();
        createInstance();
        assertThat(instance.isReplaceOnClient()).isTrue();
    }

    @Test
    public void replaceOnClientIsFalseIfOperationIsReplaceAndOwnerIsNotClient() {
        givenReplaceOnPeer();
        createInstance();
        assertThat(instance.isReplaceOnClient()).isFalse();
    }

    @Test
    public void putReturnsNull_ifOnlyExistingAndEntryIsTombstone() {
        ifOld = true;
        givenExistingRegionEntry();
        Mockito.when(existingRegionEntry.isTombstone()).thenReturn(true);
        RegionEntry result = doPut();
        assertThat(result).isNull();
    }

    @Test
    public void putReturnsExistingEntry_ifOnlyExistingAndEntryIsNotTombstone() {
        ifOld = true;
        givenExistingRegionEntry();
        Mockito.when(existingRegionEntry.isTombstone()).thenReturn(false);
        RegionEntry result = doPut();
        assertThat(result).isSameAs(existingRegionEntry);
    }

    @Test
    public void putReturnsNull_ifOnlyExistingAndEntryIsRemoved() {
        ifOld = true;
        givenExistingRegionEntry();
        Mockito.when(existingRegionEntry.isRemoved()).thenReturn(true);
        givenReplaceOnPeer();
        RegionEntry result = doPut();
        assertThat(result).isNull();
    }

    @Test
    public void putReturnsExistingEntry_ifOnlyExistingEntryIsRemovedAndReplaceOnClient() {
        ifOld = true;
        givenExistingRegionEntry();
        Mockito.when(existingRegionEntry.isRemoved()).thenReturn(true);
        givenReplaceOnClient();
        RegionEntry result = doPut();
        assertThat(result).isSameAs(existingRegionEntry);
    }

    @Test
    public void putReturnsExistingEntry_ifReplaceOnClientAndTombstoneButNoVersionTag() {
        ifOld = true;
        givenReplaceOnClient();
        givenExistingRegionEntry();
        Mockito.when(existingRegionEntry.isTombstone()).thenReturn(true);
        Mockito.when(event.getVersionTag()).thenReturn(null);
        RegionEntry result = doPut();
        assertThat(result).isSameAs(existingRegionEntry);
    }

    @Test
    public void putReturnsNull_ifReplaceOnClientAndTombstoneAndVersionTag() throws RegionClearedException {
        ifOld = true;
        givenReplaceOnClient();
        givenExistingRegionEntry();
        Mockito.when(existingRegionEntry.isTombstone()).thenReturn(true);
        Mockito.when(existingRegionEntry.getVersionStamp()).thenReturn(Mockito.mock(VersionStamp.class));
        Mockito.when(event.getVersionTag()).thenReturn(Mockito.mock(VersionTag.class));
        RegionEntry result = doPut();
        assertThat(result).isNull();
        Mockito.verify(existingRegionEntry, Mockito.times(1)).setValue(internalRegion, TOMBSTONE);
        Mockito.verify(internalRegion, Mockito.times(1)).rescheduleTombstone(ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.any());
    }

    @Test
    public void createWithoutOverwriteDestroyedReturnsNullAndCallsSetOldValueDestroyedToken_ifRegionUninitializedAndCurrentValueIsDestroyed() {
        overwriteDestroyed = false;
        Mockito.when(internalRegion.isInitialized()).thenReturn(false);
        Mockito.when(createdRegionEntry.getValueAsToken()).thenReturn(DESTROYED);
        RegionEntry result = doPut();
        assertThat(result).isNull();
        Mockito.verify(event, Mockito.times(1)).setOldValueDestroyedToken();
    }

    @Test
    public void createWithoutOverwriteDestroyedReturnsNullAndCallsSetOldValueDestroyedToken_ifRegionUninitializedAndCurrentValueIsTombstone() {
        overwriteDestroyed = false;
        Mockito.when(internalRegion.isInitialized()).thenReturn(false);
        Mockito.when(createdRegionEntry.getValueAsToken()).thenReturn(TOMBSTONE);
        RegionEntry result = doPut();
        assertThat(result).isNull();
        Mockito.verify(event, Mockito.times(1)).setOldValueDestroyedToken();
    }

    @Test
    public void createWithOverwriteDestroyedReturnsCreatedRegionEntry_ifRegionUninitializedAndCurrentValueIsDestroyed() {
        overwriteDestroyed = true;
        Mockito.when(internalRegion.isInitialized()).thenReturn(false);
        Mockito.when(createdRegionEntry.getValueAsToken()).thenReturn(DESTROYED);
        RegionEntry result = doPut();
        assertThat(result).isSameAs(createdRegionEntry);
        Mockito.verify(event, Mockito.never()).setOldValueDestroyedToken();
    }

    @Test
    public void putIgnoresRegionClearedException_ifReplaceOnClientAndTombstoneAndVersionTag() throws RegionClearedException {
        ifOld = true;
        givenReplaceOnClient();
        givenExistingRegionEntry();
        Mockito.when(existingRegionEntry.isTombstone()).thenReturn(true);
        Mockito.when(existingRegionEntry.getVersionStamp()).thenReturn(Mockito.mock(VersionStamp.class));
        Mockito.when(event.getVersionTag()).thenReturn(Mockito.mock(VersionTag.class));
        Mockito.doThrow(RegionClearedException.class).when(existingRegionEntry).setValue(internalRegion, TOMBSTONE);
        RegionEntry result = doPut();
        assertThat(result).isNull();
        Mockito.verify(existingRegionEntry, Mockito.times(1)).setValue(internalRegion, TOMBSTONE);
        Mockito.verify(internalRegion, Mockito.times(1)).rescheduleTombstone(ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.any());
    }

    @Test
    public void onlyExistingDefaultsToFalse() {
        createInstance();
        assertThat(instance.isOnlyExisting()).isFalse();
    }

    @Test
    public void onlyExistingIsTrueIfOld() {
        ifOld = true;
        createInstance();
        assertThat(instance.isOnlyExisting()).isTrue();
    }

    @Test
    public void onlyExistingIsFalseIfOldAndReplaceOnClient() {
        ifOld = true;
        givenReplaceOnClient();
        createInstance();
        assertThat(instance.isOnlyExisting()).isFalse();
    }

    @Test
    public void cacheWriteDefaultToFalse() {
        createInstance();
        assertThat(instance.isCacheWrite()).isFalse();
    }

    @Test
    public void cacheWriteIsFalseIfGenerateCallbacksButNotDistributedEtc() {
        Mockito.when(event.isGenerateCallbacks()).thenReturn(true);
        createInstance();
        assertThat(instance.isCacheWrite()).isFalse();
    }

    @Test
    public void cacheWriteIsTrueIfGenerateCallbacksAndDistributed() {
        Mockito.when(event.isGenerateCallbacks()).thenReturn(true);
        Mockito.when(internalRegion.getScope()).thenReturn(DISTRIBUTED_ACK);
        createInstance();
        assertThat(instance.isCacheWrite()).isTrue();
    }

    @Test
    public void cacheWriteIsTrueIfGenerateCallbacksAndServerProxy() {
        Mockito.when(event.isGenerateCallbacks()).thenReturn(true);
        Mockito.when(internalRegion.hasServerProxy()).thenReturn(true);
        createInstance();
        assertThat(instance.isCacheWrite()).isTrue();
    }

    @Test
    public void cacheWriteIsTrueIfGenerateCallbacksAndCacheWriter() {
        Mockito.when(event.isGenerateCallbacks()).thenReturn(true);
        Mockito.when(internalRegion.basicGetWriter()).thenReturn(Mockito.mock(CacheWriter.class));
        createInstance();
        assertThat(instance.isCacheWrite()).isTrue();
    }

    @Test
    public void isOriginRemoteCausesCacheWriteToBeFalse() {
        Mockito.when(event.isOriginRemote()).thenReturn(true);
        Mockito.when(event.isGenerateCallbacks()).thenReturn(true);
        Mockito.when(internalRegion.basicGetWriter()).thenReturn(Mockito.mock(CacheWriter.class));
        createInstance();
        assertThat(instance.isCacheWrite()).isFalse();
    }

    @Test
    public void netSearchCausesCacheWriteToBeFalse() {
        Mockito.when(event.isNetSearch()).thenReturn(true);
        Mockito.when(event.isGenerateCallbacks()).thenReturn(true);
        Mockito.when(internalRegion.basicGetWriter()).thenReturn(Mockito.mock(CacheWriter.class));
        createInstance();
        assertThat(instance.isCacheWrite()).isFalse();
    }

    @Test
    public void basicPutPart2ToldClearDidNotOccur_ifPutDoneWithoutAClear() throws Exception {
        ifNew = true;
        Mockito.when(event.getOperation()).thenReturn(CREATE);
        doPut();
        Mockito.verify(internalRegion, Mockito.times(1)).basicPutPart2(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(false));
    }

    @Test
    public void basicPutPart2ToldClearDidOccur_ifPutDoneWithAClear() throws Exception {
        ifNew = true;
        Mockito.when(event.getOperation()).thenReturn(CREATE);
        Mockito.doThrow(RegionClearedException.class).when(event).putNewEntry(ArgumentMatchers.any(), ArgumentMatchers.any());
        doPut();
        Mockito.verify(internalRegion, Mockito.times(1)).basicPutPart2(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true));
    }

    @Test
    public void lruUpdateCallbackCalled_ifPutDoneWithoutAClear() {
        ifNew = true;
        Mockito.when(focusedRegionMap.disableLruUpdateCallback()).thenReturn(true);
        Mockito.when(event.getOperation()).thenReturn(CREATE);
        doPut();
        Mockito.verify(focusedRegionMap, Mockito.times(1)).lruUpdateCallback();
    }

    @Test
    public void lruUpdateCallbackNotCalled_ifCallbacksNotDisabled() {
        ifNew = true;
        Mockito.when(focusedRegionMap.disableLruUpdateCallback()).thenReturn(false);
        Mockito.when(event.getOperation()).thenReturn(CREATE);
        doPut();
        Mockito.verify(focusedRegionMap, Mockito.never()).lruUpdateCallback();
    }

    @Test
    public void enableLruUpdateCallbackCalled_ifCallbacksDisabled() {
        ifNew = true;
        Mockito.when(focusedRegionMap.disableLruUpdateCallback()).thenReturn(true);
        Mockito.when(event.getOperation()).thenReturn(CREATE);
        doPut();
        Mockito.verify(focusedRegionMap, Mockito.times(1)).enableLruUpdateCallback();
    }

    @Test
    public void enableLruUpdateCallbackNotCalled_ifCallbacksNotDisabled() {
        ifNew = true;
        Mockito.when(focusedRegionMap.disableLruUpdateCallback()).thenReturn(false);
        Mockito.when(event.getOperation()).thenReturn(CREATE);
        doPut();
        Mockito.verify(focusedRegionMap, Mockito.never()).enableLruUpdateCallback();
    }

    @Test
    public void lruUpdateCallbackNotCalled_ifPutDoneWithAClear() throws Exception {
        ifNew = true;
        Mockito.when(event.getOperation()).thenReturn(CREATE);
        Mockito.when(focusedRegionMap.disableLruUpdateCallback()).thenReturn(true);
        Mockito.doThrow(RegionClearedException.class).when(event).putNewEntry(ArgumentMatchers.any(), ArgumentMatchers.any());
        doPut();
        Mockito.verify(focusedRegionMap, Mockito.never()).lruUpdateCallback();
    }

    @Test
    public void lruEnryCreateCalled_ifCreateDoneWithoutAClear() throws Exception {
        ifNew = true;
        Mockito.when(event.getOperation()).thenReturn(CREATE);
        doPut();
        Mockito.verify(focusedRegionMap, Mockito.times(1)).lruEntryCreate(createdRegionEntry);
    }

    @Test
    public void lruEnryCreateNotCalled_ifCreateDoneWithAClear() throws Exception {
        ifNew = true;
        Mockito.when(event.getOperation()).thenReturn(CREATE);
        Mockito.doThrow(RegionClearedException.class).when(event).putNewEntry(ArgumentMatchers.any(), ArgumentMatchers.any());
        doPut();
        Mockito.verify(focusedRegionMap, Mockito.never()).lruEntryCreate(createdRegionEntry);
    }

    @Test
    public void lruEnryUpdateCalled_ifUpdateDoneWithoutAClear() throws Exception {
        ifOld = true;
        RegionEntry existingRegionEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(focusedRegionMap.getEntry(event)).thenReturn(existingRegionEntry);
        doPut();
        Mockito.verify(focusedRegionMap, Mockito.times(1)).lruEntryUpdate(existingRegionEntry);
    }

    @Test
    public void lruEnryUpdateNotCalled_ifUpdateDoneWithAClear() throws Exception {
        ifOld = true;
        RegionEntry existingRegionEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(focusedRegionMap.getEntry(event)).thenReturn(existingRegionEntry);
        Mockito.doThrow(RegionClearedException.class).when(event).putExistingEntry(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
        doPut();
        Mockito.verify(focusedRegionMap, Mockito.never()).lruEntryUpdate(existingRegionEntry);
    }

    @Test
    public void putThrows_ifCreateDoneWithConcurrentCacheModificationException() throws Exception {
        ifNew = true;
        Mockito.when(event.getOperation()).thenReturn(CREATE);
        Mockito.doThrow(ConcurrentCacheModificationException.class).when(event).putNewEntry(ArgumentMatchers.any(), ArgumentMatchers.any());
        assertThatThrownBy(() -> doPut()).isInstanceOf(ConcurrentCacheModificationException.class);
        Mockito.verify(event, Mockito.times(1)).getVersionTag();
    }

    @Test
    public void putInvokesNotifyTimestampsToGateways_ifCreateDoneWithConcurrentCacheModificationException() throws Exception {
        ifNew = true;
        Mockito.when(event.getOperation()).thenReturn(CREATE);
        Mockito.doThrow(ConcurrentCacheModificationException.class).when(event).putNewEntry(ArgumentMatchers.any(), ArgumentMatchers.any());
        VersionTag versionTag = Mockito.mock(VersionTag.class);
        Mockito.when(versionTag.isTimeStampUpdated()).thenReturn(true);
        Mockito.when(event.getVersionTag()).thenReturn(versionTag);
        assertThatThrownBy(() -> doPut()).isInstanceOf(ConcurrentCacheModificationException.class);
        Mockito.verify(internalRegion, Mockito.times(1)).notifyTimestampsToGateways(ArgumentMatchers.same(event));
    }

    @Test
    public void putThrows_ifLruUpdateCallbackThrowsDiskAccessException() throws Exception {
        ifNew = true;
        Mockito.when(event.getOperation()).thenReturn(CREATE);
        Mockito.when(focusedRegionMap.disableLruUpdateCallback()).thenReturn(true);
        Mockito.doThrow(DiskAccessException.class).when(focusedRegionMap).lruUpdateCallback();
        assertThatThrownBy(() -> doPut()).isInstanceOf(DiskAccessException.class);
        Mockito.verify(internalRegion, Mockito.times(1)).handleDiskAccessException(ArgumentMatchers.any());
    }

    @Test
    public void createOnEmptyMapAddsEntry() throws Exception {
        ifNew = true;
        Mockito.when(event.getOperation()).thenReturn(CREATE);
        RegionEntry result = doPut();
        assertThat(result).isSameAs(createdRegionEntry);
        Mockito.verify(event, Mockito.times(1)).putNewEntry(internalRegion, createdRegionEntry);
        Mockito.verify(internalRegion, Mockito.times(1)).basicPutPart2(ArgumentMatchers.eq(event), ArgumentMatchers.eq(result), ArgumentMatchers.eq(true), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(false));
        Mockito.verify(internalRegion, Mockito.times(1)).basicPutPart3(ArgumentMatchers.eq(event), ArgumentMatchers.eq(result), ArgumentMatchers.eq(true), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true), ArgumentMatchers.eq(ifNew), ArgumentMatchers.eq(ifOld), ArgumentMatchers.eq(expectedOldValue), ArgumentMatchers.eq(requireOldValue));
    }

    @Test
    public void putWithTombstoneNewValue_callsBasicPutPart3WithFalse() {
        Mockito.when(event.basicGetNewValue()).thenReturn(TOMBSTONE);
        doPut();
        Mockito.verify(internalRegion, Mockito.times(1)).basicPutPart3(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(false), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void putWithNonTombstoneNewValue_callsBasicPutPart3WithTrue() {
        Mockito.when(event.basicGetNewValue()).thenReturn("newValue");
        doPut();
        Mockito.verify(internalRegion, Mockito.times(1)).basicPutPart3(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void putOnEmptyMapAddsEntry() throws Exception {
        ifNew = false;
        ifOld = false;
        Mockito.when(event.getOperation()).thenReturn(CREATE);
        RegionEntry result = doPut();
        assertThat(result).isSameAs(createdRegionEntry);
        Mockito.verify(event, Mockito.times(1)).putNewEntry(internalRegion, createdRegionEntry);
        Mockito.verify(internalRegion, Mockito.times(1)).basicPutPart2(ArgumentMatchers.eq(event), ArgumentMatchers.eq(result), ArgumentMatchers.eq(true), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(false));
        Mockito.verify(internalRegion, Mockito.times(1)).basicPutPart3(ArgumentMatchers.eq(event), ArgumentMatchers.eq(result), ArgumentMatchers.eq(true), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true), ArgumentMatchers.eq(ifNew), ArgumentMatchers.eq(ifOld), ArgumentMatchers.eq(expectedOldValue), ArgumentMatchers.eq(requireOldValue));
    }

    @Test
    public void updateOnEmptyMapReturnsNull() throws Exception {
        ifOld = true;
        RegionEntry result = doPut();
        assertThat(result).isNull();
        Mockito.verify(event, Mockito.never()).putNewEntry(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(event, Mockito.never()).putExistingEntry(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
        Mockito.verify(internalRegion, Mockito.never()).basicPutPart2(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean());
        Mockito.verify(internalRegion, Mockito.never()).basicPutPart3(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void createOnExistingEntryReturnsNull() throws RegionClearedException {
        ifNew = true;
        Mockito.when(focusedRegionMap.getEntry(event)).thenReturn(Mockito.mock(RegionEntry.class));
        Mockito.when(event.getOperation()).thenReturn(CREATE);
        RegionEntry result = doPut();
        assertThat(result).isNull();
        Mockito.verify(event, Mockito.never()).putNewEntry(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(event, Mockito.never()).putExistingEntry(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
        Mockito.verify(internalRegion, Mockito.never()).basicPutPart2(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean());
        Mockito.verify(internalRegion, Mockito.never()).basicPutPart3(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void createOnEntryReturnedFromPutIfAbsentDoesNothing() throws RegionClearedException {
        ifNew = true;
        Mockito.when(focusedRegionMap.getEntry(event)).thenReturn(Mockito.mock(RegionEntry.class));
        Mockito.when(focusedRegionMap.putEntryIfAbsent(event.getKey(), createdRegionEntry)).thenReturn(Mockito.mock(RegionEntry.class));
        Mockito.when(event.getOperation()).thenReturn(CREATE);
        RegionEntry result = doPut();
        assertThat(result).isNull();
        Mockito.verify(event, Mockito.never()).putNewEntry(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(event, Mockito.never()).putExistingEntry(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
        Mockito.verify(internalRegion, Mockito.never()).basicPutPart2(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean());
        Mockito.verify(internalRegion, Mockito.never()).basicPutPart3(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void createOnExistingEntryWithRemovePhase2DoesCreate() throws RegionClearedException {
        ifNew = true;
        RegionEntry existingRegionEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(existingRegionEntry.isRemovedPhase2()).thenReturn(true);
        Mockito.when(focusedRegionMap.putEntryIfAbsent(event.getKey(), createdRegionEntry)).thenReturn(existingRegionEntry).thenReturn(null);
        Mockito.when(event.getOperation()).thenReturn(CREATE);
        RegionEntry result = doPut();
        assertThat(result).isSameAs(createdRegionEntry);
        Mockito.verify(event, Mockito.times(1)).putNewEntry(internalRegion, createdRegionEntry);
        Mockito.verify(internalRegion, Mockito.times(1)).basicPutPart2(ArgumentMatchers.eq(event), ArgumentMatchers.eq(result), ArgumentMatchers.eq(true), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(false));
        Mockito.verify(internalRegion, Mockito.times(1)).basicPutPart3(ArgumentMatchers.eq(event), ArgumentMatchers.eq(result), ArgumentMatchers.eq(true), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true), ArgumentMatchers.eq(ifNew), ArgumentMatchers.eq(ifOld), ArgumentMatchers.eq(expectedOldValue), ArgumentMatchers.eq(requireOldValue));
    }

    @Test
    public void updateOnExistingEntryDoesUpdate() throws Exception {
        ifOld = true;
        Object updateValue = "update";
        RegionEntry existingRegionEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(focusedRegionMap.getEntry(event)).thenReturn(existingRegionEntry);
        Mockito.when(event.getNewValue()).thenReturn(updateValue);
        RegionEntry result = doPut();
        assertThat(result).isSameAs(existingRegionEntry);
        Mockito.verify(event, Mockito.times(1)).putExistingEntry(ArgumentMatchers.eq(internalRegion), ArgumentMatchers.eq(existingRegionEntry), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
        Mockito.verify(internalRegion, Mockito.times(1)).basicPutPart2(ArgumentMatchers.eq(event), ArgumentMatchers.eq(result), ArgumentMatchers.eq(true), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(false));
        Mockito.verify(internalRegion, Mockito.times(1)).basicPutPart3(ArgumentMatchers.eq(event), ArgumentMatchers.eq(result), ArgumentMatchers.eq(true), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true), ArgumentMatchers.eq(ifNew), ArgumentMatchers.eq(ifOld), ArgumentMatchers.eq(expectedOldValue), ArgumentMatchers.eq(requireOldValue));
    }

    @Test
    public void putOnExistingEntryDoesUpdate() throws Exception {
        ifOld = false;
        ifNew = false;
        Object updateValue = "update";
        RegionEntry existingRegionEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(focusedRegionMap.getEntry(event)).thenReturn(existingRegionEntry);
        Mockito.when(event.getNewValue()).thenReturn(updateValue);
        RegionEntry result = doPut();
        assertThat(result).isSameAs(existingRegionEntry);
        Mockito.verify(event, Mockito.times(1)).putExistingEntry(ArgumentMatchers.eq(internalRegion), ArgumentMatchers.eq(existingRegionEntry), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
        Mockito.verify(internalRegion, Mockito.times(1)).basicPutPart2(ArgumentMatchers.eq(event), ArgumentMatchers.eq(result), ArgumentMatchers.eq(true), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(false));
        Mockito.verify(internalRegion, Mockito.times(1)).basicPutPart3(ArgumentMatchers.eq(event), ArgumentMatchers.eq(result), ArgumentMatchers.eq(true), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(true), ArgumentMatchers.eq(ifNew), ArgumentMatchers.eq(ifOld), ArgumentMatchers.eq(expectedOldValue), ArgumentMatchers.eq(requireOldValue));
    }

    @Test
    public void runWileLockedForCacheModificationDoesNotLockGIIClearLockWhenRegionIsInitialized() throws Exception {
        DistributedRegion region = Mockito.mock(DistributedRegion.class);
        Mockito.when(region.isInitialized()).thenReturn(true);
        Mockito.when(region.lockWhenRegionIsInitializing()).thenCallRealMethod();
        RegionMapPut regionMapPut = new RegionMapPut(focusedRegionMap, region, cacheModificationLock, entryEventSerialization, event, ifNew, ifOld, overwriteDestroyed, requireOldValue, expectedOldValue);
        regionMapPut.runWhileLockedForCacheModification(() -> {
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
        RegionMapPut regionMapPut = new RegionMapPut(focusedRegionMap, region, cacheModificationLock, entryEventSerialization, event, ifNew, ifOld, overwriteDestroyed, requireOldValue, expectedOldValue);
        regionMapPut.runWhileLockedForCacheModification(() -> {
        });
        Mockito.verify(region).lockWhenRegionIsInitializing();
        assertThat(region.lockWhenRegionIsInitializing()).isTrue();
        Mockito.verify(region).unlockWhenRegionIsInitializing();
    }
}

