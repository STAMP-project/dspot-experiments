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
import MetaData.Request;
import MetaData.Response;
import ServerSessionListener.Adapter;
import Stream.Listener;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http2.api.Session;
import org.eclipse.jetty.http2.api.Stream;
import org.eclipse.jetty.http2.api.server.ServerSessionListener;
import org.eclipse.jetty.http2.frames.DataFrame;
import org.eclipse.jetty.http2.frames.Frame;
import org.eclipse.jetty.http2.frames.GoAwayFrame;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.http2.frames.PushPromiseFrame;
import org.eclipse.jetty.http2.frames.ResetFrame;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.FuturePromise;
import org.eclipse.jetty.util.IteratingCallback;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static Action.IDLE;
import static Action.SCHEDULED;
import static Session.Listener.Adapter.<init>;


public class RawHTTP2ProxyTest {
    private static final Logger LOGGER = Log.getLogger(RawHTTP2ProxyTest.class);

    private final List<Server> servers = new ArrayList<>();

    private final List<HTTP2Client> clients = new ArrayList<>();

    @Test
    public void testRawHTTP2Proxy() throws Exception {
        byte[] data1 = new byte[1024];
        new Random().nextBytes(data1);
        ByteBuffer buffer1 = ByteBuffer.wrap(data1);
        Server server1 = startServer("server1", new ServerSessionListener.Adapter() {
            @Override
            public Listener onNewStream(Stream stream, HeadersFrame frame) {
                if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                    RawHTTP2ProxyTest.LOGGER.debug("SERVER1 received {}", frame);

                return new Stream.Listener.Adapter() {
                    @Override
                    public void onHeaders(Stream stream, HeadersFrame frame) {
                        if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                            RawHTTP2ProxyTest.LOGGER.debug("SERVER1 received {}", frame);

                        if (frame.isEndStream()) {
                            MetaData.Response response = new MetaData.Response(HttpVersion.HTTP_2, HttpStatus.OK_200, new HttpFields());
                            HeadersFrame reply = new HeadersFrame(stream.getId(), response, null, false);
                            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                                RawHTTP2ProxyTest.LOGGER.debug("SERVER1 sending {}", reply);

                            stream.headers(reply, new Callback() {
                                @Override
                                public void succeeded() {
                                    DataFrame data = new DataFrame(stream.getId(), buffer1.slice(), true);
                                    if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                                        RawHTTP2ProxyTest.LOGGER.debug("SERVER1 sending {}", data);

                                    stream.data(data, NOOP);
                                }
                            });
                        }
                    }
                };
            }
        });
        ServerConnector connector1 = ((ServerConnector) (server1.getAttribute("connector")));
        Server server2 = startServer("server2", new ServerSessionListener.Adapter() {
            @Override
            public Listener onNewStream(Stream stream, HeadersFrame frame) {
                if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                    RawHTTP2ProxyTest.LOGGER.debug("SERVER2 received {}", frame);

                return new Stream.Listener.Adapter() {
                    @Override
                    public void onData(Stream stream, DataFrame frame, Callback callback) {
                        if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                            RawHTTP2ProxyTest.LOGGER.debug("SERVER2 received {}", frame);

                        callback.succeeded();
                        MetaData.Response response = new MetaData.Response(HttpVersion.HTTP_2, HttpStatus.OK_200, new HttpFields());
                        Callback.Completable completable1 = new Callback.Completable();
                        HeadersFrame reply = new HeadersFrame(stream.getId(), response, null, false);
                        if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                            RawHTTP2ProxyTest.LOGGER.debug("SERVER2 sending {}", reply);

                        stream.headers(reply, completable1);
                        completable1.thenCompose(( ignored) -> {
                            Callback.Completable completable2 = new Callback.Completable();
                            DataFrame data = new DataFrame(stream.getId(), buffer1.slice(), false);
                            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                                RawHTTP2ProxyTest.LOGGER.debug("SERVER2 sending {}", data);

                            stream.data(data, completable2);
                            return completable2;
                        }).thenRun(() -> {
                            MetaData trailer = new MetaData(HttpVersion.HTTP_2, new HttpFields());
                            HeadersFrame end = new HeadersFrame(stream.getId(), trailer, null, true);
                            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                                RawHTTP2ProxyTest.LOGGER.debug("SERVER2 sending {}", end);

                            stream.headers(end, Callback.NOOP);
                        });
                    }
                };
            }
        });
        ServerConnector connector2 = ((ServerConnector) (server2.getAttribute("connector")));
        HTTP2Client proxyClient = startClient("proxyClient");
        Server proxyServer = startServer("proxyServer", new RawHTTP2ProxyTest.ClientToProxySessionListener(proxyClient));
        ServerConnector proxyConnector = ((ServerConnector) (proxyServer.getAttribute("connector")));
        InetSocketAddress proxyAddress = new InetSocketAddress("localhost", proxyConnector.getLocalPort());
        HTTP2Client client = startClient("client");
        FuturePromise<Session> clientPromise = new FuturePromise();
        client.connect(proxyAddress, new Session.Listener.Adapter(), clientPromise);
        Session clientSession = clientPromise.get(5, TimeUnit.SECONDS);
        // Send a request with trailers for server1.
        HttpFields fields1 = new HttpFields();
        fields1.put("X-Target", String.valueOf(connector1.getLocalPort()));
        MetaData.Request request1 = new MetaData.Request("GET", new HttpURI("http://localhost/server1"), HttpVersion.HTTP_2, fields1);
        FuturePromise<Stream> streamPromise1 = new FuturePromise();
        CountDownLatch latch1 = new CountDownLatch(1);
        clientSession.newStream(new HeadersFrame(request1, null, false), streamPromise1, new Stream.Listener.Adapter() {
            @Override
            public void onHeaders(Stream stream, HeadersFrame frame) {
                if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                    RawHTTP2ProxyTest.LOGGER.debug("CLIENT received {}", frame);

            }

            @Override
            public void onData(Stream stream, DataFrame frame, Callback callback) {
                if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                    RawHTTP2ProxyTest.LOGGER.debug("CLIENT received {}", frame);

                Assertions.assertEquals(buffer1.slice(), frame.getData());
                callback.succeeded();
                latch1.countDown();
            }
        });
        Stream stream1 = streamPromise1.get(5, TimeUnit.SECONDS);
        stream1.headers(new HeadersFrame(stream1.getId(), new MetaData(HttpVersion.HTTP_2, new HttpFields()), null, true), Callback.NOOP);
        // Send a request for server2.
        HttpFields fields2 = new HttpFields();
        fields2.put("X-Target", String.valueOf(connector2.getLocalPort()));
        MetaData.Request request2 = new MetaData.Request("GET", new HttpURI("http://localhost/server1"), HttpVersion.HTTP_2, fields2);
        FuturePromise<Stream> streamPromise2 = new FuturePromise();
        CountDownLatch latch2 = new CountDownLatch(1);
        clientSession.newStream(new HeadersFrame(request2, null, false), streamPromise2, new Stream.Listener.Adapter() {
            @Override
            public void onHeaders(Stream stream, HeadersFrame frame) {
                if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                    RawHTTP2ProxyTest.LOGGER.debug("CLIENT received {}", frame);

                if (frame.isEndStream())
                    latch2.countDown();

            }

            @Override
            public void onData(Stream stream, DataFrame frame, Callback callback) {
                if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                    RawHTTP2ProxyTest.LOGGER.debug("CLIENT received {}", frame);

                callback.succeeded();
            }
        });
        Stream stream2 = streamPromise2.get(5, TimeUnit.SECONDS);
        stream2.data(new DataFrame(stream2.getId(), buffer1.slice(), true), Callback.NOOP);
        Assertions.assertTrue(latch1.await(5, TimeUnit.SECONDS));
        Assertions.assertTrue(latch2.await(5, TimeUnit.SECONDS));
    }

