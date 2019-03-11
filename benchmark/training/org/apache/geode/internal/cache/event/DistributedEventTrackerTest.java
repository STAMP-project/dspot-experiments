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
package org.apache.geode.internal.cache.event;


import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import org.apache.geode.CancelCriterion;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.EventID;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.cache.InternalCacheEvent;
import org.apache.geode.internal.cache.LocalRegion;
import org.apache.geode.internal.cache.ha.ThreadIdentifier;
import org.apache.geode.internal.cache.tier.sockets.ClientProxyMembershipID;
import org.apache.geode.internal.cache.versions.VersionTag;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class DistributedEventTrackerTest {
    LocalRegion region;

    RegionAttributes<?, ?> regionAttributes;

    DistributedEventTracker eventTracker;

    ClientProxyMembershipID memberId;

    DistributedMember member;

    @Test
    public void retriedBulkOpDoesNotRemoveRecordedBulkOpVersionTags() {
        byte[] memId = new byte[]{ 1, 2, 3 };
        long threadId = 1;
        long retrySeqId = 1;
        ThreadIdentifier tid = new ThreadIdentifier(memId, threadId);
        EventID retryEventID = new EventID(memId, threadId, retrySeqId);
        boolean skipCallbacks = true;
        int size = 5;
        recordPutAllEvents(memId, threadId, skipCallbacks, size);
        ConcurrentMap<ThreadIdentifier, BulkOperationHolder> map = eventTracker.getRecordedBulkOpVersionTags();
        BulkOperationHolder holder = map.get(tid);
        int beforeSize = holder.getEntryVersionTags().size();
        eventTracker.recordBulkOpStart(retryEventID, tid);
        map = eventTracker.getRecordedBulkOpVersionTags();
        holder = map.get(tid);
        // Retried bulk op should not remove exiting BulkOpVersionTags
        Assert.assertTrue(((holder.getEntryVersionTags().size()) == beforeSize));
    }

    @Test
    public void returnsCorrectNameOfCache() {
        String testName = "testing";
        Mockito.when(region.getName()).thenReturn(testName);
        eventTracker = new DistributedEventTracker(region.getCache(), Mockito.mock(CancelCriterion.class), region.getName());
        Assert.assertEquals(("Event Tracker for " + testName), eventTracker.getName());
    }

    @Test
    public void initializationCorrectlyReadiesTheTracker() throws InterruptedException {
        Assert.assertFalse(eventTracker.isInitialized());
        eventTracker.setInitialized();
        Assert.assertTrue(eventTracker.isInitialized());
        eventTracker.waitOnInitialization();
    }

    @Test
    public void startAndStopAddAndRemoveTrackerFromExpiryTask() {
        EventTrackerExpiryTask task = Mockito.mock(EventTrackerExpiryTask.class);
        InternalCache cache = Mockito.mock(InternalCache.class);
        Mockito.when(region.getCache()).thenReturn(cache);
        Mockito.when(cache.getEventTrackerTask()).thenReturn(task);
        eventTracker = new DistributedEventTracker(region.getCache(), Mockito.mock(CancelCriterion.class), region.getName());
        eventTracker.start();
        Mockito.verify(task, Mockito.times(1)).addTracker(eventTracker);
        eventTracker.stop();
        Mockito.verify(task, Mockito.times(1)).removeTracker(eventTracker);
    }

    @Test
    public void returnsEmptyMapIfRecordedEventsAreEmpty() {
        Assert.assertEquals(0, eventTracker.getState().size());
    }

    @Test
    public void returnsMapContainingSequenceIdHoldersCurrentlyPresent() {
        EventSequenceNumberHolder sequenceIdHolder = new EventSequenceNumberHolder(0L, null);
        ThreadIdentifier threadId = new ThreadIdentifier(new byte[0], 0L);
        eventTracker.recordSequenceNumber(threadId, sequenceIdHolder);
        Map<ThreadIdentifier, EventSequenceNumberHolder> state = eventTracker.getState();
        Assert.assertEquals(1, state.size());
        EventSequenceNumberHolder returnedHolder = state.get(threadId);
        Assert.assertNotNull(returnedHolder);
        // the version tag is stripped out on purpose, so passed in object and returned one are not
        // equal to each other
        Assert.assertNull(returnedHolder.getVersionTag());
        Assert.assertEquals(sequenceIdHolder.getLastSequenceNumber(), returnedHolder.getLastSequenceNumber());
    }

    @Test
    public void setToInitializedWhenStateRecorded() {
        eventTracker.recordState(null, Collections.emptyMap());
        Assert.assertTrue(eventTracker.isInitialized());
    }

    @Test
    public void setsInitialImageProvidedWhenStateRecorded() {
        InternalDistributedMember distributedMember = Mockito.mock(InternalDistributedMember.class);
        eventTracker.recordState(distributedMember, Collections.emptyMap());
        Assert.assertTrue(eventTracker.isInitialImageProvider(distributedMember));
    }

    @Test
    public void entryInRecordedStateStoredWhenNotInCurrentState() {
        EventSequenceNumberHolder sequenceIdHolder = new EventSequenceNumberHolder(0L, null);
        ThreadIdentifier threadId = new ThreadIdentifier(new byte[0], 0L);
        Map<ThreadIdentifier, EventSequenceNumberHolder> state = Collections.singletonMap(threadId, sequenceIdHolder);
        eventTracker.recordState(null, state);
        Map<ThreadIdentifier, EventSequenceNumberHolder> storedState = eventTracker.getState();
        Assert.assertEquals(storedState.get(threadId).getLastSequenceNumber(), sequenceIdHolder.getLastSequenceNumber());
    }

    @Test
    public void entryInRecordedStateNotStoredIfAlreadyInCurrentState() {
        EventSequenceNumberHolder originalSequenceIdHolder = new EventSequenceNumberHolder(0L, null);
        ThreadIdentifier threadId = new ThreadIdentifier(new byte[0], 0L);
        Map<ThreadIdentifier, EventSequenceNumberHolder> state = Collections.singletonMap(threadId, originalSequenceIdHolder);
        eventTracker.recordState(null, state);
        EventSequenceNumberHolder newSequenceIdHolder = new EventSequenceNumberHolder(1L, null);
        Map<ThreadIdentifier, EventSequenceNumberHolder> newState = Collections.singletonMap(threadId, newSequenceIdHolder);
        eventTracker.recordState(null, newState);
        Map<ThreadIdentifier, EventSequenceNumberHolder> storedState = eventTracker.getState();
        Assert.assertEquals(storedState.get(threadId).getLastSequenceNumber(), originalSequenceIdHolder.getLastSequenceNumber());
    }

    @Test
    public void hasSeenEventReturnsFalseForEventWithNoID() {
        InternalCacheEvent event = Mockito.mock(InternalCacheEvent.class);
        Mockito.when(event.getEventId()).thenReturn(null);
        Assert.assertFalse(eventTracker.hasSeenEvent(event));
    }

    @Test
    public void hasSeenEventReturnsFalseForNullEventID() {
        Assert.assertFalse(eventTracker.hasSeenEvent(((EventID) (null))));
        Assert.assertFalse(eventTracker.hasSeenEvent(null, null));
    }

    @Test
    public void hasNotSeenEventIDThatIsNotInRecordedEvents() {
        EventID eventID = new EventID(new byte[0], 0L, 0L);
        Assert.assertFalse(eventTracker.hasSeenEvent(eventID));
    }

    @Test
    public void hasSeenEventIDThatIsInRecordedEvents() {
        EventID eventID = new EventID(new byte[0], 0L, 0L);
        recordSequence(eventID);
        Assert.assertTrue(eventTracker.hasSeenEvent(eventID));
    }

    @Test
    public void hasNotSeenEventIDWhosSequenceIDIsMarkedRemoved() {
        EventID eventID = new EventID(new byte[0], 0L, 0L);
        EventSequenceNumberHolder sequenceIdHolder = new EventSequenceNumberHolder(eventID.getSequenceID(), null);
        sequenceIdHolder.setRemoved(true);
        ThreadIdentifier threadId = new ThreadIdentifier(new byte[0], 0L);
        eventTracker.recordSequenceNumber(threadId, sequenceIdHolder);
        Assert.assertFalse(eventTracker.hasSeenEvent(eventID));
    }

    @Test
    public void hasNotSeeEventIDWhosSequenceIDIsLargerThanSeen() {
        EventID eventID = new EventID(new byte[0], 0L, 0L);
        recordSequence(eventID);
        EventID higherSequenceID = new EventID(new byte[0], 0L, 1);
        Assert.assertFalse(eventTracker.hasSeenEvent(higherSequenceID));
    }

    @Test
    public void returnsNoTagIfNoSequenceForEvent() {
        EventID eventID = new EventID(new byte[0], 0L, 1L);
        Assert.assertNull(eventTracker.findVersionTagForSequence(eventID));
    }

    @Test
    public void returnsNoTagIfSequencesDoNotMatchForEvent() {
        EventID eventID = new EventID(new byte[0], 0L, 1);
        recordSequence(eventID);
        Assert.assertNull(eventTracker.findVersionTagForSequence(eventID));
    }

    @Test
    public void returnsCorrectTagForEvent() {
        EventID eventID = new EventID(new byte[0], 0L, 0L);
        EventSequenceNumberHolder sequenceIdHolder = recordSequence(eventID);
        Assert.assertEquals(sequenceIdHolder.getVersionTag(), eventTracker.findVersionTagForSequence(eventID));
    }

    @Test
    public void returnsNoTagIfNoBulkOpWhenNoEventGiven() {
        Assert.assertNull(eventTracker.findVersionTagForBulkOp(null));
    }

    @Test
    public void returnsNoTagIfNoBulkOpForEventWithSequence() {
        EventID eventID = new EventID(new byte[0], 0L, 1L);
        Assert.assertNull(eventTracker.findVersionTagForBulkOp(eventID));
    }

    @Test
    public void returnsNoTagIfBulkOpsDoNotMatchForEvent() {
        putEvent("key", "value", new byte[0], 0, false, 0);
        EventID eventIDWithoutBulkOp = new EventID(new byte[0], 0L, 1);
        Assert.assertNull(eventTracker.findVersionTagForBulkOp(eventIDWithoutBulkOp));
    }

    @Test
    public void returnsCorrectTagForEventWithBulkOp() {
        EventID eventID = new EventID(new byte[0], 0L, 0L);
        VersionTag tag = Mockito.mock(VersionTag.class);
        putEvent("key", "value", new byte[0], 0, false, 0, tag);
        Assert.assertEquals(tag, eventTracker.findVersionTagForBulkOp(eventID));
    }

    @Test
    public void executesABulkOperations() {
        EventID eventID = new EventID(new byte[0], 0L, 1L);
        Runnable bulkOperation = Mockito.mock(Runnable.class);
        eventTracker.syncBulkOp(bulkOperation, eventID, false);
        Mockito.verify(bulkOperation, Mockito.times(1)).run();
    }

    @Test
    public void executesRunnableIfNotPartOfATransaction() {
        EventID eventID = new EventID(new byte[0], 0L, 1L);
        Runnable bulkOperation = Mockito.mock(Runnable.class);
        eventTracker.syncBulkOp(bulkOperation, eventID, true);
        Mockito.verify(bulkOperation, Mockito.times(1)).run();
    }
}

