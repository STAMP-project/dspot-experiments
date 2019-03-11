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


import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class MemoryUsageTest {
    private Server server;

    private ServerConnector connector;

    private WebSocketContainer client;

    @SuppressWarnings("unused")
    @Test
    public void testMemoryUsage() throws Exception {
        int sessionCount = 1000;
        Session[] sessions = new Session[sessionCount];
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        System.gc();
        MemoryUsage heapBefore = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeapBefore = memoryMXBean.getNonHeapMemoryUsage();
        URI uri = URI.create(("ws://localhost:" + (connector.getLocalPort())));
        final CountDownLatch latch = new CountDownLatch(sessionCount);
        for (int i = 0; i < sessionCount; ++i) {
            sessions[i] = client.connectToServer(new MemoryUsageTest.EndpointAdapter() {
                @Override
                public void onMessage(String message) {
                    latch.countDown();
                }
            }, uri);
        }
        for (int i = 0; i < sessionCount; ++i) {
            sessions[i].getBasicRemote().sendText("OK");
        }
        latch.await((5 * sessionCount), TimeUnit.MILLISECONDS);
        System.gc();
        MemoryUsage heapAfter = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeapAfter = memoryMXBean.getNonHeapMemoryUsage();
        long heapUsed = (heapAfter.getUsed()) - (heapBefore.getUsed());
        long nonHeapUsed = (nonHeapAfter.getUsed()) - (nonHeapBefore.getUsed());
        System.out.println(("heapUsed = " + heapUsed));
        System.out.println(("nonHeapUsed = " + nonHeapUsed));
        // new CountDownLatch(1).await();
        // Assume no more than 25 KiB per session pair (client and server).
        long expected = (25 * 1024) * sessionCount;
        MatcherAssert.assertThat("heap used", heapUsed, Matchers.lessThan(expected));
    }

    private abstract static class EndpointAdapter extends Endpoint implements MessageHandler.Whole<String> {
        @Override
        public void onOpen(Session session, EndpointConfig config) {
            session.addMessageHandler(this);
        }
    }
}

