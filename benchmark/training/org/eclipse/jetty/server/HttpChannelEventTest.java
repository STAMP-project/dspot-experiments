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
package org.eclipse.jetty.server;


import AbstractHandler.ErrorDispatchHandler;
import HttpStatus.BAD_REQUEST_400;
import HttpStatus.OK_200;
import HttpTester.Response;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class HttpChannelEventTest {
    private Server server;

    private LocalConnector connector;

    @Test
    public void testRequestContentSlice() throws Exception {
        int data = 'x';
        CountDownLatch applicationLatch = new CountDownLatch(1);
        start(new HttpChannelEventTest.TestHandler() {
            @Override
            protected void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                ServletInputStream input = request.getInputStream();
                int content = input.read();
                Assertions.assertEquals(data, content);
                applicationLatch.countDown();
            }
        });
        CountDownLatch listenerLatch = new CountDownLatch(1);
        connector.addBean(new HttpChannel.Listener() {
            @Override
            public void onRequestContent(Request request, ByteBuffer content) {
                // Consume the buffer to verify it's a slice.
                content.position(content.limit());
                listenerLatch.countDown();
            }
        });
        HttpTester.Request request = HttpTester.newRequest();
        request.setHeader("Host", "localhost");
        request.setContent(new byte[]{ ((byte) (data)) });
        ByteBuffer buffer = connector.getResponse(request.generate(), 5, TimeUnit.SECONDS);
        // Listener event happens before the application.
        Assertions.assertTrue(listenerLatch.await(5, TimeUnit.SECONDS));
        Assertions.assertTrue(applicationLatch.await(5, TimeUnit.SECONDS));
        HttpTester.Response response = HttpTester.parseResponse(buffer);
        Assertions.assertEquals(OK_200, response.getStatus());
    }

    @Test
    public void testResponseContentSlice() throws Exception {
        byte[] data = new byte[]{ 'y' };
        start(new HttpChannelEventTest.TestHandler() {
            @Override
            protected void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                response.getOutputStream().write(data);
            }
        });
        CountDownLatch latch = new CountDownLatch(1);
        connector.addBean(new HttpChannel.Listener() {
            @Override
            public void onResponseContent(Request request, ByteBuffer content) {
                Assertions.assertTrue(content.hasRemaining());
                latch.countDown();
            }
        });
        HttpTester.Request request = HttpTester.newRequest();
        request.setHeader("Host", "localhost");
        HttpTester.Response response = HttpTester.parseResponse(connector.getResponse(request.toString(), 5, TimeUnit.SECONDS));
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        Assertions.assertEquals(OK_200, response.getStatus());
        Assertions.assertArrayEquals(data, response.getContentBytes());
    }

    @Test
    public void testRequestFailure() throws Exception {
        start(new HttpChannelEventTest.TestHandler());
        CountDownLatch latch = new CountDownLatch(2);
        connector.addBean(new HttpChannel.Listener() {
            @Override
            public void onRequestFailure(Request request, Throwable failure) {
                latch.countDown();
            }

            @Override
            public void onComplete(Request request) {
                latch.countDown();
            }
        });
        // No Host header, request will fail.
        String request = HttpTester.newRequest().toString();
        HttpTester.Response response = HttpTester.parseResponse(connector.getResponse(request, 5, TimeUnit.SECONDS));
        Assertions.assertEquals(BAD_REQUEST_400, response.getStatus());
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testResponseFailure() throws Exception {
        start(new HttpChannelEventTest.TestHandler() {
            @Override
            protected void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                // Closes all connections, response will fail.
                connector.getConnectedEndPoints().forEach(EndPoint::close);
            }
        });
        CountDownLatch latch = new CountDownLatch(2);
        connector.addBean(new HttpChannel.Listener() {
            @Override
            public void onResponseFailure(Request request, Throwable failure) {
                latch.countDown();
            }

            @Override
            public void onComplete(Request request) {
                latch.countDown();
            }
        });
        HttpTester.Request request = HttpTester.newRequest();
        request.setHeader("Host", "localhost");
        HttpTester.parseResponse(connector.getResponse(request.toString(), 5, TimeUnit.SECONDS));
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testExchangeTimeRecording() throws Exception {
        start(new HttpChannelEventTest.TestHandler());
        CountDownLatch latch = new CountDownLatch(1);
        AtomicLong elapsed = new AtomicLong();
        connector.addBean(new HttpChannel.Listener() {
            private final String attribute = (getClass().getName()) + ".begin";

            @Override
            public void onRequestBegin(Request request) {
                request.setAttribute(attribute, System.nanoTime());
            }

            @Override
            public void onComplete(Request request) {
                long endTime = System.nanoTime();
                long beginTime = ((Long) (request.getAttribute(attribute)));
                elapsed.set((endTime - beginTime));
                latch.countDown();
            }
        });
        HttpTester.Request request = HttpTester.newRequest();
        request.setHeader("Host", "localhost");
        HttpTester.Response response = HttpTester.parseResponse(connector.getResponse(request.toString(), 5, TimeUnit.SECONDS));
        Assertions.assertEquals(OK_200, response.getStatus());
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        MatcherAssert.assertThat(elapsed.get(), Matchers.greaterThan(0L));
    }

    private static class TestHandler extends AbstractHandler.ErrorDispatchHandler {
        @Override
        protected final void doNonErrorHandle(String target, Request jettyRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            jettyRequest.setHandled(true);
            handle(request, response);
        }

        protected void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        }
    }
}

