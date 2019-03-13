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


import Level.DEBUG;
import Level.INFO;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.net.mock.MockAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link RemoteAppenderStreamClient}.
 *
 * @author Carl Harris
 */
public class RemoteAppenderStreamClientTest {
    private MockAppender appender;

    private Logger logger;

    private LoggingEvent event;

    private RemoteAppenderStreamClient client;

    @Test
    public void testWithEnabledLevel() throws Exception {
        logger.setLevel(DEBUG);
        client.run();
        client.close();
        ILoggingEvent rcvdEvent = appender.getLastEvent();
        Assert.assertEquals(event.getLoggerName(), rcvdEvent.getLoggerName());
        Assert.assertEquals(event.getLevel(), rcvdEvent.getLevel());
        Assert.assertEquals(event.getMessage(), rcvdEvent.getMessage());
    }

    @Test
    public void testWithDisabledLevel() throws Exception {
        logger.setLevel(INFO);
        client.run();
        client.close();
        Assert.assertNull(appender.getLastEvent());
    }
}

