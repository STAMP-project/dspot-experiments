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
package ch.qos.logback.classic.joran.conditional;


import Logger.ROOT_LOGGER_NAME;
import ch.qos.logback.classic.ClassicTestConstants;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.read.ListAppender;
import ch.qos.logback.core.testUtil.CoreTestConstants;
import ch.qos.logback.core.testUtil.RandomUtil;
import ch.qos.logback.core.testUtil.StatusChecker;
import java.io.IOException;
import java.net.InetAddress;
import org.junit.Assert;
import org.junit.Test;


public class ConditionalTest {
    LoggerContext context = new LoggerContext();

    Logger root = context.getLogger(ROOT_LOGGER_NAME);

    int diff = RandomUtil.getPositiveInt();

    String randomOutputDir = ((CoreTestConstants.OUTPUT_DIR_PREFIX) + (diff)) + "/";

    @SuppressWarnings("rawtypes")
    @Test
    public void conditionalConsoleApp_IF_THEN_True() throws JoranException, IOException, InterruptedException {
        InetAddress localhost = InetAddress.getLocalHost();
        System.out.println((((("In conditionalConsoleApp_IF_THEN_True, canonicalHostName=\"" + (localhost.getCanonicalHostName())) + "] and hostNmae=\"") + (localhost.getHostName())) + "\""));
        context.putProperty("aHost", localhost.getHostName());
        String configFileAsStr = (ClassicTestConstants.JORAN_INPUT_PREFIX) + "conditional/conditionalConsoleApp.xml";
        configure(configFileAsStr);
        FileAppender fileAppender = ((FileAppender) (root.getAppender("FILE")));
        Assert.assertNotNull(fileAppender);
        ConsoleAppender consoleAppender = ((ConsoleAppender) (root.getAppender("CON")));
        Assert.assertNotNull(consoleAppender);
        StatusChecker checker = new StatusChecker(context);
        checker.assertIsErrorFree();
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void conditionalConsoleApp_IF_THEN_False() throws JoranException, IOException, InterruptedException {
        String configFileAsStr = (ClassicTestConstants.JORAN_INPUT_PREFIX) + "conditional/conditionalConsoleApp.xml";
        configure(configFileAsStr);
        FileAppender fileAppender = ((FileAppender) (root.getAppender("FILE")));
        Assert.assertNotNull(fileAppender);
        ConsoleAppender consoleAppender = ((ConsoleAppender) (root.getAppender("CON")));
        Assert.assertNull(consoleAppender);
        StatusChecker checker = new StatusChecker(context);
        checker.assertIsErrorFree();
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void conditionalConsoleApp_IF_THEN_ELSE() throws JoranException, IOException, InterruptedException {
        String configFileAsStr = (ClassicTestConstants.JORAN_INPUT_PREFIX) + "conditional/conditionalConsoleApp_ELSE.xml";
        configure(configFileAsStr);
        FileAppender fileAppender = ((FileAppender) (root.getAppender("FILE")));
        Assert.assertNotNull(fileAppender);
        ConsoleAppender consoleAppender = ((ConsoleAppender) (root.getAppender("CON")));
        Assert.assertNull(consoleAppender);
        ListAppender listAppender = ((ListAppender) (root.getAppender("LIST")));
        Assert.assertNotNull(listAppender);
        // StatusPrinter.printIfErrorsOccured(context);
        StatusChecker checker = new StatusChecker(context);
        checker.assertIsErrorFree();
    }

    @Test
    public void conditionalInclusionWithExistingFile() throws JoranException, IOException, InterruptedException {
        String configFileAsStr = (ClassicTestConstants.JORAN_INPUT_PREFIX) + "conditional/conditionalIncludeExistingFile.xml";
        configure(configFileAsStr);
        ConsoleAppender<ILoggingEvent> consoleAppender = ((ConsoleAppender<ILoggingEvent>) (root.getAppender("CON")));
        Assert.assertNotNull(consoleAppender);
        StatusChecker checker = new StatusChecker(context);
        checker.assertIsErrorFree();
    }

    @Test
    public void conditionalInclusionWithInexistentFile() throws JoranException, IOException, InterruptedException {
        String configFileAsStr = (ClassicTestConstants.JORAN_INPUT_PREFIX) + "conditional/conditionalIncludeInexistentFile.xml";
        configure(configFileAsStr);
        ConsoleAppender<ILoggingEvent> consoleAppender = ((ConsoleAppender<ILoggingEvent>) (root.getAppender("CON")));
        Assert.assertNull(consoleAppender);
        StatusChecker checker = new StatusChecker(context);
        checker.assertIsErrorFree();
    }
}

