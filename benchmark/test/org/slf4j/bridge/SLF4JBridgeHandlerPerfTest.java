/**
 * Copyright (c) 2004-2011 QOS.ch
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.slf4j.bridge;


import java.util.logging.Handler;
import java.util.logging.LogManager;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static java.util.logging.Logger.getLogger;


public class SLF4JBridgeHandlerPerfTest {
    static String LOGGER_NAME = "yay";

    static int RUN_LENGTH = 100 * 1000;

    // set to false to test enabled logging performance
    boolean disabledLogger = true;

    FileAppender fileAppender;

    Logger log4jRoot;

    java.util.logging.Logger julRootLogger = LogManager.getLogManager().getLogger("");

    java.util.logging.Logger julLogger = getLogger(SLF4JBridgeHandlerPerfTest.LOGGER_NAME);

    org.slf4j.Logger slf4jLogger = LoggerFactory.getLogger(SLF4JBridgeHandlerPerfTest.LOGGER_NAME);

    Handler[] existingHandlers;

    @Test
    public void testPerf() {
        SLF4JBridgeHandler.install();
        if (disabledLogger) {
            log4jRoot.setLevel(Level.ERROR);
        }
        julLoggerLoop();
        double julAvg = julLoggerLoop();
        System.out.println((("Average cost per call (JUL->SLF4J->log4j): " + julAvg) + " nanos"));
        slf4jLoggerLoop();
        double slf4jAvg = slf4jLoggerLoop();
        System.out.println((("Average cost per call (SLF4J->log4j): " + slf4jAvg) + " nanos"));
        System.out.println(("Ratio " + (julAvg / slf4jAvg)));
    }
}

