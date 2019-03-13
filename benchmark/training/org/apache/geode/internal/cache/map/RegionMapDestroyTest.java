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
package org.apache.geode.internal.cache.map;


import Token.REMOVED_PHASE2;
import Token.TOMBSTONE;
import java.util.Map;
import org.apache.geode.cache.EntryNotFoundException;
import org.apache.geode.cache.query.internal.index.IndexManager;
import org.apache.geode.internal.cache.DistributedRegion;
import org.apache.geode.internal.cache.EntryEventImpl;
import org.apache.geode.internal.cache.InternalRegion;
import org.apache.geode.internal.cache.RegionClearedException;
import org.apache.geode.internal.cache.RegionEntry;
import org.apache.geode.internal.cache.RegionEntryFactory;
import org.apache.geode.internal.cache.versions.ConcurrentCacheModificationException;
import org.apache.geode.internal.cache.versions.VersionTag;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class RegionMapDestroyTest {
    private static final Object KEY = "key";

    @SuppressWarnings("rawtypes")
    private Map entryMap;

    private FocusedRegionMap regionMap;

    private boolean withConcurrencyChecks;

    private RegionEntryFactory factory;

    private RegionEntry newRegionEntry;

    private RegionEntry existingRegionEntry;

    private InternalRegion owner;

    private EntryEventImpl event;

    private Object expectedOldValue;

    private boolean inTokenMode;

    private boolean duringRI;

    private boolean cacheWrite;

    private boolean isEviction;

    private boolean removeRecoveredEntry;

    private boolean fromRILocalDestroy;

    private RegionMapDestroy regionMapDestroy;

    private Throwable doDestroyThrowable;

    private boolean doDestroyResult;

    private Runnable testHook;

    private IndexManager indexManager;

    @Test
    public void destroyWithDuplicateVersionInvokesListener() {
        givenConcurrencyChecks(true);
        givenExistingTombstone();
        givenExistingEntryWithVersionTag(Mockito.mock(VersionTag.class));
        givenEventWithClientOrigin();
        doDestroy();
        verifyDestroyReturnedTrue();
        Mockito.verify(event, Mockito.times(1)).setIsRedestroyedEntry(true);
        verifyPart3();
    }

    @Test
    public void destroyWithEmptyRegionThrowsException() {
        givenConcurrencyChecks(false);
        doDestroy();
        Mockito.verify(owner, Mockito.times(1)).checkEntryNotFound(ArgumentMatchers.any());
    }

    @Test
    public void destroyWithEmptyRegionInTokenModeAddsAToken() throws Exception {
        givenConcurrencyChecks(false);
        givenInTokenMode();
        doDestroy();
        verifyDestroyReturnedTrue();
        verifyEntryAddedToMap(newRegionEntry);
        verifyEntryDestroyed(newRegionEntry, true);
        verifyInvokedDestroyMethodsOnRegion(false);
    }

    @Test
    public void destroyWithEmptyRegionInTokenModeNeverCallsUpdateSizeOnRemove() {
        givenConcurrencyChecks(false);
        givenInTokenMode();
        doDestroy();
        verifyDestroyReturnedTrue();
        Mockito.verify(owner, Mockito.never()).updateSizeOnRemove(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
    }

    @Test
    public void destroyInvokesTestHook() {
        givenConcurrencyChecks(false);
        givenInTokenMode();
        givenTestHook();
        doDestroy();
        verifyTestHookRun();
    }

    @Test
    public void destroyWithEmptyRegionInTokenModeWithRegionClearedExceptionDoesDestroy() throws Exception {
        givenConcurrencyChecks(false);
        givenDestroyThrowsRegionClearedException();
        givenInTokenMode();
        doDestroy();
        verifyDestroyReturnedTrue();
        verifyInvokedDestroyMethodsOnRegion(true);
    }

    @Test
    public void evictDestroyWithEmptyRegionInTokenModeDoesNothing() {
        givenEviction();
        givenInTokenMode();
        doDestroy();
        verifyDestroyReturnedFalse();
        verifyEntryRemoved(newRegionEntry);
        verifyNoDestroyInvocationsOnRegion();
    }

    @Test
    public void evictDestroyWithExistingTombstoneInTokenModeDestroyExistingEntry() throws Exception {
        givenConcurrencyChecks(true);
        givenEviction();
        givenExistingTombstone();
        givenInTokenMode();
        doDestroy();
        verifyDestroyReturnedTrue();
        verifyEntryDestroyed(existingRegionEntry, false);
        verifyInvokedDestroyMethodsOnRegion(false);
    }

    @Test
    public void evictDestroyWithExistingTombstoneInTokenModeNeverCallsUpdateSizeOnRemove() {
        givenConcurrencyChecks(true);
        givenEviction();
        givenExistingTombstone();
        givenInTokenMode();
        doDestroy();
        verifyDestroyReturnedTrue();
        Mockito.verify(owner, Mockito.never()).updateSizeOnRemove(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
    }

    @Test
    public void evictDestroyWithExistingTombstoneInUseByTransactionInTokenModeDoesNothing() throws RegionClearedException {
        givenConcurrencyChecks(true);
        givenEviction();
        givenExistingEntryWithValue(TOMBSTONE);
        givenEntryIsInUseByTransaction();
        givenInTokenMode();
        doDestroy();
        verifyDestroyReturnedFalse();
        verifyNoDestroyInvocationsOnEntry(existingRegionEntry);
        verifyNoDestroyInvocationsOnRegion();
    }

    @Test
    public void evictDestroyWithConcurrentChangeFromNullToInUseByTransactionInTokenModeDoesNothing() throws RegionClearedException {
        givenConcurrencyChecks(true);
        givenEviction();
        givenExistingEntryWithValue(null);
        givenEntryIsInUseByTransaction();
        givenInTokenMode();
        doDestroy();
        verifyDestroyReturnedFalse();
        verifyNoDestroyInvocationsOnEntry(existingRegionEntry);
        verifyNoDestroyInvocationsOnRegion();
    }

    @Test
    public void evictDestroyWithInUseByTransactionInTokenModeDoesNothing() throws RegionClearedException {
        givenConcurrencyChecks(true);
        givenEviction();
        givenExistingEntry();
        givenEntryIsInUseByTransaction();
        givenInTokenMode();
        doDestroy();
        verifyDestroyReturnedFalse();
        verifyNoDestroyInvocationsOnEntry(existingRegionEntry);
        verifyNoDestroyInvocationsOnRegion();
    }

    @Test
    public void destroyWithConcurrentChangeFromNullToValidRetriesAndDoesDestroy() throws Exception {
        givenConcurrencyChecks(true);
        givenMissThenExistingEntry();
        doDestroy();
        verifyDestroyReturnedTrue();
        verifyEntryDestroyed(existingRegionEntry, false);
        verifyInvokedDestroyMethodsOnRegion(false);
    }

    @Test
    public void destroyWithConcurrentChangeFromNullToValidRetriesAndThrowsConcurrentCacheModificationException() throws RegionClearedException {
        givenConcurrencyChecks(true);
        givenMissThenExistingEntry();
        givenEntryDestroyThrows(existingRegionEntry, ConcurrentCacheModificationException.class);
        doDestroyExpectingThrowable();
        verifyThrowableInstanceOf(ConcurrentCacheModificationException.class);
        verifyNoDestroyInvocationsOnRegion();
    }

    @Test
    public void destroyWithConcurrentChangeFromNullToValidRetriesCallsDestroyWhichReturnsFalseCausingDestroyToNotHappen() throws RegionClearedException {
        givenConcurrencyChecks(true);
        givenMissThenExistingEntry();
        givenExistingEntryWithNoVersionStamp();
        givenEntryDestroyReturnsFalse(existingRegionEntry);
        doDestroy();
        verifyDestroyReturnedTrue();
        Mockito.verify(existingRegionEntry, Mockito.times(1)).removePhase2();
        verifyNoDestroyInvocationsOnRegion();
    }

    @Test
    public void destroyWithInTokenModeAndTombstoneCallsDestroyWhichReturnsFalseCausingDestroyToNotHappen() throws RegionClearedException {
        givenConcurrencyChecks(true);
        givenExistingEntryWithValue(TOMBSTONE);
        givenMissThenExistingEntry();
        givenInTokenMode();
        givenEntryDestroyReturnsFalse(existingRegionEntry);
        doDestroy();
        // TODO since destroy returns false it seems like doDestroy should return false
        verifyDestroyReturnedTrue();
        verifyNoDestroyInvocationsOnRegion();
    }

    @Test
    public void destroyWithInTokenModeAndTombstoneCallsDestroyWhichThrowsRegionClearedStillDoesDestroy() throws RegionClearedException {
        givenConcurrencyChecks(true);
        givenMissThenExistingTombstone();
        givenInTokenMode();
        givenEntryDestroyThrows(existingRegionEntry, RegionClearedException.class);
        doDestroy();
        verifyDestroyReturnedTrue();
        verifyInvokedDestroyMethodsOnRegion(true);
    }

    @Test
    public void destroyWithInTokenModeCallsDestroyWhichReturnsFalseCausingDestroyToNotHappen() throws RegionClearedException {
        givenConcurrencyChecks(true);
        givenExistingEntry();
        givenEntryDestroyReturnsFalse(existingRegionEntry);
        givenInTokenMode();
        doDestroy();
        verifyDestroyReturnedFalse();
        Mockito.verify(existingRegionEntry, Mockito.never()).removePhase2();
        verifyNoDestroyInvocationsOnRegion();
    }

    @Test
    public void destroyExistingEntryWithVersionStampCallsDestroyWhichReturnsFalseCausingDestroyToNotHappenAndDoesNotCallRemovePhase2() throws RegionClearedException {
        givenConcurrencyChecks(true);
        givenExistingEntry();
        givenEntryDestroyReturnsFalse(existingRegionEntry);
        givenOriginIsRemote();
        doDestroy();
        verifyDestroyReturnedTrue();
        Mockito.verify(owner, Mockito.never()).rescheduleTombstone(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(existingRegionEntry, Mockito.never()).removePhase2();
        verifyNoDestroyInvocationsOnRegion();
    }

    @Test
    public void destroyTombstoneWithRemoveRecoveredEntryAndVersionStampCallsRescheduleTombstone() throws RegionClearedException {
        givenConcurrencyChecks(true);
        givenEviction();
        givenExistingEntryWithValue(TOMBSTONE);
        givenEntryDestroyReturnsFalse(existingRegionEntry);
        givenOriginIsRemote();
        givenRemoveRecoveredEntry();
        doDestroy();
        verifyDestroyReturnedTrue();
        Mockito.verify(owner, Mockito.times(1)).rescheduleTombstone(ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.any());
        Mockito.verify(existingRegionEntry, Mockito.never()).removePhase2();
        verifyNoDestroyInvocationsOnRegion();
    }

    @Test
    public void destroyTombstoneWithLocalOriginAndRemoveRecoveredEntryAndVersionStampDoesNotCallRescheduleTombstone() throws RegionClearedException {
        givenConcurrencyChecks(true);
        givenEviction();
        givenExistingEntryWithValue(TOMBSTONE);
        givenEntryDestroyReturnsFalse(existingRegionEntry);
        givenRemoveRecoveredEntry();
        doDestroy();
        verifyDestroyReturnedTrue();
        Mockito.verify(owner, Mockito.never()).rescheduleTombstone(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(existingRegionEntry, Mockito.never()).removePhase2();
        verifyNoDestroyInvocationsOnRegion();
    }

    @Test
    public void destroyWithConcurrentChangeFromNullToValidRetriesAndCallsUpdateSizeOnRemove() throws RegionClearedException {
        givenConcurrencyChecks(true);
        givenMissThenExistingEntry();
        doDestroy();
        verifyDestroyReturnedTrue();
        Mockito.verify(owner, Mockito.times(1)).updateSizeOnRemove(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
    }

    @Test
    public void destroyInTokenModeWithConcurrentChangeFromNullToRemovePhase2RetriesAndDoesDestroy() throws RegionClearedException {
        givenConcurrencyChecks(true);
        givenExistingEntry();
        givenInTokenMode();
        givenRemovePhase2Retry();
        doDestroy();
        verifyDestroyReturnedTrue();
        Mockito.verify(entryMap).remove(ArgumentMatchers.eq(RegionMapDestroyTest.KEY), ArgumentMatchers.eq(existingRegionEntry));
        Mockito.verify(regionMap, Mockito.times(2)).putEntryIfAbsent(ArgumentMatchers.eq(RegionMapDestroyTest.KEY), ArgumentMatchers.any());
        verifyNoDestroyInvocationsOnEntry(existingRegionEntry);
        verifyInvokedDestroyMethodsOnRegion(false);
    }

    @Test
    public void destroyInTokenModeWithConcurrentChangeFromNullToRemovePhase2RetriesAndNeverCallsUpdateSizeOnRemove() throws RegionClearedException {
        givenConcurrencyChecks(true);
        givenExistingEntry();
        givenInTokenMode();
        givenRemovePhase2Retry();
        doDestroy();
        verifyDestroyReturnedTrue();
        Mockito.verify(owner, Mockito.never()).updateSizeOnRemove(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
    }

    @Test
    public void destroyOfExistingEntryInTokenModeAddsAToken() throws Exception {
        givenConcurrencyChecks(false);
        givenExistingEntry();
        givenInTokenMode();
        doDestroy();
        verifyDestroyReturnedTrue();
        verifyEntryDestroyed(existingRegionEntry, false);
        verifyInvokedDestroyMethodsOnRegion(false);
    }

    @Test
    public void destroyOfExistingEntryInTokenModeInhibitsCacheListenerNotification() {
        givenConcurrencyChecks(false);
        givenExistingEntry();
        givenInTokenMode();
        doDestroy();
        verifyDestroyReturnedTrue();
        Mockito.verify(event, Mockito.times(1)).inhibitCacheListenerNotification(true);
    }

    @Test
    public void destroyOfExistingEntryInTokenModeDuringRegisterInterestDoesNotInhibitCacheListenerNotification() {
        givenConcurrencyChecks(false);
        givenExistingEntry();
        givenInTokenMode();
        givenDuringRegisterInterest();
        doDestroy();
        verifyDestroyReturnedTrue();
        Mockito.verify(event, Mockito.never()).inhibitCacheListenerNotification(true);
    }

    @Test
    public void destroyOfExistingEntryDoesNotInhibitCacheListenerNotification() {
        givenConcurrencyChecks(false);
        givenExistingEntry();
        doDestroy();
        verifyDestroyReturnedTrue();
        Mockito.verify(event, Mockito.never()).inhibitCacheListenerNotification(true);
    }

    @Test
    public void destroyOfExistingEntryCallsIndexManager() {
        givenConcurrencyChecks(false);
        givenExistingEntry();
        givenIndexManager();
        doDestroy();
        verifyDestroyReturnedTrue();
        verifyIndexManagerOrder();
    }

    @Test
    public void destroyOfExistingEntryInTokenModeCallsUpdateSizeOnRemove() {
        givenConcurrencyChecks(false);
        givenExistingEntry();
        givenInTokenMode();
        doDestroy();
        verifyDestroyReturnedTrue();
        Mockito.verify(owner, Mockito.times(1)).updateSizeOnRemove(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
    }

    @Test
    public void destroyOfExistingTombstoneInTokenModeWithConcurrencyChecksDoesNothing() throws Exception {
        givenConcurrencyChecks(true);
        givenExistingTombstoneAndVersionTag();
        givenInTokenMode();
        doDestroy();
        verifyDestroyReturnedTrue();
        // why not DESTROY token? since it was already destroyed why do we do the parts?
        verifyEntryDestroyed(existingRegionEntry, false);
        Mockito.verify(regionMap, Mockito.never()).removeEntry(ArgumentMatchers.eq(RegionMapDestroyTest.KEY), ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.anyBoolean());
        Mockito.verify(regionMap, Mockito.never()).removeEntry(ArgumentMatchers.eq(RegionMapDestroyTest.KEY), ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.anyBoolean(), ArgumentMatchers.same(event), ArgumentMatchers.same(owner));
        verifyInvokedDestroyMethodsOnRegion(false);
    }

    @Test
    public void destroyOfExistingFromPutIfAbsentWithRemoteOriginCallsBasicDestroyBeforeRemoval() throws Exception {
        givenConcurrencyChecks(true);
        givenMissThenExistingEntry();
        givenOriginIsRemote();
        doDestroy();
        verifyDestroyReturnedTrue();
        Mockito.verify(owner, Mockito.times(1)).basicDestroyBeforeRemoval(existingRegionEntry, event);
    }

    @Test
    public void destroyOfExistingFromPutIfAbsentWithTokenModeAndLocalOriginDoesNotCallBasicDestroyBeforeRemoval() throws Exception {
        givenConcurrencyChecks(true);
        givenMissThenExistingEntry();
        givenInTokenMode();
        doDestroy();
        verifyDestroyReturnedTrue();
        // TODO: this seems like a bug in the product. See the comment in:
        // RegionMapDestroy.destroyExistingFromPutIfAbsent(RegionEntry)
        Mockito.verify(owner, Mockito.never()).basicDestroyBeforeRemoval(existingRegionEntry, event);
    }

    @Test
    public void destroyOfExistingTombstoneInTokenModeWithConcurrencyChecksNeverCallsUpdateSizeOnRemove() {
        givenConcurrencyChecks(true);
        givenExistingTombstoneAndVersionTag();
        givenInTokenMode();
        doDestroy();
        verifyDestroyReturnedTrue();
        Mockito.verify(owner, Mockito.never()).updateSizeOnRemove(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
    }

    @Test
    public void destroyOfExistingTombstoneWillThrowConcurrentCacheModificationException() throws RegionClearedException {
        givenConcurrencyChecks(true);
        givenExistingTombstoneAndVersionTag();
        givenInTokenMode();
        givenEntryDestroyThrows(existingRegionEntry, ConcurrentCacheModificationException.class);
        doDestroyExpectingThrowable();
        verifyThrowableInstanceOf(ConcurrentCacheModificationException.class);
        Mockito.verify(owner, Mockito.never()).notifyTimestampsToGateways(event);
    }

    @Test
    public void destroyOfExistingTombstoneWithTimeStampUpdatedWillCallNotifyTimestampsToGateways() throws RegionClearedException {
        givenConcurrencyChecks(true);
        givenExistingTombstoneAndVersionTag();
        givenInTokenMode();
        givenEntryDestroyThrows(existingRegionEntry, ConcurrentCacheModificationException.class);
        givenEventTimeStampUpdated();
        doDestroyExpectingThrowable();
        verifyThrowableInstanceOf(ConcurrentCacheModificationException.class);
        Mockito.verify(owner, Mockito.times(1)).notifyTimestampsToGateways(event);
    }

    @Test
    public void destroyOfExistingTombstoneWithConcurrencyChecksThrowsEntryNotFound() {
        givenConcurrencyChecks(true);
        givenExistingTombstoneAndVersionTag();
        doDestroy();
        Mockito.verify(owner, Mockito.times(1)).checkEntryNotFound(ArgumentMatchers.any());
    }

    @Test
    public void destroyOfExistingTombstoneThatThrowsConcurrentCacheModificationExceptionNeverCallsNotify() {
        givenConcurrencyChecks(true);
        givenExistingTombstone();
        givenVersionStampThatDetectsConflict();
        givenEventWithVersionTag();
        doDestroyExpectingThrowable();
        verifyThrowableInstanceOf(ConcurrentCacheModificationException.class);
        Mockito.verify(owner, Mockito.never()).notifyTimestampsToGateways(ArgumentMatchers.any());
    }

    @Test
    public void destroyOfTombstoneThatBecomesNonTombstoneRetriesAndDoesDestroy() throws Exception {
        givenConcurrencyChecks(true);
        givenExistingTombstone();
        givenEventWithVersionTag();
        givenTombstoneThenAlive();
        givenNotDestroyedOrRemoved();
        doDestroy();
        verifyDestroyReturnedTrue();
        Mockito.verify(regionMap, Mockito.times(2)).getEntry(ArgumentMatchers.any());
        Mockito.verify(regionMap, Mockito.times(1)).processVersionTag(existingRegionEntry, event);
        Mockito.verify(regionMap, Mockito.times(1)).lruEntryDestroy(existingRegionEntry);
        Mockito.verifyNoMoreInteractions(regionMap);
        verifyEntryDestroyed(existingRegionEntry, false);
        verifyInvokedDestroyMethodsOnRegion(false);
    }

    @Test
    public void destroyOfExistingTombstoneThatThrowsConcurrentCacheModificationExceptionWithTimeStampUpdatedCallsNotify() throws Exception {
        givenConcurrencyChecks(true);
        givenExistingTombstone();
        givenVersionStampThatDetectsConflict();
        givenEventWithVersionTag();
        givenEventTimeStampUpdated();
        doDestroyExpectingThrowable();
        verifyThrowableInstanceOf(ConcurrentCacheModificationException.class);
        Mockito.verify(owner, Mockito.times(1)).notifyTimestampsToGateways(ArgumentMatchers.eq(event));
    }

    @Test
    public void destroyWithConcurrentChangeFromNullToValidRetriesAndCallsNotifyTimestampsToGateways() throws RegionClearedException {
        givenConcurrencyChecks(true);
        givenMissThenExistingEntry();
        givenVersionStampThatDetectsConflict();
        givenEventWithVersionTag();
        givenEventTimeStampUpdated();
        givenEntryDestroyThrows(existingRegionEntry, ConcurrentCacheModificationException.class);
        doDestroyExpectingThrowable();
        verifyThrowableInstanceOf(ConcurrentCacheModificationException.class);
        verifyNoDestroyInvocationsOnRegion();
        Mockito.verify(owner, Mockito.times(1)).notifyTimestampsToGateways(ArgumentMatchers.eq(event));
    }

    @Test
    public void destroyOfExistingTombstoneWithConcurrencyChecksAndNoTagThrowsEntryNotFound() {
        givenConcurrencyChecks(true);
        givenExistingTombstone();
        doDestroy();
        Mockito.verify(owner, Mockito.times(1)).checkEntryNotFound(ArgumentMatchers.any());
    }

    @Test
    public void destroyOfExistingTombstoneDoesDestroyAndReschedulesTombstone() throws RegionClearedException {
        givenConcurrencyChecks(true);
        givenExistingTombstone();
        givenEventWithVersionTag();
        givenOriginIsRemote();
        doDestroy();
        verifyDestroyReturnedTrue();
        Mockito.verify(owner, Mockito.times(1)).checkEntryNotFound(ArgumentMatchers.any());
        Mockito.verify(owner, Mockito.times(1)).recordEvent(event);
        Mockito.verify(owner, Mockito.times(1)).rescheduleTombstone(ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.any());
        Mockito.verify(existingRegionEntry, Mockito.times(1)).setValue(owner, TOMBSTONE);
        verifyPart2(true);
        verifyNoPart3();
    }

    @Test
    public void evictDestroyOfExistingTombstoneWithConcurrencyChecksReturnsFalse() {
        givenConcurrencyChecks(true);
        givenExistingTombstoneAndVersionTag();
        givenEviction();
        doDestroy();
        verifyDestroyReturnedFalse();
    }

    @Test
    public void destroyOfExistingTombstoneWithConcurrencyChecksAndRemoveRecoveredEntryDoesRemove() throws Exception {
        givenConcurrencyChecks(true);
        givenExistingTombstoneAndVersionTag();
        givenTombstoneThenAlive();
        givenRemoveRecoveredEntry();
        doDestroy();
        verifyDestroyReturnedTrue();
        verifyMapDoesNotContainKey();
        verifyInvokedDestroyMethodsOnRegion(false);
    }

    @Test
    public void destroyOfExistingEntryThatBecomesTombstoneAfterInitialCheckCallsProcessVersionTagAndMakeTombstoneButDoesNotDoDestroy() throws Exception {
        givenConcurrencyChecks(true);
        givenExistingTombstoneAndVersionTag();
        givenAliveThenTombstone();
        doDestroy();
        verifyDestroyReturnedFalse();
        Mockito.verify(regionMap, Mockito.times(1)).getEntry(ArgumentMatchers.any());
        Mockito.verify(regionMap, Mockito.times(1)).processVersionTag(existingRegionEntry, event);
        Mockito.verifyNoMoreInteractions(regionMap);
        Mockito.verify(existingRegionEntry, Mockito.times(1)).makeTombstone(ArgumentMatchers.any(), ArgumentMatchers.any());
        verifyNoDestroyInvocationsOnRegion();
    }

    @Test
    public void destroyOfExistingTombstoneWithConcurrencyChecksAndFromRILocalDestroyDoesRemove() throws Exception {
        givenConcurrencyChecks(true);
        givenFromRILocalDestroy();
        givenExistingTombstoneAndVersionTag();
        givenTombstoneThenAlive();
        doDestroy();
        verifyDestroyReturnedTrue();
        verifyMapDoesNotContainKey();
        verifyInvokedDestroyMethodsOnRegion(false);
    }

    @Test
    public void destroyOfExistingTombstoneWithConcurrencyChecksAndRemoveRecoveredEntryNeverCallsUpdateSizeOnRemove() {
        givenConcurrencyChecks(true);
        givenExistingTombstoneAndVersionTag();
        givenRemoveRecoveredEntry();
        doDestroy();
        verifyDestroyReturnedTrue();
        Mockito.verify(owner, Mockito.never()).updateSizeOnRemove(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
    }

    @Test
    public void destroyOfExistingRemovePhase2WithConcurrencyChecksAndRemoveRecoveredEntryDoesRetryAndThrowsEntryNotFound() {
        givenConcurrencyChecks(true);
        givenExistingEntryWithTokenAndVersionTag(REMOVED_PHASE2);
        givenRemoveRecoveredEntry();
        doDestroy();
        Mockito.verify(owner, Mockito.times(1)).checkEntryNotFound(ArgumentMatchers.any());
    }

    @Test
    public void destroyOfExistingRemovePhase2WithConcurrencyChecksDoesRetryAndThrowsEntryNotFound() {
        givenConcurrencyChecks(true);
        givenExistingEntryWithTokenAndVersionTag(REMOVED_PHASE2);
        doDestroy();
        Mockito.verify(owner, Mockito.times(1)).checkEntryNotFound(ArgumentMatchers.any());
    }

    @Test
    public void destroyOfExistingRemovePhase2WithConcurrencyChecksAndExpectedValueDoesRetryAndReturnsFalse() {
        givenConcurrencyChecks(true);
        givenExistingEntryWithTokenAndVersionTag(REMOVED_PHASE2);
        givenExpectedOldValue();
        doDestroy();
        verifyDestroyReturnedFalse();
    }

    @Test
    public void destroyOfExistingRemovePhase2WithConcurrencyChecksAndInTokenModeDoesRetryAndReturnsFalse() {
        givenConcurrencyChecks(true);
        givenExistingEntryWithTokenAndVersionTag(REMOVED_PHASE2);
        givenInTokenMode();
        doDestroy();
        verifyDestroyReturnedFalse();
    }

    @Test
    public void destroyOfExistingRemovePhase2WithConcurrencyChecksAndEvictionDoesRetryAndReturnsFalse() {
        givenConcurrencyChecks(true);
        givenExistingEntryWithTokenAndVersionTag(REMOVED_PHASE2);
        givenEviction();
        doDestroy();
        verifyDestroyReturnedFalse();
    }

    @Test
    public void destroyOfExistingRemovePhase2WithoutConcurrencyChecksDoesRetryAndThrowsEntryNotFound() {
        givenConcurrencyChecks(false);
        givenExistingEntryWithTokenAndVersionTag(REMOVED_PHASE2);
        doDestroy();
        Mockito.verify(owner, Mockito.times(1)).checkEntryNotFound(ArgumentMatchers.any());
    }

    @Test
    public void destroyOfExistingRemovePhase2WithConcurrencyChecksAndOriginRemoteDoesRetryAndDoesRemove() throws Exception {
        givenConcurrencyChecks(true);
        givenExistingEntryWithTokenAndVersionTag(REMOVED_PHASE2);
        givenOriginIsRemote();
        doDestroy();
        verifyDestroyReturnedTrue();
        Mockito.verify(entryMap, Mockito.times(1)).remove(RegionMapDestroyTest.KEY, existingRegionEntry);
        verifyEntryAddedToMap(newRegionEntry);
        verifyEntryDestroyed(newRegionEntry, true);
    }

    @Test
    public void destroyOfExistingRemovePhase2WithConcurrencyChecksAndClientOriginDoesRetryAndDoesRemove() throws Exception {
        givenConcurrencyChecks(true);
        givenExistingEntryWithTokenAndVersionTag(REMOVED_PHASE2);
        givenEventWithClientOrigin();
        doDestroy();
        verifyDestroyReturnedTrue();
        Mockito.verify(entryMap, Mockito.times(1)).remove(RegionMapDestroyTest.KEY, existingRegionEntry);
        verifyEntryAddedToMap(newRegionEntry);
        verifyEntryDestroyed(newRegionEntry, true);
    }

    @Test
    public void destroyOfExistingEntryRemovesEntryFromMapAndDoesNotifications() throws Exception {
        givenConcurrencyChecks(false);
        givenExistingEntry();
        doDestroy();
        verifyDestroyReturnedTrue();
        verifyEntryDestroyed(existingRegionEntry, false);
        Mockito.verify(regionMap, Mockito.times(1)).removeEntry(RegionMapDestroyTest.KEY, existingRegionEntry, true, event, owner);
        verifyInvokedDestroyMethodsOnRegion(false);
    }

    @Test
    public void destroyOfExistingEntryWithConflictDoesPart3() {
        givenConcurrencyChecks(false);
        givenExistingEntry();
        givenEventWithConflict();
        doDestroy();
        verifyDestroyReturnedTrue();
        verifyPart3();
    }

    @Test
    public void destroyOfExistingEntryWithConflictAndWANSkipsPart3() {
        givenConcurrencyChecks(false);
        givenExistingEntry();
        givenEventWithConflict();
        givenEventWithGatewayTag();
        doDestroy();
        verifyDestroyReturnedTrue();
        verifyNoPart3();
    }

    @Test
    public void destroyOfExistingEntryWithRegionClearedExceptionDoesDestroyAndPart2AndPart3() throws RegionClearedException {
        givenConcurrencyChecks(false);
        givenMissThenExistingEntry();
        givenEventWithVersionTag();
        givenEntryDestroyThrows(existingRegionEntry, RegionClearedException.class);
        doDestroy();
        verifyDestroyReturnedTrue();
        Mockito.verify(event, Mockito.never()).inhibitCacheListenerNotification(true);
        verifyInvokedDestroyMethodsOnRegion(true);
    }

    @Test
    public void destroyOfExistingEntryWithRegionClearedExceptionInTokenModeCallsInhibitCacheListenerNotification() throws RegionClearedException {
        givenConcurrencyChecks(false);
        givenExistingEntry();
        givenEventWithVersionTag();
        givenEntryDestroyThrows(existingRegionEntry, RegionClearedException.class);
        givenInTokenMode();
        doDestroy();
        verifyDestroyReturnedTrue();
        Mockito.verify(event, Mockito.times(1)).inhibitCacheListenerNotification(true);
        verifyInvokedDestroyMethodsOnRegion(true);
    }

    @Test
    public void expireDestroyOfExistingEntry() {
        givenConcurrencyChecks(true);
        givenExistingEntry();
        givenExpireDestroy();
        doDestroy();
        verifyDestroyReturnedTrue();
    }

    @Test
    public void expireDestroyOfExistingEntryWithOriginRemote() {
        givenConcurrencyChecks(true);
        givenExistingEntry();
        givenOriginIsRemote();
        givenExpireDestroy();
        doDestroy();
        verifyDestroyReturnedTrue();
    }

    @Test
    public void expireDestroyOfEntryInUseIsCancelled() throws RegionClearedException {
        givenConcurrencyChecks(true);
        givenExistingEntry();
        givenEntryIsInUseByTransaction();
        givenExpireDestroy();
        doDestroy();
        verifyDestroyReturnedFalse();
        verifyNoDestroyInvocationsOnEntry(existingRegionEntry);
        verifyNoDestroyInvocationsOnRegion();
    }

    @Test
    public void destroyOfExistingEntryCallsUpdateSizeOnRemove() {
        givenConcurrencyChecks(false);
        givenExistingEntry();
        doDestroy();
        verifyDestroyReturnedTrue();
        Mockito.verify(owner, Mockito.times(1)).updateSizeOnRemove(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
    }

    /**
     * This might be a bug. It seems like we should have created a tombstone but we have no version
     * tag so that might be the cause of this bug.
     */
    @Test
    public void destroyOfExistingEntryWithConcurrencyChecksAndNoVersionTagDestroysWithoutTombstone() throws Exception {
        givenConcurrencyChecks(true);
        givenExistingEntry();
        givenExistingEntryWithNoVersionStamp();
        doDestroy();
        verifyDestroyReturnedTrue();
        verifyMapDoesNotContainKey();
        Mockito.verify(existingRegionEntry, Mockito.times(1)).removePhase2();
        verifyInvokedDestroyMethodsOnRegion(false);
    }

    @Test
    public void destroyOfExistingEntryWithConcurrencyChecksAddsTombstone() throws Exception {
        givenConcurrencyChecks(true);
        givenExistingEntryWithVersionTag();
        doDestroy();
        verifyDestroyReturnedTrue();
        verifyEntryDestroyed(existingRegionEntry, false);
        verifyInvokedDestroyMethodsOnRegion(false);
    }

    @Test
    public void evictDestroyOfExistingEntryWithConcurrencyChecksAddsTombstone() throws Exception {
        givenConcurrencyChecks(true);
        givenEviction();
        givenExistingEntryWithVersionTag();
        doDestroy();
        verifyDestroyReturnedTrue();
        verifyEntryDestroyed(existingRegionEntry, false);
        verifyInvokedDestroyMethodsOnRegion(false);
    }

    @Test
    public void destroyWithEmptyRegionWithConcurrencyChecksThrowsException() {
        givenConcurrencyChecks(true);
        doDestroy();
        Mockito.verify(owner, Mockito.times(1)).checkEntryNotFound(ArgumentMatchers.any());
    }

    @Test
    public void destroyWithEmptyNonReplicateRegionWithConcurrencyChecksAndRemoteEventThrowsException() {
        givenConcurrencyChecks(true);
        givenEmptyDataPolicy();
        givenOriginIsRemote();
        doDestroy();
        Mockito.verify(owner, Mockito.times(1)).checkEntryNotFound(ArgumentMatchers.any());
    }

    @Test
    public void destroyWithEmptyNonReplicateRegionWithConcurrencyChecksAndRemoteEventAndCacheWriteThrowsException() {
        givenConcurrencyChecks(true);
        givenEmptyDataPolicy();
        givenOriginIsRemote();
        givenCacheWrite();
        doDestroy();
        Mockito.verify(owner, Mockito.times(1)).checkEntryNotFound(ArgumentMatchers.any());
    }

    @Test
    public void destroyWithEmptyNonReplicateRegionWithConcurrencyChecksAndRemoteEventAndCacheWriteAndRemoveRecoveredEntryDoesNotThrowException() {
        givenConcurrencyChecks(true);
        givenEmptyDataPolicy();
        givenOriginIsRemote();
        givenCacheWrite();
        givenRemoveRecoveredEntry();
        doDestroy();
        verifyDestroyReturnedFalse();
    }

    @Test
    public void destroyWithEmptyNonReplicateRegionWithConcurrencyChecksAndRemoteEventAndCacheWriteAndBridgeWriteBeforeDestroyReturningTrueDoesNotThrowException() {
        givenConcurrencyChecks(true);
        givenEmptyDataPolicy();
        givenOriginIsRemote();
        givenCacheWrite();
        givenBridgeWriteBeforeDestroyReturnsTrue();
        doDestroy();
        verifyDestroyReturnedFalse();
    }

    @Test
    public void destroyWithEmptyNonReplicateRegionWithConcurrencyChecksAndRemoteEventAndCacheWriteAndBridgeWriteBeforeDestroyThrows_ThrowsException() {
        givenConcurrencyChecks(true);
        givenEmptyDataPolicy();
        givenOriginIsRemote();
        givenCacheWrite();
        givenBridgeWriteBeforeDestroyThrows();
        doDestroyExpectingThrowable();
        verifyThrowableInstanceOf(EntryNotFoundException.class);
    }

    @Test
    public void localDestroyWithEmptyNonReplicateRegionWithConcurrencyChecksThrowsException() {
        givenConcurrencyChecks(true);
        givenEmptyDataPolicy();
        givenEventWithVersionTag();
        givenEventWithClientOrigin();
        givenLocalDestroy();
        doDestroy();
        Mockito.verify(owner, Mockito.times(1)).checkEntryNotFound(ArgumentMatchers.any());
    }

    @Test
    public void destroyWithEmptyNonReplicateRegionWithConcurrencyChecksAndClientTaggedEventAndCacheWriteDoesNotThrowException() throws Exception {
        givenConcurrencyChecks(true);
        givenEmptyDataPolicy();
        givenCacheWrite();
        givenEventWithVersionTag();
        givenEventWithClientOrigin();
        doDestroy();
        verifyDestroyReturnedTrue();
        verifyEntryAddedToMap(newRegionEntry);
        Mockito.verify(newRegionEntry, Mockito.times(1)).makeTombstone(ArgumentMatchers.any(), ArgumentMatchers.any());
        verifyPart3();
    }

    @Test
    public void destroyWithEmptyNonReplicateRegionWithConcurrencyChecksAndWANTaggedEventAndCacheWriteDoesNotThrowException() throws Exception {
        givenConcurrencyChecks(true);
        givenEmptyDataPolicy();
        givenCacheWrite();
        givenEventWithGatewayTag();
        doDestroy();
        verifyDestroyReturnedTrue();
        verifyEntryAddedToMap(newRegionEntry);
        Mockito.verify(newRegionEntry, Mockito.times(1)).makeTombstone(ArgumentMatchers.any(), ArgumentMatchers.any());
        verifyPart3();
    }

    /**
     * This seems to be a bug. We should not leave an evictableEntry in the entryMap added by the
     * destroy call if destroy returns false.
     */
    @Test
    public void evictDestroyWithEmptyRegionWithConcurrencyChecksDoesNothing() {
        givenConcurrencyChecks(true);
        givenEviction();
        doDestroy();
        verifyDestroyReturnedFalse();
        // the following verify should be enabled once GEODE-5573 is fixed
        // verifyMapDoesNotContainKey(KEY);
        verifyNoDestroyInvocationsOnRegion();
    }

    @Test
    public void evictDestroyWithEmptyNonReplicateRegionWithConcurrencyChecksDoesNothing() {
        givenConcurrencyChecks(true);
        givenEviction();
        givenEmptyDataPolicy();
        doDestroy();
        verifyDestroyReturnedFalse();
        // the following verify should be enabled once GEODE-5573 is fixed
        // verifyMapDoesNotContainKey(KEY);
        verifyNoDestroyInvocationsOnRegion();
    }

    @Test
    public void evictDestroyWithEmptyRegionDoesNothing() {
        givenConcurrencyChecks(false);
        givenEviction();
        doDestroy();
        verifyDestroyReturnedFalse();
        Mockito.verify(regionMap, Mockito.times(1)).getEntry(event);
        Mockito.verifyNoMoreInteractions(regionMap);
        verifyNoDestroyInvocationsOnRegion();
    }

    @Test
    public void destroyWithEmptyRegionWithConcurrencyChecksAddsATombstone() throws Exception {
        givenConcurrencyChecks(true);
        givenRemoteEventWithVersionTag();
        doDestroy();
        verifyDestroyReturnedTrue();
        verifyEntryAddedToMap(newRegionEntry);
        verifyEntryDestroyed(newRegionEntry, true);
        verifyInvokedDestroyMethodsOnRegion(false);
    }

    @Test
    public void destroyWithEmptyRegionWithConcurrencyChecksAndClientOriginEventAddsNewEntryAndCallsDestroy() throws Exception {
        givenConcurrencyChecks(true);
        givenEventWithVersionTag();
        givenEventWithClientOrigin();
        doDestroy();
        verifyDestroyReturnedTrue();
        verifyEntryAddedToMap(newRegionEntry);
        verifyEntryDestroyed(newRegionEntry, true);
        verifyInvokedDestroyMethodsOnRegion(false);
    }

    @Test
    public void destroyWithEmptyRegionWithConcurrencyChecksAndWANEventAddsATombstone() throws Exception {
        givenConcurrencyChecks(true);
        givenEventWithGatewayTag();
        doDestroy();
        verifyDestroyReturnedTrue();
        verifyEntryAddedToMap(newRegionEntry);
        verifyEntryDestroyed(newRegionEntry, true);
        verifyInvokedDestroyMethodsOnRegion(false);
    }

    @Test
    public void destroyWhoseNewRegionEntryThrowsConcurrentCheckThrowsException() throws Exception {
        givenConcurrencyChecks(true);
        givenEventWithGatewayTag();
        givenEntryDestroyThrows(newRegionEntry, ConcurrentCacheModificationException.class);
        doDestroyExpectingThrowable();
        verifyThrowableInstanceOf(ConcurrentCacheModificationException.class);
        Mockito.verify(owner, Mockito.never()).notifyTimestampsToGateways(ArgumentMatchers.any());
        verifyNoDestroyInvocationsOnRegion();
    }

    @Test
    public void destroyWhoseNewRegionEntryThrowsConcurrentCheckAndTimeStampUpdatedThrowsException() throws Exception {
        givenConcurrencyChecks(true);
        givenEventWithGatewayTag();
        givenEventTimeStampUpdated();
        givenEntryDestroyThrows(newRegionEntry, ConcurrentCacheModificationException.class);
        doDestroyExpectingThrowable();
        verifyThrowableInstanceOf(ConcurrentCacheModificationException.class);
        Mockito.verify(owner, Mockito.times(1)).notifyTimestampsToGateways(ArgumentMatchers.any());
        verifyNoDestroyInvocationsOnRegion();
    }

    @Test
    public void validateNoDestroyWhenExistingTombstoneAndNewEntryDestroyFails() throws RegionClearedException {
        givenConcurrencyChecks(true);
        givenExistingTombstone();
        givenPutEntryIfAbsentReturnsNull();
        givenEventWithGatewayTag();
        givenEntryDestroyReturnsFalse(newRegionEntry);
        givenInTokenMode();
        doDestroy();
        verifyDestroyReturnedFalse();
        verifyNoDestroyInvocationsOnRegion();
        Mockito.verify(entryMap, Mockito.never()).remove(RegionMapDestroyTest.KEY, newRegionEntry);// TODO: this seems like a bug. This

        // should be called once.
    }

    @Test
    public void validateNoDestroyInvocationsOnRegionDoesNotDoDestroyIfEntryDestroyReturnsFalse() throws RegionClearedException {
        givenConcurrencyChecks(true);
        givenEventWithGatewayTag();
        givenEntryDestroyReturnsFalse(newRegionEntry);
        doDestroy();
        verifyDestroyReturnedFalse();
        verifyNoDestroyInvocationsOnRegion();
    }

    @Test
    public void destroyWithEmptyNonReplicateRegionWithConcurrencyChecksAndEventFromServerAddsATombstone() throws Exception {
        givenConcurrencyChecks(true);
        givenRemoteEventWithVersionTag();
        givenEmptyDataPolicy();
        givenEventFromServer();
        doDestroy();
        verifyDestroyReturnedTrue();
        verifyEntryAddedToMap(newRegionEntry);
        verifyEntryDestroyed(newRegionEntry, true);
        verifyInvokedDestroyMethodsOnRegion(false);
    }

    @Test
    public void destroyWithEmptyRegionWithConcurrencyChecksAndWANEventWithConflictAddsATombstoneButDoesNotDoPart3() throws Exception {
        givenConcurrencyChecks(true);
        givenEventWithGatewayTag();
        givenEventWithConflict();
        doDestroy();
        verifyDestroyReturnedTrue();
        verifyEntryAddedToMap(newRegionEntry);
        verifyEntryDestroyed(newRegionEntry, true);
        verifyPart2(false);
        verifyNoPart3();
    }

    @Test
    public void destroyWithEmptyRegionWithConcurrencyChecksAndEventWithConflictAddsATombstone() throws Exception {
        givenConcurrencyChecks(true);
        givenRemoteEventWithVersionTag();
        givenEventWithConflict();
        doDestroy();
        verifyDestroyReturnedTrue();
        verifyEntryAddedToMap(newRegionEntry);
        verifyEntryDestroyed(newRegionEntry, true);
        verifyInvokedDestroyMethodsOnRegion(false);
    }

    @Test
    public void destroyWithEmptyRegionWithConcurrencyChecksCallsIndexManager() {
        givenConcurrencyChecks(true);
        givenRemoteEventWithVersionTag();
        givenIndexManager();
        doDestroy();
        verifyDestroyReturnedTrue();
        verifyIndexManagerOrder();
    }

    /**
     * instead of a TOMBSTONE we leave an evictableEntry whose value is REMOVE_PHASE1 this looks like
     * a bug. It is caused by some code in: AbstractRegionEntry.destroy() that calls removePhase1 when
     * the versionTag is null. It seems like this code path needs to tell the higher levels to call
     * removeEntry
     */
    @Test
    public void destroyWithEmptyRegionWithConcurrencyChecksAndNullVersionTagAddsATombstone() throws Exception {
        givenConcurrencyChecks(true);
        givenOriginIsRemote();
        doDestroy();
        verifyDestroyReturnedTrue();
        verifyEntryAddedToMap(newRegionEntry);
        Mockito.verify(regionMap, Mockito.never()).removeEntry(ArgumentMatchers.eq(RegionMapDestroyTest.KEY), ArgumentMatchers.same(newRegionEntry), ArgumentMatchers.anyBoolean());
        Mockito.verify(regionMap, Mockito.never()).removeEntry(ArgumentMatchers.eq(RegionMapDestroyTest.KEY), ArgumentMatchers.same(newRegionEntry), ArgumentMatchers.anyBoolean(), ArgumentMatchers.same(event), ArgumentMatchers.same(owner));
        verifyEntryDestroyed(newRegionEntry, true);
        verifyInvokedDestroyMethodsOnRegion(false);
    }

    @Test
    public void destroyDoesNotLockGIIClearLockWhenRegionIsInitialized() throws Exception {
        DistributedRegion region = Mockito.mock(DistributedRegion.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(region.isInitialized()).thenReturn(true);
        Mockito.when(region.lockWhenRegionIsInitializing()).thenCallRealMethod();
        RegionMapDestroy mapDestroy = new RegionMapDestroy(region, regionMap, Mockito.mock(CacheModificationLock.class));
        mapDestroy.destroy(event, inTokenMode, duringRI, cacheWrite, isEviction, expectedOldValue, removeRecoveredEntry);
        Mockito.verify(region).lockWhenRegionIsInitializing();
        assertThat(region.lockWhenRegionIsInitializing()).isFalse();
        Mockito.verify(region, Mockito.never()).unlockWhenRegionIsInitializing();
    }

    @Test
    public void destroyLockGIIClearLockWhenRegionIsInitializing() throws Exception {
        DistributedRegion region = Mockito.mock(DistributedRegion.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(region.isInitialized()).thenReturn(false);
        Mockito.when(region.lockWhenRegionIsInitializing()).thenCallRealMethod();
        RegionMapDestroy mapDestroy = new RegionMapDestroy(region, regionMap, Mockito.mock(CacheModificationLock.class));
        mapDestroy.destroy(event, inTokenMode, duringRI, cacheWrite, isEviction, expectedOldValue, removeRecoveredEntry);
        Mockito.verify(region).lockWhenRegionIsInitializing();
        assertThat(region.lockWhenRegionIsInitializing()).isTrue();
        Mockito.verify(region).unlockWhenRegionIsInitializing();
    }
}

