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
package org.eclipse.jetty.websocket.jsr356;


import java.net.URI;
import java.util.concurrent.TimeUnit;
import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Test;


public class AnnotatedEchoTest {
    private static Server server;

    private static EchoHandler handler;

    private static URI serverUri;

    @Test
    public void testEcho() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        AnnotatedEchoTest.server.addBean(container);// allow to shutdown with server

        AnnotatedEchoClient echoer = new AnnotatedEchoClient();
        Session session = container.connectToServer(echoer, AnnotatedEchoTest.serverUri);
        session.getBasicRemote().sendText("Echo");
        echoer.messageQueue.awaitMessages(1, 1000, TimeUnit.MILLISECONDS);
    }
}

