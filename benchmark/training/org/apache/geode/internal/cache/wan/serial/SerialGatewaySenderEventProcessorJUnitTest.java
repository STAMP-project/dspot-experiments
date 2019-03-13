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
package org.apache.geode.internal.cache.wan.serial;


import AbstractGatewaySender.EventWrapper;
import Operation.CREATE;
import Operation.UPDATE_VERSION_STAMP;
import java.util.Map;
import org.apache.geode.internal.cache.EntryEventImpl;
import org.apache.geode.internal.cache.EventID;
import org.apache.geode.internal.cache.LocalRegion;
import org.apache.geode.internal.cache.ha.ThreadIdentifier;
import org.apache.geode.internal.cache.wan.AbstractGatewaySender;
import org.apache.geode.internal.cache.wan.GatewaySenderStats;
import org.apache.geode.internal.logging.LogService;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import static SerialGatewaySenderEventProcessor.REAP_THRESHOLD;


public class SerialGatewaySenderEventProcessorJUnitTest {
    private AbstractGatewaySender sender;

    private TestSerialGatewaySenderEventProcessor processor;

    private static final Logger logger = LogService.getLogger();

    @Test
    public void validateUnprocessedTokensMapUpdated() throws Exception {
        GatewaySenderStats gss = Mockito.mock(GatewaySenderStats.class);
        Mockito.when(sender.getStatistics()).thenReturn(gss);
        // Handle primary event
        EventID id = handlePrimaryEvent();
        // Verify the token was added by checking the correct stat methods were called and the size of
        // the unprocessedTokensMap.
        Mockito.verify(gss).incUnprocessedTokensAddedByPrimary();
        Mockito.verify(gss, Mockito.never()).incUnprocessedEventsRemovedByPrimary();
        Assert.assertEquals(1, this.processor.getUnprocessedTokensSize());
        // Handle the event from the secondary. The call to enqueueEvent is necessary to synchronize the
        // unprocessedEventsLock and prevent the assertion error in basicHandleSecondaryEvent.
        EntryEventImpl event = Mockito.mock(EntryEventImpl.class);
        Mockito.when(event.getRegion()).thenReturn(Mockito.mock(LocalRegion.class));
        Mockito.when(event.getEventId()).thenReturn(id);
        Mockito.when(event.getOperation()).thenReturn(CREATE);
        this.processor.enqueueEvent(null, event, null);
        // Verify the token was removed by checking the correct stat methods were called and the size of
        // the unprocessedTokensMap.
        Mockito.verify(gss).incUnprocessedTokensRemovedBySecondary();
        Mockito.verify(gss, Mockito.never()).incUnprocessedEventsAddedBySecondary();
        Assert.assertEquals(0, this.processor.getUnprocessedTokensSize());
    }

    @Test
    public void verifyUpdateVersionStampEventShouldNotBeAddToSecondary() throws Exception {
        // GatewaySenderStats gss = mock(GatewaySenderStats.class);
        // when(sender.getStatistics()).thenReturn(gss);
        // when(sender.isPrimary()).thenReturn(false);
        // // Handle primary event
        // EventID id = handlePrimaryEvent();
        // Verify the token was added by checking the correct stat methods were called and the size of
        // the unprocessedTokensMap.
        // verify(gss).incUnprocessedTokensAddedByPrimary();
        // verify(gss, never()).incUnprocessedEventsRemovedByPrimary();
        // assertEquals(1, this.processor.getUnprocessedTokensSize());
        // Handle the event from the secondary. The call to enqueueEvent is necessary to synchronize the
        // unprocessedEventsLock and prevent the assertion error in basicHandleSecondaryEvent.
        EntryEventImpl event = Mockito.mock(EntryEventImpl.class);
        // when(event.getRegion()).thenReturn(mock(LocalRegion.class));
        // when(event.getEventId()).thenReturn(id);
        Mockito.when(event.getOperation()).thenReturn(UPDATE_VERSION_STAMP);
        this.processor = Mockito.spy(processor);
        this.processor.enqueueEvent(null, event, null);
        // Verify the token was removed by checking the correct stat methods were called and the size of
        // the unprocessedTokensMap.
        // verify(gss, never()).incUnprocessedEventsAddedBySecondary();
        // verify(gss).incUnprocessedTokensRemovedBySecondary();
        // verify(gss, never()).incUnprocessedEventsAddedBySecondary();
        handleSecondaryEvent(ArgumentMatchers.any());
        // assertEquals(0, this.processor.getUnprocessedTokensSize());
    }

    @Test
    public void verifyCMENotOriginRemoteShouldNotBeAddToSecondary() throws Exception {
        EntryEventImpl event = Mockito.mock(EntryEventImpl.class);
        Mockito.when(event.getOperation()).thenReturn(CREATE);
        Mockito.when(event.isConcurrencyConflict()).thenReturn(true);
        Mockito.when(event.isOriginRemote()).thenReturn(false);
        this.processor = Mockito.spy(processor);
        this.processor.enqueueEvent(null, event, null);
        handleSecondaryEvent(ArgumentMatchers.any());
    }

