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
package org.eclipse.jetty.websocket.jsr356.misbehaving;


import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.StacklessLogging;
import org.eclipse.jetty.websocket.common.WebSocketSession;
import org.eclipse.jetty.websocket.jsr356.EchoHandler;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class MisbehavingClassTest {
    private static Server server;

    private static EchoHandler handler;

    private static URI serverUri;

    @SuppressWarnings("Duplicates")
    @Test
    public void testEndpointRuntimeOnOpen() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        MisbehavingClassTest.server.addBean(container);// allow to shutdown with server

        EndpointRuntimeOnOpen socket = new EndpointRuntimeOnOpen();
        try (StacklessLogging ignore = new StacklessLogging(EndpointRuntimeOnOpen.class, WebSocketSession.class)) {
            // expecting IOException during onOpen - Should have failed .connectToServer()
            IOException e = Assertions.assertThrows(IOException.class, () -> container.connectToServer(socket, MisbehavingClassTest.serverUri));
            MatcherAssert.assertThat(e.getCause(), Matchers.instanceOf(RuntimeException.class));
            MatcherAssert.assertThat("Close should have occurred", socket.closeLatch.await(10, TimeUnit.SECONDS), Matchers.is(true));
            MatcherAssert.assertThat("Error", socket.errors.pop(), Matchers.instanceOf(RuntimeException.class));
        }
    }

    @Test
    public void testAnnotatedRuntimeOnOpen() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        MisbehavingClassTest.server.addBean(container);// allow to shutdown with server

        AnnotatedRuntimeOnOpen socket = new AnnotatedRuntimeOnOpen();
        try (StacklessLogging ignore = new StacklessLogging(AnnotatedRuntimeOnOpen.class, WebSocketSession.class)) {
            // expecting IOException during onOpen - Should have failed .connectToServer()
            IOException e = Assertions.assertThrows(IOException.class, () -> container.connectToServer(socket, MisbehavingClassTest.serverUri));
            MatcherAssert.assertThat(e.getCause(), Matchers.instanceOf(RuntimeException.class));
            MatcherAssert.assertThat("Close should have occurred", socket.closeLatch.await(10, TimeUnit.SECONDS), Matchers.is(true));
            MatcherAssert.assertThat("Error", socket.errors.pop(), Matchers.instanceOf(RuntimeException.class));
        }
    }
}

