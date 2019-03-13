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
package org.eclipse.jetty.http2.client;


import Callback.NOOP;
import MetaData.Request;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http2.api.Session;
import org.eclipse.jetty.http2.api.Stream;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.util.FuturePromise;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class AsyncIOTest extends AbstractTest {
    @Test
    public void testLastContentAvailableBeforeService() throws Exception {
        start(new HttpServlet() {
            @Override
            protected void service(final HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                // Wait for the data to fully arrive.
                AsyncIOTest.sleep(1000);
                final AsyncContext asyncContext = request.startAsync();
                asyncContext.setTimeout(0);
                request.getInputStream().setReadListener(new AsyncIOTest.EmptyReadListener() {
                    @Override
                    public void onDataAvailable() throws IOException {
                        ServletInputStream input = request.getInputStream();
                        while (input.isReady()) {
                            int read = input.read();
                            if (read < 0)
                                break;

                        } 
                        if (input.isFinished())
                            asyncContext.complete();

                    }
                });
            }
        });
        Session session = newClient(new Session.Listener.Adapter());
        HttpFields fields = new HttpFields();
        MetaData.Request metaData = newRequest("GET", fields);
        HeadersFrame frame = new HeadersFrame(metaData, null, false);
        final CountDownLatch latch = new CountDownLatch(1);
        FuturePromise<Stream> promise = new FuturePromise();
        session.newStream(frame, promise, new Stream.Listener.Adapter() {
            @Override
            public void onHeaders(Stream stream, HeadersFrame frame) {
                if (frame.isEndStream())
                    latch.countDown();

            }
        });
        Stream stream = promise.get(5, TimeUnit.SECONDS);
        stream.data(new org.eclipse.jetty.http2.frames.DataFrame(stream.getId(), ByteBuffer.allocate(16), true), NOOP);
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testLastContentAvailableAfterServiceReturns() throws Exception {
        start(new HttpServlet() {
            @Override
            protected void service(final HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                final AsyncContext asyncContext = request.startAsync();
                asyncContext.setTimeout(0);
                request.getInputStream().setReadListener(new AsyncIOTest.EmptyReadListener() {
                    @Override
                    public void onDataAvailable() throws IOException {
                        ServletInputStream input = request.getInputStream();
                        while (input.isReady()) {
                            int read = input.read();
                            if (read < 0)
                                break;

                        } 
                        if (input.isFinished())
                            asyncContext.complete();

                    }
                });
            }
        });
        Session session = newClient(new Session.Listener.Adapter());
        HttpFields fields = new HttpFields();
        MetaData.Request metaData = newRequest("GET", fields);
        HeadersFrame frame = new HeadersFrame(metaData, null, false);
        final CountDownLatch latch = new CountDownLatch(1);
        FuturePromise<Stream> promise = new FuturePromise();
        session.newStream(frame, promise, new Stream.Listener.Adapter() {
            @Override
            public void onHeaders(Stream stream, HeadersFrame frame) {
                if (frame.isEndStream())
                    latch.countDown();

            }
        });
        Stream stream = promise.get(5, TimeUnit.SECONDS);
        // Wait until service() returns.
        Thread.sleep(1000);
        stream.data(new org.eclipse.jetty.http2.frames.DataFrame(stream.getId(), ByteBuffer.allocate(16), true), NOOP);
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testSomeContentAvailableAfterServiceReturns() throws Exception {
        final AtomicInteger count = new AtomicInteger();
        start(new HttpServlet() {
            @Override
            protected void service(final HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                final AsyncContext asyncContext = request.startAsync();
                asyncContext.setTimeout(0);
                request.getInputStream().setReadListener(new AsyncIOTest.EmptyReadListener() {
                    @Override
                    public void onDataAvailable() throws IOException {
                        count.incrementAndGet();
                        ServletInputStream input = request.getInputStream();
                        while (input.isReady()) {
                            int read = input.read();
                            if (read < 0)
                                break;

                        } 
                        if (input.isFinished())
                            asyncContext.complete();

                    }
                });
            }
        });
        Session session = newClient(new Session.Listener.Adapter());
        HttpFields fields = new HttpFields();
        MetaData.Request metaData = newRequest("GET", fields);
        HeadersFrame frame = new HeadersFrame(metaData, null, false);
        final CountDownLatch latch = new CountDownLatch(1);
        FuturePromise<Stream> promise = new FuturePromise();
        session.newStream(frame, promise, new Stream.Listener.Adapter() {
            @Override
            public void onHeaders(Stream stream, HeadersFrame frame) {
                if (frame.isEndStream())
                    latch.countDown();

            }
        });
        Stream stream = promise.get(5, TimeUnit.SECONDS);
        // Wait until service() returns.
        Thread.sleep(1000);
        stream.data(new org.eclipse.jetty.http2.frames.DataFrame(stream.getId(), ByteBuffer.allocate(1), false), NOOP);
        // Wait until onDataAvailable() returns.
        Thread.sleep(1000);
        stream.data(new org.eclipse.jetty.http2.frames.DataFrame(stream.getId(), ByteBuffer.allocate(1), true), NOOP);
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        // Make sure onDataAvailable() has been called twice
        Assertions.assertEquals(2, count.get());
    }

    private static class EmptyReadListener implements ReadListener {
        @Override
        public void onDataAvailable() throws IOException {
        }

        @Override
        public void onAllDataRead() throws IOException {
        }

        @Override
        public void onError(Throwable t) {
        }
    }
}

