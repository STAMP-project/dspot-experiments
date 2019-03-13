/**
 * Copyright (c) 2010-2019. Axon Framework
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
package org.axonframework.axonserver.connector.processor;


import java.util.Optional;
import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.EventProcessor;
import org.axonframework.eventhandling.SubscribingEventProcessor;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class EventProcessorControllerTest {
    private static final String TRACKING_PROCESSOR_NAME = "some-event-processor-name";

    private static final String SUBSCRIBING_PROCESSOR_NAME = "some-other-processor";

    private static final int SEGMENT_ID = 0;

    private final EventProcessingConfiguration eventProcessingConfiguration = Mockito.mock(EventProcessingConfiguration.class);

    private final EventProcessorController testSubject = new EventProcessorController(eventProcessingConfiguration);

    private final TrackingEventProcessor testTrackingProcessor = Mockito.mock(TrackingEventProcessor.class);

    private final SubscribingEventProcessor testSubscribingProcessor = Mockito.mock(SubscribingEventProcessor.class);

    @Test
    public void testGetEventProcessorReturnsAnEventProcessor() {
        EventProcessor result = testSubject.getEventProcessor(EventProcessorControllerTest.TRACKING_PROCESSOR_NAME);
        Assert.assertEquals(testTrackingProcessor, result);
    }

    @Test(expected = RuntimeException.class)
    public void testGetEventProcessorThrowsRuntimeExceptionForNonExistingProcessor() {
        testSubject.getEventProcessor("non-existing-processor");
    }

    @Test
    public void testPauseProcessorCallsShutdownOnAnEventProcessor() {
        testSubject.pauseProcessor(EventProcessorControllerTest.TRACKING_PROCESSOR_NAME);
        Mockito.verify(testTrackingProcessor).shutDown();
    }

    @Test
    public void testStartProcessorCallsStartOnAnEventProcessor() {
        testSubject.startProcessor(EventProcessorControllerTest.TRACKING_PROCESSOR_NAME);
        Mockito.verify(testTrackingProcessor).start();
    }

    @Test
    public void testReleaseSegmentCallsReleaseSegmentOnAnEventProcessor() {
        testSubject.releaseSegment(EventProcessorControllerTest.TRACKING_PROCESSOR_NAME, EventProcessorControllerTest.SEGMENT_ID);
        Mockito.verify(testTrackingProcessor).releaseSegment(EventProcessorControllerTest.SEGMENT_ID);
    }

    @Test
    public void testReleaseSegmentDoesNothingIfTheEventProcessorIsNotOfTypeTracking() {
        testSubject.releaseSegment(EventProcessorControllerTest.SUBSCRIBING_PROCESSOR_NAME, EventProcessorControllerTest.SEGMENT_ID);
        Mockito.verifyZeroInteractions(testSubscribingProcessor);
    }

    @Test
    public void testSplitSegmentCallSplitOnAnEventProcessor() {
        boolean result = testSubject.splitSegment(EventProcessorControllerTest.TRACKING_PROCESSOR_NAME, EventProcessorControllerTest.SEGMENT_ID);
        Mockito.verify(testTrackingProcessor).splitSegment(EventProcessorControllerTest.SEGMENT_ID);
        Assert.assertTrue(result);
    }

    @Test
    public void testSplitSegmentDoesNothingIfTheEventProcessorIsNotOfTypeTracking() {
        boolean result = testSubject.splitSegment(EventProcessorControllerTest.SUBSCRIBING_PROCESSOR_NAME, EventProcessorControllerTest.SEGMENT_ID);
        Mockito.verifyZeroInteractions(testSubscribingProcessor);
        Assert.assertFalse(result);
    }

    @Test(expected = IllegalStateException.class)
    public void testSplitSegmentThrowsAnExceptionIfSplittingFails() {
        String testEventProcessorName = "failing-event-processor";
        TrackingEventProcessor testTrackingProcessor = Mockito.mock(TrackingEventProcessor.class);
        Mockito.when(eventProcessingConfiguration.eventProcessor(testEventProcessorName)).thenReturn(Optional.of(testTrackingProcessor));
        Mockito.when(testTrackingProcessor.splitSegment(EventProcessorControllerTest.SEGMENT_ID)).thenThrow(new IllegalStateException("some-exception"));
        testSubject.splitSegment(testEventProcessorName, EventProcessorControllerTest.SEGMENT_ID);
    }

    @Test
    public void testMergeSegmentCallMergeOnAnEventProcessor() {
        boolean result = testSubject.mergeSegment(EventProcessorControllerTest.TRACKING_PROCESSOR_NAME, EventProcessorControllerTest.SEGMENT_ID);
        Mockito.verify(testTrackingProcessor).mergeSegment(EventProcessorControllerTest.SEGMENT_ID);
        Assert.assertTrue(result);
    }

    @Test
    public void testMergeSegmentDoesNothingIfTheEventProcessorIsNotOfTypeTracking() {
        boolean result = testSubject.mergeSegment(EventProcessorControllerTest.SUBSCRIBING_PROCESSOR_NAME, EventProcessorControllerTest.SEGMENT_ID);
        Mockito.verifyZeroInteractions(testSubscribingProcessor);
        Assert.assertFalse(result);
    }

    @Test(expected = IllegalStateException.class)
    public void testMergeSegmentThrowsAnExceptionIfMergingFails() {
        String testEventProcessorName = "failing-event-processor";
        TrackingEventProcessor testTrackingProcessor = Mockito.mock(TrackingEventProcessor.class);
        Mockito.when(eventProcessingConfiguration.eventProcessor(testEventProcessorName)).thenReturn(Optional.of(testTrackingProcessor));
        Mockito.when(testTrackingProcessor.mergeSegment(EventProcessorControllerTest.SEGMENT_ID)).thenThrow(new IllegalStateException("some-exception"));
        testSubject.mergeSegment(testEventProcessorName, EventProcessorControllerTest.SEGMENT_ID);
    }
}

