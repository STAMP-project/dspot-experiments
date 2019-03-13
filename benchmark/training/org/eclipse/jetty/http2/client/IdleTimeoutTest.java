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


import Callback.Completable;
import Callback.NOOP;
import FlowControlStrategy.DEFAULT_WINDOW_SIZE;
import MetaData.Request;
import MetaData.Response;
import Stream.Listener;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http2.FlowControlStrategy;
import org.eclipse.jetty.http2.api.Session;
import org.eclipse.jetty.http2.api.Stream;
import org.eclipse.jetty.http2.api.server.ServerSessionListener;
import org.eclipse.jetty.http2.frames.DataFrame;
import org.eclipse.jetty.http2.frames.GoAwayFrame;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.http2.frames.ResetFrame;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.FuturePromise;
import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static InvocationType.NON_BLOCKING;


public class IdleTimeoutTest extends AbstractTest {
    private final int idleTimeout = 1000;

    @Test
    public void testServerEnforcingIdleTimeout() throws Exception {
        start(new ServerSessionListener.Adapter() {
            @Override
            public Listener onNewStream(Stream stream, HeadersFrame requestFrame) {
                stream.setIdleTimeout((10 * (idleTimeout)));
                MetaData.Response metaData = new MetaData.Response(HttpVersion.HTTP_2, 200, new HttpFields());
                HeadersFrame responseFrame = new HeadersFrame(stream.getId(), metaData, null, true);
                stream.headers(responseFrame, NOOP);
                return null;
            }
        });
        connector.setIdleTimeout(idleTimeout);
        final CountDownLatch latch = new CountDownLatch(1);
        Session session = newClient(new Session.Listener.Adapter() {
            @Override
            public void onClose(Session session, GoAwayFrame frame) {
                latch.countDown();
            }
        });
        MetaData.Request metaData = newRequest("GET", new HttpFields());
        HeadersFrame requestFrame = new HeadersFrame(metaData, null, true);
        session.newStream(requestFrame, new Promise.Adapter<Stream>() {
            @Override
            public void succeeded(Stream stream) {
                stream.setIdleTimeout((10 * (idleTimeout)));
            }
        }, new Stream.Listener.Adapter());
        Assertions.assertTrue(latch.await((5 * (idleTimeout)), TimeUnit.MILLISECONDS));
    }

    @Test
    public void testServerEnforcingIdleTimeoutWithUnrespondedStream() throws Exception {
        start(new ServerSessionListener.Adapter() {
            @Override
            public Listener onNewStream(Stream stream, HeadersFrame frame) {
                stream.setIdleTimeout((10 * (idleTimeout)));
                return null;
            }
        });
        connector.setIdleTimeout(idleTimeout);
        final CountDownLatch latch = new CountDownLatch(1);
        Session session = newClient(new Session.Listener.Adapter() {
            @Override
            public void onClose(Session session, GoAwayFrame frame) {
                latch.countDown();
            }
        });
        // The request is not replied, and the server should idle timeout.
        MetaData.Request metaData = newRequest("GET", new HttpFields());
        HeadersFrame requestFrame = new HeadersFrame(metaData, null, true);
        session.newStream(requestFrame, new Promise.Adapter<Stream>() {
            @Override
            public void succeeded(Stream stream) {
                stream.setIdleTimeout((10 * (idleTimeout)));
            }
        }, new Stream.Listener.Adapter());
        Assertions.assertTrue(latch.await((5 * (idleTimeout)), TimeUnit.MILLISECONDS));
    }

