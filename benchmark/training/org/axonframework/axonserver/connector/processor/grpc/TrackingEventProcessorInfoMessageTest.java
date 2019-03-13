/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.axonserver.connector.processor.grpc;


import EventProcessorInfo.EventTrackerInfo;
import io.axoniq.axonserver.grpc.control.EventProcessorInfo;
import java.util.HashMap;
import java.util.Map;
import org.axonframework.eventhandling.EventTrackerStatus;
import org.axonframework.eventhandling.GlobalSequenceTrackingToken;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Created by Sara Pellegrini on 01/08/2018.
 * sara.pellegrini@gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class TrackingEventProcessorInfoMessageTest {
    @Mock
    private TrackingEventProcessor trackingEventProcessor;

    private Map<Integer, EventTrackerStatus> processingStatus = new HashMap<Integer, EventTrackerStatus>() {
        {
            this.put(0, new FakeEventTrackerStatus(ROOT_SEGMENT, true, false, new GlobalSequenceTrackingToken(100)));
        }
    };

    @Test
    public void instruction() {
        Mockito.when(trackingEventProcessor.processingStatus()).thenReturn(processingStatus);
        Mockito.when(trackingEventProcessor.getName()).thenReturn("ProcessorName");
        Mockito.when(trackingEventProcessor.activeProcessorThreads()).thenReturn(3);
        Mockito.when(trackingEventProcessor.availableProcessorThreads()).thenReturn(5);
        Mockito.when(trackingEventProcessor.isRunning()).thenReturn(false);
        Mockito.when(trackingEventProcessor.isError()).thenReturn(true);
        TrackingEventProcessorInfoMessage testSubject = new TrackingEventProcessorInfoMessage(trackingEventProcessor);
        EventProcessorInfo eventProcessorInfo = testSubject.instruction().getEventProcessorInfo();
        Assert.assertEquals("ProcessorName", eventProcessorInfo.getProcessorName());
        Assert.assertEquals(3, eventProcessorInfo.getActiveThreads());
        Assert.assertEquals(1, eventProcessorInfo.getEventTrackersInfoCount());
        Assert.assertFalse(eventProcessorInfo.getRunning());
        Assert.assertTrue(eventProcessorInfo.getError());
        Assert.assertTrue(((eventProcessorInfo.getAvailableThreads()) > 0));
        EventProcessorInfo.EventTrackerInfo eventTrackersInfo = eventProcessorInfo.getEventTrackersInfo(0);
        Assert.assertEquals(0, eventTrackersInfo.getSegmentId());
        Assert.assertEquals(1, eventTrackersInfo.getOnePartOf());
        Assert.assertTrue(eventTrackersInfo.getCaughtUp());
        Assert.assertFalse(eventTrackersInfo.getReplaying());
    }
}

