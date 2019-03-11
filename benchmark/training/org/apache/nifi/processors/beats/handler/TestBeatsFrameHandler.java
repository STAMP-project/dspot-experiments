/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.beats.handler;


import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.util.listen.dispatcher.AsyncChannelDispatcher;
import org.apache.nifi.processor.util.listen.event.EventFactory;
import org.apache.nifi.processor.util.listen.response.ChannelResponder;
import org.apache.nifi.processor.util.listen.response.ChannelResponse;
import org.apache.nifi.processors.beats.event.BeatsEvent;
import org.apache.nifi.processors.beats.frame.BeatsEncoder;
import org.apache.nifi.processors.beats.frame.BeatsFrame;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestBeatsFrameHandler {
    private Charset charset;

    private EventFactory<BeatsEvent> eventFactory;

    private BlockingQueue<BeatsEvent> events;

    private SelectionKey key;

    private AsyncChannelDispatcher dispatcher;

    private BeatsEncoder encoder;

    private ComponentLog logger;

    private BeatsFrameHandler<BeatsEvent> frameHandler;

    @Test
    public void testWindow() throws IOException, InterruptedException {
        final BeatsFrame openFrame = new BeatsFrame.Builder().version(((byte) (49))).frameType(((byte) (87))).seqNumber((-1)).payload(Integer.toString(1).getBytes()).build();
        final String sender = "sender1";
        final TestBeatsFrameHandler.CapturingChannelResponder responder = new TestBeatsFrameHandler.CapturingChannelResponder();
        // call the handler and verify respond() was called once with once response
        frameHandler.handle(openFrame, responder, sender);
        // No response expected
        Assert.assertEquals(0, responder.responded);
    }

    @Test
    public void testJson() throws IOException, InterruptedException {
        final byte[] jsonPayload = new byte[]{ // Payload eq { "message": "test-content", "field": "value"}
        123, 34, 109, 101, 115, 115, 97, 103, 101, 34, 58, 32, 34, 116, 101, 115, 116, 45, 99, 111, 110, 116, 101, 110, 116, 34, 44, 32, 34, 102, 105, 101, 108, 100, 34, 58, 32, 34, 118, 97, 108, 117, 101, 34, 125 };
        final BeatsFrame jsonFrame = new BeatsFrame.Builder().version(((byte) (50))).frameType(((byte) (74))).seqNumber(1).dataSize(45).payload(jsonPayload).build();
        final String sender = "sender1";
        final TestBeatsFrameHandler.CapturingChannelResponder responder = new TestBeatsFrameHandler.CapturingChannelResponder();
        // call the handler and verify respond() was called once with once response
        frameHandler.handle(jsonFrame, responder, sender);
        // No response expected
        Assert.assertEquals(0, responder.responded);
        // But events should contain one event
        Assert.assertEquals(1, events.size());
        final BeatsEvent event = events.poll();
        Assert.assertEquals("{\"message\": \"test-content\", \"field\": \"value\"}", new String(event.getData(), charset));
    }

    private static class CapturingChannelResponder implements ChannelResponder<SocketChannel> {
        int responded;

        List<ChannelResponse> responses = new ArrayList<>();

        @Override
        public SocketChannel getChannel() {
            return Mockito.mock(SocketChannel.class);
        }

        @Override
        public List<ChannelResponse> getResponses() {
            return responses;
        }

        @Override
        public void addResponse(ChannelResponse response) {
            responses.add(response);
        }

        @Override
        public void respond() throws IOException {
            (responded)++;
        }
    }
}

