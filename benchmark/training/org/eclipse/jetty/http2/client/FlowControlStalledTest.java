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


import MetaData.Request;
import MetaData.Response;
import SettingsFrame.INITIAL_WINDOW_SIZE;
import Stream.Listener;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http2.BufferingFlowControlStrategy;
import org.eclipse.jetty.http2.FlowControlStrategy;
import org.eclipse.jetty.http2.ISession;
import org.eclipse.jetty.http2.IStream;
import org.eclipse.jetty.http2.api.Session;
import org.eclipse.jetty.http2.api.Stream;
import org.eclipse.jetty.http2.api.server.ServerSessionListener;
import org.eclipse.jetty.http2.frames.DataFrame;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Promise;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FlowControlStalledTest {
    protected ServerConnector connector;

    protected HTTP2Client client;

    protected Server server;

    @Test
    public void testStreamStalledIsInvokedOnlyOnce() throws Exception {
        AtomicReference<CountDownLatch> stallLatch = new AtomicReference<>(new CountDownLatch(1));
        CountDownLatch unstallLatch = new CountDownLatch(1);
        start(() -> new BufferingFlowControlStrategy(0.5F) {
            @Override
            public void onStreamStalled(IStream stream) {
                super.onStreamStalled(stream);
                stallLatch.get().countDown();
            }

            @Override
            protected void onStreamUnstalled(IStream stream) {
                super.onStreamUnstalled(stream);
                unstallLatch.countDown();
            }
        }, new ServerSessionListener.Adapter() {
            @Override
            public Listener onNewStream(Stream stream, HeadersFrame frame) {
                MetaData.Request request = ((MetaData.Request) (frame.getMetaData()));
                MetaData.Response response = new MetaData.Response(HttpVersion.HTTP_2, HttpStatus.OK_200, new HttpFields());
                if (request.getURIString().endsWith("/stall")) {
                    stream.headers(new HeadersFrame(stream.getId(), response, null, false), new Callback() {
                        @Override
                        public void succeeded() {
                            // Send a large chunk of data so the stream gets stalled.
                            ByteBuffer data = ByteBuffer.allocate(((FlowControlStrategy.DEFAULT_WINDOW_SIZE) + 1));
                            stream.data(new DataFrame(stream.getId(), data, true), NOOP);
                        }
                    });
                } else {
                    stream.headers(new HeadersFrame(stream.getId(), response, null, true), Callback.NOOP);
                }
                return null;
            }
        });
        // Use a large session window so that only the stream gets stalled.
        client.setInitialSessionRecvWindow((5 * (FlowControlStrategy.DEFAULT_WINDOW_SIZE)));
        Session client = newClient(new Session.Listener.Adapter());
        CountDownLatch latch = new CountDownLatch(1);
        Queue<Callback> callbacks = new ArrayDeque<>();
        MetaData.Request request = newRequest("GET", "/stall", new HttpFields());
        client.newStream(new HeadersFrame(request, null, true), new Promise.Adapter<>(), new Stream.Listener.Adapter() {
            @Override
            public void onData(Stream stream, DataFrame frame, Callback callback) {
                callbacks.offer(callback);
                if (frame.isEndStream())
                    latch.countDown();

            }
        });
        Assertions.assertTrue(stallLatch.get().await(5, TimeUnit.SECONDS));
        // First stream is now stalled, check that writing a second stream
        // does not result in the first be notified again of being stalled.
        stallLatch.set(new CountDownLatch(1));
        request = newRequest("GET", "/", new HttpFields());
        client.newStream(new HeadersFrame(request, null, true), new Promise.Adapter<>(), new Stream.Listener.Adapter());
        Assertions.assertFalse(stallLatch.get().await(1, TimeUnit.SECONDS));
        // Consume all data.
        while (!(latch.await(10, TimeUnit.MILLISECONDS))) {
            Callback callback = callbacks.poll();
            if (callback != null)
                callback.succeeded();

        } 
        // Make sure the unstall callback is invoked.
        Assertions.assertTrue(unstallLatch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testSessionStalledIsInvokedOnlyOnce() throws Exception {
        AtomicReference<CountDownLatch> stallLatch = new AtomicReference<>(new CountDownLatch(1));
        CountDownLatch unstallLatch = new CountDownLatch(1);
        start(() -> new BufferingFlowControlStrategy(0.5F) {
            @Override
            public void onSessionStalled(ISession session) {
                super.onSessionStalled(session);
                stallLatch.get().countDown();
            }

            @Override
            protected void onSessionUnstalled(ISession session) {
                super.onSessionUnstalled(session);
                unstallLatch.countDown();
            }
        }, new ServerSessionListener.Adapter() {
            @Override
            public Listener onNewStream(Stream stream, HeadersFrame frame) {
                MetaData.Request request = ((MetaData.Request) (frame.getMetaData()));
                MetaData.Response response = new MetaData.Response(HttpVersion.HTTP_2, HttpStatus.OK_200, new HttpFields());
                if (request.getURIString().endsWith("/stall")) {
                    stream.headers(new HeadersFrame(stream.getId(), response, null, false), new Callback() {
                        @Override
                        public void succeeded() {
                            // Send a large chunk of data so the session gets stalled.
                            ByteBuffer data = ByteBuffer.allocate(((FlowControlStrategy.DEFAULT_WINDOW_SIZE) + 1));
                            stream.data(new DataFrame(stream.getId(), data, true), NOOP);
                        }
                    });
                } else {
                    stream.headers(new HeadersFrame(stream.getId(), response, null, true), Callback.NOOP);
                }
                return null;
            }
        });
        // Use a large stream window so that only the session gets stalled.
        client.setInitialStreamRecvWindow((5 * (FlowControlStrategy.DEFAULT_WINDOW_SIZE)));
        Session session = newClient(new Session.Listener.Adapter() {
            @Override
            public Map<Integer, Integer> onPreface(Session session) {
                Map<Integer, Integer> settings = new HashMap<>();
                settings.put(INITIAL_WINDOW_SIZE, client.getInitialStreamRecvWindow());
                return settings;
            }
        });
        CountDownLatch latch = new CountDownLatch(1);
        Queue<Callback> callbacks = new ArrayDeque<>();
        MetaData.Request request = newRequest("GET", "/stall", new HttpFields());
        session.newStream(new HeadersFrame(request, null, true), new Promise.Adapter<>(), new Stream.Listener.Adapter() {
            @Override
            public void onData(Stream stream, DataFrame frame, Callback callback) {
                callbacks.offer(callback);
                if (frame.isEndStream())
                    latch.countDown();

            }
        });
        Assertions.assertTrue(stallLatch.get().await(5, TimeUnit.SECONDS));
        // The session is now stalled, check that writing a second stream
        // does not result in the session be notified again of being stalled.
        stallLatch.set(new CountDownLatch(1));
        request = newRequest("GET", "/", new HttpFields());
        session.newStream(new HeadersFrame(request, null, true), new Promise.Adapter<>(), new Stream.Listener.Adapter());
        Assertions.assertFalse(stallLatch.get().await(1, TimeUnit.SECONDS));
        // Consume all data.
        while (!(latch.await(10, TimeUnit.MILLISECONDS))) {
            Callback callback = callbacks.poll();
            if (callback != null)
                callback.succeeded();

        } 
        // Make sure the unstall callback is invoked.
        Assertions.assertTrue(unstallLatch.await(5, TimeUnit.SECONDS));
    }
}

