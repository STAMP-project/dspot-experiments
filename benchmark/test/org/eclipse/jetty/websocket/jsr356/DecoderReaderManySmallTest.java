/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.websocket.jsr356;


import Timeouts.CONNECT;
import Timeouts.CONNECT_UNIT;
import Timeouts.POLL_EVENT;
import Timeouts.POLL_EVENT_UNIT;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.WebSocketContainer;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.common.CloseInfo;
import org.eclipse.jetty.websocket.common.WebSocketFrame;
import org.eclipse.jetty.websocket.common.frames.TextFrame;
import org.eclipse.jetty.websocket.common.test.BlockheadConnection;
import org.eclipse.jetty.websocket.common.test.BlockheadServer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


@Disabled("Not working atm")
public class DecoderReaderManySmallTest {
    public static class EventId {
        public int eventId;
    }

    public static class EventIdDecoder implements Decoder.TextStream<DecoderReaderManySmallTest.EventId> {
        @Override
        public void init(EndpointConfig config) {
        }

        @Override
        public void destroy() {
        }

        @Override
        public DecoderReaderManySmallTest.EventId decode(Reader reader) throws IOException, DecodeException {
            DecoderReaderManySmallTest.EventId id = new DecoderReaderManySmallTest.EventId();
            try (BufferedReader buf = new BufferedReader(reader)) {
                String line;
                while ((line = buf.readLine()) != null) {
                    id.eventId = Integer.parseInt(line);
                } 
            }
            return id;
        }
    }

    @ClientEndpoint(decoders = { DecoderReaderManySmallTest.EventIdDecoder.class })
    public static class EventIdSocket {
        public LinkedBlockingQueue<DecoderReaderManySmallTest.EventId> messageQueue = new LinkedBlockingQueue<>();

        private CountDownLatch closeLatch = new CountDownLatch(1);

        @OnClose
        public void onClose(CloseReason close) {
            closeLatch.countDown();
        }

        @OnMessage
        public void onMessage(DecoderReaderManySmallTest.EventId msg) {
            messageQueue.offer(msg);
        }

        public void awaitClose() throws InterruptedException {
            closeLatch.await(4, TimeUnit.SECONDS);
        }
    }

    private static final Logger LOG = Log.getLogger(DecoderReaderManySmallTest.class);

    private static BlockheadServer server;

    private WebSocketContainer client;

    @Test
    public void testManyIds() throws Exception {
        // Hook into server connection creation
        CompletableFuture<BlockheadConnection> serverConnFut = new CompletableFuture<>();
        DecoderReaderManySmallTest.server.addConnectFuture(serverConnFut);
        DecoderReaderManySmallTest.EventIdSocket ids = new DecoderReaderManySmallTest.EventIdSocket();
        client.connectToServer(ids, DecoderReaderManySmallTest.server.getWsUri());
        final int from = 1000;
        final int to = 2000;
        try (BlockheadConnection serverConn = serverConnFut.get(CONNECT, CONNECT_UNIT)) {
            // Setup echo of frames on server side
            serverConn.setIncomingFrameConsumer(( frame) -> {
                WebSocketFrame wsFrame = ((WebSocketFrame) (frame));
                if ((wsFrame.getOpCode()) == OpCode.TEXT) {
                    String msg = wsFrame.getPayloadAsUTF8();
                    if (msg == "generate") {
                        for (int id = from; id < to; id++) {
                            TextFrame event = new TextFrame();
                            event.setPayload(Integer.toString(id));
                            serverConn.write(event);
                        }
                        serverConn.write(new CloseInfo(StatusCode.NORMAL).asFrame());
                    }
                }
            });
            int count = from - to;
            ids.awaitClose();
            // collect seen ids
            List<Integer> seen = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                DecoderReaderManySmallTest.EventId id = ids.messageQueue.poll(POLL_EVENT, POLL_EVENT_UNIT);
                // validate that ids don't repeat.
                Assertions.assertFalse(seen.contains(id.eventId), ("Already saw ID: " + (id.eventId)));
                seen.add(id.eventId);
            }
            // validate that all expected ids have been seen (order is irrelevant here)
            for (int expected = from; expected < to; expected++) {
                MatcherAssert.assertThat(("Has expected id:" + expected), expected, Matchers.isIn(seen));
            }
        }
    }
}

