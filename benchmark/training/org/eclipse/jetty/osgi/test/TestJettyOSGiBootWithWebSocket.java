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
package org.eclipse.jetty.osgi.test;


import java.net.URI;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.osgi.framework.BundleContext;


/**
 *
 */
@RunWith(PaxExam.class)
public class TestJettyOSGiBootWithWebSocket {
    private static final String LOG_LEVEL = "WARN";

    @Inject
    BundleContext bundleContext = null;

    @Test
    public void testWebsocket() throws Exception {
        if (Boolean.getBoolean(TestOSGiUtil.BUNDLE_DEBUG))
            assertAllBundlesActiveOrResolved();

        String port = System.getProperty("boot.websocket.port");
        Assert.assertNotNull(port);
        URI uri = new URI((("ws://127.0.0.1:" + port) + "/ws/foo"));
        WebSocketClient client = new WebSocketClient();
        try {
            SimpleEchoSocket socket = new SimpleEchoSocket();
            client.start();
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            request.setSubProtocols("chat");
            client.connect(socket, uri, request);
            // wait for closed socket connection.
            Assert.assertTrue(socket.awaitClose(5, TimeUnit.SECONDS));
        } finally {
            client.stop();
        }
    }
}