    @Test
    public void testServerNotEnforcingIdleTimeoutWithinCallback() throws Exception {
        start(new ServerSessionListener.Adapter() {
            @Override
            public Listener onNewStream(Stream stream, HeadersFrame frame) {
                stream.setIdleTimeout((10 * (idleTimeout)));
                // Stay in the callback for more than idleTimeout,
                // but not for an integer number of idle timeouts,
                // to avoid a race where the idle timeout fires
                // again before we can send the headers to the client.
                sleep(((idleTimeout) + ((idleTimeout) / 2)));
                MetaData.Response metaData = new MetaData.Response(HttpVersion.HTTP_2, 200, new HttpFields());
                HeadersFrame responseFrame = new HeadersFrame(stream.getId(), metaData, null, true);
                stream.headers(responseFrame, NOOP);
                return null;
            }
        });
        connector.setIdleTimeout(idleTimeout);
        final CountDownLatch closeLatch = new CountDownLatch(1);
        Session session = newClient(new ServerSessionListener.Adapter() {
            @Override
            public void onClose(Session session, GoAwayFrame frame) {
                closeLatch.countDown();
            }
        });
        final CountDownLatch replyLatch = new CountDownLatch(1);
        MetaData.Request metaData = newRequest("GET", new HttpFields());
        HeadersFrame requestFrame = new HeadersFrame(metaData, null, true);
        session.newStream(requestFrame, new Promise.Adapter<Stream>() {
            @Override
            public void succeeded(Stream stream) {
                stream.setIdleTimeout((10 * (idleTimeout)));
            }
        }, new Stream.Listener.Adapter() {
            @Override
            public void onHeaders(Stream stream, HeadersFrame frame) {
                replyLatch.countDown();
            }
        });
        Assertions.assertTrue(replyLatch.await((5 * (idleTimeout)), TimeUnit.MILLISECONDS));
        // Just make sure onClose() has never been called, but don't wait too much
        Assertions.assertFalse(closeLatch.await(((idleTimeout) / 2), TimeUnit.MILLISECONDS));
    }

    @Test
    public void testClientEnforcingIdleTimeout() throws Exception {
        final CountDownLatch closeLatch = new CountDownLatch(1);
        start(new ServerSessionListener.Adapter() {
            @Override
            public Listener onNewStream(Stream stream, HeadersFrame frame) {
                stream.setIdleTimeout((10 * (idleTimeout)));
                MetaData.Response metaData = new MetaData.Response(HttpVersion.HTTP_2, 200, new HttpFields());
                HeadersFrame responseFrame = new HeadersFrame(stream.getId(), metaData, null, true);
                stream.headers(responseFrame, NOOP);
                return null;
            }

            @Override
            public void onClose(Session session, GoAwayFrame frame) {
                closeLatch.countDown();
            }
        });
        client.setIdleTimeout(idleTimeout);
        Session session = newClient(new Session.Listener.Adapter());
        MetaData.Request metaData = newRequest("GET", new HttpFields());
        HeadersFrame requestFrame = new HeadersFrame(metaData, null, true);
        session.newStream(requestFrame, new Promise.Adapter<Stream>() {
            @Override
            public void succeeded(Stream stream) {
                stream.setIdleTimeout((10 * (idleTimeout)));
            }
        }, new Stream.Listener.Adapter());
        Assertions.assertTrue(closeLatch.await((5 * (idleTimeout)), TimeUnit.MILLISECONDS));
        Assertions.assertTrue(session.isClosed());
    }

    @Test
    public void testClientEnforcingIdleTimeoutWithUnrespondedStream() throws Exception {
        final CountDownLatch closeLatch = new CountDownLatch(1);
        start(new ServerSessionListener.Adapter() {
            @Override
            public Listener onNewStream(Stream stream, HeadersFrame frame) {
                stream.setIdleTimeout((10 * (idleTimeout)));
                return null;
            }

            @Override
            public void onClose(Session session, GoAwayFrame frame) {
                closeLatch.countDown();
            }
        });
        client.setIdleTimeout(idleTimeout);
        Session session = newClient(new Session.Listener.Adapter());
        MetaData.Request metaData = newRequest("GET", new HttpFields());
        HeadersFrame requestFrame = new HeadersFrame(metaData, null, true);
        session.newStream(requestFrame, new Promise.Adapter<Stream>() {
            @Override
            public void succeeded(Stream stream) {
                stream.setIdleTimeout((10 * (idleTimeout)));
            }
        }, new Stream.Listener.Adapter());
        Assertions.assertTrue(closeLatch.await((5 * (idleTimeout)), TimeUnit.MILLISECONDS));
    }

