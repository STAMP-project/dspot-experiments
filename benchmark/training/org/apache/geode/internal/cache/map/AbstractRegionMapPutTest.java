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


import java.util.Map;
import org.apache.geode.cache.DiskAccessException;
import org.apache.geode.cache.query.internal.index.IndexManager;
import org.apache.geode.internal.cache.EntryEventImpl;
import org.apache.geode.internal.cache.InternalRegion;
import org.apache.geode.internal.cache.RegionEntry;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class AbstractRegionMapPutTest {
    private final InternalRegion internalRegion = Mockito.mock(InternalRegion.class);

    private final FocusedRegionMap focusedRegionMap = Mockito.mock(FocusedRegionMap.class);

    @SuppressWarnings("rawtypes")
    private final Map entryMap = Mockito.mock(Map.class);

    private final EntryEventImpl event = Mockito.mock(EntryEventImpl.class);

    private final RegionEntry createdRegionEntry = Mockito.mock(RegionEntry.class);

    private final AbstractRegionMapPutTest.TestableRegionMapPut instance = Mockito.spy(new AbstractRegionMapPutTest.TestableRegionMapPut());

    @Test
    public void validateOwnerInitialized() {
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        AbstractRegionMapPutTest.TestableRegionMapPut testableRegionMapPut = new AbstractRegionMapPutTest.TestableRegionMapPut();
        assertThat(isOwnerInitialized()).isTrue();
    }

    @Test
    public void validateOwnerUninitialized() {
        Mockito.when(internalRegion.isInitialized()).thenReturn(false);
        AbstractRegionMapPutTest.TestableRegionMapPut testableRegionMapPut = new AbstractRegionMapPutTest.TestableRegionMapPut();
        assertThat(isOwnerInitialized()).isFalse();
    }

    @Test
    public void validateSetLastModifiedTime() {
        setLastModifiedTime(99L);
        assertThat(getLastModifiedTime()).isEqualTo(99L);
    }

    @Test
    public void validateSetClearOccurred() {
        setClearOccurred(true);
        assertThat(isClearOccurred()).isTrue();
    }

    @Test
    public void putWithUnsatisfiedPreconditionsReturnsNull() {
        instance.checkPreconditions = false;
        RegionEntry result = put();
        assertThat(result).isNull();
        Mockito.verify(focusedRegionMap, Mockito.times(1)).getEntry(ArgumentMatchers.eq(event));
        Mockito.verify(focusedRegionMap, Mockito.times(1)).putEntryIfAbsent(ArgumentMatchers.any(), ArgumentMatchers.eq(createdRegionEntry));
        Mockito.verify(instance, Mockito.times(1)).isOnlyExisting();
        Mockito.verify(instance, Mockito.never()).entryExists(ArgumentMatchers.any());
        Mockito.verify(instance, Mockito.times(1)).serializeNewValueIfNeeded();
        Mockito.verify(instance, Mockito.times(1)).runWhileLockedForCacheModification(ArgumentMatchers.any());
        Mockito.verify(instance, Mockito.times(1)).setOldValueForDelta();
        Mockito.verify(instance, Mockito.times(1)).setOldValueInEvent();
        Mockito.verify(instance, Mockito.times(1)).unsetOldValueForDelta();
        Mockito.verify(instance, Mockito.times(1)).checkPreconditions();
        Mockito.verify(instance, Mockito.never()).invokeCacheWriter();
        Mockito.verify(instance, Mockito.never()).createOrUpdateEntry();
        Mockito.verify(instance, Mockito.times(1)).shouldCreatedEntryBeRemoved();
        Mockito.verify(instance, Mockito.never()).doBeforeCompletionActions();
        Mockito.verify(instance, Mockito.times(1)).doAfterCompletionActions(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void putWithDisableLruUpdateCallbackTrueCallsDoAfterCompletionActionsWithTrue() {
        instance.checkPreconditions = false;
        Mockito.when(focusedRegionMap.disableLruUpdateCallback()).thenReturn(true);
        instance.put();
        Mockito.verify(instance, Mockito.times(1)).doAfterCompletionActions(true);
    }

    @Test
    public void putWithDisableLruUpdateCallbackFalseCallsDoAfterCompletionActionsWithFalse() {
        instance.checkPreconditions = false;
        Mockito.when(focusedRegionMap.disableLruUpdateCallback()).thenReturn(false);
        instance.put();
        Mockito.verify(instance, Mockito.times(1)).doAfterCompletionActions(false);
    }

    @Test
    public void putWithShouldCreatedEntryBeRemovedCallsRemoveEntry() {
        instance.shouldCreatedEntryBeRemoved = true;
        instance.put();
        Mockito.verify(focusedRegionMap, Mockito.times(1)).removeEntry(ArgumentMatchers.any(), ArgumentMatchers.eq(createdRegionEntry), ArgumentMatchers.eq(false));
    }

    @Test
    public void putWithOqlIndexManagerCallInitAndCountDown() {
        IndexManager oqlIndexManager = Mockito.mock(IndexManager.class);
        Mockito.when(internalRegion.getIndexManager()).thenReturn(oqlIndexManager);
        instance.checkPreconditions = true;
        instance.put();
        InOrder inOrder = Mockito.inOrder(oqlIndexManager, instance);
        inOrder.verify(oqlIndexManager, Mockito.times(1)).waitForIndexInit();
        inOrder.verify(instance, Mockito.times(1)).createOrUpdateEntry();
        inOrder.verify(oqlIndexManager, Mockito.times(1)).countDownIndexUpdaters();
    }

    @Test
    public void putCallsHandleDiskAccessExceptionWhenThrownDuringPut() {
        instance.checkPreconditions = true;
        Mockito.doThrow(DiskAccessException.class).when(instance).createOrUpdateEntry();
        assertThatThrownBy(() -> instance.put()).isInstanceOf(DiskAccessException.class);
        Mockito.verify(internalRegion, Mockito.times(1)).handleDiskAccessException(ArgumentMatchers.any());
    }

    @Test
    public void putWithSatisfiedPreconditionsAndNoExistingEntryReturnsRegionEntryFromFactory() {
        instance.checkPreconditions = true;
        Mockito.when(focusedRegionMap.getEntry(event)).thenReturn(null);
        RegionEntry result = put();
        assertThat(result).isSameAs(createdRegionEntry);
        verifyMapContractWhenCreateSucceeds();
        verifyAbstractContract();
    }

    @Test
    public void regionWithIndexMaintenanceSynchronousCallsSetUpdateInProgress() {
        Mockito.when(internalRegion.getIndexMaintenanceSynchronous()).thenReturn(true);
        instance.checkPreconditions = true;
        instance.put();
        InOrder inOrder = Mockito.inOrder(createdRegionEntry, instance);
        inOrder.verify(createdRegionEntry, Mockito.times(1)).setUpdateInProgress(true);
        inOrder.verify(instance, Mockito.times(1)).createOrUpdateEntry();
        inOrder.verify(createdRegionEntry, Mockito.times(1)).setUpdateInProgress(false);
    }

    @Test
    public void putWithOnlyExistingTrueAndNoEntryExistsReturnsNull() {
        instance.onlyExisting = true;
        instance.entryExists = false;
        RegionEntry result = put();
        assertThat(result).isNull();
        Mockito.verify(focusedRegionMap, Mockito.times(1)).getEntry(ArgumentMatchers.eq(event));
        Mockito.verify(focusedRegionMap, Mockito.never()).putEntryIfAbsent(ArgumentMatchers.any(), ArgumentMatchers.any());
        verifyAbstractContract();
    }

    @Test
    public void putWithExistingEntryReturnsExistingEntry() {
        instance.checkPreconditions = true;
        instance.onlyExisting = true;
        instance.entryExists = true;
        RegionEntry existingEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(focusedRegionMap.getEntry(ArgumentMatchers.eq(event))).thenReturn(existingEntry);
        RegionEntry result = put();
        assertThat(result).isSameAs(existingEntry);
        Mockito.verify(focusedRegionMap, Mockito.times(1)).getEntry(ArgumentMatchers.eq(event));
        Mockito.verify(focusedRegionMap, Mockito.never()).getEntryFactory();
        Mockito.verify(focusedRegionMap, Mockito.never()).putEntryIfAbsent(ArgumentMatchers.any(), ArgumentMatchers.eq(createdRegionEntry));
        verifyAbstractContract();
    }

    @Test
    public void putWithExistingEntryFromPutIfAbsentReturnsExistingEntry() {
        instance.checkPreconditions = true;
        RegionEntry existingEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(focusedRegionMap.putEntryIfAbsent(ArgumentMatchers.any(), ArgumentMatchers.eq(createdRegionEntry))).thenReturn(existingEntry);
        RegionEntry result = put();
        assertThat(result).isSameAs(existingEntry);
        Mockito.verify(focusedRegionMap, Mockito.times(1)).getEntry(ArgumentMatchers.eq(event));
        Mockito.verify(focusedRegionMap, Mockito.times(1)).getEntryFactory();
        verifyAbstractContract();
    }

    @Test
    public void putWithExistingEntryFromPutIfAbsentThatIsRemovedReturnsExistingEntry() {
        instance.checkPreconditions = true;
        RegionEntry existingEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(existingEntry.isRemovedPhase2()).thenReturn(true).thenReturn(false);
        Mockito.when(focusedRegionMap.putEntryIfAbsent(ArgumentMatchers.any(), ArgumentMatchers.eq(createdRegionEntry))).thenReturn(existingEntry);
        RegionEntry result = put();
        assertThat(result).isSameAs(existingEntry);
        Mockito.verify(focusedRegionMap, Mockito.times(2)).getEntry(ArgumentMatchers.eq(event));
        Mockito.verify(focusedRegionMap, Mockito.times(2)).getEntryFactory();
        Mockito.verify(entryMap, Mockito.times(1)).remove(ArgumentMatchers.any(), ArgumentMatchers.eq(existingEntry));
        verifyAbstractContractWithRetry();
    }

    private class TestableRegionMapPut extends AbstractRegionMapPut {
        public boolean checkPreconditions;

        public boolean onlyExisting;

        public boolean entryExists;

        public boolean shouldCreatedEntryBeRemoved;

        public TestableRegionMapPut() {
            super(focusedRegionMap, internalRegion, event);
        }

        @Override
        protected boolean isOnlyExisting() {
            return onlyExisting;
        }

        @Override
        protected boolean entryExists(RegionEntry regionEntry) {
            return entryExists;
        }

        @Override
        protected void serializeNewValueIfNeeded() {
        }

        @Override
        protected void runWhileLockedForCacheModification(Runnable r) {
            r.run();
        }

        @Override
        protected void setOldValueForDelta() {
        }

        @Override
        protected void setOldValueInEvent() {
        }

        @Override
        protected void unsetOldValueForDelta() {
        }

        @Override
        protected boolean checkPreconditions() {
            return checkPreconditions;
        }

        @Override
        protected void invokeCacheWriter() {
        }

        @Override
        protected void createOrUpdateEntry() {
        }

        @Override
        protected void doBeforeCompletionActions() {
        }

        @Override
        protected boolean shouldCreatedEntryBeRemoved() {
            return shouldCreatedEntryBeRemoved;
        }

        @Override
        protected void doAfterCompletionActions(boolean disabledEviction) {
        }
    }
}

