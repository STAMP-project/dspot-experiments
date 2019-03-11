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
package org.eclipse.jetty.websocket.common.events;


import examples.AdapterConnectCloseSocket;
import examples.ListenerBasicSocket;
import org.eclipse.jetty.websocket.api.InvalidWebSocketException;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.common.annotations.NotASocket;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class EventDriverFactoryTest {
    /**
     * Test Case for no exceptions and 5 methods (extends WebSocketAdapter)
     */
    @Test
    public void testAdapterConnectCloseSocket() {
        EventDriverFactory factory = new EventDriverFactory(new org.eclipse.jetty.websocket.common.scopes.SimpleContainerScope(WebSocketPolicy.newClientPolicy()));
        AdapterConnectCloseSocket socket = new AdapterConnectCloseSocket();
        EventDriver driver = factory.wrap(socket);
        String classId = AdapterConnectCloseSocket.class.getSimpleName();
        MatcherAssert.assertThat(("EventDriver for " + classId), driver, Matchers.instanceOf(JettyListenerEventDriver.class));
    }

    /**
     * Test Case for bad declaration (duplicate OnWebSocketBinary declarations)
     */
    @Test
    public void testBadNotASocket() {
        EventDriverFactory factory = new EventDriverFactory(new org.eclipse.jetty.websocket.common.scopes.SimpleContainerScope(WebSocketPolicy.newClientPolicy()));
        try {
            NotASocket bad = new NotASocket();
            // Should toss exception
            factory.wrap(bad);
        } catch (InvalidWebSocketException e) {
            // Validate that we have clear error message to the developer
            MatcherAssert.assertThat(e.getMessage(), Matchers.allOf(Matchers.containsString(WebSocketListener.class.getSimpleName()), Matchers.containsString(WebSocket.class.getSimpleName())));
        }
    }

    /**
     * Test Case for no exceptions and 5 methods (implement WebSocketListener)
     */
    @Test
    public void testListenerBasicSocket() {
        EventDriverFactory factory = new EventDriverFactory(new org.eclipse.jetty.websocket.common.scopes.SimpleContainerScope(WebSocketPolicy.newClientPolicy()));
        ListenerBasicSocket socket = new ListenerBasicSocket();
        EventDriver driver = factory.wrap(socket);
        String classId = ListenerBasicSocket.class.getSimpleName();
        MatcherAssert.assertThat(("EventDriver for " + classId), driver, Matchers.instanceOf(JettyListenerEventDriver.class));
    }
}

