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
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDir;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDirExtension;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


/**
 * Test Echo of Large messages, targeting the {@code @OnMessage(maxMessage=###)} functionality
 */
@ExtendWith(WorkDirExtension.class)
public class LargeAnnotatedTest {
    public WorkDir testdir;

    private WSServer server;

    @SuppressWarnings("Duplicates")
    @Test
    public void testEcho() throws Exception {
        URI uri = server.getServerBaseURI();
        WebAppContext webapp = server.createWebAppContext();
        server.deployWebapp(webapp);
        // wsb.dump();
        WebSocketClient client = new WebSocketClient();
        try {
            client.getPolicy().setMaxTextMessageSize((128 * 1024));
            client.start();
            JettyEchoSocket clientEcho = new JettyEchoSocket();
            Future<Session> foo = client.connect(clientEcho, uri.resolve("echo/large"));
            // wait for connect
            foo.get(1, TimeUnit.SECONDS);
            // The message size should be bigger than default, but smaller than the limit that LargeEchoSocket specifies
            byte[] txt = new byte[100 * 1024];
            Arrays.fill(txt, ((byte) ('o')));
            String msg = new String(txt, StandardCharsets.UTF_8);
            clientEcho.sendMessage(msg);
            LinkedBlockingQueue<String> msgs = clientEcho.incomingMessages;
            Assertions.assertEquals(msg, msgs.poll(POLL_EVENT, POLL_EVENT_UNIT), "Expected message");
        } finally {
            client.stop();
        }
    }
}

