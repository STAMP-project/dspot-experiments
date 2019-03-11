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


import EventFactory.SENDER_KEY;
import LumberjackMetadata.SEQNUMBER_KEY;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import javax.net.ssl.SSLContext;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.util.listen.dispatcher.AsyncChannelDispatcher;
import org.apache.nifi.processor.util.listen.dispatcher.ChannelDispatcher;
import org.apache.nifi.processor.util.listen.event.Event;
import org.apache.nifi.processor.util.listen.event.EventFactory;
import org.apache.nifi.processor.util.listen.handler.ChannelHandlerFactory;
import org.apache.nifi.processor.util.listen.response.ChannelResponder;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


@SuppressWarnings("deprecation")
public class ITLumberjackSocketChannelHandler {
    private EventFactory<ITLumberjackSocketChannelHandler.TestEvent> eventFactory;

    private ChannelHandlerFactory<ITLumberjackSocketChannelHandler.TestEvent, AsyncChannelDispatcher> channelHandlerFactory;

    private BlockingQueue<ByteBuffer> byteBuffers;

    private BlockingQueue<ITLumberjackSocketChannelHandler.TestEvent> events;

    private ComponentLog logger = Mockito.mock(ComponentLog.class);

    private int maxConnections;

    private SSLContext sslContext;

    private Charset charset;

    private ChannelDispatcher dispatcher;

    @Test
    public void testBasicHandling() throws IOException, InterruptedException {
        final String multiFrameData = "3143000000d7785ec48fcf6ac4201087b3bbe9defb06be40ab669b1602bdf5d49728" + (((("031957a97f82232979fbaaa7c0924b2e018701f537f37df2ab699a53aea75cad321673ffe43a38e4e04c043f02" + "1f71461b26873e711bee9480f48b0af10fe2889113b8c9e28f4322b82395413a50cafd79957c253d0b992faf41") + "29c2f27c12e5af35be2cedbec133d9b34e0ee27db87db05596fd62f4680796b421964fc9b032ac4dcb54d2575") + "a28a3559df3413ae7be12edf6e9367c2e07f95ca4a848bb856e1b42ed61427d45da2df4f628f40f0000ffff01000") + "0ffff35e0eff0");
        final List<String> messages = new ArrayList<>();
        messages.add(multiFrameData);
        run(messages);
        // Check for the 4 frames (from the hex string above) are back...
        Assert.assertEquals(4, events.size());
        boolean found1 = false;
        boolean found2 = false;
        boolean found3 = false;
        boolean found4 = false;
        ITLumberjackSocketChannelHandler.TestEvent event;
        while ((event = events.poll()) != null) {
            Map<String, String> metadata = event.metadata;
            Assert.assertTrue(metadata.containsKey(SEQNUMBER_KEY));
            final String seqNum = metadata.get(SEQNUMBER_KEY);
            if (seqNum.equals("1")) {
                found1 = true;
            } else
                if (seqNum.equals("2")) {
                    found2 = true;
                } else
                    if (seqNum.equals("3")) {
                        found3 = true;
                    } else
                        if (seqNum.equals("4")) {
                            found4 = true;
                        }



        } 
        Assert.assertTrue(found1);
        Assert.assertTrue(found2);
        Assert.assertTrue(found3);
        Assert.assertTrue(found4);
    }

    // Test event to produce from the data
    private static class TestEvent implements Event<SocketChannel> {
        private byte[] data;

        private Map<String, String> metadata;

        public TestEvent(byte[] data, Map<String, String> metadata) {
            this.data = data;
            this.metadata = metadata;
        }

        @Override
        public String getSender() {
            return metadata.get(SENDER_KEY);
        }

        @Override
        public byte[] getData() {
            return data;
        }

        @Override
        public ChannelResponder<SocketChannel> getResponder() {
            return null;
        }
    }

    // Factory to create test events and send responses for testing
    private static class TestEventHolderFactory implements EventFactory<ITLumberjackSocketChannelHandler.TestEvent> {
        @Override
        public ITLumberjackSocketChannelHandler.TestEvent create(final byte[] data, final Map<String, String> metadata, final ChannelResponder responder) {
            return new ITLumberjackSocketChannelHandler.TestEvent(data, metadata);
        }
    }
}

