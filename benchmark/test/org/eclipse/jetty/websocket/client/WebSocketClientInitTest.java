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
package org.eclipse.jetty.websocket.client;


import org.eclipse.jetty.client.HttpClient;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Test various init techniques for WebSocketClient
 */
public class WebSocketClientInitTest {
    /**
     * This is the new Jetty 9.4 advanced usage mode of WebSocketClient,
     * that allows for more robust HTTP configurations (such as authentication,
     * cookies, and proxies)
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testInit_HttpClient_StartedOutside() throws Exception {
        HttpClient http = new HttpClient();
        http.start();
        try {
            WebSocketClient ws = new WebSocketClient(http);
            ws.start();
            try {
                MatcherAssert.assertThat("HttpClient", ws.getHttpClient(), Matchers.is(http));
                MatcherAssert.assertThat("WebSocketClient started", ws.isStarted(), Matchers.is(true));
                MatcherAssert.assertThat("HttpClient started", http.isStarted(), Matchers.is(true));
                HttpClient httpBean = ws.getBean(HttpClient.class);
                MatcherAssert.assertThat("HttpClient should not be found in WebSocketClient", httpBean, Matchers.nullValue());
                MatcherAssert.assertThat("HttpClient bean is managed", ws.isManaged(httpBean), Matchers.is(false));
                MatcherAssert.assertThat("WebSocketClient should not be found in HttpClient", http.getBean(WebSocketClient.class), Matchers.nullValue());
            } finally {
                ws.stop();
            }
            MatcherAssert.assertThat("WebSocketClient stopped", ws.isStopped(), Matchers.is(true));
            MatcherAssert.assertThat("HttpClient stopped", http.isStopped(), Matchers.is(false));
        } finally {
            http.stop();
        }
        MatcherAssert.assertThat("HttpClient stopped", http.isStopped(), Matchers.is(true));
    }

    /**
     * This is the backward compatibility mode of WebSocketClient.
     * This is also the primary mode that JSR356 Standalone WebSocket Client is initialized.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testInit_HttpClient_SyntheticStart() throws Exception {
        HttpClient http = null;
        WebSocketClient ws = new WebSocketClient();
        ws.start();
        try {
            http = ws.getHttpClient();
            MatcherAssert.assertThat("WebSocketClient started", ws.isStarted(), Matchers.is(true));
            MatcherAssert.assertThat("HttpClient started", http.isStarted(), Matchers.is(true));
            HttpClient httpBean = ws.getBean(HttpClient.class);
            MatcherAssert.assertThat("HttpClient bean found in WebSocketClient", httpBean, Matchers.is(http));
            MatcherAssert.assertThat("HttpClient bean is managed", ws.isManaged(httpBean), Matchers.is(true));
            MatcherAssert.assertThat("WebSocketClient should not be found in HttpClient", http.getBean(WebSocketClient.class), Matchers.nullValue());
        } finally {
            ws.stop();
        }
        MatcherAssert.assertThat("WebSocketClient stopped", ws.isStopped(), Matchers.is(true));
        MatcherAssert.assertThat("HttpClient stopped", http.isStopped(), Matchers.is(true));
    }
}

