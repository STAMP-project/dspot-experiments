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
package ch.qos.logback.classic.net.server;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.net.mock.MockAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEventVO;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/**
 * A functional test for {@link ServerSocketReceiver}.
 * <p>
 * In this test we create a SocketServer, connect to it over the local
 * network interface, and validate that it receives messages and delivers
 * them to its appender.
 */
@Ignore
public class ServerSocketReceiverFunctionalTest {
    private static final int EVENT_COUNT = 10;

    private static final int SHUTDOWN_DELAY = 10000;

    private MockAppender appender;

    private Logger logger;

    private ServerSocket serverSocket;

    private InstrumentedServerSocketReceiver receiver;

    private LoggerContext lc;

    @Test
    public void testLogEventFromClient() throws Exception {
        ILoggingEvent event = new ch.qos.logback.classic.spi.LoggingEvent(logger.getName(), logger, Level.DEBUG, "test message", null, new Object[0]);
        Socket socket = new Socket(InetAddress.getLocalHost(), serverSocket.getLocalPort());
        try {
            LoggingEventVO eventVO = LoggingEventVO.build(event);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            for (int i = 0; i < (ServerSocketReceiverFunctionalTest.EVENT_COUNT); i++) {
                oos.writeObject(eventVO);
            }
            oos.writeObject(eventVO);
            oos.flush();
        } finally {
            socket.close();
        }
        ILoggingEvent rcvdEvent = appender.awaitAppend(ServerSocketReceiverFunctionalTest.SHUTDOWN_DELAY);
        Assert.assertNotNull(rcvdEvent);
        Assert.assertEquals(event.getLoggerName(), rcvdEvent.getLoggerName());
        Assert.assertEquals(event.getLevel(), rcvdEvent.getLevel());
        Assert.assertEquals(event.getMessage(), rcvdEvent.getMessage());
    }
}

