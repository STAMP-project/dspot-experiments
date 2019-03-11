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
package ch.qos.logback.classic.net;


import Level.DEBUG;
import Level.INFO;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.net.mock.MockAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEventVO;
import ch.qos.logback.core.net.SocketConnector;
import ch.qos.logback.core.status.Status;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.SocketFactory;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Unit tests for {@link SocketReceiver}.
 *
 * @author Carl Harris
 */
@Ignore
public class SocketReceiverTest {
    private static final int DELAY = 1000;

    private static final String TEST_HOST_NAME = "NOT.A.VALID.HOST.NAME";

    private ServerSocket serverSocket;

    private Socket socket;

    private SocketReceiverTest.MockSocketFactory socketFactory = new SocketReceiverTest.MockSocketFactory();

    private SocketReceiverTest.MockSocketConnector connector;

    private MockAppender appender;

    private LoggerContext lc;

    private Logger logger;

    private SocketReceiverTest.InstrumentedSocketReceiver receiver = new SocketReceiverTest.InstrumentedSocketReceiver();

    @Test
    public void testStartNoRemoteAddress() throws Exception {
        start();
        Assert.assertFalse(isStarted());
        int count = lc.getStatusManager().getCount();
        Status status = lc.getStatusManager().getCopyOfStatusList().get((count - 1));
        Assert.assertTrue(status.getMessage().contains("host"));
    }

    @Test
    public void testStartNoPort() throws Exception {
        setRemoteHost(SocketReceiverTest.TEST_HOST_NAME);
        start();
        Assert.assertFalse(isStarted());
        int count = lc.getStatusManager().getCount();
        Status status = lc.getStatusManager().getCopyOfStatusList().get((count - 1));
        Assert.assertTrue(status.getMessage().contains("port"));
    }

    @Test
    public void testStartUnknownHost() throws Exception {
        setPort(6000);
        setRemoteHost(SocketReceiverTest.TEST_HOST_NAME);
        start();
        Assert.assertFalse(isStarted());
        int count = lc.getStatusManager().getCount();
        Status status = lc.getStatusManager().getCopyOfStatusList().get((count - 1));
        Assert.assertTrue(status.getMessage().contains("unknown host"));
    }

    @Test
    public void testStartStop() throws Exception {
        setRemoteHost(InetAddress.getLocalHost().getHostName());
        setPort(6000);
        setAcceptConnectionTimeout(((SocketReceiverTest.DELAY) / 2));
        start();
        Assert.assertTrue(isStarted());
        receiver.awaitConnectorCreated(SocketReceiverTest.DELAY);
        stop();
        Assert.assertFalse(isStarted());
    }

    @Test
    public void testServerSlowToAcceptConnection() throws Exception {
        setRemoteHost(InetAddress.getLocalHost().getHostName());
        setPort(6000);
        setAcceptConnectionTimeout(((SocketReceiverTest.DELAY) / 4));
        start();
        Assert.assertTrue(receiver.awaitConnectorCreated(((SocketReceiverTest.DELAY) / 2)));
        // note that we don't call serverSocket.accept() here
        // but processPriorToRemoval (in tearDown) should still clean up everything
    }

    @Test
    public void testServerDropsConnection() throws Exception {
        setRemoteHost(InetAddress.getLocalHost().getHostName());
        setPort(6000);
        start();
        Assert.assertTrue(receiver.awaitConnectorCreated(SocketReceiverTest.DELAY));
        Socket socket = serverSocket.accept();
        socket.close();
    }

    @Test
    public void testDispatchEventForEnabledLevel() throws Exception {
        setRemoteHost(InetAddress.getLocalHost().getHostName());
        setPort(6000);
        start();
        Assert.assertTrue(receiver.awaitConnectorCreated(SocketReceiverTest.DELAY));
        Socket socket = serverSocket.accept();
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        logger.setLevel(DEBUG);
        ILoggingEvent event = new ch.qos.logback.classic.spi.LoggingEvent(logger.getName(), logger, Level.DEBUG, "test message", null, new Object[0]);
        LoggingEventVO eventVO = LoggingEventVO.build(event);
        oos.writeObject(eventVO);
        oos.flush();
        ILoggingEvent rcvdEvent = appender.awaitAppend(SocketReceiverTest.DELAY);
        Assert.assertNotNull(rcvdEvent);
        Assert.assertEquals(event.getLoggerName(), rcvdEvent.getLoggerName());
        Assert.assertEquals(event.getLevel(), rcvdEvent.getLevel());
        Assert.assertEquals(event.getMessage(), rcvdEvent.getMessage());
    }

    @Test
    public void testNoDispatchEventForDisabledLevel() throws Exception {
        setRemoteHost(InetAddress.getLocalHost().getHostName());
        setPort(6000);
        start();
        Assert.assertTrue(receiver.awaitConnectorCreated(SocketReceiverTest.DELAY));
        Socket socket = serverSocket.accept();
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        logger.setLevel(INFO);
        ILoggingEvent event = new ch.qos.logback.classic.spi.LoggingEvent(logger.getName(), logger, Level.DEBUG, "test message", null, new Object[0]);
        LoggingEventVO eventVO = LoggingEventVO.build(event);
        oos.writeObject(eventVO);
        oos.flush();
        Assert.assertNull(appender.awaitAppend(SocketReceiverTest.DELAY));
    }

    /**
     * A {@link SocketReceiver} with instrumentation for unit testing.
     */
    private class InstrumentedSocketReceiver extends SocketReceiver {
        private boolean connectorCreated;

        @Override
        protected synchronized SocketConnector newConnector(InetAddress address, int port, int initialDelay, int retryDelay) {
            connectorCreated = true;
            notifyAll();
            return connector;
        }

        @Override
        protected SocketFactory getSocketFactory() {
            return socketFactory;
        }

        public synchronized boolean awaitConnectorCreated(long delay) throws InterruptedException {
            while (!(connectorCreated)) {
                wait(delay);
            } 
            return connectorCreated;
        }
    }

    /**
     * A {@link SocketConnector} with instrumentation for unit testing.
     */
    private static class MockSocketConnector implements SocketConnector {
        private final Socket socket;

        public MockSocketConnector(Socket socket) {
            this.socket = socket;
        }

        public Socket call() throws InterruptedException {
            return socket;
        }

        public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        }

        public void setSocketFactory(SocketFactory socketFactory) {
        }
    }

    /**
     * A no-op {@link SocketFactory} to support unit testing.
     */
    private static class MockSocketFactory extends SocketFactory {
        @Override
        public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Socket createSocket(InetAddress host, int port) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            throw new UnsupportedOperationException();
        }
    }
}

