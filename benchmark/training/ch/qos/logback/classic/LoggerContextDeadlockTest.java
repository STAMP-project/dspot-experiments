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
package ch.qos.logback.classic;


import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import java.io.ByteArrayInputStream;
import org.junit.Test;


public class LoggerContextDeadlockTest {
    LoggerContext loggerContext = new LoggerContext();

    JoranConfigurator jc = new JoranConfigurator();

    LoggerContextDeadlockTest.GetLoggerThread getLoggerThread = new LoggerContextDeadlockTest.GetLoggerThread(loggerContext);

    @Test(timeout = 20000)
    public void testLBCLASSIC_81() throws JoranException {
        getLoggerThread.start();
        for (int i = 0; i < 500; i++) {
            ByteArrayInputStream baos = new ByteArrayInputStream("<configuration><root level=\"DEBUG\"/></configuration>".getBytes());
            jc.doConfigure(baos);
        }
    }

    class GetLoggerThread extends Thread {
        final LoggerContext loggerContext;

        GetLoggerThread(LoggerContext loggerContext) {
            this.loggerContext = loggerContext;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                if ((i % 100) == 0) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                    }
                }
                loggerContext.getLogger(("a" + i));
            }
        }
    }
}

