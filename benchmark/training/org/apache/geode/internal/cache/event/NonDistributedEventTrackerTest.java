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


import NonDistributedEventTracker.NAME;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.internal.cache.EventID;
import org.apache.geode.internal.cache.InternalCacheEvent;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class NonDistributedEventTrackerTest {
    private NonDistributedEventTracker tracker = NonDistributedEventTracker.getInstance();

    @Test
    public void getStateReturnsNull() {
        Assert.assertNull(tracker.getState());
    }

    @Test
    public void hasSeenEventReturnsFalse() {
        Assert.assertFalse(tracker.hasSeenEvent(Mockito.mock(InternalCacheEvent.class)));
        Assert.assertFalse(tracker.hasSeenEvent(Mockito.mock(EventID.class)));
        Assert.assertFalse(tracker.hasSeenEvent(Mockito.mock(EventID.class), Mockito.mock(InternalCacheEvent.class)));
    }

    @Test
    public void findVersionTagForSequenceReturnsNull() {
        Assert.assertNull(tracker.findVersionTagForSequence(Mockito.mock(EventID.class)));
    }

    @Test
    public void findVersionTagForBulkOpReturnsNull() {
        Assert.assertNull(tracker.findVersionTagForBulkOp(Mockito.mock(EventID.class)));
    }

    @Test
    public void returnsCorrectName() {
        Assert.assertEquals(NAME, tracker.getName());
    }

    @Test
    public void syncBulkOpExecutesProvidedRunnable() {
        Runnable runnable = Mockito.mock(Runnable.class);
        tracker.syncBulkOp(runnable, Mockito.mock(EventID.class), false);
        tracker.syncBulkOp(runnable, Mockito.mock(EventID.class), true);
        Mockito.verify(runnable, Mockito.times(2)).run();
    }

    @Test
    public void isInitializedReturnsTrue() {
        Assert.assertTrue(tracker.isInitialized());
    }

    @Test
    public void isInitialImageProviderReturnsFalse() {
        Assert.assertFalse(tracker.isInitialImageProvider(Mockito.mock(DistributedMember.class)));
    }

    @Test
    public void getRecordedBulkOpVersionTagsReturnsNull() {
        Assert.assertNull(tracker.getRecordedBulkOpVersionTags());
    }

    @Test
    public void getRecordedEventsReturnsNull() {
        Assert.assertNull(tracker.getRecordedEvents());
    }
}

