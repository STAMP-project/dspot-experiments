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
import java.util.List;
import org.axonframework.eventhandling.AnnotationEventHandlerAdapter;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.GenericEventMessage;
import org.axonframework.eventhandling.GlobalSequenceTrackingToken;
import org.axonframework.eventhandling.ReplayToken;
import org.axonframework.eventhandling.TrackingToken;
import org.junit.Assert;
import org.junit.Test;


public class ReplayAwareMessageHandlerWrapperTest {
    private ReplayAwareMessageHandlerWrapperTest.SomeHandler handler;

    private AnnotationEventHandlerAdapter testSubject;

    private ReplayToken replayToken;

    private GlobalSequenceTrackingToken regularToken;

    @Test
    public void testInvokeWithReplayTokens() throws Exception {
        GenericTrackedEventMessage<Object> stringEvent = new GenericTrackedEventMessage(replayToken, GenericEventMessage.asEventMessage("1"));
        GenericTrackedEventMessage<Object> longEvent = new GenericTrackedEventMessage(replayToken, GenericEventMessage.asEventMessage(1L));
        Assert.assertTrue(testSubject.canHandle(stringEvent));
        Assert.assertTrue(testSubject.canHandle(longEvent));
        testSubject.handle(stringEvent);
        testSubject.handle(longEvent);
        Assert.assertTrue(handler.receivedLongs.isEmpty());
        Assert.assertFalse(handler.receivedStrings.isEmpty());
    }

    @AllowReplay(false)
    private static class SomeHandler {
        private List<String> receivedStrings = new ArrayList<>();

        private List<Long> receivedLongs = new ArrayList<>();

        @AllowReplay
        @EventHandler
        public void handle(String event, TrackingToken token) {
            Assert.assertFalse((token instanceof ReplayToken));
            receivedStrings.add(event);
        }

        @EventHandler
        public void handle(Long event, TrackingToken token) {
            Assert.assertFalse((token instanceof ReplayToken));
            receivedLongs.add(event);
        }
    }
}

