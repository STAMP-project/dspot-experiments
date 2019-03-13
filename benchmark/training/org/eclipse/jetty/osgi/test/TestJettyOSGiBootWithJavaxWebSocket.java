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


import Constants.BUNDLE_SYMBOLICNAME;
import Constants.FRAGMENT_HOST;
import Constants.REQUIRE_CAPABILITY;
import RemoteEndpoint.Basic;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.websocket.ContainerProvider;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.tinybundles.core.TinyBundle;
import org.ops4j.pax.tinybundles.core.TinyBundles;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;


/**
 * Test using websocket in osgi
 */
@RunWith(PaxExam.class)
public class TestJettyOSGiBootWithJavaxWebSocket {
    private static final String LOG_LEVEL = "WARN";

    @Inject
    BundleContext bundleContext = null;

    @Test
    public void testWebsocket() throws Exception {
        if (Boolean.getBoolean(TestOSGiUtil.BUNDLE_DEBUG))
            assertAllBundlesActiveOrResolved();

        // this is necessary because the javax.websocket-api jar does not have manifest headers
        // that allow it to use ServiceLoader in osgi, this corrects that defect
        TinyBundle bundle = TinyBundles.bundle();
        bundle.set(FRAGMENT_HOST, "javax.websocket-api");
        bundle.set(REQUIRE_CAPABILITY, "osgi.serviceloader;filter:=\"(osgi.serviceloader=javax.websocket.ContainerProvider)\";resolution:=optional;cardinality:=multiple, osgi.extender; filter:=\"(osgi.extender=osgi.serviceloader.processor)\"");
        bundle.set(BUNDLE_SYMBOLICNAME, "javax.websocket.api.fragment");
        InputStream is = bundle.build(TinyBundles.withBnd());
        bundleContext.installBundle("dummyLocation", is);
        Bundle websocketApiBundle = TestOSGiUtil.getBundle(bundleContext, "javax.websocket-api");
        Assert.assertNotNull(websocketApiBundle);
        websocketApiBundle.update();
        websocketApiBundle.start();
        Bundle javaxWebsocketClient = TestOSGiUtil.getBundle(bundleContext, "org.eclipse.jetty.websocket.javax.websocket");
        Assert.assertNotNull(javaxWebsocketClient);
        javaxWebsocketClient.start();
        Bundle javaxWebsocketServer = TestOSGiUtil.getBundle(bundleContext, "org.eclipse.jetty.websocket.javax.websocket.server");
        Assert.assertNotNull(javaxWebsocketServer);
        javaxWebsocketServer.start();
        String port = System.getProperty("boot.javax.websocket.port");
        Assert.assertNotNull(port);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        Assert.assertNotNull(container);
        SimpleJavaxWebSocket socket = new SimpleJavaxWebSocket();
        URI uri = new URI((("ws://127.0.0.1:" + port) + "/javax.websocket/"));
        Session session = container.connectToServer(socket, uri);
        try {
            RemoteEndpoint.Basic remote = session.getBasicRemote();
            String msg = "Foo";
            remote.sendText(msg);
            Assert.assertTrue(socket.messageLatch.await(1, TimeUnit.SECONDS));// give remote 1 second to respond

        } finally {
            session.close();
            Assert.assertTrue(socket.closeLatch.await(1, TimeUnit.SECONDS));// give remote 1 second to acknowledge response

        }
    }
}

