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
package org.apache.nifi.processors.lumberjack.handler;


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
import org.apache.nifi.processors.lumberjack.event.LumberjackEvent;
import org.apache.nifi.processors.lumberjack.frame.LumberjackEncoder;
import org.apache.nifi.processors.lumberjack.frame.LumberjackFrame;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


@SuppressWarnings("deprecation")
public class TestLumberjackFrameHandler {
    private Charset charset;

    private EventFactory<LumberjackEvent> eventFactory;

    private BlockingQueue<LumberjackEvent> events;

    private SelectionKey key;

    private AsyncChannelDispatcher dispatcher;

    private LumberjackEncoder encoder;

    private ComponentLog logger;

    private LumberjackFrameHandler<LumberjackEvent> frameHandler;

    @Test
    public void testWindow() throws IOException, InterruptedException {
        final LumberjackFrame openFrame = new LumberjackFrame.Builder().version(((byte) (49))).frameType(((byte) (87))).seqNumber((-1)).payload(Integer.toString(1).getBytes()).build();
        final String sender = "sender1";
        final TestLumberjackFrameHandler.CapturingChannelResponder responder = new TestLumberjackFrameHandler.CapturingChannelResponder();
        // call the handler and verify respond() was called once with once response
        frameHandler.handle(openFrame, responder, sender);
        // No response expected
        Assert.assertEquals(0, responder.responded);
    }

    @Test
    public void testData() throws IOException, InterruptedException {
        final byte[] payload = new byte[]{ 0, 0, 0, 2// Number of pairs
        , 0, 0, 0, 4// Length of first pair key ('line')
        , 108, 105, 110, 101// 'line'
        , 0, 0, 0, 12// Length of 'test-content'
        , 116, 101, 115, 116// 
        , 45, 99, 111, 110// 'test-content'
        , 116, 101, 110, 116// 
        , 0, 0, 0, 5// Length of 2nd pair key (field)
        , 102, 105, 101, 108// 
        , 100, // 'field'
        0, 0, 0, 5// Length of 'value'
        , 118, 97, 108, 117// 'value'
        , 101 };
        final LumberjackFrame dataFrame = // Payload eq { enil: hello }
        new LumberjackFrame.Builder().version(((byte) (49))).frameType(((byte) (68))).seqNumber(1).payload(payload).build();
        final String sender = "sender1";
        final TestLumberjackFrameHandler.CapturingChannelResponder responder = new TestLumberjackFrameHandler.CapturingChannelResponder();
        // call the handler and verify respond() was called once with once response
        frameHandler.handle(dataFrame, responder, sender);
        // No response expected
        Assert.assertEquals(0, responder.responded);
        // But events should contain one event
        Assert.assertEquals(1, events.size());
        final LumberjackEvent event = events.poll();
        Assert.assertEquals("{\"field\":\"value\"}", new String(event.getFields()));
        Assert.assertEquals("test-content", new String(event.getData(), charset));
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

