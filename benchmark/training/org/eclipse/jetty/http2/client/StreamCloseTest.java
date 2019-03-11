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
import ErrorCode.REFUSED_STREAM_ERROR;
import MetaData.Request;
import MetaData.Response;
import Stream.Listener;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http2.api.Session;
import org.eclipse.jetty.http2.api.Stream;
import org.eclipse.jetty.http2.api.server.ServerSessionListener;
import org.eclipse.jetty.http2.frames.DataFrame;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.http2.frames.PushPromiseFrame;
import org.eclipse.jetty.http2.frames.ResetFrame;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.FuturePromise;
import org.eclipse.jetty.util.Promise;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class StreamCloseTest extends AbstractTest {
    @Test
    public void testRequestClosedRemotelyClosesStream() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        start(new ServerSessionListener.Adapter() {
            @Override
            public Listener onNewStream(Stream stream, HeadersFrame frame) {
                Assertions.assertTrue(isRemotelyClosed());
                latch.countDown();
                return null;
            }
        });
        Session session = newClient(new Session.Listener.Adapter());
        HeadersFrame frame = new HeadersFrame(newRequest("GET", new HttpFields()), null, true);
        FuturePromise<Stream> promise = new FuturePromise();
        session.newStream(frame, promise, null);
        Stream stream = promise.get(5, TimeUnit.SECONDS);
        Assertions.assertTrue(isLocallyClosed());
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testRequestClosedResponseClosedClosesStream() throws Exception {
        final CountDownLatch latch = new CountDownLatch(2);
        start(new ServerSessionListener.Adapter() {
            @Override
            public Listener onNewStream(final Stream stream, HeadersFrame frame) {
                MetaData.Response metaData = new MetaData.Response(HttpVersion.HTTP_2, 200, new HttpFields());
                HeadersFrame response = new HeadersFrame(stream.getId(), metaData, null, true);
                stream.headers(response, new Callback() {
                    @Override
                    public void succeeded() {
                        Assertions.assertTrue(stream.isClosed());
                        Assertions.assertEquals(0, stream.getSession().getStreams().size());
                        latch.countDown();
                    }
                });
                return null;
            }
        });
        Session session = newClient(new Session.Listener.Adapter());
        HeadersFrame frame = new HeadersFrame(newRequest("GET", new HttpFields()), null, true);
        FuturePromise<Stream> promise = new FuturePromise();
        session.newStream(frame, promise, new Stream.Listener.Adapter() {
            @Override
            public void onHeaders(Stream stream, HeadersFrame frame) {
                // The stream promise may not be notified yet here.
                latch.countDown();
            }
        });
        Stream stream = promise.get(5, TimeUnit.SECONDS);
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        Assertions.assertTrue(stream.isClosed());
    }

    @Test
    public void testRequestDataClosedResponseDataClosedClosesStream() throws Exception {
        final CountDownLatch serverDataLatch = new CountDownLatch(1);
        start(new ServerSessionListener.Adapter() {
            @Override
            public Listener onNewStream(Stream stream, HeadersFrame frame) {
                MetaData.Response metaData = new MetaData.Response(HttpVersion.HTTP_2, 200, new HttpFields());
                HeadersFrame response = new HeadersFrame(stream.getId(), metaData, null, false);
                Callback.Completable completable = new Callback.Completable();
                stream.headers(response, completable);
                return new Stream.Listener.Adapter() {
                    @Override
                    public void onData(final Stream stream, DataFrame frame, final Callback callback) {
                        Assertions.assertTrue(isRemotelyClosed());
                        completable.thenRun(() -> stream.data(frame, new Callback() {
                            @Override
                            public void succeeded() {
                                assertTrue(stream.isClosed());
                                assertEquals(0, stream.getSession().getStreams().size());
                                callback.succeeded();
                                serverDataLatch.countDown();
                            }
                        }));
                    }
                };
            }
        });
        final CountDownLatch completeLatch = new CountDownLatch(1);
        Session session = newClient(new Session.Listener.Adapter());
        HeadersFrame frame = new HeadersFrame(newRequest("GET", new HttpFields()), null, false);
        FuturePromise<Stream> promise = new FuturePromise();
        session.newStream(frame, promise, new Stream.Listener.Adapter() {
            @Override
            public void onData(Stream stream, DataFrame frame, Callback callback) {
                // The sent data callback may not be notified yet here.
                callback.succeeded();
                completeLatch.countDown();
            }
        });
        final Stream stream = promise.get(5, TimeUnit.SECONDS);
        Assertions.assertFalse(stream.isClosed());
        Assertions.assertFalse(isLocallyClosed());
        final CountDownLatch clientDataLatch = new CountDownLatch(1);
        stream.data(new DataFrame(stream.getId(), ByteBuffer.wrap(new byte[512]), true), new Callback() {
            @Override
            public void succeeded() {
                // Here the stream may be just locally closed or fully closed.
                clientDataLatch.countDown();
            }
        });
        Assertions.assertTrue(clientDataLatch.await(5, TimeUnit.SECONDS));
        Assertions.assertTrue(serverDataLatch.await(5, TimeUnit.SECONDS));
        Assertions.assertTrue(completeLatch.await(5, TimeUnit.SECONDS));
        Assertions.assertTrue(stream.isClosed());
        Assertions.assertEquals(0, stream.getSession().getStreams().size());
    }

    @Test
    public void testPushedStreamIsClosed() throws Exception {
        final CountDownLatch serverLatch = new CountDownLatch(1);
        start(new ServerSessionListener.Adapter() {
            @Override
            public Listener onNewStream(Stream stream, HeadersFrame frame) {
                PushPromiseFrame pushFrame = new PushPromiseFrame(stream.getId(), 0, newRequest("GET", new HttpFields()));
                stream.push(pushFrame, new Promise.Adapter<Stream>() {
                    @Override
                    public void succeeded(final Stream pushedStream) {
                        // When created, pushed stream must be implicitly remotely closed.
                        assertTrue(isRemotelyClosed());
                        // Send some data with endStream = true.
                        pushedStream.data(new DataFrame(pushedStream.getId(), ByteBuffer.allocate(16), true), new Callback() {
                            @Override
                            public void succeeded() {
                                assertTrue(pushedStream.isClosed());
                                serverLatch.countDown();
                            }
                        });
                    }
                }, new Stream.Listener.Adapter());
                HeadersFrame response = new HeadersFrame(stream.getId(), new MetaData.Response(HttpVersion.HTTP_2, 200, new HttpFields()), null, true);
                stream.headers(response, NOOP);
                return null;
            }
        });
        Session session = newClient(new Session.Listener.Adapter());
        HeadersFrame frame = new HeadersFrame(newRequest("GET", new HttpFields()), null, true);
        final CountDownLatch clientLatch = new CountDownLatch(1);
        session.newStream(frame, new Promise.Adapter<>(), new Stream.Listener.Adapter() {
            @Override
            public Listener onPush(Stream pushedStream, PushPromiseFrame frame) {
                Assertions.assertTrue(isLocallyClosed());
                return new Adapter() {
                    @Override
                    public void onData(Stream pushedStream, DataFrame frame, Callback callback) {
                        Assertions.assertTrue(pushedStream.isClosed());
                        callback.succeeded();
                        clientLatch.countDown();
                    }
                };
            }
        });
        Assertions.assertTrue(serverLatch.await(5, TimeUnit.SECONDS));
        Assertions.assertTrue(clientLatch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testPushedStreamResetIsClosed() throws Exception {
        final CountDownLatch serverLatch = new CountDownLatch(1);
        start(new ServerSessionListener.Adapter() {
            @Override
            public Listener onNewStream(final Stream stream, HeadersFrame frame) {
                PushPromiseFrame pushFrame = new PushPromiseFrame(stream.getId(), 0, newRequest("GET", new HttpFields()));
                stream.push(pushFrame, new Promise.Adapter<>(), new Stream.Listener.Adapter() {
                    @Override
                    public void onReset(Stream pushedStream, ResetFrame frame) {
                        Assertions.assertTrue(pushedStream.isReset());
                        Assertions.assertTrue(pushedStream.isClosed());
                        HeadersFrame response = new HeadersFrame(stream.getId(), new MetaData.Response(HttpVersion.HTTP_2, 200, new HttpFields()), null, true);
                        stream.headers(response, NOOP);
                        serverLatch.countDown();
                    }
                });
                return null;
            }
        });
        Session session = newClient(new Session.Listener.Adapter());
        HeadersFrame frame = new HeadersFrame(newRequest("GET", new HttpFields()), null, true);
        final CountDownLatch clientLatch = new CountDownLatch(2);
        session.newStream(frame, new Promise.Adapter<>(), new Stream.Listener.Adapter() {
            @Override
            public Listener onPush(final Stream pushedStream, PushPromiseFrame frame) {
                pushedStream.reset(new ResetFrame(pushedStream.getId(), REFUSED_STREAM_ERROR.code), new Callback() {
                    @Override
                    public void succeeded() {
                        Assertions.assertTrue(pushedStream.isReset());
                        Assertions.assertTrue(pushedStream.isClosed());
                        clientLatch.countDown();
                    }
                });
                return null;
            }

            @Override
            public void onHeaders(Stream stream, HeadersFrame frame) {
                clientLatch.countDown();
            }
        });
        Assertions.assertTrue(serverLatch.await(5, TimeUnit.SECONDS));
        Assertions.assertTrue(clientLatch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testFailedSessionClosesIdleStream() throws Exception {
        AtomicReference<Session> sessionRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);
        final List<Stream> streams = new ArrayList<>();
        start(new ServerSessionListener.Adapter() {
            @Override
            public Listener onNewStream(Stream stream, HeadersFrame frame) {
                streams.add(stream);
                MetaData.Request request = ((MetaData.Request) (frame.getMetaData()));
                if ("GET".equals(request.getMethod())) {
                    getEndPoint().close();
                    // Try to write something to force an error.
                    stream.data(new DataFrame(stream.getId(), ByteBuffer.allocate(1024), true), NOOP);
                }
                return null;
            }

            @Override
            public void onFailure(Session session, Throwable failure) {
                sessionRef.set(session);
                latch.countDown();
            }
        });
        Session session = newClient(new Session.Listener.Adapter());
        // First stream will be idle on server.
        HeadersFrame request1 = new HeadersFrame(newRequest("HEAD", new HttpFields()), null, true);
        session.newStream(request1, new Promise.Adapter<>(), new Stream.Listener.Adapter());
        // Second stream will fail on server.
        HeadersFrame request2 = new HeadersFrame(newRequest("GET", new HttpFields()), null, true);
        session.newStream(request2, new Promise.Adapter<>(), new Stream.Listener.Adapter());
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        Session serverSession = sessionRef.get();
        // Wait for the server to finish the close activities.
        Thread.sleep(1000);
        Assertions.assertEquals(0, serverSession.getStreams().size());
        for (Stream stream : streams)
            Assertions.assertTrue(stream.isClosed());

    }
}