    private static class ClientToProxySessionListener extends ServerSessionListener.Adapter {
        private final Map<Integer, RawHTTP2ProxyTest.ClientToProxyToServer> forwarders = new ConcurrentHashMap<>();

        private final HTTP2Client client;

        private ClientToProxySessionListener(HTTP2Client client) {
            this.client = client;
        }

        @Override
        public Listener onNewStream(Stream stream, HeadersFrame frame) {
            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                RawHTTP2ProxyTest.LOGGER.debug("Received {} for {} on {}: {}", frame, stream, stream.getSession(), frame.getMetaData());

            // Forward to the right server.
            MetaData metaData = frame.getMetaData();
            HttpFields fields = metaData.getFields();
            int port = Integer.parseInt(fields.get("X-Target"));
            RawHTTP2ProxyTest.ClientToProxyToServer clientToProxyToServer = forwarders.computeIfAbsent(port, ( p) -> new RawHTTP2ProxyTest.ClientToProxyToServer("localhost", p, client));
            clientToProxyToServer.offer(stream, frame, Callback.NOOP);
            return clientToProxyToServer;
        }

        @Override
        public void onClose(Session session, GoAwayFrame frame) {
            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                RawHTTP2ProxyTest.LOGGER.debug("Received {} on {}", frame, session);

            // TODO
        }