    @Test
    public void testClientNotEnforcingIdleTimeoutWithinCallback() throws Exception {
        final CountDownLatch closeLatch = new CountDownLatch(1);
        start(new ServerSessionListener.Adapter() {
            @Override
            public Listener onNewStream(Stream stream, HeadersFrame frame) {
                stream.setIdleTimeout((10 * (idleTimeout)));
                MetaData.Response metaData = new MetaData.Response(HttpVersion.HTTP_2, 200, new HttpFields());
                HeadersFrame responseFrame = new HeadersFrame(stream.getId(), metaData, null, true);
                stream.headers(responseFrame, NOOP);
                return null;
            }

            @Override
            public void onClose(Session session, GoAwayFrame frame) {
                closeLatch.countDown();
            }
        });
        client.setIdleTimeout(idleTimeout);
        Session session = newClient(new Session.Listener.Adapter());
        final CountDownLatch replyLatch = new CountDownLatch(1);
        MetaData.Request metaData = newRequest("GET", new HttpFields());
        HeadersFrame requestFrame = new HeadersFrame(metaData, null, true);
        session.newStream(requestFrame, new Promise.Adapter<Stream>() {
            @Override
            public void succeeded(Stream stream) {
                stream.setIdleTimeout((10 * (idleTimeout)));
            }
        }, new Stream.Listener.Adapter() {
            @Override
            public void onHeaders(Stream stream, HeadersFrame frame) {
                // Stay in the callback for more than idleTimeout,
                // but not for an integer number of idle timeouts,
                // to avoid that the idle timeout fires again.
                sleep(((idleTimeout) + ((idleTimeout) / 2)));
                replyLatch.countDown();
            }
        });
        Assertions.assertFalse(closeLatch.await((2 * (idleTimeout)), TimeUnit.MILLISECONDS));
        Assertions.assertTrue(replyLatch.await((5 * (idleTimeout)), TimeUnit.MILLISECONDS));
    }

