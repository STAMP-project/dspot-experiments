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


import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import org.eclipse.jetty.util.thread.ShutdownThread;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ShutdownMonitorTest {
    @Test
    public void testStatus() throws Exception {
        ShutdownMonitor monitor = ShutdownMonitor.getInstance();
        // monitor.setDebug(true);
        monitor.setPort(0);
        monitor.setExitVm(false);
        monitor.start();
        String key = monitor.getKey();
        int port = monitor.getPort();
        // Try more than once to be sure that the ServerSocket has not been closed.
        for (int i = 0; i < 2; ++i) {
            try (Socket socket = new Socket("localhost", port)) {
                OutputStream output = socket.getOutputStream();
                String command = "status";
                output.write((((key + "\r\n") + command) + "\r\n").getBytes());
                output.flush();
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String reply = input.readLine();
                Assertions.assertEquals("OK", reply);
                // Socket must be closed afterwards.
                Assertions.assertNull(input.readLine());
            }
        }
    }

    @Test
    public void testForceStopCommand() throws Exception {
        ShutdownMonitor monitor = ShutdownMonitor.getInstance();
        // monitor.setDebug(true);
        monitor.setPort(0);
        monitor.setExitVm(false);
        monitor.start();
        try (ShutdownMonitorTest.CloseableServer server = new ShutdownMonitorTest.CloseableServer()) {
            start();
            // shouldn't be registered for shutdown on jvm
            Assertions.assertTrue((!(ShutdownThread.isRegistered(server))));
            Assertions.assertTrue(ShutdownMonitor.isRegistered(server));
            String key = monitor.getKey();
            int port = monitor.getPort();
            stop("forcestop", port, key, true);
            monitor.await();
            Assertions.assertTrue((!(monitor.isAlive())));
            Assertions.assertTrue(server.stopped);
            Assertions.assertTrue((!(server.destroyed)));
            Assertions.assertTrue((!(ShutdownThread.isRegistered(server))));
            Assertions.assertTrue((!(ShutdownMonitor.isRegistered(server))));
        }
    }

    @Test
    public void testOldStopCommandWithStopOnShutdownTrue() throws Exception {
        ShutdownMonitor monitor = ShutdownMonitor.getInstance();
        // monitor.setDebug(true);
        monitor.setPort(0);
        monitor.setExitVm(false);
        monitor.start();
        try (ShutdownMonitorTest.CloseableServer server = new ShutdownMonitorTest.CloseableServer()) {
            setStopAtShutdown(true);
            start();
            // should be registered for shutdown on exit
            Assertions.assertTrue(ShutdownThread.isRegistered(server));
            Assertions.assertTrue(ShutdownMonitor.isRegistered(server));
            String key = monitor.getKey();
            int port = monitor.getPort();
            stop("stop", port, key, true);
            monitor.await();
            Assertions.assertTrue((!(monitor.isAlive())));
            Assertions.assertTrue(server.stopped);
            Assertions.assertTrue((!(server.destroyed)));
            Assertions.assertTrue((!(ShutdownThread.isRegistered(server))));
            Assertions.assertTrue((!(ShutdownMonitor.isRegistered(server))));
        }
    }

    @Test
    public void testOldStopCommandWithStopOnShutdownFalse() throws Exception {
        ShutdownMonitor monitor = ShutdownMonitor.getInstance();
        // monitor.setDebug(true);
        monitor.setPort(0);
        monitor.setExitVm(false);
        monitor.start();
        try (ShutdownMonitorTest.CloseableServer server = new ShutdownMonitorTest.CloseableServer()) {
            setStopAtShutdown(false);
            start();
            Assertions.assertTrue((!(ShutdownThread.isRegistered(server))));
            Assertions.assertTrue(ShutdownMonitor.isRegistered(server));
            String key = monitor.getKey();
            int port = monitor.getPort();
            stop("stop", port, key, true);
            monitor.await();
            Assertions.assertTrue((!(monitor.isAlive())));
            Assertions.assertTrue((!(server.stopped)));
            Assertions.assertTrue((!(server.destroyed)));
            Assertions.assertTrue((!(ShutdownThread.isRegistered(server))));
            Assertions.assertTrue(ShutdownMonitor.isRegistered(server));
        }
    }

    public class CloseableServer extends Server implements Closeable {
        boolean destroyed = false;

        boolean stopped = false;

        @Override
        protected void doStop() throws Exception {
            stopped = true;
            super.doStop();
        }

        @Override
        public void destroy() {
            destroyed = true;
            super.destroy();
        }

        @Override
        protected void doStart() throws Exception {
            stopped = false;
            destroyed = false;
            super.doStart();
        }

        @Override
        public void close() {
            try {
                stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

