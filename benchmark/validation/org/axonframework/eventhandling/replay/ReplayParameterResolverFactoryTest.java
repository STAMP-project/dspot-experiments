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
package org.axonframework.eventhandling.replay;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.axonframework.eventhandling.GenericEventMessage;
import org.axonframework.eventhandling.GlobalSequenceTrackingToken;
import org.axonframework.eventhandling.TrackingToken;
import org.junit.Assert;
import org.junit.Test;


public class ReplayParameterResolverFactoryTest {
    private ReplayParameterResolverFactoryTest.SomeHandler handler;

    private AnnotationEventHandlerAdapter testSubject;

    private ReplayToken replayToken;

    private GlobalSequenceTrackingToken regularToken;

    @Test
    public void testInvokeWithReplayTokens() throws Exception {
        GenericTrackedEventMessage<Object> replayEvent = new GenericTrackedEventMessage(replayToken, GenericEventMessage.asEventMessage(1L));
        GenericTrackedEventMessage<Object> liveEvent = new GenericTrackedEventMessage(regularToken, GenericEventMessage.asEventMessage(2L));
        Assert.assertTrue(testSubject.canHandle(replayEvent));
        Assert.assertTrue(testSubject.canHandle(liveEvent));
        testSubject.handle(replayEvent);
        testSubject.handle(liveEvent);
        Assert.assertEquals(Arrays.asList(1L, 2L), handler.receivedLongs);
        Assert.assertEquals(Collections.singletonList(1L), handler.receivedInReplay);
    }

    private static class SomeHandler {
        private List<Long> receivedLongs = new ArrayList<>();

        private List<Long> receivedInReplay = new ArrayList<>();

        @EventHandler
        public void handle(Long event, TrackingToken token, ReplayStatus replayStatus) {
            Assert.assertFalse((token instanceof ReplayToken));
            receivedLongs.add(event);
            if (replayStatus.isReplay()) {
                receivedInReplay.add(event);
            }
        }
    }
}

