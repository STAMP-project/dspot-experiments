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
package ch.qos.logback.classic.encoder;


import Status.WARN;
import ch.qos.logback.classic.ClassicTestConstants;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.testUtil.StatusChecker;
import ch.qos.logback.core.util.StatusPrinter;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.ch.qos.logback.classic.Logger;


public class LayoutInsteadOfEncoderTest {
    // TeztConstants.TEST_SRC_PREFIX + "input/joran/ignore.xml"
    JoranConfigurator jc = new JoranConfigurator();

    LoggerContext loggerContext = new LoggerContext();

    // jc.doConfigure(TeztConstants.TEST_SRC_PREFIX + "input/joran/ignore.xml");
    @Test
    public void layoutInsteadOfEncoer() throws JoranException {
        jc.doConfigure(((ClassicTestConstants.JORAN_INPUT_PREFIX) + "compatibility/layoutInsteadOfEncoder.xml"));
        StatusPrinter.print(loggerContext);
        StatusChecker checker = new StatusChecker(loggerContext);
        checker.assertContainsMatch(WARN, "This appender no longer admits a layout as a sub-component");
        checker.assertContainsMatch(WARN, (("See also " + (CoreConstants.CODES_URL)) + "#layoutInsteadOfEncoder for details"));
        ch.qos.logback.classic.Logger root = ((ch.qos.logback.classic.Logger) (loggerContext.getLogger(Logger.ROOT_LOGGER_NAME)));
        FileAppender<ILoggingEvent> fileAppender = ((FileAppender<ILoggingEvent>) (root.getAppender("LIOE")));
        Assert.assertTrue(fileAppender.isStarted());
        Assert.assertTrue(((fileAppender.getEncoder()) instanceof LayoutWrappingEncoder));
    }

    @Test
    public void immediateFlushInEncoder_TRUE() throws JoranException {
        immediateFlushInEncoder(true);
    }

    @Test
    public void immediateFlushInEncoder_FALSE() throws JoranException {
        immediateFlushInEncoder(false);
    }
}

