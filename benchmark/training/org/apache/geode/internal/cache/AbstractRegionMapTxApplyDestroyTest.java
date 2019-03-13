/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License; private Version
 * 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing; private software distributed under the
 * License
 * is distributed on an "AS IS" BASIS; private WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND; private
 * either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache;


import IndexManager.REMOVE_ENTRY;
import IndexProtocol.OTHER_OP;
import Token.DESTROYED;
import Token.NOT_AVAILABLE;
import Token.REMOVED_PHASE1;
import Token.TOMBSTONE;
import java.util.ArrayList;
import java.util.List;
import org.apache.geode.cache.DiskAccessException;
import org.apache.geode.cache.Operation;
import org.apache.geode.cache.query.internal.index.IndexManager;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.TXEntryState.DistTxThinEntryState;
import org.apache.geode.internal.cache.tier.sockets.ClientProxyMembershipID;
import org.apache.geode.internal.cache.versions.VersionTag;
import org.apache.geode.internal.util.concurrent.ConcurrentMapWithReusableEntries;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;


@SuppressWarnings("unchecked")
public class AbstractRegionMapTxApplyDestroyTest {
    // parameters
    private Object key = "key";

    @Mock
    private TXId txId;

    private TXRmtEvent txEvent;

    private boolean inTokenMode;

    private boolean inRI;

    private Operation operation = Operation.DESTROY;

    @Mock
    private EventID eventId;

    private Object aCallbackArgument = "aCallbackArgument";

    private final List<EntryEventImpl> pendingCallbacks = new ArrayList<>();

    private FilterRoutingInfo filterRoutingInfo = null;// Provide a meaningful value for this?


    @Mock
    private ClientProxyMembershipID bridgeContext;

    private boolean isOriginRemote = false;

    @Mock
    private TXEntryState txEntryState;

    private VersionTag versionTag;

    private long tailKey = 223L;

    @Mock
    private InternalDistributedMember myId;

    @Mock
    private InternalDistributedMember remoteId;

    @Mock
    private KeyInfo keyInfo;

    @Mock
    private ConcurrentMapWithReusableEntries entryMap;

    @Mock
    private RegionEntryFactory regionEntryFactory;

    @Mock
    private RegionEntry existingRegionEntry;

    @Mock
    private RegionEntry factoryRegionEntry;

    @Mock
    private RegionEntry oldRegionEntry;

    @Mock
    private PartitionedRegion partitionedRegion;

    @Mock
    private VersionTag existingVersionTag;

    @Mock
    private CachePerfStats cachePerfStats;

    private LocalRegion owner;

    private AbstractRegionMapTxApplyDestroyTest.TestableAbstractRegionMap regionMap;

    // tests for no region entry.
    // Each of these tests requires givenNotInTokenMode and givenNoConcurrencyChecks
    @Test
    public void txApplyDestroySetCorrectPendingCallback_givenNoRegionEntryNotInTokenModeNoConcurrencyChecks() {
        givenLocalRegion();
        givenNoRegionEntry();
        givenNotInTokenMode();
        givenNoConcurrencyChecks();
        Mockito.when(owner.generateEventID()).thenReturn(true);
        Mockito.when(keyInfo.getCallbackArg()).thenReturn(aCallbackArgument);
        doTxApplyDestroy();
        assertThat(pendingCallbacks).hasSize(1);
        EntryEventImpl callbackEvent = pendingCallbacks.get(0);
        // noinspection Duplicates
        assertSoftly(( softly) -> {
            softly.assertThat(callbackEvent.getRegion()).isSameAs(owner);
            softly.assertThat(callbackEvent.getOperation()).isSameAs(operation);
            softly.assertThat(callbackEvent.getKey()).isSameAs(key);
            softly.assertThat(callbackEvent.getNewValue()).isNull();
            softly.assertThat(callbackEvent.getTransactionId()).isSameAs(txId);
            softly.assertThat(callbackEvent.getEventId()).isSameAs(eventId);
            softly.assertThat(callbackEvent.getCallbackArgument()).isSameAs(aCallbackArgument);
            softly.assertThat(callbackEvent.getLocalFilterInfo()).isSameAs(filterRoutingInfo);
            softly.assertThat(callbackEvent.getContext()).isSameAs(bridgeContext);
            softly.assertThat(callbackEvent.isOriginRemote()).isEqualTo(isOriginRemote);
            softly.assertThat(callbackEvent.getVersionTag()).isEqualTo(versionTag);
            softly.assertThat(callbackEvent.getTailKey()).isEqualTo(tailKey);
        });
    }

    @Test
    public void addsCallbackEvent_givenNoRegionEntryNotInTokenModeNoConcurrencyChecks_andBucket() {
        givenBucketRegion();
        givenNoRegionEntry();
        givenNotInTokenMode();
        givenNoConcurrencyChecks();
        Mockito.when(partitionedRegion.generateEventID()).thenReturn(true);
        Mockito.when(keyInfo.getCallbackArg()).thenReturn(aCallbackArgument);
        doTxApplyDestroy();
        Mockito.verify(owner, Mockito.times(1)).handleWANEvent(ArgumentMatchers.any());
        Mockito.verify(txEntryState, Mockito.times(1)).setTailKey(tailKey);
        assertThat(pendingCallbacks).hasSize(1);
        EntryEventImpl callbackEvent = pendingCallbacks.get(0);
        // noinspection Duplicates
        assertSoftly(( softly) -> {
            softly.assertThat(callbackEvent.getRegion()).isSameAs(partitionedRegion);
            softly.assertThat(callbackEvent.getOperation()).isSameAs(operation);
            softly.assertThat(callbackEvent.getKey()).isSameAs(key);
            softly.assertThat(callbackEvent.getNewValue()).isNull();
            softly.assertThat(callbackEvent.getTransactionId()).isSameAs(txId);
            softly.assertThat(callbackEvent.getEventId()).isSameAs(eventId);
            softly.assertThat(callbackEvent.getCallbackArgument()).isSameAs(aCallbackArgument);
            softly.assertThat(callbackEvent.getLocalFilterInfo()).isSameAs(filterRoutingInfo);
            softly.assertThat(callbackEvent.getContext()).isSameAs(bridgeContext);
            softly.assertThat(callbackEvent.isOriginRemote()).isEqualTo(isOriginRemote);
            softly.assertThat(callbackEvent.getVersionTag()).isEqualTo(versionTag);
            softly.assertThat(callbackEvent.getTailKey()).isEqualTo(tailKey);
        });
    }

    @Test
    public void txApplyDestroyCallReleaseEvent_givenNoRegionEntryNotInTokenModeNoConcurrencyChecksAndBucket_whenHandleWANEventThrows() {
        givenBucketRegion();
        givenNoRegionEntry();
        givenNotInTokenMode();
        givenNoConcurrencyChecks();
        Mockito.doThrow(RuntimeException.class).when(owner).handleWANEvent(ArgumentMatchers.any());
        assertThatThrownBy(this::doTxApplyDestroy).isInstanceOf(RuntimeException.class);
        releaseEvent(ArgumentMatchers.any());
    }

