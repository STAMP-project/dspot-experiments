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
package org.eclipse.jetty.websocket.jsr356.server;


import Timeouts.POLL_EVENT;
import Timeouts.POLL_EVENT_UNIT;
import java.net.URI;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.MappedByteBufferPool;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDir;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDirExtension;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.websocket.jsr356.server.samples.echo.BasicEchoSocket;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


/**
 * Testing the use of an alternate {@link org.eclipse.jetty.websocket.server.WebSocketUpgradeFilter}
 * defined in the WEB-INF/web.xml
 */
@ExtendWith(WorkDirExtension.class)
public class AltFilterTest {
    public WorkDir testdir;

    public ByteBufferPool bufferPool = new MappedByteBufferPool();

    @Test
    public void testEcho() throws Exception {
        WSServer wsb = new WSServer(testdir, "app");
        wsb.copyWebInf("alt-filter-web.xml");
        // the endpoint (extends javax.websocket.Endpoint)
        wsb.copyClass(BasicEchoSocket.class);
        try {
            wsb.start();
            URI uri = wsb.getServerBaseURI();
            WebAppContext webapp = wsb.createWebAppContext();
            wsb.deployWebapp(webapp);
            FilterHolder filterWebXml = webapp.getServletHandler().getFilter("wsuf-test");
            MatcherAssert.assertThat("Filter[wsuf-test]", filterWebXml, CoreMatchers.notNullValue());
            FilterHolder filterSCI = webapp.getServletHandler().getFilter("Jetty_WebSocketUpgradeFilter");
            MatcherAssert.assertThat("Filter[Jetty_WebSocketUpgradeFilter]", filterSCI, CoreMatchers.nullValue());
            WebSocketClient client = new WebSocketClient(bufferPool);
            try {
                client.start();
                JettyEchoSocket clientEcho = new JettyEchoSocket();
                Future<Session> future = client.connect(clientEcho, uri.resolve("echo;jsession=xyz"));
                // wait for connect
                future.get(1, TimeUnit.SECONDS);
                clientEcho.sendMessage("Hello Echo");
                LinkedBlockingQueue<String> msgs = clientEcho.incomingMessages;
                Assertions.assertEquals("Hello Echo", msgs.poll(POLL_EVENT, POLL_EVENT_UNIT), "Expected message");
            } finally {
                client.stop();
            }
        } finally {
            wsb.stop();
        }
    }
}

