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
package ch.qos.logback.access.db;


import ch.qos.logback.access.spi.AccessContext;
import ch.qos.logback.core.testUtil.EnvUtilForTests;
import ch.qos.logback.core.testUtil.StatusChecker;
import java.util.Random;
import org.junit.Test;


public class DBAppenderIntegrationTest {
    static String LOCAL_HOST_NAME = EnvUtilForTests.getLocalHostName();

    static String[] CONFORMING_HOST_LIST = new String[]{ "Orion" };

    int diff = new Random(System.nanoTime()).nextInt(10000);

    AccessContext context = new AccessContext();

    StatusChecker statusChecker = new StatusChecker(context);

    @Test
    public void sqlserver() throws Exception {
        // perform test only on conforming hosts
        if (!(DBAppenderIntegrationTest.isConformingHostAndJDK16OrHigher())) {
            return;
        }
        doTest("src/test/input/integration/db/sqlserver-with-driver.xml");
    }

    @Test
    public void mysql() throws Exception {
        // perform test only on conforming hosts
        if (!(DBAppenderIntegrationTest.isConformingHostAndJDK16OrHigher())) {
            return;
        }
        doTest("src/test/input/integration/db/mysql-with-driver.xml");
    }

    @Test
    public void postgres() throws Exception {
        // perform test only on conforming hosts
        if (!(DBAppenderIntegrationTest.isConformingHostAndJDK16OrHigher())) {
            return;
        }
        doTest("src/test/input/integration/db/postgresql-with-driver.xml");
    }
}

