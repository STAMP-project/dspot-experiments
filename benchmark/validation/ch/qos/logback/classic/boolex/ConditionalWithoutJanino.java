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
package ch.qos.logback.classic.boolex;


import IfAction.MISSING_JANINO_MSG;
import Level.WARN;
import Logger.ROOT_LOGGER_NAME;
import ch.qos.logback.classic.ClassicTestConstants;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.testUtil.StatusChecker;
import ch.qos.logback.core.util.StatusPrinter;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class ConditionalWithoutJanino {
    LoggerContext loggerContext = new LoggerContext();

    Logger root = loggerContext.getLogger(ROOT_LOGGER_NAME);

    // assume that janino.jar ia NOT on the classpath
    @Test
    public void conditionalWithoutJanino() throws JoranException {
        String configFile = (ClassicTestConstants.JORAN_INPUT_PREFIX) + "conditional/withoutJanino.xml";
        String currentDir = System.getProperty("user.dir");
        if (!(currentDir.contains("logback-classic"))) {
            configFile = "logback-classic/" + configFile;
        }
        configure(configFile);
        StatusPrinter.print(loggerContext);
        StatusChecker checker = new StatusChecker(loggerContext);
        checker.assertContainsMatch(MISSING_JANINO_MSG);
        Assert.assertSame(WARN, loggerContext.getLogger("a").getLevel());
        Assert.assertSame(WARN, root.getLevel());
    }
}