    @Test
    public void testClientEnforcingStreamIdleTimeout() throws Exception {
        final int idleTimeout = 1000;
        start(new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                sleep((2 * idleTimeout));
            }
        });
        Session session = newClient(new Session.Listener.Adapter());
        final CountDownLatch dataLatch = new CountDownLatch(1);
        final CountDownLatch timeoutLatch = new CountDownLatch(1);
        MetaData.Request metaData = newRequest("GET", new HttpFields());
        HeadersFrame requestFrame = new HeadersFrame(metaData, null, true);
        session.newStream(requestFrame, new Promise.Adapter<Stream>() {
            @Override
            public void succeeded(Stream stream) {
                stream.setIdleTimeout(idleTimeout);
            }
        }, new Stream.Listener.Adapter() {
            @Override
            public void onData(Stream stream, DataFrame frame, Callback callback) {
                callback.succeeded();
                dataLatch.countDown();
            }

            @Override
            public boolean onIdleTimeout(Stream stream, Throwable x) {
                MatcherAssert.assertThat(x, Matchers.instanceOf(TimeoutException.class));
                timeoutLatch.countDown();
                return true;
            }
        });
        Assertions.assertTrue(timeoutLatch.await(5, TimeUnit.SECONDS));
        // We must not receive any DATA frame.
        Assertions.assertFalse(dataLatch.await((2 * idleTimeout), TimeUnit.MILLISECONDS));
        // Stream must be gone.
        Assertions.assertTrue(session.getStreams().isEmpty());
        // Session must not be closed, nor disconnected.
        Assertions.assertFalse(session.isClosed());
        Assertions.assertFalse(isDisconnected());
    }

    @Test
    public void testServerEnforcingStreamIdleTimeout() throws Exception {
        final CountDownLatch timeoutLatch = new CountDownLatch(1);
        start(new ServerSessionListener.Adapter() {
            @Override
            public Listener onNewStream(Stream stream, HeadersFrame frame) {
                stream.setIdleTimeout(idleTimeout);
                return new Stream.Listener.Adapter() {
                    @Override
                    public boolean onIdleTimeout(Stream stream, Throwable x) {
                        timeoutLatch.countDown();
                        return true;
                    }
                };
            }
        });
        final CountDownLatch resetLatch = new CountDownLatch(1);
        Session session = newClient(new Session.Listener.Adapter());
        MetaData.Request metaData = newRequest("GET", new HttpFields());
        // Stream does not end here, but we won't send any DATA frame.
        HeadersFrame requestFrame = new HeadersFrame(metaData, null, false);
        session.newStream(requestFrame, new Promise.Adapter<>(), new Stream.Listener.Adapter() {
            @Override
            public void onReset(Stream stream, ResetFrame frame) {
                resetLatch.countDown();
            }
        });
        Assertions.assertTrue(timeoutLatch.await(5, TimeUnit.SECONDS));
        Assertions.assertTrue(resetLatch.await(5, TimeUnit.SECONDS));
        // Stream must be gone.
        Assertions.assertTrue(session.getStreams().isEmpty());
        // Session must not be closed, nor disconnected.
        Assertions.assertFalse(session.isClosed());
        Assertions.assertFalse(isDisconnected());
    }

    @Test
    public void testServerStreamIdleTimeoutIsNotEnforcedWhenReceiving() throws Exception {
        final CountDownLatch timeoutLatch = new CountDownLatch(1);
        start(new ServerSessionListener.Adapter() {
            @Override
            public Listener onNewStream(Stream stream, HeadersFrame frame) {
                stream.setIdleTimeout(idleTimeout);
                return new Stream.Listener.Adapter() {
                    @Override
                    public boolean onIdleTimeout(Stream stream, Throwable x) {
                        timeoutLatch.countDown();
                        return true;
                    }
                };
            }
        });
        Session session = newClient(new Session.Listener.Adapter());
        MetaData.Request metaData = newRequest("GET", new HttpFields());
        HeadersFrame requestFrame = new HeadersFrame(metaData, null, false);
        FuturePromise<Stream> promise = new FuturePromise();
        session.newStream(requestFrame, promise, new Stream.Listener.Adapter());
        final Stream stream = promise.get(5, TimeUnit.SECONDS);
        sleep(((idleTimeout) / 2));
        final CountDownLatch dataLatch = new CountDownLatch(1);
        stream.data(new DataFrame(stream.getId(), ByteBuffer.allocate(1), false), new Callback() {
            private int sends;

            @Override
            public void succeeded() {
                sleep(((idleTimeout) / 2));
                final boolean last = (++(sends)) == 2;
                stream.data(new DataFrame(stream.getId(), ByteBuffer.allocate(1), last), (!last ? this : new Callback() {
                    @Override
                    public InvocationType getInvocationType() {
                        return NON_BLOCKING;
                    }

                    @Override
                    public void succeeded() {
                        // Idle timeout should not fire while the server is receiving.
                        Assertions.assertEquals(1, timeoutLatch.getCount());
                        dataLatch.countDown();
                    }
                }));
            }
        });
        Assertions.assertTrue(dataLatch.await((5 * (idleTimeout)), TimeUnit.MILLISECONDS));
        // The server did not send a response, so it will eventually timeout.
        Assertions.assertTrue(timeoutLatch.await((5 * (idleTimeout)), TimeUnit.SECONDS));
    }

    @Test
    public void testClientStreamIdleTimeoutIsNotEnforcedWhenSending() throws Exception {
        final CountDownLatch resetLatch = new CountDownLatch(1);
        start(new ServerSessionListener.Adapter() {
            @Override
            public Listener onNewStream(Stream stream, HeadersFrame frame) {
                MetaData.Response response = new MetaData.Response(HttpVersion.HTTP_2, 200, new HttpFields());
                stream.headers(new HeadersFrame(stream.getId(), response, null, true), NOOP);
                return null;
            }

            @Override
            public void onReset(Session session, ResetFrame frame) {
                resetLatch.countDown();
            }
        });
        Session session = newClient(new Session.Listener.Adapter());
        MetaData.Request metaData = newRequest("GET", new HttpFields());
        HeadersFrame requestFrame = new HeadersFrame(metaData, null, false);
        FuturePromise<Stream> promise = new FuturePromise<Stream>() {
            @Override
            public void succeeded(Stream stream) {
                stream.setIdleTimeout(idleTimeout);
                super.succeeded(stream);
            }
        };
        session.newStream(requestFrame, promise, new Stream.Listener.Adapter());
        final Stream stream = promise.get(5, TimeUnit.SECONDS);
        Callback.Completable completable1 = new Callback.Completable();
        sleep(((idleTimeout) / 2));
        stream.data(new DataFrame(stream.getId(), ByteBuffer.allocate(1), false), completable1);
        completable1.thenCompose(( nil) -> {
            Callback.Completable completable2 = new Callback.Completable();
            sleep(((idleTimeout) / 2));
            stream.data(new DataFrame(stream.getId(), ByteBuffer.allocate(1), false), completable2);
            return completable2;
        }).thenRun(() -> {
            sleep(((idleTimeout) / 2));
            stream.data(new DataFrame(stream.getId(), ByteBuffer.allocate(1), true), Callback.NOOP);
        });
        Assertions.assertFalse(resetLatch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testBufferedReadsResetStreamIdleTimeout() throws Exception {
        int bufferSize = 8192;
        long delay = 1000;
        start(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                ServletInputStream input = request.getInputStream();
                byte[] buffer = new byte[bufferSize];
                while (true) {
                    int read = input.read(buffer);
                    Log.getLogger(IdleTimeoutTest.class).info("Read {} bytes", read);
                    if (read < 0)
                        break;

                    sleep(delay);
                } 
            }
        });
        connector.setIdleTimeout((2 * delay));
        Session session = newClient(new Session.Listener.Adapter());
        MetaData.Request metaData = newRequest("POST", new HttpFields());
        HeadersFrame requestFrame = new HeadersFrame(metaData, null, false);
        FuturePromise<Stream> promise = new FuturePromise();
        CountDownLatch latch = new CountDownLatch(1);
        session.newStream(requestFrame, promise, new Stream.Listener.Adapter() {
            @Override
            public void onHeaders(Stream stream, HeadersFrame frame) {
                if (frame.isEndStream())
                    latch.countDown();

            }
        });
        Stream stream = promise.get(5, TimeUnit.SECONDS);
        // Send data larger than the flow control window.
        // The client will send bytes up to the flow control window immediately
        // and they will be buffered by the server; the Servlet will consume them slowly.
        // Servlet reads should reset the idle timeout.
        int contentLength = (FlowControlStrategy.DEFAULT_WINDOW_SIZE) + 1;
        ByteBuffer data = ByteBuffer.allocate(contentLength);
        stream.data(new DataFrame(stream.getId(), data, true), NOOP);
        Assertions.assertTrue(latch.await(((2 * ((contentLength / bufferSize) + 1)) * delay), TimeUnit.MILLISECONDS));
    }

    @Test
    public void testServerIdleTimeoutIsEnforcedForQueuedRequest() throws Exception {
        long idleTimeout = 2000;
        // Use a small thread pool to cause request queueing.
        QueuedThreadPool serverExecutor = new QueuedThreadPool(5);
        serverExecutor.setName("server");
        server = new org.eclipse.jetty.server.Server(serverExecutor);
        HTTP2ServerConnectionFactory h2 = new HTTP2ServerConnectionFactory(new HttpConfiguration());
        h2.setInitialSessionRecvWindow(DEFAULT_WINDOW_SIZE);
        h2.setInitialStreamRecvWindow(DEFAULT_WINDOW_SIZE);
        h2.setStreamIdleTimeout(idleTimeout);
        connector = new org.eclipse.jetty.server.ServerConnector(server, 1, 1, h2);
        connector.setIdleTimeout((10 * idleTimeout));
        server.addConnector(connector);
        ServletContextHandler context = new ServletContextHandler(server, "/", true, false);
        AtomicReference<CountDownLatch> phaser = new AtomicReference<>();
        context.addServlet(new ServletHolder(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                phaser.get().countDown();
                // Hold the dispatched requests enough for the idle requests to idle timeout.
                sleep((2 * idleTimeout));
            }
        }), ((servletPath) + "/*"));
        server.start();
        prepareClient();
        client.start();
        Session client = newClient(new Session.Listener.Adapter());
        // Send requests until one is queued on the server but not dispatched.
        while (true) {
            phaser.set(new CountDownLatch(1));
            MetaData.Request request = newRequest("GET", new HttpFields());
            HeadersFrame frame = new HeadersFrame(request, null, false);
            FuturePromise<Stream> promise = new FuturePromise();
            client.newStream(frame, promise, new Stream.Listener.Adapter());
            Stream stream = promise.get(5, TimeUnit.SECONDS);
            ByteBuffer data = ByteBuffer.allocate(10);
            stream.data(new DataFrame(stream.getId(), data, true), NOOP);
            if (!(phaser.get().await(1, TimeUnit.SECONDS)))
                break;

        } 
        // Send one more request to consume the whole session flow control window.
        CountDownLatch resetLatch = new CountDownLatch(1);
        MetaData.Request request = newRequest("GET", new HttpFields());
        HeadersFrame frame = new HeadersFrame(request, null, false);
        FuturePromise<Stream> promise = new FuturePromise();
        client.newStream(frame, promise, new Stream.Listener.Adapter() {
            @Override
            public void onReset(Stream stream, ResetFrame frame) {
                resetLatch.countDown();
            }
        });
        Stream stream = promise.get(5, TimeUnit.SECONDS);
        ByteBuffer data = ByteBuffer.allocate(updateSendWindow(0));
        stream.data(new DataFrame(stream.getId(), data, true), NOOP);
        Assertions.assertTrue(resetLatch.await((2 * idleTimeout), TimeUnit.MILLISECONDS));
        // Wait for WINDOW_UPDATEs to be processed by the client.
        sleep(1000);
        MatcherAssert.assertThat(updateSendWindow(0), Matchers.greaterThan(0));
    }
}

