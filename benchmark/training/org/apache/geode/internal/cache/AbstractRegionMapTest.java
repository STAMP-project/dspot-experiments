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


import DataPolicy.REPLICATE;
import EnumListenerEvent.AFTER_UPDATE;
import Operation.CREATE;
import Operation.UPDATE;
import Scope.LOCAL;
import Token.DESTROYED;
import Token.INVALID;
import Token.REMOVED_PHASE1;
import Token.REMOVED_PHASE2;
import Token.TOMBSTONE;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.geode.cache.EntryNotFoundException;
import org.apache.geode.cache.EvictionAttributes;
import org.apache.geode.cache.Operation;
import org.apache.geode.cache.TransactionId;
import org.apache.geode.cache.client.internal.ServerRegionProxy;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.Version;
import org.apache.geode.internal.cache.entries.DiskEntry.RecoveredEntry;
import org.apache.geode.internal.cache.eviction.EvictableEntry;
import org.apache.geode.internal.cache.eviction.EvictionController;
import org.apache.geode.internal.cache.eviction.EvictionCounters;
import org.apache.geode.internal.cache.tier.sockets.ClientProxyMembershipID;
import org.apache.geode.internal.cache.versions.ConcurrentCacheModificationException;
import org.apache.geode.internal.cache.versions.RegionVersionVector;
import org.apache.geode.internal.cache.versions.VersionHolder;
import org.apache.geode.internal.cache.versions.VersionSource;
import org.apache.geode.internal.cache.versions.VersionStamp;
import org.apache.geode.internal.cache.versions.VersionTag;
import org.apache.geode.internal.util.concurrent.ConcurrentMapWithReusableEntries;
import org.apache.geode.internal.util.concurrent.CustomEntryConcurrentHashMap;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static AbstractRegionMap.FORCE_INVALIDATE_EVENT;
import static Token.DESTROYED;
import static Token.REMOVED_PHASE1;
import static Token.REMOVED_PHASE2;
import static Token.TOMBSTONE;


public class AbstractRegionMapTest {
    private static final Object KEY = "key";

    private static final EntryEventImpl UPDATEEVENT = Mockito.mock(EntryEventImpl.class);

