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


import Operation.PUTALL_CREATE;
import java.util.concurrent.ConcurrentMap;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.event.BulkOperationHolder;
import org.apache.geode.internal.cache.event.EventTracker;
import org.apache.geode.internal.cache.ha.ThreadIdentifier;
import org.apache.geode.internal.cache.tier.sockets.ClientProxyMembershipID;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class DistributedRegionJUnitTest extends AbstractDistributedRegionJUnitTest {
    @Test
    public void retriedBulkOpGetsSavedVersionTag() {
        DistributedRegion region = prepare(true, true);
        DistributedMember member = Mockito.mock(DistributedMember.class);
        ClientProxyMembershipID memberId = Mockito.mock(ClientProxyMembershipID.class);
        byte[] memId = new byte[]{ 1, 2, 3 };
        long threadId = 1;
        long retrySeqId = 1;
        ThreadIdentifier tid = new ThreadIdentifier(memId, threadId);
        EventID retryEventID = new EventID(memId, threadId, retrySeqId);
        boolean skipCallbacks = true;
        int size = 2;
        recordPutAllEvents(region, memId, threadId, skipCallbacks, member, memberId, size);
        EventTracker eventTracker = region.getEventTracker();
        ConcurrentMap<ThreadIdentifier, BulkOperationHolder> map = eventTracker.getRecordedBulkOpVersionTags();
        BulkOperationHolder holder = map.get(tid);
        EntryEventImpl retryEvent = EntryEventImpl.create(region, PUTALL_CREATE, "key1", "value1", null, false, member, (!skipCallbacks), retryEventID);
        retryEvent.setContext(memberId);
        retryEvent.setPutAllOperation(Mockito.mock(DistributedPutAllOperation.class));
        region.hasSeenEvent(retryEvent);
        Assert.assertTrue(retryEvent.getVersionTag().equals(holder.getEntryVersionTags().get(retryEventID)));
    }

    @Test
    public void testThatMemoryThresholdInfoRelectsStateOfRegion() {
        InternalDistributedMember internalDM = Mockito.mock(InternalDistributedMember.class);
        DistributedRegion distRegion = prepare(true, false);
        distRegion.addCriticalMember(internalDM);
        MemoryThresholdInfo info = distRegion.getAtomicThresholdInfo();
        assertThat(distRegion.isMemoryThresholdReached()).isTrue();
        assertThat(distRegion.getAtomicThresholdInfo().getMembersThatReachedThreshold()).containsExactly(internalDM);
        assertThat(info.isMemoryThresholdReached()).isTrue();
        assertThat(info.getMembersThatReachedThreshold()).containsExactly(internalDM);
    }

    @Test
    public void testThatMemoryThresholdInfoDoesNotChangeWhenRegionChanges() {
        InternalDistributedMember internalDM = Mockito.mock(InternalDistributedMember.class);
        DistributedRegion distRegion = prepare(true, false);
        MemoryThresholdInfo info = distRegion.getAtomicThresholdInfo();
        distRegion.addCriticalMember(internalDM);
        assertThat(distRegion.isMemoryThresholdReached()).isTrue();
        assertThat(distRegion.getAtomicThresholdInfo().getMembersThatReachedThreshold()).containsExactly(internalDM);
        assertThat(info.isMemoryThresholdReached()).isFalse();
        assertThat(info.getMembersThatReachedThreshold()).isEmpty();
    }
}