        @Override
        public boolean onIdleTimeout(Session session) {
            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                RawHTTP2ProxyTest.LOGGER.debug("Idle timeout on {}", session);

            // TODO
            return true;
        }

        @Override
        public void onFailure(Session session, Throwable failure) {
            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                RawHTTP2ProxyTest.LOGGER.debug(("Failure on " + session), failure);

            // TODO
        }
    }

    private static class ClientToProxyToServer extends IteratingCallback implements Stream.Listener {
        private final Object lock = this;

        private final Map<Stream, Deque<RawHTTP2ProxyTest.FrameInfo>> frames = new HashMap<>();

        private final Map<Stream, Stream> streams = new HashMap<>();

        private final RawHTTP2ProxyTest.ServerToProxyToClient serverToProxyToClient = new RawHTTP2ProxyTest.ServerToProxyToClient();

        private final String host;

        private final int port;

        private final HTTP2Client client;

        private Session proxyToServerSession;

        private RawHTTP2ProxyTest.FrameInfo frameInfo;

        private Stream clientToProxyStream;

        private ClientToProxyToServer(String host, int port, HTTP2Client client) {
            this.host = host;
            this.port = port;
            this.client = client;
        }

        private void offer(Stream stream, Frame frame, Callback callback) {
            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                RawHTTP2ProxyTest.LOGGER.debug("CPS queueing {} for {} on {}", frame, stream, stream.getSession());

            boolean connected;
            synchronized(lock) {
                Deque<RawHTTP2ProxyTest.FrameInfo> deque = frames.computeIfAbsent(stream, ( s) -> new ArrayDeque<>());
                deque.offer(new RawHTTP2ProxyTest.FrameInfo(frame, callback));
                connected = (proxyToServerSession) != null;
            }
            if (connected)
                iterate();
            else
                connect();

        }

        private void connect() {
            InetSocketAddress address = new InetSocketAddress(host, port);
            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                RawHTTP2ProxyTest.LOGGER.debug("CPS connecting to {}", address);

            client.connect(address, new RawHTTP2ProxyTest.ServerToProxySessionListener(), new org.eclipse.jetty.util.Promise<Session>() {
                @Override
                public void succeeded(Session result) {
                    if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                        RawHTTP2ProxyTest.LOGGER.debug("CPS connected to {} with {}", address, result);

                    synchronized(lock) {
                        proxyToServerSession = result;
                    }
                    iterate();
                }

                @Override
                public void failed(Throwable x) {
                    if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                        RawHTTP2ProxyTest.LOGGER.debug("CPS connect failed to {}", address);

                    // TODO: drain the queue and fail the streams.
                }
            });
        }

