/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.util.log;


import java.util.Map;
import org.junit.jupiter.api.Test;


public class LogTest {
    private static Logger originalLogger;

    private static Map<String, Logger> originalLoggers;

    @Test
    public void testDefaultLogging() {
        Logger log = Log.getLogger(LogTest.class);
        log.info("Test default logging");
    }

    @Test
    public void testNamedLogNamed_StdErrLog() {
        Log.setLog(new StdErrLog());
        assertNamedLogging(Red.class);
        assertNamedLogging(Blue.class);
        assertNamedLogging(Green.class);
    }

    @Test
    public void testNamedLogNamed_JUL() {
        Log.setLog(new JavaUtilLog());
        assertNamedLogging(Red.class);
        assertNamedLogging(Blue.class);
        assertNamedLogging(Green.class);
    }

    @Test
    public void testNamedLogNamed_Slf4J() throws Exception {
        Log.setLog(new Slf4jLog());
        assertNamedLogging(Red.class);
        assertNamedLogging(Blue.class);
        assertNamedLogging(Green.class);
    }
}