    @Test
    public void verifyCMEButOriginRemoteShouldBeAddToSecondary() throws Exception {
        EntryEventImpl event = Mockito.mock(EntryEventImpl.class);
        Mockito.when(event.getOperation()).thenReturn(CREATE);
        Mockito.when(event.isConcurrencyConflict()).thenReturn(true);
        Mockito.when(event.isOriginRemote()).thenReturn(true);
        LocalRegion region = Mockito.mock(LocalRegion.class);
        Mockito.when(event.getRegion()).thenReturn(region);
        Mockito.when(region.getFullPath()).thenReturn("/testRegion");
        EventID id = Mockito.mock(EventID.class);
        Mockito.when(event.getEventId()).thenReturn(id);
        this.processor = Mockito.spy(processor);
        handleSecondaryEvent(ArgumentMatchers.any());
        this.processor.enqueueEvent(null, event, null);
        handleSecondaryEvent(ArgumentMatchers.any());
    }

    @Test
    public void verifyNotCMENotUpdateVersionStampShouldBeAddToSecondary() throws Exception {
        EntryEventImpl event = Mockito.mock(EntryEventImpl.class);
        Mockito.when(event.getOperation()).thenReturn(CREATE);
        Mockito.when(event.isConcurrencyConflict()).thenReturn(false);
        LocalRegion region = Mockito.mock(LocalRegion.class);
        Mockito.when(event.getRegion()).thenReturn(region);
        Mockito.when(region.getFullPath()).thenReturn("/testRegion");
        EventID id = Mockito.mock(EventID.class);
        Mockito.when(event.getEventId()).thenReturn(id);
        this.processor = Mockito.spy(processor);
        handleSecondaryEvent(ArgumentMatchers.any());
        this.processor.enqueueEvent(null, event, null);
        handleSecondaryEvent(ArgumentMatchers.any());
    }

    @Test
    public void validateUnprocessedTokensMapReaping() throws Exception {
        // Set the token timeout low
        int originalTokenTimeout = AbstractGatewaySender.TOKEN_TIMEOUT;
        AbstractGatewaySender.TOKEN_TIMEOUT = 500;
        try {
            GatewaySenderStats gss = Mockito.mock(GatewaySenderStats.class);
            Mockito.when(sender.getStatistics()).thenReturn(gss);
            // Add REAP_THRESHOLD + 1 events to the unprocessed tokens map. This causes the uncheckedCount
            // in the reaper to be REAP_THRESHOLD. The next event will cause the reaper to run.\
            int numEvents = (REAP_THRESHOLD) + 1;
            for (int i = 0; i < numEvents; i++) {
                handlePrimaryEvent();
            }
            Assert.assertEquals(numEvents, this.processor.getUnprocessedTokensSize());
            // Wait for the timeout
            Thread.sleep(((AbstractGatewaySender.TOKEN_TIMEOUT) + 1000));
            // Add one more event to the unprocessed tokens map. This will reap all of the previous
            // tokens.
            handlePrimaryEvent();
            Assert.assertEquals(1, this.processor.getUnprocessedTokensSize());
        } finally {
            AbstractGatewaySender.TOKEN_TIMEOUT = originalTokenTimeout;
        }
    }

    @Test
    public void validateUnProcessedEventsList() {
        Map<EventID, AbstractGatewaySender.EventWrapper> unprocessedEvents = ((Map<EventID, AbstractGatewaySender.EventWrapper>) (ReflectionTestUtils.getField(processor, "unprocessedEvents")));
        long complexThreadId1 = ThreadIdentifier.createFakeThreadIDForParallelGSPrimaryBucket(0, 1, 1);
        long complexThreadId3 = ThreadIdentifier.createFakeThreadIDForParallelGSPrimaryBucket(0, 3, 3);
        unprocessedEvents.put(new EventID("mem1".getBytes(), complexThreadId1, 1L), null);
        unprocessedEvents.put(new EventID("mem2".getBytes(), 2L, 2L), null);
        String unProcessedEvents = printUnprocessedEvents();
        SerialGatewaySenderEventProcessorJUnitTest.logger.info(("UnprocessedEvents: " + unProcessedEvents));
        assertThat(unProcessedEvents).contains("threadID=0x1010000|1;sequenceID=1");
        assertThat(unProcessedEvents).contains("threadID=2;sequenceID=2");
        processor.unprocessedTokens.put(new EventID("mem3".getBytes(), complexThreadId3, 3L), 3L);
        processor.unprocessedTokens.put(new EventID("mem4".getBytes(), 4L, 4L), 4L);
        String unProcessedTokens = printUnprocessedTokens();
        SerialGatewaySenderEventProcessorJUnitTest.logger.info(("UnprocessedTokens: " + unProcessedTokens));
        assertThat(unProcessedTokens).contains("threadID=0x3010000|3;sequenceID=3");
        assertThat(unProcessedTokens).contains("threadID=4;sequenceID=4");
    }
}

