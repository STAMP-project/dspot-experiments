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


import java.net.URI;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.log.StacklessLogging;
import org.eclipse.jetty.websocket.jsr356.annotations.JsrEvents;
import org.junit.jupiter.api.Test;


public class IdleTimeoutTest {
    private static final Logger LOG = Log.getLogger(IdleTimeoutTest.class);

    private static WSServer server;

    @Test
    public void testAnnotated() throws Exception {
        try (StacklessLogging ignored = new StacklessLogging(JsrEvents.class)) {
            URI uri = IdleTimeoutTest.server.getServerBaseURI();
            assertConnectionTimeout(uri.resolve("idle-onopen-socket"));
        }
    }

    @Test
    public void testEndpoint() throws Exception {
        URI uri = IdleTimeoutTest.server.getServerBaseURI();
        assertConnectionTimeout(uri.resolve("idle-onopen-endpoint"));
    }
}

