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
import java.util.Objects;
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
import org.eclipse.jetty.websocket.common.test.BlockheadConnection;
import org.eclipse.jetty.websocket.common.test.BlockheadServer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class DecoderReaderTest {
    public static class Quotes {
        private String author;

        private List<String> quotes = new ArrayList<>();

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public List<String> getQuotes() {
            return quotes;
        }

        public void addQuote(String quote) {
            quotes.add(quote);
        }
    }

    public static class QuotesDecoder implements Decoder.TextStream<DecoderReaderTest.Quotes> {
        @Override
        public void init(EndpointConfig config) {
        }

        @Override
        public void destroy() {
        }

        @Override
        public DecoderReaderTest.Quotes decode(Reader reader) throws IOException, DecodeException {
            DecoderReaderTest.Quotes quotes = new DecoderReaderTest.Quotes();
            try (BufferedReader buf = new BufferedReader(reader)) {
                String line;
                while ((line = buf.readLine()) != null) {
                    switch (line.charAt(0)) {
                        case 'a' :
                            quotes.setAuthor(line.substring(2));
                            break;
                        case 'q' :
                            quotes.addQuote(line.substring(2));
                            break;
                    }
                } 
            }
            return quotes;
        }
    }

    @ClientEndpoint(decoders = { DecoderReaderTest.QuotesDecoder.class })
    public static class QuotesSocket {
        private static final Logger LOG = Log.getLogger(DecoderReaderTest.QuotesSocket.class);

        public LinkedBlockingQueue<DecoderReaderTest.Quotes> messageQueue = new LinkedBlockingQueue<>();

        private CountDownLatch closeLatch = new CountDownLatch(1);

        @OnClose
        public void onClose(CloseReason close) {
            closeLatch.countDown();
        }

        @OnMessage
        public synchronized void onMessage(DecoderReaderTest.Quotes msg) {
            messageQueue.offer(msg);
            if (DecoderReaderTest.QuotesSocket.LOG.isDebugEnabled()) {
                String hashcode = Integer.toHexString(Objects.hashCode(this));
                DecoderReaderTest.QuotesSocket.LOG.debug("{}: Quotes from: {}", hashcode, msg.author);
                for (String quote : msg.quotes) {
                    DecoderReaderTest.QuotesSocket.LOG.debug("{}: - {}", hashcode, quote);
                }
            }
        }

        public void awaitClose() throws InterruptedException {
            closeLatch.await(4, TimeUnit.SECONDS);
        }
    }

    private static BlockheadServer server;

    private WebSocketContainer client;

    @Test
    public void testSingleQuotes() throws Exception {
        // Hook into server connection creation
        CompletableFuture<BlockheadConnection> serverConnFut = new CompletableFuture<>();
        DecoderReaderTest.server.addConnectFuture(serverConnFut);
        DecoderReaderTest.QuotesSocket quoter = new DecoderReaderTest.QuotesSocket();
        client.connectToServer(quoter, DecoderReaderTest.server.getWsUri());
        try (BlockheadConnection serverConn = serverConnFut.get(CONNECT, CONNECT_UNIT)) {
            writeQuotes(serverConn, "quotes-ben.txt");
            DecoderReaderTest.Quotes quotes = quoter.messageQueue.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Quotes Author", quotes.author, Matchers.is("Benjamin Franklin"));
            MatcherAssert.assertThat("Quotes Count", quotes.quotes.size(), Matchers.is(3));
        }
    }

    /**
     * Test that multiple quotes can go through decoder without issue.
     * <p>
     *     Since this decoder is Reader based, this is a useful test to ensure
     *     that the Reader creation / dispatch / hand off to the user endpoint
     *     works properly.
     * </p>
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testTwoQuotes() throws Exception {
        // Hook into server connection creation
        CompletableFuture<BlockheadConnection> serverConnFut = new CompletableFuture<>();
        DecoderReaderTest.server.addConnectFuture(serverConnFut);
        DecoderReaderTest.QuotesSocket quoter = new DecoderReaderTest.QuotesSocket();
        client.connectToServer(quoter, DecoderReaderTest.server.getWsUri());
        try (BlockheadConnection serverConn = serverConnFut.get(CONNECT, CONNECT_UNIT)) {
            writeQuotes(serverConn, "quotes-ben.txt");
            DecoderReaderTest.Quotes quotes = quoter.messageQueue.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Quotes Author", quotes.author, Matchers.is("Benjamin Franklin"));
            MatcherAssert.assertThat("Quotes Count", quotes.quotes.size(), Matchers.is(3));
            writeQuotes(serverConn, "quotes-twain.txt");
            quotes = quoter.messageQueue.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Quotes Author", quotes.author, Matchers.is("Mark Twain"));
        }
    }
}

