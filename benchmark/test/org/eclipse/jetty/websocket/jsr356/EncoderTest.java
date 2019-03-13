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


import ClientEndpointConfig.Builder;
import Timeouts.CONNECT;
import Timeouts.CONNECT_UNIT;
import Timeouts.POLL_EVENT;
import Timeouts.POLL_EVENT_UNIT;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.eclipse.jetty.websocket.common.OpCode;
import org.eclipse.jetty.websocket.common.WebSocketFrame;
import org.eclipse.jetty.websocket.common.test.BlockheadConnection;
import org.eclipse.jetty.websocket.common.test.BlockheadServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class EncoderTest {
    public static class Quotes {
        private String author;

        private List<String> quotes = new ArrayList<>();

        public void addQuote(String quote) {
            quotes.add(quote);
        }

        public String getAuthor() {
            return author;
        }

        public List<String> getQuotes() {
            return quotes;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
    }

    public static class QuotesEncoder implements Encoder.Text<EncoderTest.Quotes> {
        @Override
        public void destroy() {
        }

        @Override
        public String encode(EncoderTest.Quotes q) throws EncodeException {
            StringBuilder buf = new StringBuilder();
            buf.append("Author: ").append(q.getAuthor());
            buf.append(System.lineSeparator());
            for (String quote : q.quotes) {
                buf.append("Quote: ").append(quote);
                buf.append(System.lineSeparator());
            }
            return buf.toString();
        }

        @Override
        public void init(EndpointConfig config) {
        }
    }

    public static class QuotesSocket extends Endpoint implements MessageHandler.Whole<String> {
        private Session session;

        private LinkedBlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

        @Override
        public void onMessage(String message) {
            messageQueue.offer(message);
        }

        @Override
        public void onOpen(Session session, EndpointConfig config) {
            this.session = session;
            this.session.addMessageHandler(this);
        }

        public void write(EncoderTest.Quotes quotes) throws IOException, EncodeException {
            if (EncoderTest.LOG.isDebugEnabled())
                EncoderTest.LOG.debug("Writing Quotes: {}", quotes);

            this.session.getBasicRemote().sendObject(quotes);
        }
    }

    private static final Logger LOG = Log.getLogger(EncoderTest.class);

    private static BlockheadServer server;

    private WebSocketContainer client;

    @Test
    public void testSingleQuotes() throws Exception {
        // Hook into server connection creation
        CompletableFuture<BlockheadConnection> serverConnFut = new CompletableFuture<>();
        EncoderTest.server.addConnectFuture(serverConnFut);
        EncoderTest.QuotesSocket quoter = new EncoderTest.QuotesSocket();
        ClientEndpointConfig.Builder builder = Builder.create();
        List<Class<? extends Encoder>> encoders = new ArrayList<>();
        encoders.add(EncoderTest.QuotesEncoder.class);
        builder.encoders(encoders);
        ClientEndpointConfig cec = builder.build();
        client.connectToServer(quoter, cec, EncoderTest.server.getWsUri());
        try (BlockheadConnection serverConn = serverConnFut.get(CONNECT, CONNECT_UNIT)) {
            Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
                // Setup echo of frames on server side
                serverConn.setIncomingFrameConsumer(new EncoderTest.DataFrameEcho(serverConn));
                EncoderTest.Quotes ben = getQuotes("quotes-ben.txt");
                quoter.write(ben);
                String result = quoter.messageQueue.poll(POLL_EVENT, POLL_EVENT_UNIT);
                assertReceivedQuotes(result, ben);
            });
        }
    }

    @Test
    public void testTwoQuotes() throws Exception {
        // Hook into server connection creation
        CompletableFuture<BlockheadConnection> serverConnFut = new CompletableFuture<>();
        EncoderTest.server.addConnectFuture(serverConnFut);
        EncoderTest.QuotesSocket quoter = new EncoderTest.QuotesSocket();
        ClientEndpointConfig.Builder builder = Builder.create();
        List<Class<? extends Encoder>> encoders = new ArrayList<>();
        encoders.add(EncoderTest.QuotesEncoder.class);
        builder.encoders(encoders);
        ClientEndpointConfig cec = builder.build();
        client.connectToServer(quoter, cec, EncoderTest.server.getWsUri());
        try (BlockheadConnection serverConn = serverConnFut.get(CONNECT, CONNECT_UNIT)) {
            Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
                // Setup echo of frames on server side
                serverConn.setIncomingFrameConsumer(new EncoderTest.DataFrameEcho(serverConn));
                EncoderTest.Quotes ben = getQuotes("quotes-ben.txt");
                EncoderTest.Quotes twain = getQuotes("quotes-twain.txt");
                quoter.write(ben);
                quoter.write(twain);
                String result = quoter.messageQueue.poll(POLL_EVENT, POLL_EVENT_UNIT);
                assertReceivedQuotes(result, ben);
                result = quoter.messageQueue.poll(POLL_EVENT, POLL_EVENT_UNIT);
                assertReceivedQuotes(result, twain);
            });
        }
    }

    private static class DataFrameEcho implements Consumer<Frame> {
        private final BlockheadConnection connection;

        public DataFrameEcho(BlockheadConnection connection) {
            this.connection = connection;
        }

        @Override
        public void accept(Frame frame) {
            if (OpCode.isDataFrame(frame.getOpCode())) {
                WebSocketFrame copy = WebSocketFrame.copy(frame);
                copy.setMask(null);// remove client masking

                connection.write(copy);
            }
        }
    }
}

