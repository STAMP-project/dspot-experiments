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
package ch.qos.logback.classic.db;


import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.testUtil.RandomUtil;
import org.junit.Test;


public class DBAppenderIntegrationTest {
    static String LOCAL_HOST_NAME;

    static String[] CONFORMING_HOST_LIST = new String[]{ "Orion" };

    static String[] POSTGRES_CONFORMING_HOST_LIST = new String[]{ "haro" };

    static String[] MYSQL_CONFORMING_HOST_LIST = new String[]{ "xharo" };

    static String[] ORACLE_CONFORMING_HOST_LIST = new String[]{ "xharo" };

    int diff = RandomUtil.getPositiveInt();

    LoggerContext lc = new LoggerContext();

    @Test
    public void sqlserver() throws Exception {
        // perform test only on conforming hosts
        if (!(DBAppenderIntegrationTest.isConformingHostAndJDK16OrHigher())) {
            return;
        }
        doTest("src/test/input/integration/db/sqlserver-with-driver.xml");
    }

    @Test
    public void oracle10g() throws Exception {
        // perform test only on conforming hosts
        if (!(DBAppenderIntegrationTest.isConformingHostAndJDK16OrHigher(DBAppenderIntegrationTest.ORACLE_CONFORMING_HOST_LIST))) {
            return;
        }
        doTest("src/test/input/integration/db/oracle10g-with-driver.xml");
    }

    @Test
    public void mysql() throws Exception {
        // perform test only on conforming hosts
        if (!(DBAppenderIntegrationTest.isConformingHostAndJDK16OrHigher(DBAppenderIntegrationTest.MYSQL_CONFORMING_HOST_LIST))) {
            return;
        }
        doTest("src/test/input/integration/db/mysql-with-driver.xml");
    }

    @Test
    public void postgres() throws Exception {
        // perform test only on conforming hosts
        if (!(DBAppenderIntegrationTest.isConformingHostAndJDK16OrHigher(DBAppenderIntegrationTest.POSTGRES_CONFORMING_HOST_LIST))) {
            return;
        }
        System.out.println("running postgres() test");
        doTest("src/test/input/integration/db/postgresql-with-driver.xml");
    }
}

