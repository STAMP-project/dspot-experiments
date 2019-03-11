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
package org.eclipse.jetty.server;


import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class LowResourcesMonitorTest {
    QueuedThreadPool _threadPool;

    Server _server;

    ServerConnector _connector;

    LowResourceMonitor _lowResourcesMonitor;

    @Test
    public void testLowOnThreads() throws Exception {
        _lowResourcesMonitor.setMonitorThreads(true);
        Thread.sleep(1200);
        _threadPool.setMaxThreads((((_threadPool.getThreads()) - (_threadPool.getIdleThreads())) + 10));
        Thread.sleep(1200);
        Assertions.assertFalse(_lowResourcesMonitor.isLowOnResources(), _lowResourcesMonitor.getReasons());
        final CountDownLatch latch = new CountDownLatch(1);
        for (int i = 0; i < 100; i++) {
            _threadPool.execute(() -> {
                try {
                    latch.await();
                } catch ( e) {
                    e.printStackTrace();
                }
            });
        }
        Thread.sleep(1200);
        Assertions.assertTrue(_lowResourcesMonitor.isLowOnResources());
        latch.countDown();
        Thread.sleep(1200);
        Assertions.assertFalse(_lowResourcesMonitor.isLowOnResources(), _lowResourcesMonitor.getReasons());
    }

    @Test
    public void testNotAccepting() throws Exception {
        _lowResourcesMonitor.setAcceptingInLowResources(false);
        _lowResourcesMonitor.setMonitorThreads(true);
        Thread.sleep(1200);
        int maxThreads = ((_threadPool.getThreads()) - (_threadPool.getIdleThreads())) + 10;
        System.out.println(("maxThreads:" + maxThreads));
        _threadPool.setMaxThreads(maxThreads);
        Thread.sleep(1200);
        Assertions.assertFalse(_lowResourcesMonitor.isLowOnResources(), _lowResourcesMonitor.getReasons());
        for (AbstractConnector c : _server.getBeans(AbstractConnector.class))
            MatcherAssert.assertThat(c.isAccepting(), Matchers.is(true));

        final CountDownLatch latch = new CountDownLatch(1);
        for (int i = 0; i < 100; i++) {
            _threadPool.execute(() -> {
                try {
                    latch.await();
                } catch ( e) {
                    e.printStackTrace();
                }
            });
        }
        Thread.sleep(1200);
        Assertions.assertTrue(_lowResourcesMonitor.isLowOnResources());
        for (AbstractConnector c : _server.getBeans(AbstractConnector.class))
            MatcherAssert.assertThat(c.isAccepting(), Matchers.is(false));

        latch.countDown();
        Thread.sleep(1200);
        Assertions.assertFalse(_lowResourcesMonitor.isLowOnResources(), _lowResourcesMonitor.getReasons());
        for (AbstractConnector c : _server.getBeans(AbstractConnector.class))
            MatcherAssert.assertThat(c.isAccepting(), Matchers.is(true));

    }

    @Test
    public void testMaxConnectionsAndMaxIdleTime() throws Exception {
        _lowResourcesMonitor.setMaxMemory(0);
        Assertions.assertFalse(_lowResourcesMonitor.isLowOnResources(), _lowResourcesMonitor.getReasons());
        Assertions.assertEquals(20, _lowResourcesMonitor.getMaxConnections());
        Socket[] socket = new Socket[(_lowResourcesMonitor.getMaxConnections()) + 1];
        for (int i = 0; i < (socket.length); i++)
            socket[i] = new Socket("localhost", _connector.getLocalPort());

        Thread.sleep(1200);
        Assertions.assertTrue(_lowResourcesMonitor.isLowOnResources());
        try (Socket newSocket = new Socket("localhost", _connector.getLocalPort())) {
            // wait for low idle time to close sockets, but not new Socket
            Thread.sleep(1200);
            Assertions.assertFalse(_lowResourcesMonitor.isLowOnResources(), _lowResourcesMonitor.getReasons());
            for (int i = 0; i < (socket.length); i++)
                Assertions.assertEquals((-1), socket[i].getInputStream().read());

            newSocket.getOutputStream().write("GET / HTTP/1.0\r\n\r\n".getBytes(StandardCharsets.UTF_8));
            Assertions.assertEquals('H', newSocket.getInputStream().read());
        }
    }

    @Test
    public void testMaxLowResourcesTime() throws Exception {
        int monitorPeriod = _lowResourcesMonitor.getPeriod();
        int lowResourcesIdleTimeout = _lowResourcesMonitor.getLowResourcesIdleTimeout();
        MatcherAssert.assertThat(lowResourcesIdleTimeout, Matchers.lessThan(monitorPeriod));
        int maxLowResourcesTime = 5 * monitorPeriod;
        _lowResourcesMonitor.setMaxLowResourcesTime(maxLowResourcesTime);
        Assertions.assertFalse(_lowResourcesMonitor.isLowOnResources(), _lowResourcesMonitor.getReasons());
        try (Socket socket0 = new Socket("localhost", _connector.getLocalPort())) {
            // Put the lowResourceMonitor in low mode.
            _lowResourcesMonitor.setMaxMemory(1);
            // Wait a couple of monitor periods so that
            // lowResourceMonitor detects it is in low mode.
            Thread.sleep((2 * monitorPeriod));
            Assertions.assertTrue(_lowResourcesMonitor.isLowOnResources());
            // We already waited enough for lowResourceMonitor to close socket0.
            Assertions.assertEquals((-1), socket0.getInputStream().read());
            // New connections are not affected by the
            // low mode until maxLowResourcesTime elapses.
            try (Socket socket1 = new Socket("localhost", _connector.getLocalPort())) {
                // Set a very short read timeout so we can test if the server closed.
                socket1.setSoTimeout(1);
                InputStream input1 = socket1.getInputStream();
                Assertions.assertTrue(_lowResourcesMonitor.isLowOnResources());
                Assertions.assertThrows(SocketTimeoutException.class, () -> input1.read());
                // Wait a couple of lowResources idleTimeouts.
                Thread.sleep((2 * lowResourcesIdleTimeout));
                // Verify the new socket is still open.
                Assertions.assertTrue(_lowResourcesMonitor.isLowOnResources());
                Assertions.assertThrows(SocketTimeoutException.class, () -> input1.read());
                // Let the maxLowResourcesTime elapse.
                Thread.sleep(maxLowResourcesTime);
                Assertions.assertTrue(_lowResourcesMonitor.isLowOnResources());
                // Now also the new socket should be closed.
                Assertions.assertEquals((-1), input1.read());
            }
        }
    }
}

