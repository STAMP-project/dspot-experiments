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


import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * This class tests receiving of messages by different types of {@link MessageHandler}
 */
public class MessageReceivingTest {
    private static final Logger LOG = Log.getLogger(EndpointEchoTest.class);

    private static Server server;

    private static EchoHandler handler;

    private static URI serverUri;

    private WebSocketContainer container;

    private final String VERY_LONG_STRING;

    public MessageReceivingTest() {
        byte[] raw = new byte[1024 * 1024];
        Arrays.fill(raw, ((byte) ('x')));
        VERY_LONG_STRING = new String(raw, StandardCharsets.UTF_8);
    }

    /**
     * Method tests receiving of text messages by parts.
     *
     * @throws Exception
     * 		on exception occur
     */
    @Test
    public void testPartialTextMessage() throws Exception {
        final MessageReceivingTest.TestEndpoint echoer = new MessageReceivingTest.TestEndpoint(new MessageReceivingTest.PartialStringCaptureHandler());
        MatcherAssert.assertThat(echoer, Matchers.instanceOf(Endpoint.class));
        // Issue connect using instance of class that extends Endpoint
        final Session session = container.connectToServer(echoer, MessageReceivingTest.serverUri);
        if (MessageReceivingTest.LOG.isDebugEnabled())
            MessageReceivingTest.LOG.debug("Client Connected: {}", session);

        session.getBasicRemote().sendText("");
        session.getBasicRemote().sendText("Echo");
        if (MessageReceivingTest.LOG.isDebugEnabled())
            MessageReceivingTest.LOG.debug("Client Message Sent");

        echoer.handler.getMessageQueue().awaitMessages(2, 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * Method tests receiving of binary messages at once.
     *
     * @throws Exception
     * 		on exception occur
     */
    @Test
    public void testWholeBinaryMessage() throws Exception {
        final MessageReceivingTest.TestEndpoint echoer = new MessageReceivingTest.TestEndpoint(new MessageReceivingTest.WholeByteBufferCaptureHandler());
        MatcherAssert.assertThat(echoer, Matchers.instanceOf(Endpoint.class));
        // Issue connect using instance of class that extends Endpoint
        final Session session = container.connectToServer(echoer, MessageReceivingTest.serverUri);
        if (MessageReceivingTest.LOG.isDebugEnabled())
            MessageReceivingTest.LOG.debug("Client Connected: {}", session);

        MessageReceivingTest.sendBinary(session, "");
        MessageReceivingTest.sendBinary(session, "Echo");
        if (MessageReceivingTest.LOG.isDebugEnabled())
            MessageReceivingTest.LOG.debug("Client Message Sent");

        echoer.handler.getMessageQueue().awaitMessages(2, 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * Method tests receiving of binary messages by parts.
     *
     * @throws Exception
     * 		on exception occur
     */
    @Test
    public void testPartialBinaryMessage() throws Exception {
        final MessageReceivingTest.TestEndpoint echoer = new MessageReceivingTest.TestEndpoint(new MessageReceivingTest.PartialByteBufferCaptureHandler());
        MatcherAssert.assertThat(echoer, Matchers.instanceOf(Endpoint.class));
        // Issue connect using instance of class that extends Endpoint
        final Session session = container.connectToServer(echoer, MessageReceivingTest.serverUri);
        if (MessageReceivingTest.LOG.isDebugEnabled())
            MessageReceivingTest.LOG.debug("Client Connected: {}", session);

        MessageReceivingTest.sendBinary(session, "");
        MessageReceivingTest.sendBinary(session, "Echo");
        if (MessageReceivingTest.LOG.isDebugEnabled())
            MessageReceivingTest.LOG.debug("Client Message Sent");

        echoer.handler.getMessageQueue().awaitMessages(2, 1000, TimeUnit.MILLISECONDS);
    }

    private static class TestEndpoint extends Endpoint {
        public final MessageReceivingTest.AbstractHandler handler;

        public TestEndpoint(MessageReceivingTest.AbstractHandler handler) {
            this.handler = handler;
        }

        @Override
        public void onOpen(Session session, EndpointConfig config) {
            session.addMessageHandler(handler);
        }
    }

    /**
     * Abstract message handler implementation, used for tests.
     */
    private abstract static class AbstractHandler implements MessageHandler {
        /**
         * Message queue to put the result messages.
         */
        private final MessageQueue messageQueue = new MessageQueue();

        /**
         * Returns message queue to test received messages count.
         *
         * @return message queue object
         */
        public MessageQueue getMessageQueue() {
            return messageQueue;
        }
    }

    /**
     * Partial message handler for receiving binary messages.
     */
    public static class PartialByteBufferCaptureHandler extends MessageReceivingTest.AbstractHandler implements MessageHandler.Partial<ByteBuffer> {
        /**
         * Parts of the current message. This list is appended with every non-last part and is
         * cleared after last part of a message has been received.
         */
        private final List<ByteBuffer> currentMessage = new ArrayList<>();

        @Override
        public void onMessage(ByteBuffer messagePart, boolean last) {
            final ByteBuffer bufferCopy = ByteBuffer.allocate(messagePart.capacity());
            bufferCopy.put(messagePart);
            currentMessage.add(bufferCopy);
            if (last) {
                int totalSize = 0;
                for (ByteBuffer bb : currentMessage) {
                    totalSize += bb.capacity();
                }
                final ByteBuffer result = ByteBuffer.allocate(totalSize);
                for (ByteBuffer bb : currentMessage) {
                    result.put(bb);
                }
                final String stringResult = new String(result.array());
                add(stringResult);
                currentMessage.clear();
            }
        }
    }

    /**
     * Whole message handler for receiving binary messages.
     */
    public class WholeByteBufferCaptureHandler extends MessageReceivingTest.AbstractHandler implements MessageHandler.Whole<ByteBuffer> {
        @Override
        public void onMessage(ByteBuffer message) {
            final String stringResult = new String(message.array());
            add(stringResult);
        }
    }

    /**
     * Partial message handler for receiving text messages.
     */
    public static class PartialStringCaptureHandler extends MessageReceivingTest.AbstractHandler implements MessageHandler.Partial<String> {
        /**
         * Parts of the current message. This list is appended with every non-last part and is
         * cleared after last part of a message has been received.
         */
        private StringBuilder sb = new StringBuilder();

        @Override
        public void onMessage(String messagePart, boolean last) {
            sb.append(messagePart);
            if (last) {
                add(sb.toString());
                sb = new StringBuilder();
            }
        }
    }

    /**
     * Whole message handler for receiving text messages.
     */
    public class WholeStringCaptureHandler extends MessageReceivingTest.AbstractHandler implements MessageHandler.Whole<String> {
        @Override
        public void onMessage(String message) {
            add(message);
        }
    }
}

