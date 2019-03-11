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
package ch.qos.logback.core.net.server;


import ch.qos.logback.core.net.mock.MockContext;
import ch.qos.logback.core.util.ExecutorServiceUtil;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ScheduledExecutorService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/**
 * A functional test for {@link AbstractServerSocketAppender}.
 *
 * @author Carl Harris
 */
@Ignore
public class ServerSocketAppenderBaseFunctionalTest {
    private static final String TEST_EVENT = "test event";

    private static final int EVENT_COUNT = 10;

    private ScheduledExecutorService executor = ExecutorServiceUtil.newScheduledExecutorService();

    private MockContext context = new MockContext(executor);

    private ServerSocket serverSocket;

    private InstrumentedServerSocketAppenderBase appender;

    @Test
    public void testLogEventClient() throws Exception {
        start();
        Socket socket = new Socket(InetAddress.getLocalHost(), serverSocket.getLocalPort());
        socket.setSoTimeout(1000);
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        for (int i = 0; i < (ServerSocketAppenderBaseFunctionalTest.EVENT_COUNT); i++) {
            append(((ServerSocketAppenderBaseFunctionalTest.TEST_EVENT) + i));
            Assert.assertEquals(((ServerSocketAppenderBaseFunctionalTest.TEST_EVENT) + i), ois.readObject());
        }
        socket.close();
        stop();
    }
}