        @Override
        protected Action process() throws Throwable {
            Stream proxyToServerStream = null;
            Session proxyToServerSession = null;
            synchronized(lock) {
                for (Map.Entry<Stream, Deque<RawHTTP2ProxyTest.FrameInfo>> entry : frames.entrySet()) {
                    frameInfo = entry.getValue().poll();
                    if ((frameInfo) != null) {
                        clientToProxyStream = entry.getKey();
                        proxyToServerStream = streams.get(clientToProxyStream);
                        proxyToServerSession = this.proxyToServerSession;
                        break;
                    }
                }
            }
            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                RawHTTP2ProxyTest.LOGGER.debug("CPS processing {} for {} to {}", frameInfo, clientToProxyStream, proxyToServerStream);

            if ((frameInfo) == null)
                return IDLE;

            if (proxyToServerStream == null) {
                HeadersFrame clientToProxyFrame = ((HeadersFrame) (frameInfo.frame));
                HeadersFrame proxyToServerFrame = new HeadersFrame(clientToProxyFrame.getMetaData(), clientToProxyFrame.getPriority(), clientToProxyFrame.isEndStream());
                proxyToServerSession.newStream(proxyToServerFrame, new org.eclipse.jetty.util.Promise<Stream>() {
                    @Override
                    public void succeeded(Stream result) {
                        synchronized(lock) {
                            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                                RawHTTP2ProxyTest.LOGGER.debug("CPS created {}", result);

                            streams.put(clientToProxyStream, result);
                        }
                        serverToProxyToClient.link(result, clientToProxyStream);
                        RawHTTP2ProxyTest.ClientToProxyToServer.this.succeeded();
                    }

                    @Override
                    public void failed(Throwable failure) {
                        if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                            RawHTTP2ProxyTest.LOGGER.debug("CPS create failed", failure);

                        // TODO: cannot open stream to server.
                        RawHTTP2ProxyTest.ClientToProxyToServer.this.failed(failure);
                    }
                }, serverToProxyToClient);
                return SCHEDULED;
            } else {
                if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                    RawHTTP2ProxyTest.LOGGER.debug("CPS forwarding {} from {} to {}", frameInfo, clientToProxyStream, proxyToServerStream);

                switch (frameInfo.frame.getType()) {
                    case HEADERS :
                        {
                            HeadersFrame clientToProxyFrame = ((HeadersFrame) (frameInfo.frame));
                            HeadersFrame proxyToServerFrame = new HeadersFrame(proxyToServerStream.getId(), clientToProxyFrame.getMetaData(), clientToProxyFrame.getPriority(), clientToProxyFrame.isEndStream());
                            proxyToServerStream.headers(proxyToServerFrame, this);
                            return SCHEDULED;
                        }
                    case DATA :
                        {
                            DataFrame clientToProxyFrame = ((DataFrame) (frameInfo.frame));
                            DataFrame proxyToServerFrame = new DataFrame(proxyToServerStream.getId(), clientToProxyFrame.getData(), clientToProxyFrame.isEndStream());
                            proxyToServerStream.data(proxyToServerFrame, this);
                            return SCHEDULED;
                        }
                    default :
                        {
                            throw new IllegalStateException();
                        }
                }
            }
        }

        @Override
        public void succeeded() {
            frameInfo.callback.succeeded();
            super.succeeded();
        }

        @Override
        public void failed(Throwable failure) {
            frameInfo.callback.failed(failure);
            super.failed(failure);
        }

        @Override
        public void onHeaders(Stream stream, HeadersFrame frame) {
            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                RawHTTP2ProxyTest.LOGGER.debug("CPS received {} on {}", frame, stream);

            offer(stream, frame, NOOP);
        }

        @Override
        public Listener onPush(Stream stream, PushPromiseFrame frame) {
            // Clients don't push.
            return null;
        }

        @Override
        public void onData(Stream stream, DataFrame frame, Callback callback) {
            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                RawHTTP2ProxyTest.LOGGER.debug("CPS received {} on {}", frame, stream);

            offer(stream, frame, callback);
        }

        @Override
        public void onReset(Stream stream, ResetFrame frame) {
            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                RawHTTP2ProxyTest.LOGGER.debug("CPS received {} on {}", frame, stream);

            // TODO: drain the queue for that stream, and notify server.
        }

        @Override
        public boolean onIdleTimeout(Stream stream, Throwable x) {
            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                RawHTTP2ProxyTest.LOGGER.debug("CPS idle timeout for {}", stream);

            // TODO: drain the queue for that stream, reset stream, and notify server.
            return true;
        }
    }

    private static class ServerToProxySessionListener extends Session.Listener.Adapter {
        @Override
        public void onClose(Session session, GoAwayFrame frame) {
            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                RawHTTP2ProxyTest.LOGGER.debug("Received {} on {}", frame, session);

            // TODO
        }

        @Override
        public boolean onIdleTimeout(Session session) {
            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                RawHTTP2ProxyTest.LOGGER.debug("Idle timeout on {}", session);

            // TODO
            return true;
        }

        @Override
        public void onFailure(Session session, Throwable failure) {
            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                RawHTTP2ProxyTest.LOGGER.debug(("Failure on " + session), failure);

            // TODO
        }
    }

    private static class ServerToProxyToClient extends IteratingCallback implements Stream.Listener {
        private final Object lock = this;

        private final Map<Stream, Deque<RawHTTP2ProxyTest.FrameInfo>> frames = new HashMap<>();

        private final Map<Stream, Stream> streams = new HashMap<>();

        private RawHTTP2ProxyTest.FrameInfo frameInfo;

        private Stream serverToProxyStream;