    // tests for "existingRegionEntry"
    @Test
    public void txApplyDestroyHasNoPendingCallback_givenExistingRegionEntryThatIsRemoved() {
        givenLocalRegion();
        givenExistingRegionEntry();
        Mockito.when(existingRegionEntry.isRemoved()).thenReturn(true);
        doTxApplyDestroy();
        assertThat(pendingCallbacks).isEmpty();
    }

    @Test
    public void txApplyDestroyInvokesRescheduleTombstone_givenExistingRegionEntryThatIsTombstone() {
        givenLocalRegion();
        givenExistingRegionEntry();
        Mockito.when(existingRegionEntry.isRemoved()).thenReturn(true);
        Mockito.when(existingRegionEntry.isTombstone()).thenReturn(true);
        doTxApplyDestroy();
        Mockito.verify(owner, Mockito.times(1)).rescheduleTombstone(ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.any());
    }

    @Test
    public void doesNotAddCallbackEvent_ifExistingRegionEntryIsTombstone() throws Exception {
        givenLocalRegion();
        givenExistingRegionEntry();
        Mockito.when(existingRegionEntry.isRemoved()).thenReturn(true);
        Mockito.when(existingRegionEntry.isTombstone()).thenReturn(true);
        doTxApplyDestroy();
        assertThat(pendingCallbacks).isEmpty();
        Mockito.verify(existingRegionEntry, Mockito.never()).makeTombstone(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(txEntryState, Mockito.never()).setVersionTag(ArgumentMatchers.any());
    }

    @Test
    public void txApplyDestroyHasNoPendingCallback_givenExistingRegionEntryWithInTokenModeAndNotInRI() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenExistingRegionEntry();
        this.inTokenMode = true;
        this.inRI = false;
        doTxApplyDestroy();
        assertThat(pendingCallbacks).isEmpty();
        Mockito.verify(owner, Mockito.times(1)).txApplyDestroyPart2(ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.eq(key), ArgumentMatchers.eq(this.inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(false));
    }

    @Test
    public void doesNotAddCallbackEvent_givenExistingRegionEntryWithInTokenModeAndNotInRI() throws Exception {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenExistingRegionEntry();
        this.inTokenMode = true;
        this.inRI = false;
        doTxApplyDestroy();
        assertThat(pendingCallbacks).isEmpty();
        Mockito.verify(owner).generateAndSetVersionTag(ArgumentMatchers.any(EntryEventImpl.class), ArgumentMatchers.same(existingRegionEntry));
        Mockito.verify(existingRegionEntry, Mockito.never()).makeTombstone(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(txEntryState).setVersionTag(ArgumentMatchers.any());
    }

    @Test
    public void setsRegionEntryOnEvent_ifExistingRegionEntryIsValid() throws Exception {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenExistingRegionEntry();
        Object oldValue = "oldValue";
        Mockito.when(existingRegionEntry.getValueInVM(owner)).thenReturn(oldValue);
        doTxApplyDestroy();
        assertThat(pendingCallbacks).size().isEqualTo(1);
        EntryEventImpl event = pendingCallbacks.get(0);
        assertThat(event.getRegionEntry()).isSameAs(existingRegionEntry);
        assertThat(event.getOldValue()).isSameAs(oldValue);
        Mockito.verify(owner).generateAndSetVersionTag(ArgumentMatchers.any(EntryEventImpl.class), ArgumentMatchers.same(existingRegionEntry));
        Mockito.verify(existingRegionEntry, Mockito.never()).makeTombstone(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(txEntryState).setVersionTag(event.getVersionTag());
    }

    @Test
    public void txApplyDestroyUpdateIndexes_givenExistingRegionEntryThatIsValid() throws Exception {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenExistingRegionEntry();
        IndexManager indexManager = Mockito.mock(IndexManager.class);
        Mockito.when(owner.getIndexManager()).thenReturn(indexManager);
        doTxApplyDestroy();
        Mockito.verify(indexManager, Mockito.times(1)).updateIndexes(ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.eq(REMOVE_ENTRY), ArgumentMatchers.eq(OTHER_OP));
    }

    @Test
    public void txApplyDestroyCallsAddDestroy_givenExistingRegionEntryThatIsValidAndTxRmtEvent() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenExistingRegionEntry();
        txEvent = Mockito.mock(TXRmtEvent.class);
        doTxApplyDestroy();
        Mockito.verify(txEvent, Mockito.times(1)).addDestroy(ArgumentMatchers.same(owner), ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.eq(key), ArgumentMatchers.same(aCallbackArgument));
    }

    @Test
    public void callsProcessAndGenerateTXVersionTag_givenExistingRegionEntryThatIsValid() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenExistingRegionEntry();
        doTxApplyDestroy();
        EntryEventImpl callbackEvent = pendingCallbacks.get(0);
        Mockito.verify(regionMap, Mockito.times(1)).processAndGenerateTXVersionTag(ArgumentMatchers.same(callbackEvent), ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.same(txEntryState));
        assertThat(callbackEvent.getNextRegionVersion()).isEqualTo((-1L));// Default value

    }

    @Test
    public void setsEventNextRegionVersionOnCallbackEvent_givenExistingRegionEntryThatIsValidAndDistTxEntryStateExists() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenExistingRegionEntry();
        DistTxThinEntryState distTxEntryStates = Mockito.mock(DistTxThinEntryState.class);
        Mockito.when(txEntryState.getDistTxEntryStates()).thenReturn(distTxEntryStates);
        Mockito.when(distTxEntryStates.getRegionVersion()).thenReturn(999L);
        doTxApplyDestroy();
        EntryEventImpl callbackEvent = pendingCallbacks.get(0);
        assertThat(callbackEvent.getNextRegionVersion()).isEqualTo(999L);// Default value

    }

    @Test
    public void txApplyDestroySetsValueToDestroyToken_givenExistingRegionEntryThatIsValidWithInTokenMode() throws Exception {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenExistingRegionEntry();
        inTokenMode = true;
        doTxApplyDestroy();
        Mockito.verify(existingRegionEntry, Mockito.times(1)).setValue(ArgumentMatchers.same(owner), ArgumentMatchers.eq(DESTROYED));
    }

    @Test
    public void txApplyDestroyHandlesClear_givenExistingRegionEntryThatIsValidWithInTokenModeAndSetValueThrowsRegionClearedException() throws Exception {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenExistingRegionEntry();
        inTokenMode = true;
        Mockito.doThrow(RegionClearedException.class).when(existingRegionEntry).setValue(ArgumentMatchers.same(owner), ArgumentMatchers.eq(DESTROYED));
        doTxApplyDestroy();
        Mockito.verify(owner, Mockito.times(1)).txApplyDestroyPart2(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(true), ArgumentMatchers.anyBoolean());
        lruEntryDestroy(ArgumentMatchers.any());
    }

