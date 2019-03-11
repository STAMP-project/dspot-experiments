/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.core.net;


import ch.qos.logback.core.net.SocketConnector.ExceptionHandler;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link DefaultSocketConnector}.
 *
 * @author Carl Harris
 */
public class DefaultSocketConnectorTest {
    private static final int DELAY = 1000;

    private static final int SHORT_DELAY = 10;

    private static final int RETRY_DELAY = 10;

    private DefaultSocketConnectorTest.MockExceptionHandler exceptionHandler = new DefaultSocketConnectorTest.MockExceptionHandler();

    private ServerSocket serverSocket;

    private DefaultSocketConnector connector;

    ExecutorService executor = Executors.newSingleThreadExecutor();

    @Test
    public void testConnect() throws Exception {
        Future<Socket> connectorTask = executor.submit(connector);
        Socket socket = connectorTask.get((2 * (DefaultSocketConnectorTest.DELAY)), TimeUnit.MILLISECONDS);
        Assert.assertNotNull(socket);
        connectorTask.cancel(true);
        Assert.assertTrue(connectorTask.isDone());
        socket.close();
    }

    @Test
    public void testConnectionFails() throws Exception {
        serverSocket.close();
        Future<Socket> connectorTask = executor.submit(connector);
        // this connection attempt will always timeout
        try {
            connectorTask.get(DefaultSocketConnectorTest.SHORT_DELAY, TimeUnit.MILLISECONDS);
            Assert.fail();
        } catch (TimeoutException e) {
        }
        Exception lastException = exceptionHandler.awaitConnectionFailed(DefaultSocketConnectorTest.DELAY);
        Assert.assertTrue((lastException instanceof ConnectException));
        Assert.assertFalse(connectorTask.isDone());
        connectorTask.cancel(true);
        // thread.join(4 * DELAY);
        Assert.assertTrue(connectorTask.isCancelled());
    }

    @Test(timeout = 5000)
    public void testConnectEventually() throws Exception {
        serverSocket.close();
        Future<Socket> connectorTask = executor.submit(connector);
        // this connection attempt will always timeout
        try {
            connectorTask.get(DefaultSocketConnectorTest.SHORT_DELAY, TimeUnit.MILLISECONDS);
            Assert.fail();
        } catch (TimeoutException e) {
        }
        // on Ceki's machine (Windows 7) this always takes 1second regardless of the value of DELAY
        Exception lastException = exceptionHandler.awaitConnectionFailed(DefaultSocketConnectorTest.DELAY);
        Assert.assertNotNull(lastException);
        Assert.assertTrue((lastException instanceof ConnectException));
        // now rebind to the same local address
        SocketAddress address = serverSocket.getLocalSocketAddress();
        serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);
        serverSocket.bind(address);
        // now we should be able to connect
        Socket socket = connectorTask.get((2 * (DefaultSocketConnectorTest.DELAY)), TimeUnit.MILLISECONDS);
        Assert.assertNotNull(socket);
        Assert.assertFalse(connectorTask.isCancelled());
        socket.close();
    }

    private static class MockExceptionHandler implements ExceptionHandler {
        private final Lock lock = new ReentrantLock();

        private final Condition failedCondition = lock.newCondition();

        private Exception lastException;

        public void connectionFailed(SocketConnector connector, Exception ex) {
            lastException = ex;
        }

        public Exception awaitConnectionFailed(long delay) throws InterruptedException {
            lock.lock();
            try {
                long increment = 10;
                while (((lastException) == null) && (delay > 0)) {
                    boolean success = failedCondition.await(increment, TimeUnit.MILLISECONDS);
                    delay -= increment;
                    if (success)
                        break;

                } 
                return lastException;
            } finally {
                lock.unlock();
            }
        }
    }
}

