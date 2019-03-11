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


import HttpMethod.GET;
import MetaData.Request;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.http.HostPortHttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http2.ISession;
import org.eclipse.jetty.http2.IStream;
import org.eclipse.jetty.http2.api.Session;
import org.eclipse.jetty.http2.api.Stream;
import org.eclipse.jetty.http2.api.server.ServerSessionListener;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.FuturePromise;
import org.eclipse.jetty.util.Promise;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FlowControlWindowsTest {
    private Server server;

    private ServerConnector connector;

    private HTTP2Client client;

    private int serverSessionRecvWindow = (3 * 1024) * 1024;

    private int serverStreamRecvWindow = (2 * 1024) * 1024;

    private int clientSessionRecvWindow = (5 * 1024) * 1024;

    private int clientStreamRecvWindow = (4 * 1024) * 1024;

    @Test
    public void testClientFlowControlWindows() throws Exception {
        start(new ServerSessionListener.Adapter());
        ISession clientSession = newClient(new Session.Listener.Adapter());
        // Wait while client and server exchange SETTINGS and WINDOW_UPDATE frames.
        Thread.sleep(1000);
        int sessionSendWindow = clientSession.updateSendWindow(0);
        Assertions.assertEquals(serverSessionRecvWindow, sessionSendWindow);
        int sessionRecvWindow = clientSession.updateRecvWindow(0);
        Assertions.assertEquals(clientSessionRecvWindow, sessionRecvWindow);
        HostPortHttpField hostPort = new HostPortHttpField(("localhost:" + (connector.getLocalPort())));
        MetaData.Request request = new MetaData.Request(GET.asString(), HttpScheme.HTTP, hostPort, "/", HttpVersion.HTTP_2, new HttpFields());
        HeadersFrame frame = new HeadersFrame(request, null, true);
        FuturePromise<Stream> promise = new FuturePromise();
        clientSession.newStream(frame, promise, new Stream.Listener.Adapter());
        IStream clientStream = ((IStream) (promise.get(5, TimeUnit.SECONDS)));
        int streamSendWindow = clientStream.updateSendWindow(0);
        Assertions.assertEquals(serverStreamRecvWindow, streamSendWindow);
        int streamRecvWindow = clientStream.updateRecvWindow(0);
        Assertions.assertEquals(clientStreamRecvWindow, streamRecvWindow);
    }

    @Test
    public void testServerFlowControlWindows() throws Exception {
        AtomicReference<ISession> sessionRef = new AtomicReference<>();
        CountDownLatch sessionLatch = new CountDownLatch(1);
        AtomicReference<IStream> streamRef = new AtomicReference<>();
        CountDownLatch streamLatch = new CountDownLatch(1);
        start(new ServerSessionListener.Adapter() {
            @Override
            public void onAccept(Session session) {
                sessionRef.set(((ISession) (session)));
                sessionLatch.countDown();
            }

            @Override
            public Stream.Listener onNewStream(Stream stream, HeadersFrame frame) {
                streamRef.set(((IStream) (stream)));
                streamLatch.countDown();
                return null;
            }
        });
        ISession clientSession = newClient(new Session.Listener.Adapter());
        Assertions.assertTrue(sessionLatch.await(5, TimeUnit.SECONDS));
        ISession serverSession = sessionRef.get();
        // Wait while client and server exchange SETTINGS and WINDOW_UPDATE frames.
        Thread.sleep(1000);
        int sessionSendWindow = serverSession.updateSendWindow(0);
        Assertions.assertEquals(clientSessionRecvWindow, sessionSendWindow);
        int sessionRecvWindow = serverSession.updateRecvWindow(0);
        Assertions.assertEquals(serverSessionRecvWindow, sessionRecvWindow);
        HostPortHttpField hostPort = new HostPortHttpField(("localhost:" + (connector.getLocalPort())));
        MetaData.Request request = new MetaData.Request(GET.asString(), HttpScheme.HTTP, hostPort, "/", HttpVersion.HTTP_2, new HttpFields());
        HeadersFrame frame = new HeadersFrame(request, null, true);
        clientSession.newStream(frame, new Promise.Adapter<>(), new Stream.Listener.Adapter());
        Assertions.assertTrue(streamLatch.await(5, TimeUnit.SECONDS));
        IStream serverStream = streamRef.get();
        int streamSendWindow = serverStream.updateSendWindow(0);
        Assertions.assertEquals(clientStreamRecvWindow, streamSendWindow);
        int streamRecvWindow = serverStream.updateRecvWindow(0);
        Assertions.assertEquals(serverStreamRecvWindow, streamRecvWindow);
    }
}