    @Test
    public void txApplyDestroyHandlesNoClear_givenExistingRegionEntryThatIsValidWithInTokenMode() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenExistingRegionEntry();
        inTokenMode = true;
        doTxApplyDestroy();
        Mockito.verify(owner, Mockito.times(1)).txApplyDestroyPart2(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(false), ArgumentMatchers.anyBoolean());
        lruEntryDestroy(ArgumentMatchers.any());
    }

    @Test
    public void txApplyDestroyCallsUnscheduleTombstone_givenExistingRegionEntryThatIsTombstoneWithInTokenMode() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenExistingRegionEntry();
        inTokenMode = true;
        Mockito.when(existingRegionEntry.getValueInVM(owner)).thenReturn(TOMBSTONE);
        doTxApplyDestroy();
        Mockito.verify(owner, Mockito.times(1)).unscheduleTombstone(ArgumentMatchers.same(existingRegionEntry));
    }

    @Test
    public void txApplyDestroyCallsRemoveEntry_givenExistingRegionEntryThatIsValidWithoutInTokenModeWithoutConcurrencyCheck() throws Exception {
        givenLocalRegion();
        givenNoConcurrencyChecks();
        givenExistingRegionEntry();
        inTokenMode = false;
        doTxApplyDestroy();
        Mockito.verify(existingRegionEntry, Mockito.times(1)).removePhase1(ArgumentMatchers.same(owner), ArgumentMatchers.eq(false));
        Mockito.verify(existingRegionEntry, Mockito.times(1)).removePhase2();
        Mockito.verify(regionMap, Mockito.times(1)).removeEntry(ArgumentMatchers.eq(key), ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.eq(false));
    }

    @Test
    public void txApplyDestroyCallsRescheduleTombstone_givenExistingRegionEntryThatIsValidWithoutInTokenModeWithConcurrencyCheckButNoVersionTag() throws Exception {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenExistingRegionEntry();
        inTokenMode = false;
        versionTag = null;
        doTxApplyDestroy();
        Mockito.verify(existingRegionEntry, Mockito.times(1)).removePhase1(ArgumentMatchers.same(owner), ArgumentMatchers.eq(false));
        Mockito.verify(existingRegionEntry, Mockito.times(1)).removePhase2();
        Mockito.verify(regionMap, Mockito.times(1)).removeEntry(ArgumentMatchers.eq(key), ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.eq(false));
    }

    @Test
    public void txApplyDestroyCallsMakeTombstone_givenExistingRegionEntryThatIsValidWithoutInTokenModeWithConcurrencyCheckAndVersionTag() throws Exception {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenExistingRegionEntry();
        inTokenMode = false;
        versionTag = Mockito.mock(VersionTag.class);
        doTxApplyDestroy();
        Mockito.verify(existingRegionEntry, Mockito.times(1)).makeTombstone(ArgumentMatchers.same(owner), ArgumentMatchers.same(versionTag));
    }

    @Test
    public void txApplyDestroyCallsUpdateSizeOnRemove_givenExistingRegionEntryThatIsValid() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenExistingRegionEntry();
        int oldSize = 79;
        Mockito.when(owner.calculateRegionEntryValueSize(ArgumentMatchers.same(existingRegionEntry))).thenReturn(oldSize);
        doTxApplyDestroy();
        Mockito.verify(owner, Mockito.times(1)).updateSizeOnRemove(ArgumentMatchers.eq(key), ArgumentMatchers.eq(oldSize));
    }

    @Test
    public void txApplyDestroyCallsReleaseEvent_givenExistingRegionEntry() {
        givenLocalRegion();
        givenExistingRegionEntry();
        doTxApplyDestroy();
        releaseEvent(ArgumentMatchers.any());
    }

    @Test
    public void txApplyDestroyHasPendingCallback_givenExistingRegionEntryWithoutInTokenModeAndNotInRI() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenExistingRegionEntry();
        inTokenMode = false;
        inRI = false;
        doTxApplyDestroy();
        assertThat(pendingCallbacks).hasSize(1);
        Mockito.verify(owner, Mockito.times(1)).txApplyDestroyPart2(ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.eq(key), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(false));
    }

    @Test
    public void txApplyDestroyHasPendingCallback_givenExistingRemovedRegionEntryWithoutInTokenModeAndNotInRI() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenExistingRemovedRegionEntry();
        inTokenMode = false;
        inRI = false;
        doTxApplyDestroy();
        assertThat(pendingCallbacks).hasSize(1);
        Mockito.verify(owner, Mockito.times(1)).txApplyDestroyPart2(ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.eq(key), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(true));
    }

    @Test
    public void txApplyDestroyHasPendingCallback_givenExistingRegionEntryWithoutInTokenModeAndWithInRI() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenExistingRegionEntry();
        inTokenMode = false;
        inRI = true;
        doTxApplyDestroy();
        assertThat(pendingCallbacks).hasSize(1);
        Mockito.verify(owner, Mockito.times(1)).txApplyDestroyPart2(ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.eq(key), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(false));
    }

    @Test
    public void txApplyDestroyHasPendingCallback_givenExistingRemovedRegionEntryWithoutInTokenModeAndWithInRI() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenExistingRemovedRegionEntry();
        inTokenMode = false;
        inRI = true;
        doTxApplyDestroy();
        assertThat(pendingCallbacks).hasSize(1);
        Mockito.verify(owner, Mockito.times(1)).txApplyDestroyPart2(ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.eq(key), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(true));
    }

    @Test
    public void txApplyDestroyHasPendingCallback_givenExistingRegionEntryWithInTokenModeAndInRI() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenExistingRegionEntry();
        inTokenMode = true;
        inRI = true;
        doTxApplyDestroy();
        assertThat(pendingCallbacks).hasSize(1);
        Mockito.verify(owner, Mockito.times(1)).txApplyDestroyPart2(ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.eq(key), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(false));
    }

    @Test
    public void txApplyDestroyHasPendingCallback_givenExistingRemovedRegionEntryWithInTokenModeAndInRI() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenExistingRemovedRegionEntry();
        inTokenMode = true;
        inRI = true;
        doTxApplyDestroy();
        assertThat(pendingCallbacks).hasSize(1);
        Mockito.verify(owner, Mockito.times(1)).txApplyDestroyPart2(ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.eq(key), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(true));
    }

    @Test
    public void txApplyDestroyHasNoPendingCallback_givenExistingRegionEntryWithPartitionedRegion() {
        givenBucketRegion();
        givenExistingRegionEntry();
        doTxApplyDestroy();
        assertThat(pendingCallbacks).isEmpty();
        Mockito.verify(owner, Mockito.times(1)).txApplyDestroyPart2(ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.eq(key), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(false));
    }

    @Test
    public void txApplyDestroyHasNoPendingCallback_givenExistingRemovedRegionEntryWithPartitionedRegion() {
        givenBucketRegion();
        givenExistingRemovedRegionEntry();
        doTxApplyDestroy();
        assertThat(pendingCallbacks).isEmpty();
        Mockito.verify(owner, Mockito.times(1)).txApplyDestroyPart2(ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.eq(key), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(true));
    }

    @Test
    public void txApplyDestroyCallsHandleWanEvent_givenExistingRegionEntryWithPartitionedRegion() {
        givenBucketRegion();
        givenExistingRegionEntry();
        doTxApplyDestroy();
        Mockito.verify(owner, Mockito.times(1)).handleWANEvent(ArgumentMatchers.any());
        Mockito.verify(txEntryState, Mockito.times(1)).setTailKey(tailKey);
    }

    @Test
    public void txApplyDestroyDoesNotCallSetVersionTag_givenExistingRegionEntryWithPartitionedRegionButNoConcurrencyChecks() {
        givenBucketRegion();
        givenExistingRegionEntry();
        versionTag = Mockito.mock(VersionTag.class);
        givenNoConcurrencyChecks();
        doTxApplyDestroy();
        Mockito.verify(txEntryState, Mockito.never()).setVersionTag(ArgumentMatchers.any());
    }

    @Test
    public void txApplyDestroyPreparesAndReleasesIndexManager_givenExistingRegionEntryWithIndexManager() {
        givenLocalRegion();
        givenExistingRegionEntry();
        IndexManager indexManager = Mockito.mock(IndexManager.class);
        Mockito.when(owner.getIndexManager()).thenReturn(indexManager);
        doTxApplyDestroy();
        InOrder inOrder = Mockito.inOrder(indexManager);
        inOrder.verify(indexManager, Mockito.times(1)).waitForIndexInit();
        inOrder.verify(indexManager, Mockito.times(1)).countDownIndexUpdaters();
    }

    @Test
    public void txApplyDestroyCallsSetVersionTag_givenExistingRegionEntryWithPartitionedRegionAndConcurrencyChecks() {
        givenBucketRegion();
        givenExistingRegionEntry();
        versionTag = Mockito.mock(VersionTag.class);
        givenConcurrencyChecks();
        doTxApplyDestroy();
        Mockito.verify(txEntryState, Mockito.times(1)).setVersionTag(ArgumentMatchers.same(versionTag));
    }

    @Test
    public void txApplyDestroyHasNoPendingCallback_givenExistingRegionEntryWithoutConcurrencyChecks() {
        givenLocalRegion();
        givenExistingRegionEntry();
        givenNoConcurrencyChecks();
        doTxApplyDestroy();
        assertThat(pendingCallbacks).isEmpty();
        Mockito.verify(owner, Mockito.times(1)).txApplyDestroyPart2(ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.eq(key), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(false));
    }

    @Test
    public void txApplyDestroyHasNoPendingCallback_givenExistingRemovedRegionEntryWithoutConcurrencyChecks() {
        givenLocalRegion();
        givenExistingRemovedRegionEntry();
        givenNoConcurrencyChecks();
        doTxApplyDestroy();
        assertThat(pendingCallbacks).isEmpty();
        Mockito.verify(owner, Mockito.times(1)).txApplyDestroyPart2(ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.eq(key), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(true));
    }

    @Test
    public void txApplyDestroyHasPendingCallback_givenExistingRegionEntryWithShouldDispatchListenerEvent() {
        givenLocalRegion();
        givenExistingRegionEntry();
        Mockito.when(owner.shouldDispatchListenerEvent()).thenReturn(true);
        doTxApplyDestroy();
        assertThat(pendingCallbacks).hasSize(1);
        Mockito.verify(owner, Mockito.times(1)).txApplyDestroyPart2(ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.eq(key), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(false));
    }

    @Test
    public void txApplyDestroyHasPendingCallback_givenExistingRemovedRegionEntryWithShouldDispatchListenerEvent() {
        givenLocalRegion();
        givenExistingRemovedRegionEntry();
        Mockito.when(owner.shouldDispatchListenerEvent()).thenReturn(true);
        doTxApplyDestroy();
        assertThat(pendingCallbacks).hasSize(1);
        Mockito.verify(owner, Mockito.times(1)).txApplyDestroyPart2(ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.eq(key), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(true));
    }

    @Test
    public void txApplyDestroyHasPendingCallback_givenExistingRegionEntryWithshouldNotifyBridgeClients() {
        givenLocalRegion();
        givenExistingRegionEntry();
        Mockito.when(owner.shouldNotifyBridgeClients()).thenReturn(true);
        doTxApplyDestroy();
        assertThat(pendingCallbacks).hasSize(1);
        Mockito.verify(owner, Mockito.times(1)).txApplyDestroyPart2(ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.eq(key), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(false));
    }

    @Test
    public void txApplyDestroyHasPendingCallback_givenExistingRemovedRegionEntryWithshouldNotifyBridgeClients() {
        givenLocalRegion();
        givenExistingRemovedRegionEntry();
        Mockito.when(owner.shouldNotifyBridgeClients()).thenReturn(true);
        doTxApplyDestroy();
        assertThat(pendingCallbacks).hasSize(1);
        Mockito.verify(owner, Mockito.times(1)).txApplyDestroyPart2(ArgumentMatchers.same(existingRegionEntry), ArgumentMatchers.eq(key), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(true));
    }

    @Test
    public void txApplyDestroyDoesCallTxApplyDestroyPart2_givenFactoryRegionEntry() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenFactoryRegionEntry();
        versionTag = Mockito.mock(VersionTag.class);
        doTxApplyDestroy();
        Mockito.verify(owner, Mockito.times(1)).txApplyDestroyPart2(ArgumentMatchers.same(factoryRegionEntry), ArgumentMatchers.eq(key), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(true));
    }

    // tests for "factoryRegionEntry" (that is, no existing region entry and no oldRegionEntry).
    // All these tests require no existing region entry (that is, not found by getEntry).
    // All these tests need one of the following: givenConcurrencyChecks OR inTokenMode
    @Test
    public void txApplyDestroyCallsCreateEntry_givenFactoryRegionEntryAndInTokenMode() {
        givenLocalRegion();
        givenFactoryRegionEntry();
        inTokenMode = true;
        doTxApplyDestroy();
        Mockito.verify(regionEntryFactory, Mockito.times(1)).createEntry(ArgumentMatchers.same(owner), ArgumentMatchers.eq(key), ArgumentMatchers.eq(REMOVED_PHASE1));
    }

    @Test
    public void txApplyDestroyCallsCreateEntry_givenFactoryRegionEntryAndConcurrencyChecks() {
        givenLocalRegion();
        givenFactoryRegionEntry();
        givenConcurrencyChecks();
        doTxApplyDestroy();
        Mockito.verify(regionEntryFactory, Mockito.times(1)).createEntry(ArgumentMatchers.same(owner), ArgumentMatchers.eq(key), ArgumentMatchers.eq(REMOVED_PHASE1));
    }

    @Test
    public void txApplyDestroyPreparesAndReleasesIndexManager_givenFactoryRegionEntryAndConcurrencyChecksWithIndexManager() {
        givenLocalRegion();
        givenFactoryRegionEntry();
        givenConcurrencyChecks();
        IndexManager indexManager = Mockito.mock(IndexManager.class);
        Mockito.when(owner.getIndexManager()).thenReturn(indexManager);
        doTxApplyDestroy();
        InOrder inOrder = Mockito.inOrder(indexManager);
        inOrder.verify(indexManager, Mockito.times(1)).waitForIndexInit();
        inOrder.verify(indexManager, Mockito.times(1)).countDownIndexUpdaters();
    }

    @Test
    public void txApplyDestroyHasNoPendingCallback_givenFactoryRegionEntryWithInTokenModeAndNotInRI() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenFactoryRegionEntry();
        inTokenMode = true;
        inRI = false;
        doTxApplyDestroy();
        assertThat(pendingCallbacks).isEmpty();
    }

    @Test
    public void txApplyDestroyHasPendingCallback_givenFactoryRegionEntryWithoutInTokenModeAndNotInRI() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenFactoryRegionEntry();
        inTokenMode = false;
        inRI = false;
        doTxApplyDestroy();
        assertThat(pendingCallbacks).hasSize(1);
    }

    @Test
    public void txApplyDestroyHasPendingCallback_givenFactoryRegionEntryWithoutInTokenModeAndWithInRI() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenFactoryRegionEntry();
        inTokenMode = false;
        inRI = true;
        doTxApplyDestroy();
        assertThat(pendingCallbacks).hasSize(1);
    }

    @Test
    public void txApplyDestroyHasPendingCallback_givenFactoryRegionEntryWithInTokenModeAndInRI() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenFactoryRegionEntry();
        inTokenMode = true;
        inRI = true;
        doTxApplyDestroy();
        assertThat(pendingCallbacks).hasSize(1);
    }

    @Test
    public void txApplyDestroyHasNoPendingCallback_givenFactoryRegionEntryWithPartitionedRegion() {
        givenBucketRegion();
        givenConcurrencyChecks();
        givenFactoryRegionEntry();
        Mockito.when(partitionedRegion.getConcurrencyChecksEnabled()).thenReturn(false);
        doTxApplyDestroy();
        assertThat(pendingCallbacks).isEmpty();
    }

    @Test
    public void txApplyDestroyHasNoPendingCallback_givenFactoryRegionEntryWithoutConcurrencyChecksInTokenMode() {
        givenLocalRegion();
        givenFactoryRegionEntry();
        givenNoConcurrencyChecks();
        inTokenMode = true;
        doTxApplyDestroy();
        assertThat(pendingCallbacks).isEmpty();
    }

    @Test
    public void txApplyDestroyHasPendingCallback_givenFactoryRegionEntryWithShouldDispatchListenerEvent() {
        givenLocalRegion();
        givenNoConcurrencyChecks();
        givenFactoryRegionEntry();
        inTokenMode = true;
        inRI = true;
        Mockito.when(owner.shouldDispatchListenerEvent()).thenReturn(true);
        doTxApplyDestroy();
        assertThat(pendingCallbacks).hasSize(1);
    }

    @Test
    public void txApplyDestroyHasPendingCallback_givenFactoryRegionEntryWithshouldNotifyBridgeClients() {
        givenLocalRegion();
        givenNoConcurrencyChecks();
        givenFactoryRegionEntry();
        inTokenMode = true;
        inRI = true;
        Mockito.when(owner.shouldNotifyBridgeClients()).thenReturn(true);
        doTxApplyDestroy();
        assertThat(pendingCallbacks).hasSize(1);
    }

    @Test
    public void txApplyDestroySetsRegionEntryOnEvent_givenFactoryRegionEntry() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenFactoryRegionEntry();
        doTxApplyDestroy();
        EntryEventImpl event = pendingCallbacks.get(0);
        assertThat(event.getRegionEntry()).isSameAs(factoryRegionEntry);
    }

    @Test
    public void txApplyDestroySetsOldValueOnEventToNotAvailable_givenFactoryRegionEntry() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenFactoryRegionEntry();
        doTxApplyDestroy();
        EntryEventImpl event = pendingCallbacks.get(0);
        assertThat(event.getRawOldValue()).isSameAs(NOT_AVAILABLE);
    }

    @Test
    public void txApplyDestroyCallsHandleWanEvent_givenFactoryRegionEntryWithPartitionedRegion() {
        givenBucketRegion();
        givenConcurrencyChecks();
        givenFactoryRegionEntry();
        doTxApplyDestroy();
        Mockito.verify(owner, Mockito.times(1)).handleWANEvent(ArgumentMatchers.any());
        Mockito.verify(txEntryState, Mockito.times(1)).setTailKey(tailKey);
    }

    @Test
    public void txApplyDestroyCallsProcessAndGenerateTXVersionTag_givenFactoryRegionEntry() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenFactoryRegionEntry();
        doTxApplyDestroy();
        Mockito.verify(regionMap, Mockito.times(1)).processAndGenerateTXVersionTag(ArgumentMatchers.any(), ArgumentMatchers.same(factoryRegionEntry), ArgumentMatchers.same(txEntryState));
    }

    @Test
    public void txApplyDestroySetsRemoteOrigin_givenFactoryRegionEntryAndRemoteTxId() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenFactoryRegionEntry();
        Mockito.when(txId.getMemberId()).thenReturn(remoteId);
        doTxApplyDestroy();
        EntryEventImpl event = pendingCallbacks.get(0);
        assertThat(event.isOriginRemote()).isTrue();
    }

    @Test
    public void txApplyDestroySetsRemoteOrigin_givenFactoryRegionEntry() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenFactoryRegionEntry();
        doTxApplyDestroy();
        EntryEventImpl event = pendingCallbacks.get(0);
        assertThat(event.isOriginRemote()).isFalse();
    }

    @Test
    public void txApplyDestroySetsRegionOnEvent_givenFactoryRegionEntryAndBucket() {
        givenBucketRegion();
        givenConcurrencyChecks();
        givenFactoryRegionEntry();
        doTxApplyDestroy();
        EntryEventImpl event = pendingCallbacks.get(0);
        assertThat(event.getRegion()).isSameAs(partitionedRegion);
    }

    @Test
    public void txApplyDestroyNeverCallsUpdateSizeOnCreate_givenFactoryRegionEntry() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenFactoryRegionEntry();
        doTxApplyDestroy();
        Mockito.verify(owner, Mockito.never()).updateSizeOnCreate(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
    }

    @Test
    public void txApplyDestroyCallsMakeTombstone_givenFactoryRegionEntryWithConcurrencyCheckAndVersionTag() throws Exception {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenFactoryRegionEntry();
        versionTag = Mockito.mock(VersionTag.class);
        doTxApplyDestroy();
        Mockito.verify(factoryRegionEntry, Mockito.times(1)).makeTombstone(ArgumentMatchers.same(owner), ArgumentMatchers.same(versionTag));
    }

    @Test
    public void txApplyDestroyNeverCallsMakeTombstone_givenFactoryRegionEntryWithConcurrencyCheckButNoVersionTag() throws Exception {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenFactoryRegionEntry();
        doTxApplyDestroy();
        Mockito.verify(factoryRegionEntry, Mockito.never()).makeTombstone(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void txApplyDestroyNeverCallsMakeTombstone_givenFactoryRegionEntryWithoutConcurrencyCheckButWithInTokenModeAndVersionTag() throws Exception {
        givenLocalRegion();
        givenNoConcurrencyChecks();
        givenFactoryRegionEntry();
        inTokenMode = true;
        versionTag = Mockito.mock(VersionTag.class);
        doTxApplyDestroy();
        Mockito.verify(factoryRegionEntry, Mockito.never()).makeTombstone(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void txApplyDestroyCallsRemoveEntry_givenFactoryRegionEntryWithConcurrencyCheck() throws Exception {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenFactoryRegionEntry();
        doTxApplyDestroy();
        Mockito.verify(factoryRegionEntry, Mockito.times(1)).removePhase1(ArgumentMatchers.same(owner), ArgumentMatchers.eq(false));
        Mockito.verify(factoryRegionEntry, Mockito.times(1)).removePhase2();
        Mockito.verify(regionMap, Mockito.times(1)).removeEntry(ArgumentMatchers.eq(key), ArgumentMatchers.same(factoryRegionEntry), ArgumentMatchers.eq(false));
    }

    @Test
    public void txApplyDestroyCallsReleaseEvent_givenFactoryRegionEntryWithoutConcurrencyChecksInTokenMode() {
        givenLocalRegion();
        givenFactoryRegionEntry();
        givenNoConcurrencyChecks();
        inTokenMode = true;
        doTxApplyDestroy();
        releaseEvent(ArgumentMatchers.any());
    }

    @Test
    public void txApplyDestroyCallsSetValueWithDestroyedToken_givenFactoryRegionEntryWithoutConcurrencyChecksInTokenMode() throws RegionClearedException {
        givenLocalRegion();
        givenFactoryRegionEntry();
        givenNoConcurrencyChecks();
        inTokenMode = true;
        doTxApplyDestroy();
        Mockito.verify(factoryRegionEntry, Mockito.times(1)).setValue(ArgumentMatchers.same(owner), ArgumentMatchers.eq(DESTROYED));
    }

    @Test
    public void txApplyDestroyDoesNotCallSetVersionTag_givenFactoryRegionEntryWithPartitionedRegionButNoConcurrencyChecks() {
        givenBucketRegion();
        givenFactoryRegionEntry();
        versionTag = Mockito.mock(VersionTag.class);
        givenNoConcurrencyChecks();
        inTokenMode = true;
        doTxApplyDestroy();
        Mockito.verify(txEntryState, Mockito.never()).setVersionTag(ArgumentMatchers.any());
    }

    @Test
    public void txApplyDestroyDoesNotCallSetVersionTag_givenFactoryRegionEntryWithPartitionedRegionAndConcurrencyChecks() {
        givenBucketRegion();
        givenFactoryRegionEntry();
        versionTag = Mockito.mock(VersionTag.class);
        givenConcurrencyChecks();
        doTxApplyDestroy();
        Mockito.verify(txEntryState, Mockito.times(1)).setVersionTag(ArgumentMatchers.same(versionTag));
    }

    @Test
    public void txApplyDestroyDoesNotCallTxApplyDestroyPart2_givenFactoryRegionEntryWithMakeTombstoneThrowingRegionDestroyedException() throws Exception {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenFactoryRegionEntry();
        versionTag = Mockito.mock(VersionTag.class);
        Mockito.doThrow(RegionClearedException.class).when(factoryRegionEntry).makeTombstone(ArgumentMatchers.any(), ArgumentMatchers.any());
        doTxApplyDestroy();
        Mockito.verify(owner, Mockito.never()).txApplyDestroyPart2(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
    }

    // tests for "oldRegionEntry" (that is, an existing region entry found by putIfAbsent).
    // All these tests require no existing region entry (that is, not found by getEntry).
    // All these tests need one of the following: givenConcurrencyChecks OR inTokenMode
    @Test
    public void txApplyDestroyRetries_givenOldRegionEntryWithRemovedPhase2() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenOldRegionEntry();
        inTokenMode = true;
        Mockito.when(oldRegionEntry.isRemovedPhase2()).thenReturn(true).thenReturn(false);
        doTxApplyDestroy();
        Mockito.verify(cachePerfStats, Mockito.times(1)).incRetries();
        Mockito.verify(entryMap, Mockito.times(1)).remove(ArgumentMatchers.eq(key), ArgumentMatchers.same(oldRegionEntry));
    }

    @Test
    public void txApplyDestroyHasNoPendingCallback_givenOldRegionEntryWithInTokenModeAndNotInRI() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenOldRegionEntry();
        inTokenMode = true;
        inRI = false;
        doTxApplyDestroy();
        assertThat(pendingCallbacks).isEmpty();
    }

    @Test
    public void txApplyDestroyHasPendingCallback_givenOldRegionEntrydWithoutInTokenModeAndNotInRI() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenOldRegionEntry();
        inTokenMode = false;
        inRI = false;
        doTxApplyDestroy();
        assertThat(pendingCallbacks).hasSize(1);
    }

    @Test
    public void txApplyDestroyHasPendingCallback_givenOldRegionEntryWithoutInTokenModeAndWithInRI() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenOldRegionEntry();
        inTokenMode = false;
        inRI = true;
        doTxApplyDestroy();
        assertThat(pendingCallbacks).hasSize(1);
    }

    @Test
    public void addsCallbackEvent_givenOldRegionEntryWithInTokenModeAndInRI() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenOldRegionEntry();
        inTokenMode = true;
        inRI = true;
        Mockito.when(owner.generateEventID()).thenReturn(true);
        Mockito.when(keyInfo.getCallbackArg()).thenReturn(aCallbackArgument);
        doTxApplyDestroy();
        assertThat(pendingCallbacks).hasSize(1);
        EntryEventImpl callbackEvent = pendingCallbacks.get(0);
        // noinspection Duplicates
        assertSoftly(( softly) -> {
            softly.assertThat(callbackEvent.getRegion()).isSameAs(owner);
            softly.assertThat(callbackEvent.getOperation()).isSameAs(operation);
            softly.assertThat(callbackEvent.getKey()).isSameAs(key);
            softly.assertThat(callbackEvent.getNewValue()).isNull();
            softly.assertThat(callbackEvent.getTransactionId()).isSameAs(txId);
            softly.assertThat(callbackEvent.getEventId()).isSameAs(eventId);
            softly.assertThat(callbackEvent.getCallbackArgument()).isSameAs(aCallbackArgument);
            softly.assertThat(callbackEvent.getLocalFilterInfo()).isSameAs(filterRoutingInfo);
            softly.assertThat(callbackEvent.getContext()).isSameAs(bridgeContext);
            softly.assertThat(callbackEvent.isOriginRemote()).isEqualTo(isOriginRemote);
            softly.assertThat(callbackEvent.getVersionTag()).isEqualTo(versionTag);
            softly.assertThat(callbackEvent.getTailKey()).isEqualTo(tailKey);
            softly.assertThat(callbackEvent.getRegionEntry()).isSameAs(oldRegionEntry);
            softly.assertThat(callbackEvent.getOldValue()).isNull();
        });
    }

    @Test
    public void txApplyDestroyHasNoPendingCallback_givenOldRegionEntryWithPartitionedRegion() {
        givenBucketRegion();
        givenConcurrencyChecks();
        givenOldRegionEntry();
        Mockito.when(partitionedRegion.getConcurrencyChecksEnabled()).thenReturn(false);
        doTxApplyDestroy();
        assertThat(pendingCallbacks).isEmpty();
    }

    @Test
    public void txApplyDestroyHasNoPendingCallback_givenOldRegionEntryWithoutConcurrencyChecksInTokenMode() {
        givenLocalRegion();
        givenOldRegionEntry();
        givenNoConcurrencyChecks();
        inTokenMode = true;
        doTxApplyDestroy();
        assertThat(pendingCallbacks).isEmpty();
    }

    @Test
    public void txApplyDestroyHasPendingCallback_givenOldRegionEntryWithShouldDispatchListenerEvent() {
        givenLocalRegion();
        givenNoConcurrencyChecks();
        givenOldRegionEntry();
        inTokenMode = true;
        inRI = true;
        Mockito.when(owner.shouldDispatchListenerEvent()).thenReturn(true);
        doTxApplyDestroy();
        assertThat(pendingCallbacks).hasSize(1);
    }

    @Test
    public void txApplyDestroyHasPendingCallback_givenOldRegionEntryWithshouldNotifyBridgeClients() {
        givenLocalRegion();
        givenNoConcurrencyChecks();
        givenOldRegionEntry();
        inTokenMode = true;
        inRI = true;
        Mockito.when(owner.shouldNotifyBridgeClients()).thenReturn(true);
        doTxApplyDestroy();
        assertThat(pendingCallbacks).hasSize(1);
    }

    @Test
    public void txApplyDestroySetsRegionEntryOnEvent_givenOldRegionEntry() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenOldRegionEntry();
        doTxApplyDestroy();
        EntryEventImpl event = pendingCallbacks.get(0);
        assertThat(event.getRegionEntry()).isSameAs(oldRegionEntry);
    }

    @Test
    public void txApplyDestroySetsOldValueOnEventToNotAvailable_givenOldRegionEntry() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenOldRegionEntry();
        doTxApplyDestroy();
        EntryEventImpl event = pendingCallbacks.get(0);
        assertThat(event.getRawOldValue()).isSameAs(NOT_AVAILABLE);
    }

    @Test
    public void txApplyDestroyCallsHandleWanEvent_givenOldRegionEntryWithPartitionedRegion() {
        givenBucketRegion();
        givenConcurrencyChecks();
        givenOldRegionEntry();
        doTxApplyDestroy();
        Mockito.verify(owner, Mockito.times(1)).handleWANEvent(ArgumentMatchers.any());
        Mockito.verify(txEntryState, Mockito.times(1)).setTailKey(tailKey);
    }

    @Test
    public void txApplyDestroyCallsProcessAndGenerateTXVersionTag_givenOldRegionEntry() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenOldRegionEntry();
        doTxApplyDestroy();
        Mockito.verify(regionMap, Mockito.times(1)).processAndGenerateTXVersionTag(ArgumentMatchers.any(), ArgumentMatchers.same(oldRegionEntry), ArgumentMatchers.same(txEntryState));
    }

    @Test
    public void txApplyDestroySetsRemoteOrigin_givenOldRegionEntryAndRemoteTxId() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenOldRegionEntry();
        Mockito.when(txId.getMemberId()).thenReturn(remoteId);
        doTxApplyDestroy();
        EntryEventImpl event = pendingCallbacks.get(0);
        assertThat(event.isOriginRemote()).isTrue();
    }

    @Test
    public void txApplyDestroySetsRemoteOrigin_givenOldRegionEntry() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenOldRegionEntry();
        doTxApplyDestroy();
        EntryEventImpl event = pendingCallbacks.get(0);
        assertThat(event.isOriginRemote()).isFalse();
    }

    @Test
    public void addsCallbackEvent_givenOldRegionEntryAndBucket() {
        givenBucketRegion();
        givenConcurrencyChecks();
        givenOldRegionEntry();
        Mockito.when(partitionedRegion.generateEventID()).thenReturn(true);
        Mockito.when(keyInfo.getCallbackArg()).thenReturn(aCallbackArgument);
        doTxApplyDestroy();
        EntryEventImpl callbackEvent = pendingCallbacks.get(0);
        // noinspection Duplicates
        assertSoftly(( softly) -> {
            softly.assertThat(callbackEvent.getRegion()).isSameAs(partitionedRegion);
            softly.assertThat(callbackEvent.getOperation()).isSameAs(operation);
            softly.assertThat(callbackEvent.getKey()).isSameAs(key);
            softly.assertThat(callbackEvent.getNewValue()).isNull();
            softly.assertThat(callbackEvent.getTransactionId()).isSameAs(txId);
            softly.assertThat(callbackEvent.getEventId()).isSameAs(eventId);
            softly.assertThat(callbackEvent.getCallbackArgument()).isSameAs(aCallbackArgument);
            softly.assertThat(callbackEvent.getLocalFilterInfo()).isSameAs(filterRoutingInfo);
            softly.assertThat(callbackEvent.getContext()).isSameAs(bridgeContext);
            softly.assertThat(callbackEvent.isOriginRemote()).isEqualTo(isOriginRemote);
            softly.assertThat(callbackEvent.getVersionTag()).isEqualTo(versionTag);
            softly.assertThat(callbackEvent.getTailKey()).isEqualTo(tailKey);
            softly.assertThat(callbackEvent.getRegionEntry()).isSameAs(oldRegionEntry);
            softly.assertThat(callbackEvent.getOldValue()).isNull();
        });
    }

    @Test
    public void txApplyDestroyCallUpdateSizeOnRemoveWithZero_givenOldRegionEntryThatIsTombstone() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenOldRegionEntry();
        Mockito.when(oldRegionEntry.isTombstone()).thenReturn(true);
        doTxApplyDestroy();
        Mockito.verify(owner, Mockito.times(1)).updateSizeOnRemove(ArgumentMatchers.eq(key), ArgumentMatchers.eq(0));
    }

    @Test
    public void txApplyDestroyCallUpdateSizeOnRemoveWithOldSize_givenOldRegionEntryThatIsNotTombstone() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenOldRegionEntry();
        int oldSize = 79;
        Mockito.when(owner.calculateRegionEntryValueSize(oldRegionEntry)).thenReturn(oldSize);
        Mockito.when(oldRegionEntry.isTombstone()).thenReturn(false);
        doTxApplyDestroy();
        Mockito.verify(owner, Mockito.times(1)).updateSizeOnRemove(ArgumentMatchers.eq(key), ArgumentMatchers.eq(oldSize));
    }

    @Test
    public void txApplyDestroySetsValueToTokenDestroyed_givenOldRegionEntry() throws Exception {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenOldRegionEntry();
        doTxApplyDestroy();
        Mockito.verify(oldRegionEntry, Mockito.times(1)).setValue(ArgumentMatchers.same(owner), ArgumentMatchers.eq(DESTROYED));
    }

    @Test
    public void txApplyDestroySetsValueToTokenDestroyed_givenOldRegionEntryAndInTokenMode() throws Exception {
        givenLocalRegion();
        givenNoConcurrencyChecks();
        givenOldRegionEntry();
        inTokenMode = true;
        doTxApplyDestroy();
        Mockito.verify(oldRegionEntry, Mockito.times(1)).setValue(ArgumentMatchers.same(owner), ArgumentMatchers.eq(DESTROYED));
    }

    @Test
    public void txApplyDestroyCallsUnscheduleTombstone_givenOldRegionEntryThatIsTombstone() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenOldRegionEntry();
        Mockito.when(oldRegionEntry.isTombstone()).thenReturn(true);
        doTxApplyDestroy();
        Mockito.verify(owner, Mockito.times(1)).unscheduleTombstone(ArgumentMatchers.same(oldRegionEntry));
    }

    @Test
    public void txApplyDestroyCallsTxApplyDestroyPart2_givenOldRegionEntry() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenOldRegionEntry();
        doTxApplyDestroy();
        Mockito.verify(owner, Mockito.times(1)).txApplyDestroyPart2(ArgumentMatchers.same(oldRegionEntry), ArgumentMatchers.eq(key), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(false));
    }

    @Test
    public void txApplyDestroyCallsTxApplyDestroyPart2_givenOldRemovedRegionEntry() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenOldRemovedRegionEntry();
        doTxApplyDestroy();
        Mockito.verify(owner, Mockito.times(1)).txApplyDestroyPart2(ArgumentMatchers.same(oldRegionEntry), ArgumentMatchers.eq(key), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(true));
    }

    @Test
    public void txApplyDestroyCallsTxApplyDestroyPart2_givenOldRegionEntryWithInTokenMode() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenOldRegionEntry();
        inTokenMode = true;
        doTxApplyDestroy();
        Mockito.verify(owner, Mockito.times(1)).txApplyDestroyPart2(ArgumentMatchers.same(oldRegionEntry), ArgumentMatchers.eq(key), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(false));
    }

    @Test
    public void txApplyDestroyCallsTxApplyDestroyPart2_givenOldRemovedRegionEntryWithInTokenMode() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenOldRemovedRegionEntry();
        inTokenMode = true;
        doTxApplyDestroy();
        Mockito.verify(owner, Mockito.times(1)).txApplyDestroyPart2(ArgumentMatchers.same(oldRegionEntry), ArgumentMatchers.eq(key), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(false), ArgumentMatchers.eq(true));
    }

    @Test
    public void txApplyDestroyCallsLruEntryDestroy_givenOldRegionEntry() {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenOldRegionEntry();
        doTxApplyDestroy();
        Mockito.verify(regionMap, Mockito.times(1)).lruEntryDestroy(oldRegionEntry);
    }

    @Test
    public void txApplyDestroyCallsReleaseEvent_givenOldRegionEntryWithoutConcurrencyChecksInTokenMode() {
        givenLocalRegion();
        givenOldRegionEntry();
        givenNoConcurrencyChecks();
        inTokenMode = true;
        doTxApplyDestroy();
        releaseEvent(ArgumentMatchers.any());
    }

    @Test
    public void txApplyDestroyDoesCallTxApplyDestroyPart2_givenOldRegionEntryWithMakeTombstoneThrowingRegionDestroyedException() throws Exception {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenOldRegionEntry();
        Mockito.doThrow(RegionClearedException.class).when(oldRegionEntry).setValue(ArgumentMatchers.any(), ArgumentMatchers.any());
        doTxApplyDestroy();
        Mockito.verify(owner, Mockito.times(1)).txApplyDestroyPart2(ArgumentMatchers.same(oldRegionEntry), ArgumentMatchers.eq(key), ArgumentMatchers.eq(inTokenMode), ArgumentMatchers.eq(true), ArgumentMatchers.eq(true));
    }

    @Test
    public void txApplyDestroyCallsMakeTombstone_givenOldRegionEntryWithConcurrencyCheckAndVersionTag() throws Exception {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenOldRegionEntry();
        versionTag = Mockito.mock(VersionTag.class);
        doTxApplyDestroy();
        Mockito.verify(oldRegionEntry, Mockito.times(1)).makeTombstone(ArgumentMatchers.same(owner), ArgumentMatchers.same(versionTag));
    }

    @Test
    public void txApplyDestroyCallsRemoveEntry_givenOldRegionEntry() throws Exception {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenOldRegionEntry();
        doTxApplyDestroy();
        Mockito.verify(oldRegionEntry, Mockito.never()).makeTombstone(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(oldRegionEntry, Mockito.times(1)).removePhase1(ArgumentMatchers.same(owner), ArgumentMatchers.eq(false));
        Mockito.verify(oldRegionEntry, Mockito.times(1)).removePhase2();
        Mockito.verify(regionMap, Mockito.times(1)).removeEntry(ArgumentMatchers.eq(key), ArgumentMatchers.same(oldRegionEntry), ArgumentMatchers.eq(false));
    }

    @Test
    public void txApplyDestroyNeverCallsRemoveEntry_givenOldRegionEntryAndInTokenMode() throws Exception {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenOldRegionEntry();
        inTokenMode = true;
        doTxApplyDestroy();
        Mockito.verify(oldRegionEntry, Mockito.never()).makeTombstone(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(oldRegionEntry, Mockito.never()).removePhase1(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
        Mockito.verify(oldRegionEntry, Mockito.never()).removePhase2();
        removeEntry(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void txApplyDestroyDoesCallsHandleDiskAccessException_givenOldRegionEntrySetValueThatThrowsDiskAccessException() throws Exception {
        givenLocalRegion();
        givenConcurrencyChecks();
        givenOldRegionEntry();
        Mockito.doThrow(DiskAccessException.class).when(oldRegionEntry).setValue(ArgumentMatchers.any(), ArgumentMatchers.any());
        assertThatThrownBy(this::doTxApplyDestroy).isInstanceOf(DiskAccessException.class);
        Mockito.verify(owner, Mockito.times(1)).handleDiskAccessException(ArgumentMatchers.any());
    }

    private class TestableAbstractRegionMap extends AbstractRegionMap {
        TestableAbstractRegionMap() {
            super(null);
            initialize(owner, new Attributes(), null, false);
            setEntryMap(entryMap);
            setEntryFactory(regionEntryFactory);
        }
    }
}

