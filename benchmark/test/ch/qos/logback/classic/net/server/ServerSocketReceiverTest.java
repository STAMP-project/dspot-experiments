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


import ch.qos.logback.core.net.mock.MockContext;
import ch.qos.logback.core.net.server.test.MockServerListener;
import ch.qos.logback.core.net.server.test.MockServerRunner;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.Status;
import java.io.IOException;
import java.net.ServerSocket;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link ServerSocketReceiver}.
 *
 * @author Carl Harris
 */
public class ServerSocketReceiverTest {
    private MockContext context = new MockContext();

    private MockServerRunner<RemoteAppenderClient> runner = new MockServerRunner<RemoteAppenderClient>();

    private MockServerListener<RemoteAppenderClient> listener = new MockServerListener<RemoteAppenderClient>();

    private ServerSocket serverSocket;

    private InstrumentedServerSocketReceiver receiver;

    @Test
    public void testStartStop() throws Exception {
        start();
        Assert.assertTrue(runner.isContextInjected());
        Assert.assertTrue(runner.isRunning());
        Assert.assertSame(listener, receiver.getLastListener());
        stop();
        Assert.assertFalse(runner.isRunning());
    }

    @Test
    public void testStartWhenAlreadyStarted() throws Exception {
        start();
        start();
        Assert.assertEquals(1, runner.getStartCount());
    }

    @Test
    public void testStopThrowsException() throws Exception {
        start();
        Assert.assertTrue(isStarted());
        IOException ex = new IOException("test exception");
        runner.setStopException(ex);
        stop();
        Status status = context.getLastStatus();
        Assert.assertNotNull(status);
        Assert.assertTrue((status instanceof ErrorStatus));
        Assert.assertTrue(status.getMessage().contains(ex.getMessage()));
        Assert.assertSame(ex, status.getThrowable());
    }

    @Test
    public void testStopWhenNotStarted() throws Exception {
        stop();
        Assert.assertEquals(0, runner.getStartCount());
    }
}