    @Test
    public void shouldBeMockable() throws Exception {
        AbstractRegionMap mockAbstractRegionMap = Mockito.mock(AbstractRegionMap.class);
        RegionEntry mockRegionEntry = Mockito.mock(RegionEntry.class);
        VersionHolder mockVersionHolder = Mockito.mock(VersionHolder.class);
        Mockito.when(mockAbstractRegionMap.removeTombstone(ArgumentMatchers.eq(mockRegionEntry), ArgumentMatchers.eq(mockVersionHolder), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(true);
        assertThat(mockAbstractRegionMap.removeTombstone(mockRegionEntry, mockVersionHolder, true, true)).isTrue();
    }

    @Test
    public void invalidateOfNonExistentRegionThrowsEntryNotFound() {
        AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap();
        EntryEventImpl event = createEventForInvalidate(_getOwner());
        Mockito.when(_getOwner().isInitialized()).thenReturn(true);
        assertThatThrownBy(() -> arm.invalidate(event, true, false, false)).isInstanceOf(EntryNotFoundException.class);
        Mockito.verify(_getOwner(), Mockito.never()).basicInvalidatePart2(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        Mockito.verify(_getOwner(), Mockito.never()).invokeInvalidateCallbacks(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void invalidateOfNonExistentRegionThrowsEntryNotFoundWithForce() {
        FORCE_INVALIDATE_EVENT = true;
        AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap();
        EntryEventImpl event = createEventForInvalidate(_getOwner());
        Mockito.when(_getOwner().isInitialized()).thenReturn(true);
        assertThatThrownBy(() -> arm.invalidate(event, true, false, false)).isInstanceOf(EntryNotFoundException.class);
        Mockito.verify(_getOwner(), Mockito.never()).basicInvalidatePart2(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        Mockito.verify(_getOwner(), Mockito.times(1)).invokeInvalidateCallbacks(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void invalidateOfAlreadyInvalidEntryReturnsFalse() {
        AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap();
        EntryEventImpl event = createEventForInvalidate(_getOwner());
        // invalidate on region that is not initialized should create
        // entry in map as invalid.
        Mockito.when(_getOwner().isInitialized()).thenReturn(false);
        assertThatThrownBy(() -> arm.invalidate(event, true, false, false)).isInstanceOf(EntryNotFoundException.class);
        Mockito.when(_getOwner().isInitialized()).thenReturn(true);
        Assert.assertFalse(arm.invalidate(event, true, false, false));
        Mockito.verify(_getOwner(), Mockito.never()).basicInvalidatePart2(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        Mockito.verify(_getOwner(), Mockito.never()).invokeInvalidateCallbacks(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void invalidateOfAlreadyInvalidEntryReturnsFalseWithForce() {
        FORCE_INVALIDATE_EVENT = true;
        AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap();
        EntryEventImpl event = createEventForInvalidate(_getOwner());
        // invalidate on region that is not initialized should create
        // entry in map as invalid.
        Mockito.when(_getOwner().isInitialized()).thenReturn(false);
        assertThatThrownBy(() -> arm.invalidate(event, true, false, false)).isInstanceOf(EntryNotFoundException.class);
        Mockito.when(_getOwner().isInitialized()).thenReturn(true);
        Assert.assertFalse(arm.invalidate(event, true, false, false));
        Mockito.verify(_getOwner(), Mockito.never()).basicInvalidatePart2(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        Mockito.verify(_getOwner(), Mockito.times(1)).invokeInvalidateCallbacks(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void invalidateForceNewEntryOfAlreadyInvalidEntryReturnsFalse() {
        AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap();
        EntryEventImpl event = createEventForInvalidate(_getOwner());
        // invalidate on region that is not initialized should create
        // entry in map as invalid.
        Assert.assertTrue(arm.invalidate(event, true, true, false));
        Mockito.verify(_getOwner(), Mockito.times(1)).basicInvalidatePart2(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        Mockito.when(_getOwner().isInitialized()).thenReturn(true);
        Assert.assertFalse(arm.invalidate(event, true, true, false));
        Mockito.verify(_getOwner(), Mockito.times(1)).basicInvalidatePart2(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        Mockito.verify(_getOwner(), Mockito.never()).invokeInvalidateCallbacks(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void invalidateForceNewEntryOfAlreadyInvalidEntryReturnsFalseWithForce() {
        FORCE_INVALIDATE_EVENT = true;
        try {
            AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap();
            EntryEventImpl event = createEventForInvalidate(_getOwner());
            // invalidate on region that is not initialized should create
            // entry in map as invalid.
            Mockito.when(_getOwner().isInitialized()).thenReturn(false);
            Assert.assertTrue(arm.invalidate(event, true, true, false));
            Mockito.verify(_getOwner(), Mockito.times(1)).basicInvalidatePart2(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
            Mockito.verify(_getOwner(), Mockito.never()).invokeInvalidateCallbacks(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
            Mockito.when(_getOwner().isInitialized()).thenReturn(true);
            Assert.assertFalse(arm.invalidate(event, true, true, false));
            Mockito.verify(_getOwner(), Mockito.times(1)).basicInvalidatePart2(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
            Mockito.verify(_getOwner(), Mockito.times(1)).invokeInvalidateCallbacks(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
        } finally {
            FORCE_INVALIDATE_EVENT = false;
        }
    }

    @Test
    public void destroyWithEmptyRegionThrowsException() {
        AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap();
        EntryEventImpl event = createEventForDestroy(_getOwner());
        assertThatThrownBy(() -> arm.destroy(event, false, false, false, false, null, false)).isInstanceOf(EntryNotFoundException.class);
    }

    @Test
    public void destroyWithEmptyRegionInTokenModeAddsAToken() {
        final AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap();
        final EntryEventImpl event = createEventForDestroy(_getOwner());
        final Object expectedOldValue = null;
        final boolean inTokenMode = true;
        final boolean duringRI = false;
        assertThat(arm.destroy(event, inTokenMode, duringRI, false, false, expectedOldValue, false)).isTrue();
        assertThat(getEntryMap().containsKey(event.getKey())).isTrue();
        RegionEntry re = ((RegionEntry) (getEntryMap().get(event.getKey())));
        assertThat(re.getValueAsToken()).isEqualTo(DESTROYED);
        boolean invokeCallbacks = true;
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart2(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks));
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart3(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks), ArgumentMatchers.eq(expectedOldValue));
    }

    @Test
    public void destroyWithEmptyRegionInTokenModeWithRegionClearedExceptionDoesDestroy() throws Exception {
        CustomEntryConcurrentHashMap<String, EvictableEntry> map = Mockito.mock(CustomEntryConcurrentHashMap.class);
        EvictableEntry entry = Mockito.mock(EvictableEntry.class);
        Mockito.when(entry.destroy(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenThrow(RegionClearedException.class);
        Mockito.when(map.get(AbstractRegionMapTest.KEY)).thenReturn(null);
        RegionEntryFactory factory = Mockito.mock(RegionEntryFactory.class);
        Mockito.when(factory.createEntry(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(entry);
        final AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(false, map, factory);
        final EntryEventImpl event = createEventForDestroy(_getOwner());
        final Object expectedOldValue = null;
        final boolean inTokenMode = true;
        final boolean duringRI = false;
        assertThat(arm.destroy(event, inTokenMode, duringRI, false, false, expectedOldValue, false)).isTrue();
        boolean invokeCallbacks = true;
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart2(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(true), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks));
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart3(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks), ArgumentMatchers.eq(expectedOldValue));
    }

    @Test
    public void evictDestroyWithEmptyRegionInTokenModeDoesNothing() {
        final AbstractRegionMapTest.TestableVMLRURegionMap arm = new AbstractRegionMapTest.TestableVMLRURegionMap(true);
        final EntryEventImpl event = createEventForDestroy(_getOwner());
        final Object expectedOldValue = null;
        final boolean inTokenMode = true;
        final boolean duringRI = false;
        final boolean evict = true;
        assertThat(arm.destroy(event, inTokenMode, duringRI, false, evict, expectedOldValue, false)).isFalse();
        assertThat(getEntryMap().containsKey(event.getKey())).isFalse();
        Mockito.verify(_getOwner(), Mockito.never()).basicDestroyPart2(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        Mockito.verify(_getOwner(), Mockito.never()).basicDestroyPart3(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
    }

    @Test
    public void evictDestroyWithExistingTombstoneInTokenModeChangesToDestroyToken() {
        final AbstractRegionMapTest.TestableVMLRURegionMap arm = new AbstractRegionMapTest.TestableVMLRURegionMap(true);
        addEntry(arm, TOMBSTONE);
        final EntryEventImpl event = createEventForDestroy(_getOwner());
        final Object expectedOldValue = null;
        final boolean inTokenMode = true;
        final boolean duringRI = false;
        final boolean evict = true;
        assertThat(arm.destroy(event, inTokenMode, duringRI, false, evict, expectedOldValue, false)).isTrue();
        assertThat(getEntryMap().containsKey(event.getKey())).isTrue();
        RegionEntry re = ((RegionEntry) (getEntryMap().get(event.getKey())));
        assertThat(re.getValueAsToken()).isEqualTo(DESTROYED);
        boolean invokeCallbacks = true;
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart2(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks));
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart3(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks), ArgumentMatchers.eq(expectedOldValue));
    }

    @Test
    public void evictDestroyWithExistingTombstoneInUseByTransactionInTokenModeDoesNothing() throws RegionClearedException {
        CustomEntryConcurrentHashMap<String, EvictableEntry> map = Mockito.mock(CustomEntryConcurrentHashMap.class);
        EvictableEntry entry = Mockito.mock(EvictableEntry.class);
        Mockito.when(entry.isInUseByTransaction()).thenReturn(true);
        Mockito.when(entry.getValue()).thenReturn(TOMBSTONE);
        Mockito.when(map.get(AbstractRegionMapTest.KEY)).thenReturn(entry);
        final AbstractRegionMapTest.TestableVMLRURegionMap arm = new AbstractRegionMapTest.TestableVMLRURegionMap(true, map);
        final EntryEventImpl event = createEventForDestroy(_getOwner());
        final Object expectedOldValue = null;
        final boolean inTokenMode = true;
        final boolean duringRI = false;
        final boolean evict = true;
        assertThat(arm.destroy(event, inTokenMode, duringRI, false, evict, expectedOldValue, false)).isFalse();
        Mockito.verify(entry, Mockito.never()).destroy(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        Mockito.verify(_getOwner(), Mockito.never()).basicDestroyPart2(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        Mockito.verify(_getOwner(), Mockito.never()).basicDestroyPart3(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
    }

    @Test
    public void evictDestroyWithConcurrentChangeFromNullToInUseByTransactionInTokenModeDoesNothing() throws RegionClearedException {
        CustomEntryConcurrentHashMap<Object, EvictableEntry> map = Mockito.mock(CustomEntryConcurrentHashMap.class);
        EvictableEntry entry = Mockito.mock(EvictableEntry.class);
        Mockito.when(entry.isInUseByTransaction()).thenReturn(true);
        Mockito.when(map.get(AbstractRegionMapTest.KEY)).thenReturn(null);
        Mockito.when(map.putIfAbsent(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.any())).thenReturn(entry);
        final AbstractRegionMapTest.TestableVMLRURegionMap arm = new AbstractRegionMapTest.TestableVMLRURegionMap(true, map);
        final EntryEventImpl event = createEventForDestroy(_getOwner());
        final Object expectedOldValue = null;
        final boolean inTokenMode = true;
        final boolean duringRI = false;
        final boolean evict = true;
        assertThat(arm.destroy(event, inTokenMode, duringRI, false, evict, expectedOldValue, false)).isFalse();
        Mockito.verify(entry, Mockito.never()).destroy(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        Mockito.verify(_getOwner(), Mockito.never()).basicDestroyPart2(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        Mockito.verify(_getOwner(), Mockito.never()).basicDestroyPart3(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
    }

    @Test
    public void destroyWithConcurrentChangeFromNullToValidRetriesAndDoesDestroy() throws RegionClearedException {
        CustomEntryConcurrentHashMap<Object, EvictableEntry> map = Mockito.mock(CustomEntryConcurrentHashMap.class);
        EvictableEntry entry = Mockito.mock(EvictableEntry.class);
        Mockito.when(entry.getValue()).thenReturn("value");
        Mockito.when(entry.destroy(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(true);
        Mockito.when(map.get(AbstractRegionMapTest.KEY)).thenReturn(null).thenReturn(entry);
        Mockito.when(map.putIfAbsent(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.any())).thenReturn(entry);
        final AbstractRegionMapTest.TestableVMLRURegionMap arm = new AbstractRegionMapTest.TestableVMLRURegionMap(true, map);
        final EntryEventImpl event = createEventForDestroy(_getOwner());
        final Object expectedOldValue = null;
        final boolean inTokenMode = false;
        final boolean duringRI = false;
        final boolean evict = false;
        assertThat(arm.destroy(event, inTokenMode, duringRI, false, evict, expectedOldValue, false)).isTrue();
        Mockito.verify(entry, Mockito.times(1)).destroy(ArgumentMatchers.eq(_getOwner()), ArgumentMatchers.eq(event), ArgumentMatchers.eq(false), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(expectedOldValue), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        boolean invokeCallbacks = true;
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart2(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks));
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart3(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks), ArgumentMatchers.eq(expectedOldValue));
    }

    @Test
    public void destroyInTokenModeWithConcurrentChangeFromNullToRemovePhase2RetriesAndDoesDestroy() throws RegionClearedException {
        CustomEntryConcurrentHashMap<Object, EvictableEntry> map = Mockito.mock(CustomEntryConcurrentHashMap.class);
        EvictableEntry entry = Mockito.mock(EvictableEntry.class);
        Mockito.when(entry.isRemovedPhase2()).thenReturn(true);
        Mockito.when(entry.destroy(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(true);
        Mockito.when(map.get(AbstractRegionMapTest.KEY)).thenReturn(null);
        Mockito.when(map.putIfAbsent(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.any())).thenReturn(entry).thenReturn(null);
        final AbstractRegionMapTest.TestableVMLRURegionMap arm = new AbstractRegionMapTest.TestableVMLRURegionMap(true, map);
        final EntryEventImpl event = createEventForDestroy(_getOwner());
        final Object expectedOldValue = null;
        final boolean inTokenMode = true;
        final boolean duringRI = false;
        final boolean evict = false;
        assertThat(arm.destroy(event, inTokenMode, duringRI, false, evict, expectedOldValue, false)).isTrue();
        Mockito.verify(map).remove(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.eq(entry));
        Mockito.verify(map, Mockito.times(2)).putIfAbsent(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.any());
        Mockito.verify(entry, Mockito.never()).destroy(ArgumentMatchers.eq(_getOwner()), ArgumentMatchers.eq(event), ArgumentMatchers.eq(false), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(expectedOldValue), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        boolean invokeCallbacks = true;
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart2(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks));
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart3(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks), ArgumentMatchers.eq(expectedOldValue));
    }

    @Test
    public void destroyWithConcurrentChangeFromTombstoneToValidRetriesAndDoesDestroy() throws RegionClearedException {
        CustomEntryConcurrentHashMap<Object, EvictableEntry> map = Mockito.mock(CustomEntryConcurrentHashMap.class);
        EvictableEntry entry = Mockito.mock(EvictableEntry.class);
        Mockito.when(entry.getValue()).thenReturn("value");
        Mockito.when(entry.isTombstone()).thenReturn(true).thenReturn(false);
        Mockito.when(entry.destroy(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(true);
        Mockito.when(map.get(AbstractRegionMapTest.KEY)).thenReturn(entry);
        final AbstractRegionMapTest.TestableVMLRURegionMap arm = new AbstractRegionMapTest.TestableVMLRURegionMap(true, map);
        final EntryEventImpl event = createEventForDestroy(_getOwner());
        final Object expectedOldValue = null;
        final boolean inTokenMode = false;
        final boolean duringRI = false;
        final boolean evict = false;
        assertThat(arm.destroy(event, inTokenMode, duringRI, false, evict, expectedOldValue, false)).isTrue();
        Mockito.verify(entry, Mockito.times(1)).destroy(ArgumentMatchers.eq(_getOwner()), ArgumentMatchers.eq(event), ArgumentMatchers.eq(false), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(expectedOldValue), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        boolean invokeCallbacks = true;
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart2(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks));
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart3(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks), ArgumentMatchers.eq(expectedOldValue));
    }

    @Test
    public void destroyOfExistingEntryInTokenModeAddsAToken() {
        final AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap();
        addEntry(arm);
        final EntryEventImpl event = createEventForDestroy(_getOwner());
        final Object expectedOldValue = null;
        final boolean inTokenMode = true;
        final boolean duringRI = false;
        assertThat(arm.destroy(event, inTokenMode, duringRI, false, false, expectedOldValue, false)).isTrue();
        assertThat(getEntryMap().containsKey(event.getKey())).isTrue();
        RegionEntry re = ((RegionEntry) (getEntryMap().get(event.getKey())));
        assertThat(re.getValueAsToken()).isEqualTo(DESTROYED);
        boolean invokeCallbacks = true;
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart2(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks));
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart3(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks), ArgumentMatchers.eq(expectedOldValue));
    }

    @Test
    public void destroyOfExistingTombstoneInTokenModeWithConcurrencyChecksDoesNothing() {
        final AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(true);
        RegionVersionVector<?> versionVector = Mockito.mock(RegionVersionVector.class);
        Mockito.when(_getOwner().getVersionVector()).thenReturn(versionVector);
        final EntryEventImpl event = createEventForDestroy(_getOwner());
        VersionTag<?> versionTag = Mockito.mock(VersionTag.class);
        Mockito.when(versionTag.hasValidVersion()).thenReturn(true);
        event.setVersionTag(versionTag);
        addEntry(arm, TOMBSTONE);
        final Object expectedOldValue = null;
        final boolean inTokenMode = true;
        final boolean duringRI = false;
        assertThat(arm.destroy(event, inTokenMode, duringRI, false, false, expectedOldValue, false)).isTrue();
        assertThat(getEntryMap().containsKey(event.getKey())).isTrue();
        RegionEntry re = ((RegionEntry) (getEntryMap().get(event.getKey())));
        // why not DESTROY token?
        assertThat(re.getValueAsToken()).isEqualTo(TOMBSTONE);
        // since it was already destroyed why do we do the parts?
        boolean invokeCallbacks = true;
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart2(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks));
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart3(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks), ArgumentMatchers.eq(expectedOldValue));
    }

    @Test
    public void destroyOfExistingTombstoneWithConcurrencyChecksThrowsEntryNotFound() {
        final AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(true);
        RegionVersionVector<?> versionVector = Mockito.mock(RegionVersionVector.class);
        Mockito.when(_getOwner().getVersionVector()).thenReturn(versionVector);
        final EntryEventImpl event = createEventForDestroy(_getOwner());
        VersionTag<?> versionTag = Mockito.mock(VersionTag.class);
        Mockito.when(versionTag.hasValidVersion()).thenReturn(true);
        event.setVersionTag(versionTag);
        addEntry(arm, TOMBSTONE);
        final Object expectedOldValue = null;
        final boolean inTokenMode = false;
        final boolean duringRI = false;
        assertThatThrownBy(() -> arm.destroy(event, inTokenMode, duringRI, false, false, expectedOldValue, false)).isInstanceOf(EntryNotFoundException.class);
    }

    @Test
    public void destroyOfExistingTombstoneWithConcurrencyChecksAndRemoveRecoveredEntryDoesRemove() {
        final AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(true);
        RegionVersionVector<?> versionVector = Mockito.mock(RegionVersionVector.class);
        Mockito.when(_getOwner().getVersionVector()).thenReturn(versionVector);
        final EntryEventImpl event = createEventForDestroy(_getOwner());
        VersionTag<?> versionTag = Mockito.mock(VersionTag.class);
        Mockito.when(versionTag.hasValidVersion()).thenReturn(true);
        event.setVersionTag(versionTag);
        addEntry(arm, TOMBSTONE);
        final Object expectedOldValue = null;
        final boolean inTokenMode = false;
        final boolean duringRI = false;
        final boolean removeRecoveredEntry = true;
        assertThat(arm.destroy(event, inTokenMode, duringRI, false, false, expectedOldValue, removeRecoveredEntry)).isTrue();
        assertThat(getEntryMap().containsKey(event.getKey())).isFalse();
        boolean invokeCallbacks = true;
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart2(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks));
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart3(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks), ArgumentMatchers.eq(expectedOldValue));
    }

    @Test
    public void destroyOfExistingRemovePhase2WithConcurrencyChecksAndRemoveRecoveredEntryDoesRetryAndThrowsEntryNotFound() {
        final AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(true);
        RegionVersionVector<?> versionVector = Mockito.mock(RegionVersionVector.class);
        Mockito.when(_getOwner().getVersionVector()).thenReturn(versionVector);
        final EntryEventImpl event = createEventForDestroy(_getOwner());
        VersionTag<?> versionTag = Mockito.mock(VersionTag.class);
        Mockito.when(versionTag.hasValidVersion()).thenReturn(true);
        event.setVersionTag(versionTag);
        addEntry(arm, REMOVED_PHASE2);
        final Object expectedOldValue = null;
        final boolean inTokenMode = false;
        final boolean duringRI = false;
        final boolean removeRecoveredEntry = true;
        assertThatThrownBy(() -> arm.destroy(event, inTokenMode, duringRI, false, false, expectedOldValue, removeRecoveredEntry)).isInstanceOf(EntryNotFoundException.class);
    }

    @Test
    public void destroyOfExistingEntryRemovesEntryFromMapAndDoesNotifications() {
        final AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap();
        addEntry(arm);
        final EntryEventImpl event = createEventForDestroy(_getOwner());
        final Object expectedOldValue = null;
        final boolean inTokenMode = false;
        final boolean duringRI = false;
        assertThat(arm.destroy(event, inTokenMode, duringRI, false, false, expectedOldValue, false)).isTrue();
        assertThat(getEntryMap().containsKey(event.getKey())).isFalse();
        boolean invokeCallbacks = true;
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart2(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks));
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart3(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks), ArgumentMatchers.eq(expectedOldValue));
    }

    @Test
    public void destroyOfExistingEntryWithConcurrencyChecksAndNoVersionTagDestroysWithoutTombstone() {
        final AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(true);
        addEntry(arm);
        final EntryEventImpl event = createEventForDestroy(_getOwner());
        final Object expectedOldValue = null;
        final boolean inTokenMode = false;
        final boolean duringRI = false;
        assertThat(arm.destroy(event, inTokenMode, duringRI, false, false, expectedOldValue, false)).isTrue();
        // This might be a bug. It seems like we should have created a tombstone but we have no
        // version tag so that might be the cause of this bug.
        assertThat(getEntryMap().containsKey(event.getKey())).isFalse();
        boolean invokeCallbacks = true;
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart2(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks));
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart3(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks), ArgumentMatchers.eq(expectedOldValue));
    }

    @Test
    public void destroyOfExistingEntryWithConcurrencyChecksAddsTombstone() {
        final AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(true);
        RegionVersionVector versionVector = Mockito.mock(RegionVersionVector.class);
        Mockito.when(_getOwner().getVersionVector()).thenReturn(versionVector);
        addEntry(arm);
        final EntryEventImpl event = createEventForDestroy(_getOwner());
        VersionTag versionTag = Mockito.mock(VersionTag.class);
        Mockito.when(versionTag.hasValidVersion()).thenReturn(true);
        event.setVersionTag(versionTag);
        final Object expectedOldValue = null;
        final boolean inTokenMode = false;
        final boolean duringRI = false;
        assertThat(arm.destroy(event, inTokenMode, duringRI, false, false, expectedOldValue, false)).isTrue();
        assertThat(getEntryMap().containsKey(event.getKey())).isTrue();
        RegionEntry re = ((RegionEntry) (getEntryMap().get(event.getKey())));
        assertThat(re.getValueAsToken()).isEqualTo(TOMBSTONE);
        boolean invokeCallbacks = true;
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart2(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks));
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart3(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks), ArgumentMatchers.eq(expectedOldValue));
    }

    @Test
    public void evictDestroyOfExistingEntryWithConcurrencyChecksAddsTombstone() {
        final AbstractRegionMapTest.TestableVMLRURegionMap arm = new AbstractRegionMapTest.TestableVMLRURegionMap(true);
        RegionVersionVector<?> versionVector = Mockito.mock(RegionVersionVector.class);
        Mockito.when(_getOwner().getVersionVector()).thenReturn(versionVector);
        addEntry(arm);
        final EntryEventImpl event = createEventForDestroy(_getOwner());
        VersionTag<?> versionTag = Mockito.mock(VersionTag.class);
        Mockito.when(versionTag.hasValidVersion()).thenReturn(true);
        event.setVersionTag(versionTag);
        final Object expectedOldValue = null;
        final boolean inTokenMode = false;
        final boolean duringRI = false;
        final boolean evict = true;
        assertThat(arm.destroy(event, inTokenMode, duringRI, false, evict, expectedOldValue, false)).isTrue();
        assertThat(getEntryMap().containsKey(event.getKey())).isTrue();
        RegionEntry re = ((RegionEntry) (getEntryMap().get(event.getKey())));
        assertThat(re.getValueAsToken()).isEqualTo(TOMBSTONE);
        boolean invokeCallbacks = true;
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart2(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks));
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart3(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks), ArgumentMatchers.eq(expectedOldValue));
    }

    @Test
    public void destroyWithEmptyRegionWithConcurrencyChecksThrowsException() {
        AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(true);
        EntryEventImpl event = createEventForDestroy(_getOwner());
        assertThatThrownBy(() -> arm.destroy(event, false, false, false, false, null, false)).isInstanceOf(EntryNotFoundException.class);
    }

    @Test
    public void evictDestroyWithEmptyRegionWithConcurrencyChecksDoesNothing() {
        final AbstractRegionMapTest.TestableVMLRURegionMap arm = new AbstractRegionMapTest.TestableVMLRURegionMap(true);
        EntryEventImpl event = createEventForDestroy(_getOwner());
        assertThat(arm.destroy(event, false, false, false, true, null, false)).isFalse();
        Mockito.verify(_getOwner(), Mockito.never()).basicDestroyPart2(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        Mockito.verify(_getOwner(), Mockito.never()).basicDestroyPart3(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
        // This seems to be a bug. We should not leave an entry in the map
        // added by the destroy call if destroy returns false.
        assertThat(getEntryMap().containsKey(event.getKey())).isTrue();
        RegionEntry re = ((RegionEntry) (getEntryMap().get(event.getKey())));
        assertThat(re.getValueAsToken()).isEqualTo(REMOVED_PHASE1);
    }

    @Test
    public void evictDestroyWithEmptyRegionDoesNothing() {
        final AbstractRegionMapTest.TestableVMLRURegionMap arm = new AbstractRegionMapTest.TestableVMLRURegionMap(false);
        EntryEventImpl event = createEventForDestroy(_getOwner());
        assertThat(arm.destroy(event, false, false, false, true, null, false)).isFalse();
        Mockito.verify(_getOwner(), Mockito.never()).basicDestroyPart2(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        Mockito.verify(_getOwner(), Mockito.never()).basicDestroyPart3(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
        assertThat(getEntryMap().containsKey(event.getKey())).isFalse();
    }

    @Test
    public void destroyWithEmptyRegionWithConcurrencyChecksAddsATombstone() {
        final AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(true);
        RegionVersionVector versionVector = Mockito.mock(RegionVersionVector.class);
        Mockito.when(_getOwner().getVersionVector()).thenReturn(versionVector);
        final EntryEventImpl event = createEventForDestroy(_getOwner());
        VersionTag versionTag = Mockito.mock(VersionTag.class);
        Mockito.when(versionTag.hasValidVersion()).thenReturn(true);
        event.setVersionTag(versionTag);
        event.setOriginRemote(true);
        final Object expectedOldValue = null;
        final boolean inTokenMode = false;
        final boolean duringRI = false;
        assertThat(arm.destroy(event, inTokenMode, duringRI, false, false, expectedOldValue, false)).isTrue();
        assertThat(getEntryMap().containsKey(event.getKey())).isTrue();
        RegionEntry re = ((RegionEntry) (getEntryMap().get(event.getKey())));
        assertThat(re.getValueAsToken()).isEqualTo(TOMBSTONE);
        boolean invokeCallbacks = true;
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart2(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks));
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart3(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks), ArgumentMatchers.eq(expectedOldValue));
    }

    @Test
    public void destroyWithEmptyRegionWithConcurrencyChecksAndNullVersionTagAddsATombstone() {
        final AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(true);
        final EntryEventImpl event = createEventForDestroy(_getOwner());
        event.setOriginRemote(true);
        final Object expectedOldValue = null;
        final boolean inTokenMode = false;
        final boolean duringRI = false;
        assertThat(arm.destroy(event, inTokenMode, duringRI, false, false, expectedOldValue, false)).isTrue();
        assertThat(getEntryMap().containsKey(event.getKey())).isTrue();
        boolean invokeCallbacks = true;
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart2(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks));
        Mockito.verify(_getOwner(), Mockito.times(1)).basicDestroyPart3(ArgumentMatchers.any(), ArgumentMatchers.eq(event), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(duringRI), ArgumentMatchers.eq(invokeCallbacks), ArgumentMatchers.eq(expectedOldValue));
        // instead of a TOMBSTONE we leave an entry whose value is REMOVE_PHASE1
        // this looks like a bug. It is caused by some code in: AbstractRegionEntry.destroy()
        // that calls removePhase1 when the versionTag is null.
        // It seems like this code path needs to tell the higher levels
        // to call removeEntry
        RegionEntry re = ((RegionEntry) (getEntryMap().get(event.getKey())));
        assertThat(re.getValueAsToken()).isEqualTo(REMOVED_PHASE1);
    }

    @Test
    public void txApplyInvalidateDoesNotInvalidateRemovedToken() throws RegionClearedException {
        AbstractRegionMapTest.TxTestableAbstractRegionMap arm = new AbstractRegionMapTest.TxTestableAbstractRegionMap();
        Object newValue = "value";
        arm.txApplyPut(CREATE, AbstractRegionMapTest.KEY, newValue, false, new TXId(Mockito.mock(InternalDistributedMember.class), 1), Mockito.mock(TXRmtEvent.class), Mockito.mock(EventID.class), null, new ArrayList<EntryEventImpl>(), null, null, null, null, 1);
        RegionEntry re = getEntry(AbstractRegionMapTest.KEY);
        Assert.assertNotNull(re);
        Token[] removedTokens = new Token[]{ REMOVED_PHASE2, REMOVED_PHASE1, DESTROYED, TOMBSTONE };
        for (Token token : removedTokens) {
            verifyTxApplyInvalidate(arm, AbstractRegionMapTest.KEY, re, token);
        }
    }

    @Test
    public void updateRecoveredEntry_givenExistingDestroyedOrRemovedAndSettingToTombstone_neverCallsUpdateSizeOnRemove() {
        RecoveredEntry recoveredEntry = Mockito.mock(RecoveredEntry.class);
        RegionEntry regionEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(regionEntry.isTombstone()).thenReturn(false).thenReturn(true);
        Mockito.when(regionEntry.isDestroyedOrRemoved()).thenReturn(true);
        Mockito.when(regionEntry.getVersionStamp()).thenReturn(Mockito.mock(VersionStamp.class));
        AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(false, null, null, regionEntry);
        arm.updateRecoveredEntry(AbstractRegionMapTest.KEY, recoveredEntry);
        Mockito.verify(_getOwner(), Mockito.never()).updateSizeOnRemove(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
    }

    @Test
    public void updateRecoveredEntry_givenExistingRemovedNonTombstone_neverCallsUpdateSizeOnRemove() {
        RecoveredEntry recoveredEntry = Mockito.mock(RecoveredEntry.class);
        RegionEntry regionEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(regionEntry.isRemoved()).thenReturn(true);
        Mockito.when(regionEntry.isTombstone()).thenReturn(false);
        AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(false, null, null, regionEntry);
        arm.updateRecoveredEntry(AbstractRegionMapTest.KEY, recoveredEntry);
        Mockito.verify(_getOwner(), Mockito.never()).updateSizeOnRemove(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
    }

    @Test
    public void updateRecoveredEntry_givenNoExistingEntry_neverCallsUpdateSizeOnRemove() {
        RecoveredEntry recoveredEntry = Mockito.mock(RecoveredEntry.class);
        AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(false, null, null, null);
        arm.updateRecoveredEntry(AbstractRegionMapTest.KEY, recoveredEntry);
        Mockito.verify(_getOwner(), Mockito.never()).updateSizeOnRemove(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
    }

    @Test
    public void updateRecoveredEntry_givenExistingNonTombstoneAndSettingToTombstone_callsUpdateSizeOnRemove() {
        RecoveredEntry recoveredEntry = Mockito.mock(RecoveredEntry.class);
        RegionEntry regionEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(regionEntry.isTombstone()).thenReturn(false).thenReturn(true);
        Mockito.when(regionEntry.getVersionStamp()).thenReturn(Mockito.mock(VersionStamp.class));
        AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(false, null, null, regionEntry);
        arm.updateRecoveredEntry(AbstractRegionMapTest.KEY, recoveredEntry);
        Mockito.verify(_getOwner(), Mockito.times(1)).updateSizeOnRemove(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.anyInt());
    }

    @Test
    public void initialImagePut_givenPutIfAbsentReturningDestroyedOrRemovedEntry_neverCallsUpdateSizeOnRemove() throws RegionClearedException {
        ConcurrentMapWithReusableEntries map = Mockito.mock(ConcurrentMapWithReusableEntries.class);
        RegionEntry entry = Mockito.mock(RegionEntry.class);
        Mockito.when(entry.isDestroyedOrRemoved()).thenReturn(true);
        Mockito.when(entry.initialImagePut(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(true);
        VersionStamp versionStamp = Mockito.mock(VersionStamp.class);
        Mockito.when(entry.getVersionStamp()).thenReturn(versionStamp);
        Mockito.when(versionStamp.asVersionTag()).thenReturn(Mockito.mock(VersionTag.class));
        Mockito.when(map.putIfAbsent(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.any())).thenReturn(entry).thenReturn(null);
        AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(false, map, null);
        Mockito.when(_getOwner().getConcurrencyChecksEnabled()).thenReturn(true);
        Mockito.when(_getOwner().getServerProxy()).thenReturn(Mockito.mock(ServerRegionProxy.class));
        VersionTag versionTag = Mockito.mock(VersionTag.class);
        Mockito.when(versionTag.getMemberID()).thenReturn(Mockito.mock(VersionSource.class));
        arm.initialImagePut(AbstractRegionMapTest.KEY, 0, TOMBSTONE, false, false, versionTag, null, false);
        Mockito.verify(_getOwner(), Mockito.never()).updateSizeOnRemove(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
    }

    @Test
    public void initialImagePut_givenPutIfAbsentReturningNonTombstone_callsUpdateSizeOnRemove() throws RegionClearedException {
        ConcurrentMapWithReusableEntries map = Mockito.mock(ConcurrentMapWithReusableEntries.class);
        RegionEntry entry = Mockito.mock(RegionEntry.class);
        Mockito.when(entry.isTombstone()).thenReturn(false);
        Mockito.when(entry.isDestroyedOrRemoved()).thenReturn(false);
        Mockito.when(entry.initialImagePut(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(true);
        VersionStamp versionStamp = Mockito.mock(VersionStamp.class);
        Mockito.when(entry.getVersionStamp()).thenReturn(versionStamp);
        Mockito.when(versionStamp.asVersionTag()).thenReturn(Mockito.mock(VersionTag.class));
        Mockito.when(map.putIfAbsent(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.any())).thenReturn(entry).thenReturn(null);
        AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(false, map, null);
        Mockito.when(_getOwner().getConcurrencyChecksEnabled()).thenReturn(true);
        Mockito.when(_getOwner().getServerProxy()).thenReturn(Mockito.mock(ServerRegionProxy.class));
        VersionTag versionTag = Mockito.mock(VersionTag.class);
        Mockito.when(versionTag.getMemberID()).thenReturn(Mockito.mock(VersionSource.class));
        arm.initialImagePut(AbstractRegionMapTest.KEY, 0, TOMBSTONE, false, false, versionTag, null, false);
        Mockito.verify(_getOwner(), Mockito.times(1)).updateSizeOnRemove(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.anyInt());
    }

    @Test
    public void initialImagePut_givenPutIfAbsentReturningRemoveTokenOnFirstTryWillTryUntilRegionEntryIsPut() throws RegionClearedException {
        ConcurrentMapWithReusableEntries map = Mockito.mock(ConcurrentMapWithReusableEntries.class);
        RegionEntry entry = Mockito.mock(RegionEntry.class);
        Mockito.when(entry.isTombstone()).thenReturn(false);
        Mockito.when(entry.isDestroyedOrRemoved()).thenReturn(false);
        Mockito.when(entry.initialImagePut(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(true);
        RegionEntry removedTokenEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(removedTokenEntry.isRemovedPhase2()).thenReturn(true);
        VersionStamp versionStamp = Mockito.mock(VersionStamp.class);
        Mockito.when(entry.getVersionStamp()).thenReturn(versionStamp);
        Mockito.when(versionStamp.asVersionTag()).thenReturn(Mockito.mock(VersionTag.class));
        Answer returnRemovedTokenAnswer = new Answer() {
            private int putTimes = 0;

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if (((putTimes)++) == 0) {
                    return removedTokenEntry;
                }
                return entry;
            }
        };
        Mockito.when(map.putIfAbsent(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.any())).thenAnswer(returnRemovedTokenAnswer);
        AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(false, map, null);
        Mockito.when(_getOwner().getConcurrencyChecksEnabled()).thenReturn(true);
        Mockito.when(_getOwner().getServerProxy()).thenReturn(Mockito.mock(ServerRegionProxy.class));
        VersionTag versionTag = Mockito.mock(VersionTag.class);
        Mockito.when(versionTag.getMemberID()).thenReturn(Mockito.mock(VersionSource.class));
        arm.initialImagePut(AbstractRegionMapTest.KEY, 0, TOMBSTONE, false, false, versionTag, null, false);
        Mockito.verify(map, Mockito.times(2)).putIfAbsent(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.any());
    }

    @Test
    public void initialImagePut_givenPutIfAbsentReturningRegionEntryAndProcessVersionTagThrowsConcurrentCacheModificationException_createdEntryRemovedFromMapAndNotInitialImageInit() throws RegionClearedException {
        ConcurrentMapWithReusableEntries map = Mockito.mock(ConcurrentMapWithReusableEntries.class);
        RegionEntry entry = Mockito.mock(RegionEntry.class);
        Mockito.when(entry.isTombstone()).thenReturn(false);
        Mockito.when(entry.isDestroyedOrRemoved()).thenReturn(false);
        Mockito.when(entry.initialImagePut(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(true);
        VersionStamp versionStamp = Mockito.mock(VersionStamp.class);
        Mockito.when(entry.getVersionStamp()).thenReturn(versionStamp);
        Mockito.when(versionStamp.asVersionTag()).thenReturn(Mockito.mock(VersionTag.class));
        Mockito.doThrow(new ConcurrentCacheModificationException()).when(versionStamp).processVersionTag(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
        RegionEntry createdEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(createdEntry.getVersionStamp()).thenReturn(versionStamp);
        RegionEntryFactory factory = Mockito.mock(RegionEntryFactory.class);
        Mockito.when(factory.createEntry(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(createdEntry);
        AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(false, map, factory);
        Mockito.when(map.putIfAbsent(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.any())).thenReturn(entry);
        Mockito.when(_getOwner().getConcurrencyChecksEnabled()).thenReturn(true);
        Mockito.when(_getOwner().getServerProxy()).thenReturn(Mockito.mock(ServerRegionProxy.class));
        VersionTag versionTag = Mockito.mock(VersionTag.class);
        Mockito.when(versionTag.getMemberID()).thenReturn(Mockito.mock(VersionSource.class));
        arm.initialImagePut(AbstractRegionMapTest.KEY, 0, TOMBSTONE, false, false, versionTag, null, false);
        Mockito.verify(map, Mockito.times(1)).remove(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.eq(createdEntry));
        Mockito.verify(entry, Mockito.never()).initialImagePut(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void initialImagePut_givenPutIfAbsentReturningRegionEntryAndSameTombstoneWillAttemptToRemoveREAndInvokeNothingElse() throws RegionClearedException {
        ConcurrentMapWithReusableEntries map = Mockito.mock(ConcurrentMapWithReusableEntries.class);
        RegionEntry entry = Mockito.mock(RegionEntry.class);
        Mockito.when(entry.isTombstone()).thenReturn(false);
        Mockito.when(entry.isDestroyedOrRemoved()).thenReturn(false);
        Mockito.when(entry.initialImagePut(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(true);
        VersionStamp versionStamp = Mockito.mock(VersionStamp.class);
        Mockito.when(entry.getVersionStamp()).thenReturn(versionStamp);
        Mockito.when(entry.isTombstone()).thenReturn(true);
        AbstractRegionMapTest.TestableVersionTag versionTag = new AbstractRegionMapTest.TestableVersionTag();
        versionTag.setVersionSource(Mockito.mock(VersionSource.class));
        Mockito.when(versionStamp.asVersionTag()).thenReturn(versionTag);
        RegionEntry createdEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(createdEntry.getVersionStamp()).thenReturn(versionStamp);
        RegionEntryFactory factory = Mockito.mock(RegionEntryFactory.class);
        Mockito.when(factory.createEntry(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(createdEntry);
        AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(false, map, factory);
        Mockito.when(map.putIfAbsent(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.eq(createdEntry))).thenReturn(entry);
        Mockito.when(_getOwner().getConcurrencyChecksEnabled()).thenReturn(true);
        Mockito.when(_getOwner().getServerProxy()).thenReturn(Mockito.mock(ServerRegionProxy.class));
        arm.initialImagePut(AbstractRegionMapTest.KEY, 0, TOMBSTONE, false, false, versionTag, null, false);
        Mockito.verify(map, Mockito.times(1)).remove(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.eq(createdEntry));
        Mockito.verify(entry, Mockito.never()).initialImagePut(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void initialImagePut_givenPutIfAbsentReturningRegionEntryOldIsTombstone_callUnscheduleTombstone() throws RegionClearedException {
        ConcurrentMapWithReusableEntries map = Mockito.mock(ConcurrentMapWithReusableEntries.class);
        RegionEntry entry = Mockito.mock(RegionEntry.class);
        Mockito.when(entry.isTombstone()).thenReturn(true);
        Mockito.when(entry.isDestroyedOrRemoved()).thenReturn(false);
        Mockito.when(entry.initialImagePut(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(true);
        VersionStamp versionStamp = Mockito.mock(VersionStamp.class);
        Mockito.when(entry.getVersionStamp()).thenReturn(versionStamp);
        Mockito.when(versionStamp.asVersionTag()).thenReturn(Mockito.mock(VersionTag.class));
        RegionEntry createdEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(createdEntry.getVersionStamp()).thenReturn(versionStamp);
        Mockito.when(createdEntry.initialImagePut(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(true);
        RegionEntryFactory factory = Mockito.mock(RegionEntryFactory.class);
        Mockito.when(factory.createEntry(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(createdEntry);
        AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(false, map, factory);
        Mockito.when(map.putIfAbsent(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.any())).thenReturn(entry);
        Mockito.when(_getOwner().getConcurrencyChecksEnabled()).thenReturn(true);
        Mockito.when(_getOwner().getServerProxy()).thenReturn(Mockito.mock(ServerRegionProxy.class));
        VersionTag versionTag = Mockito.mock(VersionTag.class);
        Mockito.when(versionTag.getMemberID()).thenReturn(Mockito.mock(VersionSource.class));
        arm.initialImagePut(AbstractRegionMapTest.KEY, 0, TOMBSTONE, false, false, versionTag, null, false);
        Mockito.verify(_getOwner(), Mockito.times(1)).unscheduleTombstone(entry);
    }

    @Test
    public void initialImagePut_givenPutIfAbsentReturnsNullAndProcessVersionTagThrowsConcurrentCacheModificationException_createdEntryRemovedFromMapAndNotInitialImageInit() throws RegionClearedException {
        ConcurrentMapWithReusableEntries map = Mockito.mock(ConcurrentMapWithReusableEntries.class);
        Mockito.when(map.putIfAbsent(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.any())).thenReturn(null);
        RegionEntryFactory factory = Mockito.mock(RegionEntryFactory.class);
        VersionStamp mockVersionStamp = Mockito.mock(VersionStamp.class);
        Mockito.doThrow(new ConcurrentCacheModificationException()).when(mockVersionStamp).processVersionTag(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
        RegionEntry createdEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(createdEntry.getVersionStamp()).thenReturn(mockVersionStamp);
        Mockito.when(factory.createEntry(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(createdEntry);
        final AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(false, map, factory);
        Mockito.when(_getOwner().getConcurrencyChecksEnabled()).thenReturn(true);
        Mockito.when(_getOwner().getServerProxy()).thenReturn(Mockito.mock(ServerRegionProxy.class));
        VersionTag versionTag = Mockito.mock(VersionTag.class);
        Mockito.when(versionTag.getMemberID()).thenReturn(Mockito.mock(VersionSource.class));
        arm.initialImagePut(AbstractRegionMapTest.KEY, 0, TOMBSTONE, false, false, versionTag, null, false);
        Mockito.verify(map, Mockito.times(1)).remove(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.eq(createdEntry));
        Mockito.verify(createdEntry, Mockito.never()).initialImageInit(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void initialImagePut_givenPutIfAbsentReturnsNullAndValueIsTombstone_callToScheduleTombstone() throws RegionClearedException {
        ConcurrentMapWithReusableEntries map = Mockito.mock(ConcurrentMapWithReusableEntries.class);
        Mockito.when(map.putIfAbsent(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.any())).thenReturn(null);
        RegionEntryFactory factory = Mockito.mock(RegionEntryFactory.class);
        VersionStamp mockVersionStamp = Mockito.mock(VersionStamp.class);
        RegionEntry createdEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(createdEntry.getVersionStamp()).thenReturn(mockVersionStamp);
        Mockito.when(createdEntry.initialImageInit(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(true);
        Mockito.when(factory.createEntry(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(createdEntry);
        final AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(false, map, factory);
        Mockito.when(_getOwner().getConcurrencyChecksEnabled()).thenReturn(true);
        Mockito.when(_getOwner().getServerProxy()).thenReturn(Mockito.mock(ServerRegionProxy.class));
        VersionTag versionTag = Mockito.mock(VersionTag.class);
        Mockito.when(versionTag.getMemberID()).thenReturn(Mockito.mock(VersionSource.class));
        arm.initialImagePut(AbstractRegionMapTest.KEY, 0, TOMBSTONE, false, false, versionTag, null, false);
        Mockito.verify(_getOwner(), Mockito.times(1)).scheduleTombstone(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void initialImagePut_givenPutIfAbsentReturnsNullAndValueIsNotTombstone_callUpdateSizeOnCreate() throws RegionClearedException {
        ConcurrentMapWithReusableEntries map = Mockito.mock(ConcurrentMapWithReusableEntries.class);
        Mockito.when(map.putIfAbsent(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.any())).thenReturn(null);
        RegionEntryFactory factory = Mockito.mock(RegionEntryFactory.class);
        VersionStamp mockVersionStamp = Mockito.mock(VersionStamp.class);
        RegionEntry createdEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(createdEntry.getVersionStamp()).thenReturn(mockVersionStamp);
        Mockito.when(createdEntry.initialImageInit(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(true);
        Mockito.when(factory.createEntry(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(createdEntry);
        final AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(false, map, factory);
        Mockito.when(_getOwner().getConcurrencyChecksEnabled()).thenReturn(true);
        Mockito.when(_getOwner().getServerProxy()).thenReturn(Mockito.mock(ServerRegionProxy.class));
        VersionTag versionTag = Mockito.mock(VersionTag.class);
        Mockito.when(versionTag.getMemberID()).thenReturn(Mockito.mock(VersionSource.class));
        arm.initialImagePut(AbstractRegionMapTest.KEY, 0, "", false, false, versionTag, null, false);
        Mockito.verify(_getOwner(), Mockito.times(1)).updateSizeOnCreate(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
    }

    @Test
    public void initialImagePut_ExceptionThrownWhenCreatingNewRegionEntry_removeDoesNotGetCalled() throws RegionClearedException {
        ConcurrentMapWithReusableEntries map = Mockito.mock(ConcurrentMapWithReusableEntries.class);
        Mockito.when(map.putIfAbsent(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.any())).thenReturn(null);
        RegionEntryFactory factory = Mockito.mock(RegionEntryFactory.class);
        VersionStamp mockVersionStamp = Mockito.mock(VersionStamp.class);
        RegionEntry createdEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(createdEntry.getVersionStamp()).thenReturn(mockVersionStamp);
        Mockito.when(createdEntry.initialImageInit(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(true);
        Mockito.when(factory.createEntry(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(new RuntimeException());
        final AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(false, map, factory);
        Mockito.when(_getOwner().getConcurrencyChecksEnabled()).thenReturn(true);
        Mockito.when(_getOwner().getServerProxy()).thenReturn(Mockito.mock(ServerRegionProxy.class));
        VersionTag versionTag = Mockito.mock(VersionTag.class);
        Mockito.when(versionTag.getMemberID()).thenReturn(Mockito.mock(VersionSource.class));
        try {
            arm.initialImagePut(AbstractRegionMapTest.KEY, 0, "", false, false, versionTag, null, false);
        } catch (RuntimeException e) {
            // expected to be thrown, we set up the test this way
        }
        Mockito.verify(map, Mockito.never()).remove(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.eq(createdEntry));
    }

    @Test
    public void txApplyDestroy_givenExistingDestroyedOrRemovedEntry_neverCallsUpdateSizeOnRemove() {
        RegionEntry regionEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(regionEntry.isTombstone()).thenReturn(false);
        Mockito.when(regionEntry.isDestroyedOrRemoved()).thenReturn(true);
        Mockito.when(regionEntry.getVersionStamp()).thenReturn(Mockito.mock(VersionStamp.class));
        TXId txId = Mockito.mock(TXId.class);
        Mockito.when(txId.getMemberId()).thenReturn(Mockito.mock(InternalDistributedMember.class));
        AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(false, null, null, regionEntry);
        arm.txApplyDestroy(AbstractRegionMapTest.KEY, txId, null, false, false, null, null, null, new ArrayList(), null, null, false, null, null, 0);
        Mockito.verify(_getOwner(), Mockito.never()).updateSizeOnRemove(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
    }

    @Test
    public void txApplyDestroy_givenExistingNonTombstone_callsUpdateSizeOnRemove() {
        RegionEntry regionEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(regionEntry.isTombstone()).thenReturn(false);
        Mockito.when(regionEntry.getVersionStamp()).thenReturn(Mockito.mock(VersionStamp.class));
        TXId txId = Mockito.mock(TXId.class);
        Mockito.when(txId.getMemberId()).thenReturn(Mockito.mock(InternalDistributedMember.class));
        AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(false, null, null, regionEntry);
        arm.txApplyDestroy(AbstractRegionMapTest.KEY, txId, null, false, false, null, null, null, new ArrayList(), null, null, false, null, null, 0);
        Mockito.verify(_getOwner(), Mockito.times(1)).updateSizeOnRemove(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.anyInt());
    }

    @Test
    public void txApplyDestroy_givenPutIfAbsentReturningDestroyedOrRemovedEntry_neverCallsUpdateSizeOnRemove() throws RegionClearedException {
        ConcurrentMapWithReusableEntries map = Mockito.mock(ConcurrentMapWithReusableEntries.class);
        RegionEntry entry = Mockito.mock(RegionEntry.class);
        Mockito.when(entry.isTombstone()).thenReturn(false);
        Mockito.when(entry.isDestroyedOrRemoved()).thenReturn(true);
        VersionStamp versionStamp = Mockito.mock(VersionStamp.class);
        Mockito.when(entry.getVersionStamp()).thenReturn(versionStamp);
        Mockito.when(versionStamp.asVersionTag()).thenReturn(Mockito.mock(VersionTag.class));
        Mockito.when(map.putIfAbsent(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.any())).thenReturn(entry).thenReturn(null);
        TXId txId = Mockito.mock(TXId.class);
        Mockito.when(txId.getMemberId()).thenReturn(Mockito.mock(InternalDistributedMember.class));
        AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(false, map, null);
        Mockito.when(_getOwner().getConcurrencyChecksEnabled()).thenReturn(true);
        arm.txApplyDestroy(AbstractRegionMapTest.KEY, txId, null, false, false, null, null, null, new ArrayList(), null, null, false, null, null, 0);
        Mockito.verify(_getOwner(), Mockito.never()).updateSizeOnRemove(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
    }

    @Test
    public void txApplyDestroy_givenPutIfAbsentReturningNonTombstone_callsUpdateSizeOnRemove() throws RegionClearedException {
        ConcurrentMapWithReusableEntries map = Mockito.mock(ConcurrentMapWithReusableEntries.class);
        RegionEntry entry = Mockito.mock(RegionEntry.class);
        Mockito.when(entry.getKey()).thenReturn(AbstractRegionMapTest.KEY);
        Mockito.when(entry.isTombstone()).thenReturn(false);
        VersionStamp versionStamp = Mockito.mock(VersionStamp.class);
        Mockito.when(entry.getVersionStamp()).thenReturn(versionStamp);
        Mockito.when(versionStamp.asVersionTag()).thenReturn(Mockito.mock(VersionTag.class));
        Mockito.when(map.putIfAbsent(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.any())).thenReturn(entry).thenReturn(null);
        TXId txId = Mockito.mock(TXId.class);
        Mockito.when(txId.getMemberId()).thenReturn(Mockito.mock(InternalDistributedMember.class));
        AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(false, map, null);
        Mockito.when(_getOwner().getConcurrencyChecksEnabled()).thenReturn(true);
        arm.txApplyDestroy(AbstractRegionMapTest.KEY, txId, null, false, false, null, null, null, new ArrayList(), null, null, false, null, null, 0);
        Mockito.verify(_getOwner(), Mockito.times(1)).updateSizeOnRemove(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.anyInt());
    }

    @Test
    public void txApplyDestroy_givenFactory_neverCallsUpdateSizeOnRemove() throws RegionClearedException {
        ConcurrentMapWithReusableEntries map = Mockito.mock(ConcurrentMapWithReusableEntries.class);
        RegionEntry entry = Mockito.mock(RegionEntry.class);
        TXId txId = Mockito.mock(TXId.class);
        Mockito.when(txId.getMemberId()).thenReturn(Mockito.mock(InternalDistributedMember.class));
        RegionEntryFactory factory = Mockito.mock(RegionEntryFactory.class);
        Mockito.when(factory.createEntry(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(entry);
        AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(false, map, factory);
        Mockito.when(_getOwner().getConcurrencyChecksEnabled()).thenReturn(true);
        arm.txApplyDestroy(AbstractRegionMapTest.KEY, txId, null, false, false, null, null, null, new ArrayList(), null, null, false, null, null, 0);
        Mockito.verify(_getOwner(), Mockito.never()).updateSizeOnCreate(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
    }

    /**
     * TestableAbstractRegionMap
     */
    private static class TestableAbstractRegionMap extends AbstractRegionMap {
        private final RegionEntry regionEntryForGetEntry;

        protected TestableAbstractRegionMap() {
            this(false);
        }

        protected TestableAbstractRegionMap(boolean withConcurrencyChecks) {
            this(withConcurrencyChecks, null, null);
        }

        protected TestableAbstractRegionMap(boolean withConcurrencyChecks, ConcurrentMapWithReusableEntries map, RegionEntryFactory factory) {
            this(withConcurrencyChecks, false, map, factory, null);
        }

        protected TestableAbstractRegionMap(boolean withConcurrencyChecks, ConcurrentMapWithReusableEntries map, RegionEntryFactory factory, RegionEntry regionEntryForGetEntry) {
            this(withConcurrencyChecks, false, map, factory, regionEntryForGetEntry);
        }

        protected TestableAbstractRegionMap(boolean withConcurrencyChecks, boolean isDistributedRegion, ConcurrentMapWithReusableEntries map, RegionEntryFactory factory, RegionEntry regionEntryForGetEntry) {
            super(null);
            this.regionEntryForGetEntry = regionEntryForGetEntry;
            LocalRegion owner = (isDistributedRegion) ? Mockito.mock(DistributedRegion.class, Mockito.RETURNS_DEEP_STUBS) : Mockito.mock(LocalRegion.class);
            CachePerfStats cachePerfStats = Mockito.mock(CachePerfStats.class);
            Mockito.when(owner.getCachePerfStats()).thenReturn(cachePerfStats);
            Mockito.when(owner.getConcurrencyChecksEnabled()).thenReturn(withConcurrencyChecks);
            Mockito.when(owner.getDataPolicy()).thenReturn(REPLICATE);
            Mockito.when(owner.getScope()).thenReturn(LOCAL);
            Mockito.when(owner.isInitialized()).thenReturn(true);
            Mockito.doThrow(EntryNotFoundException.class).when(owner).checkEntryNotFound(ArgumentMatchers.any());
            initialize(owner, new Attributes(), null, false);
            if (map != null) {
                setEntryMap(map);
            }
            if (factory != null) {
                setEntryFactory(factory);
            }
        }

        @Override
        public RegionEntry getEntry(Object key) {
            if ((this.regionEntryForGetEntry) != null) {
                return this.regionEntryForGetEntry;
            } else {
                return getEntry(key);
            }
        }
    }

    /**
     * TestableVMLRURegionMap
     */
    private static class TestableVMLRURegionMap extends VMLRURegionMap {
        private static EvictionAttributes evictionAttributes = EvictionAttributes.createLRUEntryAttributes();

        protected TestableVMLRURegionMap() {
            this(false);
        }

        private static LocalRegion createOwner(boolean withConcurrencyChecks) {
            LocalRegion owner = Mockito.mock(LocalRegion.class);
            CachePerfStats cachePerfStats = Mockito.mock(CachePerfStats.class);
            Mockito.when(owner.getCachePerfStats()).thenReturn(cachePerfStats);
            Mockito.when(owner.getEvictionAttributes()).thenReturn(AbstractRegionMapTest.TestableVMLRURegionMap.evictionAttributes);
            Mockito.when(owner.getConcurrencyChecksEnabled()).thenReturn(withConcurrencyChecks);
            Mockito.when(owner.getDataPolicy()).thenReturn(REPLICATE);
            Mockito.doThrow(EntryNotFoundException.class).when(owner).checkEntryNotFound(ArgumentMatchers.any());
            return owner;
        }

        private static EvictionController createEvictionController() {
            EvictionController result = Mockito.mock(EvictionController.class);
            Mockito.when(result.getEvictionAlgorithm()).thenReturn(AbstractRegionMapTest.TestableVMLRURegionMap.evictionAttributes.getAlgorithm());
            EvictionCounters evictionCounters = Mockito.mock(EvictionCounters.class);
            Mockito.when(result.getCounters()).thenReturn(evictionCounters);
            return result;
        }

        protected TestableVMLRURegionMap(boolean withConcurrencyChecks) {
            super(AbstractRegionMapTest.TestableVMLRURegionMap.createOwner(withConcurrencyChecks), new Attributes(), null, AbstractRegionMapTest.TestableVMLRURegionMap.createEvictionController());
        }

        protected TestableVMLRURegionMap(boolean withConcurrencyChecks, ConcurrentMapWithReusableEntries hashMap) {
            this(withConcurrencyChecks);
            setEntryMap(hashMap);
        }
    }

    /**
     * TxTestableAbstractRegionMap
     */
    private static class TxTestableAbstractRegionMap extends AbstractRegionMap {
        protected TxTestableAbstractRegionMap(boolean isInitialized) {
            super(null);
            InternalRegion owner;
            if (isInitialized) {
                owner = Mockito.mock(LocalRegion.class);
                Mockito.when(owner.isInitialized()).thenReturn(true);
            } else {
                owner = Mockito.mock(DistributedRegion.class);
                Mockito.when(owner.isInitialized()).thenReturn(false);
            }
            KeyInfo keyInfo = Mockito.mock(KeyInfo.class);
            Mockito.when(keyInfo.getKey()).thenReturn(AbstractRegionMapTest.KEY);
            Mockito.when(owner.getKeyInfo(ArgumentMatchers.eq(AbstractRegionMapTest.KEY), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(keyInfo);
            Mockito.when(owner.getMyId()).thenReturn(Mockito.mock(InternalDistributedMember.class));
            Mockito.when(owner.getCache()).thenReturn(Mockito.mock(InternalCache.class));
            Mockito.when(owner.isAllEvents()).thenReturn(true);
            Mockito.when(owner.shouldNotifyBridgeClients()).thenReturn(true);
            Mockito.when(owner.lockWhenRegionIsInitializing()).thenCallRealMethod();
            initialize(owner, new Attributes(), null, false);
        }

        protected TxTestableAbstractRegionMap() {
            this(true);
        }
    }

    @Test
    public void txApplyPutOnSecondaryConstructsPendingCallbacksWhenRegionEntryExists() throws Exception {
        AbstractRegionMap arm = new AbstractRegionMapTest.TxRegionEntryTestableAbstractRegionMap();
        List<EntryEventImpl> pendingCallbacks = new ArrayList<>();
        TXId txId = new TXId(Mockito.mock(InternalDistributedMember.class), 1);
        TXRmtEvent txRmtEvent = Mockito.mock(TXRmtEvent.class);
        EventID eventId = Mockito.mock(EventID.class);
        Object newValue = "value";
        arm.txApplyPut(UPDATE, AbstractRegionMapTest.KEY, newValue, false, txId, txRmtEvent, eventId, null, pendingCallbacks, null, null, null, null, 1);
        Assert.assertEquals(1, pendingCallbacks.size());
        Mockito.verify(arm._getOwner(), Mockito.times(1)).txApplyPutPart2(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        Mockito.verify(arm._getOwner(), Mockito.never()).invokeTXCallbacks(AFTER_UPDATE, AbstractRegionMapTest.UPDATEEVENT, false);
    }

    @Test
    public void txApplyPutOnPrimaryConstructsPendingCallbacksWhenPutIfAbsentReturnsExistingEntry() throws Exception {
        AbstractRegionMap arm = new AbstractRegionMapTest.TxPutIfAbsentTestableAbstractRegionMap();
        List<EntryEventImpl> pendingCallbacks = new ArrayList<>();
        TXId txId = new TXId(arm._getOwner().getMyId(), 1);
        EventID eventId = Mockito.mock(EventID.class);
        TXEntryState txEntryState = Mockito.mock(TXEntryState.class);
        Object newValue = "value";
        arm.txApplyPut(UPDATE, AbstractRegionMapTest.KEY, newValue, false, txId, null, eventId, null, pendingCallbacks, null, null, txEntryState, null, 1);
        Assert.assertEquals(1, pendingCallbacks.size());
        Mockito.verify(arm._getOwner(), Mockito.times(1)).txApplyPutPart2(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        Mockito.verify(arm._getOwner(), Mockito.never()).invokeTXCallbacks(AFTER_UPDATE, AbstractRegionMapTest.UPDATEEVENT, false);
    }

    @Test
    public void txApplyPutOnSecondaryNotifiesClientsWhenRegionEntryIsRemoved() throws Exception {
        AbstractRegionMap arm = new AbstractRegionMapTest.TxRemovedRegionEntryTestableAbstractRegionMap();
        List<EntryEventImpl> pendingCallbacks = new ArrayList<>();
        TXId txId = new TXId(Mockito.mock(InternalDistributedMember.class), 1);
        TXRmtEvent txRmtEvent = Mockito.mock(TXRmtEvent.class);
        EventID eventId = Mockito.mock(EventID.class);
        Object newValue = "value";
        arm.txApplyPut(UPDATE, AbstractRegionMapTest.KEY, newValue, false, txId, txRmtEvent, eventId, null, pendingCallbacks, null, null, null, null, 1);
        Assert.assertEquals(0, pendingCallbacks.size());
        Mockito.verify(arm._getOwner(), Mockito.never()).txApplyPutPart2(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        Mockito.verify(arm._getOwner(), Mockito.times(1)).invokeTXCallbacks(AFTER_UPDATE, AbstractRegionMapTest.UPDATEEVENT, false);
    }

    @Test
    public void txApplyPutOnSecondaryNotifiesClientsWhenRegionEntryIsNull() throws Exception {
        AbstractRegionMap arm = new AbstractRegionMapTest.TxNoRegionEntryTestableAbstractRegionMap();
        List<EntryEventImpl> pendingCallbacks = new ArrayList<>();
        TXId txId = new TXId(Mockito.mock(InternalDistributedMember.class), 1);
        TXRmtEvent txRmtEvent = Mockito.mock(TXRmtEvent.class);
        EventID eventId = Mockito.mock(EventID.class);
        Object newValue = "value";
        arm.txApplyPut(UPDATE, AbstractRegionMapTest.KEY, newValue, false, txId, txRmtEvent, eventId, null, pendingCallbacks, null, null, null, null, 1);
        Assert.assertEquals(0, pendingCallbacks.size());
        Mockito.verify(arm._getOwner(), Mockito.never()).txApplyPutPart2(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        Mockito.verify(arm._getOwner(), Mockito.times(1)).invokeTXCallbacks(AFTER_UPDATE, AbstractRegionMapTest.UPDATEEVENT, false);
    }

    @Test
    public void txApplyPutDoesNotLockWhenRegionIsInitialized() {
        AbstractRegionMap arm = new AbstractRegionMapTest.TxTestableAbstractRegionMap();
        TXId txId = Mockito.mock(TXId.class, Mockito.RETURNS_DEEP_STUBS);
        EventID eventId = Mockito.mock(EventID.class);
        TXRmtEvent txRmtEvent = Mockito.mock(TXRmtEvent.class);
        arm.txApplyPut(UPDATE, AbstractRegionMapTest.KEY, "", false, txId, txRmtEvent, eventId, null, new ArrayList(), null, null, null, null, 1);
        Mockito.verify(arm._getOwner()).lockWhenRegionIsInitializing();
        assertThat(arm._getOwner().lockWhenRegionIsInitializing()).isFalse();
        Mockito.verify(arm._getOwner(), Mockito.never()).unlockWhenRegionIsInitializing();
    }

    @Test
    public void txApplyPutLockWhenRegionIsInitializing() {
        AbstractRegionMap arm = new AbstractRegionMapTest.TxTestableAbstractRegionMap(false);
        TXId txId = Mockito.mock(TXId.class, Mockito.RETURNS_DEEP_STUBS);
        EventID eventId = Mockito.mock(EventID.class);
        TXRmtEvent txRmtEvent = Mockito.mock(TXRmtEvent.class);
        arm.txApplyPut(UPDATE, AbstractRegionMapTest.KEY, "", false, txId, txRmtEvent, eventId, null, new ArrayList(), null, null, null, null, 1);
        Mockito.verify(arm._getOwner()).lockWhenRegionIsInitializing();
        assertThat(arm._getOwner().lockWhenRegionIsInitializing()).isTrue();
        Mockito.verify(arm._getOwner()).unlockWhenRegionIsInitializing();
    }

    @Test
    public void txApplyDestroyDoesNotLockWhenRegionIsInitialized() {
        AbstractRegionMap arm = new AbstractRegionMapTest.TxTestableAbstractRegionMap();
        TXId txId = Mockito.mock(TXId.class, Mockito.RETURNS_DEEP_STUBS);
        arm.txApplyDestroy(AbstractRegionMapTest.KEY, txId, null, false, false, null, null, null, new ArrayList(), null, null, true, null, null, 0);
        Mockito.verify(arm._getOwner()).lockWhenRegionIsInitializing();
        assertThat(arm._getOwner().lockWhenRegionIsInitializing()).isFalse();
        Mockito.verify(arm._getOwner(), Mockito.never()).unlockWhenRegionIsInitializing();
    }

    @Test
    public void txApplyDestroyLockWhenRegionIsInitializing() {
        AbstractRegionMap arm = new AbstractRegionMapTest.TxTestableAbstractRegionMap(false);
        TXId txId = Mockito.mock(TXId.class, Mockito.RETURNS_DEEP_STUBS);
        arm.txApplyDestroy(AbstractRegionMapTest.KEY, txId, null, false, false, null, null, null, new ArrayList(), null, null, true, null, null, 0);
        Mockito.verify(arm._getOwner()).lockWhenRegionIsInitializing();
        assertThat(arm._getOwner().lockWhenRegionIsInitializing()).isTrue();
        Mockito.verify(arm._getOwner()).unlockWhenRegionIsInitializing();
    }

    @Test
    public void txApplyInvalidateDoesNotLockWhenRegionIsInitialized() {
        AbstractRegionMap arm = new AbstractRegionMapTest.TxTestableAbstractRegionMap();
        TXId txId = Mockito.mock(TXId.class, Mockito.RETURNS_DEEP_STUBS);
        arm.txApplyInvalidate(new Object(), INVALID, false, txId, Mockito.mock(TXRmtEvent.class), false, Mockito.mock(EventID.class), null, new ArrayList<EntryEventImpl>(), null, null, null, null, 1);
        Mockito.verify(arm._getOwner()).lockWhenRegionIsInitializing();
        assertThat(arm._getOwner().lockWhenRegionIsInitializing()).isFalse();
        Mockito.verify(arm._getOwner(), Mockito.never()).unlockWhenRegionIsInitializing();
    }

    @Test
    public void txApplyInvalidateLockWhenRegionIsInitializing() {
        AbstractRegionMap arm = new AbstractRegionMapTest.TxTestableAbstractRegionMap(false);
        TXId txId = Mockito.mock(TXId.class, Mockito.RETURNS_DEEP_STUBS);
        arm.txApplyInvalidate(new Object(), INVALID, false, txId, Mockito.mock(TXRmtEvent.class), false, Mockito.mock(EventID.class), null, new ArrayList<EntryEventImpl>(), null, null, null, null, 1);
        Mockito.verify(arm._getOwner()).lockWhenRegionIsInitializing();
        assertThat(arm._getOwner().lockWhenRegionIsInitializing()).isTrue();
        Mockito.verify(arm._getOwner()).unlockWhenRegionIsInitializing();
    }

    @Test
    public void invalidateDoesNotLockWhenRegionIsInitialized() {
        AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(false, true, Mockito.mock(ConcurrentMapWithReusableEntries.class), Mockito.mock(RegionEntryFactory.class), Mockito.mock(RegionEntry.class));
        EntryEventImpl event = createEventForInvalidate(_getOwner());
        Mockito.when(_getOwner().isInitialized()).thenReturn(true);
        Mockito.when(_getOwner().lockWhenRegionIsInitializing()).thenCallRealMethod();
        arm.invalidate(event, false, false, false);
        Mockito.verify(_getOwner()).lockWhenRegionIsInitializing();
        assertThat(_getOwner().lockWhenRegionIsInitializing()).isFalse();
        Mockito.verify(_getOwner(), Mockito.never()).unlockWhenRegionIsInitializing();
    }

    @Test
    public void invalidateLocksWhenRegionIsInitializing() {
        AbstractRegionMapTest.TestableAbstractRegionMap arm = new AbstractRegionMapTest.TestableAbstractRegionMap(false, true, Mockito.mock(ConcurrentMapWithReusableEntries.class), Mockito.mock(RegionEntryFactory.class), Mockito.mock(RegionEntry.class));
        EntryEventImpl event = createEventForInvalidate(_getOwner());
        Mockito.when(_getOwner().isInitialized()).thenReturn(false);
        Mockito.when(_getOwner().lockWhenRegionIsInitializing()).thenCallRealMethod();
        arm.invalidate(event, false, false, false);
        Mockito.verify(_getOwner()).lockWhenRegionIsInitializing();
        assertThat(_getOwner().lockWhenRegionIsInitializing()).isTrue();
        Mockito.verify(_getOwner()).unlockWhenRegionIsInitializing();
    }

    private static class TxNoRegionEntryTestableAbstractRegionMap extends AbstractRegionMapTest.TxTestableAbstractRegionMap {
        @Override
        public RegionEntry getEntry(Object key) {
            return null;
        }

        @Override
        EntryEventImpl createTransactionCallbackEvent(final LocalRegion re, Operation op, Object key, Object newValue, TransactionId txId, TXRmtEvent txEvent, EventID eventId, Object aCallbackArgument, FilterRoutingInfo filterRoutingInfo, ClientProxyMembershipID bridgeContext, TXEntryState txEntryState, VersionTag versionTag, long tailKey) {
            return AbstractRegionMapTest.UPDATEEVENT;
        }
    }

    private static class TxRegionEntryTestableAbstractRegionMap extends AbstractRegionMapTest.TxTestableAbstractRegionMap {
        @Override
        public RegionEntry getEntry(Object key) {
            return Mockito.mock(RegionEntry.class);
        }
    }

    private static class TxPutIfAbsentTestableAbstractRegionMap extends AbstractRegionMapTest.TxTestableAbstractRegionMap {
        @Override
        public RegionEntry putEntryIfAbsent(Object key, RegionEntry newRe) {
            return Mockito.mock(RegionEntry.class);
        }
    }

    private static class TxRemovedRegionEntryTestableAbstractRegionMap extends AbstractRegionMapTest.TxTestableAbstractRegionMap {
        @Override
        public RegionEntry getEntry(Object key) {
            RegionEntry regionEntry = Mockito.mock(RegionEntry.class);
            Mockito.when(regionEntry.isDestroyedOrRemoved()).thenReturn(true);
            return regionEntry;
        }

        @Override
        EntryEventImpl createTransactionCallbackEvent(final LocalRegion re, Operation op, Object key, Object newValue, TransactionId txId, TXRmtEvent txEvent, EventID eventId, Object aCallbackArgument, FilterRoutingInfo filterRoutingInfo, ClientProxyMembershipID bridgeContext, TXEntryState txEntryState, VersionTag versionTag, long tailKey) {
            return AbstractRegionMapTest.UPDATEEVENT;
        }
    }

    private static class TestableVersionTag extends VersionTag {
        private VersionSource versionSource;

        @Override
        public boolean equals(Object o) {
            return true;
        }

        @Override
        public Version[] getSerializationVersions() {
            return new Version[0];
        }

        @Override
        public VersionSource readMember(DataInput in) throws IOException, ClassNotFoundException {
            return null;
        }

        @Override
        public void writeMember(VersionSource memberID, DataOutput out) throws IOException {
        }

        @Override
        public int getDSFID() {
            return 0;
        }

        @Override
        public VersionSource getMemberID() {
            return versionSource;
        }

        public void setVersionSource(VersionSource versionSource) {
            this.versionSource = versionSource;
        }
    }
}