        @Override
        protected Action process() throws Throwable {
            Stream proxyToClientStream = null;
            synchronized(lock) {
                for (Map.Entry<Stream, Deque<RawHTTP2ProxyTest.FrameInfo>> entry : frames.entrySet()) {
                    frameInfo = entry.getValue().poll();
                    if ((frameInfo) != null) {
                        serverToProxyStream = entry.getKey();
                        proxyToClientStream = streams.get(serverToProxyStream);
                        break;
                    }
                }
            }
            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                RawHTTP2ProxyTest.LOGGER.debug("SPC processing {} for {} to {}", frameInfo, serverToProxyStream, proxyToClientStream);

            // It may happen that we received a frame from the server,
            // but the proxyToClientStream is not linked yet.
            if (proxyToClientStream == null)
                return Action.IDLE;

            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                RawHTTP2ProxyTest.LOGGER.debug("SPC forwarding {} for {} to {}", frameInfo, serverToProxyStream, proxyToClientStream);

            switch (frameInfo.frame.getType()) {
                case HEADERS :
                    {
                        HeadersFrame serverToProxyFrame = ((HeadersFrame) (frameInfo.frame));
                        HeadersFrame proxyToClientFrame = new HeadersFrame(proxyToClientStream.getId(), serverToProxyFrame.getMetaData(), serverToProxyFrame.getPriority(), serverToProxyFrame.isEndStream());
                        proxyToClientStream.headers(proxyToClientFrame, this);
                        return Action.SCHEDULED;
                    }
                case DATA :
                    {
                        DataFrame clientToProxyFrame = ((DataFrame) (frameInfo.frame));
                        DataFrame proxyToServerFrame = new DataFrame(serverToProxyStream.getId(), clientToProxyFrame.getData(), clientToProxyFrame.isEndStream());
                        proxyToClientStream.data(proxyToServerFrame, this);
                        return Action.SCHEDULED;
                    }
                case PUSH_PROMISE :
                    {
                        // TODO
                        throw new UnsupportedOperationException();
                    }
                default :
                    {
                        throw new IllegalStateException();
                    }
            }
        }

        @Override
        public void succeeded() {
            frameInfo.callback.succeeded();
            super.succeeded();
        }

        @Override
        public void failed(Throwable failure) {
            frameInfo.callback.failed(failure);
            super.failed(failure);
        }

        private void offer(Stream stream, Frame frame, Callback callback) {
            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                RawHTTP2ProxyTest.LOGGER.debug("SPC queueing {} for {} on {}", frame, stream, stream.getSession());

            synchronized(lock) {
                Deque<RawHTTP2ProxyTest.FrameInfo> deque = frames.computeIfAbsent(stream, ( s) -> new ArrayDeque<>());
                deque.offer(new RawHTTP2ProxyTest.FrameInfo(frame, callback));
            }
            iterate();
        }

        @Override
        public void onHeaders(Stream stream, HeadersFrame frame) {
            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                RawHTTP2ProxyTest.LOGGER.debug("SPC received {} on {}", frame, stream);

            offer(stream, frame, NOOP);
        }

        @Override
        public Listener onPush(Stream stream, PushPromiseFrame frame) {
            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                RawHTTP2ProxyTest.LOGGER.debug("SPC received {} on {}", frame, stream);

            // TODO
            return null;
        }

        @Override
        public void onData(Stream stream, DataFrame frame, Callback callback) {
            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                RawHTTP2ProxyTest.LOGGER.debug("SPC received {} on {}", frame, stream);

            offer(stream, frame, callback);
        }

        @Override
        public void onReset(Stream stream, ResetFrame frame) {
            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                RawHTTP2ProxyTest.LOGGER.debug("SPC received {} on {}", frame, stream);

            // TODO: drain queue, reset client stream.
        }

        @Override
        public boolean onIdleTimeout(Stream stream, Throwable x) {
            if (RawHTTP2ProxyTest.LOGGER.isDebugEnabled())
                RawHTTP2ProxyTest.LOGGER.debug("SPC idle timeout for {}", stream);

            // TODO:
            return false;
        }

        private void link(Stream proxyToServerStream, Stream clientToProxyStream) {
            synchronized(lock) {
                streams.put(proxyToServerStream, clientToProxyStream);
            }
            iterate();
        }
    }

    private static class FrameInfo {
        private final Frame frame;

        private final Callback callback;

        private FrameInfo(Frame frame, Callback callback) {
            this.frame = frame;
            this.callback = callback;
        }

        @Override
        public String toString() {
            return frame.toString();
        }
    }
}

