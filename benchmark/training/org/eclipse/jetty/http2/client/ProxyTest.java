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


import AsyncProxyServlet.Transparent;
import HttpVersion.HTTP_1_1;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http2.api.Session;
import org.eclipse.jetty.http2.api.Stream;
import org.eclipse.jetty.http2.frames.DataFrame;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.proxy.AsyncProxyServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Promise;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ProxyTest {
    private HTTP2Client client;

    private Server proxy;

    private ServerConnector proxyConnector;

    private Server server;

    private ServerConnector serverConnector;

    @Test
    public void testServerBigDownloadSlowClient() throws Exception {
        final CountDownLatch serverLatch = new CountDownLatch(1);
        final byte[] content = new byte[1024 * 1024];
        startServer(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                response.getOutputStream().write(content);
                serverLatch.countDown();
            }
        });
        Map<String, String> params = new HashMap<>();
        params.put("proxyTo", ("http://localhost:" + (serverConnector.getLocalPort())));
        startProxy(new AsyncProxyServlet.Transparent() {
            @Override
            protected void sendProxyRequest(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Request proxyRequest) {
                proxyRequest.version(HTTP_1_1);
                super.sendProxyRequest(clientRequest, proxyResponse, proxyRequest);
            }
        }, params);
        startClient();
        final CountDownLatch clientLatch = new CountDownLatch(1);
        Session session = newClient(new Session.Listener.Adapter());
        MetaData.Request metaData = newRequest("GET", "/", new HttpFields());
        HeadersFrame frame = new HeadersFrame(metaData, null, true);
        session.newStream(frame, new Promise.Adapter<>(), new Stream.Listener.Adapter() {
            @Override
            public void onData(Stream stream, DataFrame frame, Callback callback) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                    callback.succeeded();
                    if (frame.isEndStream())
                        clientLatch.countDown();

                } catch (InterruptedException x) {
                    callback.failed(x);
                }
            }
        });
        Assertions.assertTrue(serverLatch.await(15, TimeUnit.SECONDS));
        Assertions.assertTrue(clientLatch.await(15, TimeUnit.SECONDS));
    }
}

