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


import DBAppender.ARG0_INDEX;
import DBAppender.CALLER_CLASS_INDEX;
import DBAppender.CALLER_FILENAME_INDEX;
import DBAppender.CALLER_METHOD_INDEX;
import DBAppender.FORMATTED_MESSAGE_INDEX;
import DBAppender.LEVEL_STRING_INDEX;
import DBAppender.LOGGER_NAME_INDEX;
import DBAppender.REFERENCE_FLAG_INDEX;
import DBAppender.THREAD_NAME_INDEX;
import DBAppender.TIMESTMP_INDEX;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.db.DriverManagerConnectionSource;
import ch.qos.logback.core.testUtil.RandomUtil;
import ch.qos.logback.core.util.StatusPrinter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import org.apache.log4j.MDC;
import org.junit.Assert;
import org.junit.Test;


public class DBAppenderHSQLTest {
    LoggerContext lc;

    Logger logger;

    DBAppender appender;

    DriverManagerConnectionSource connectionSource;

    static DBAppenderHSQLTestFixture DB_APPENDER_HSQL_TEST_FIXTURE;

    int diff = RandomUtil.getPositiveInt();

    int existingRowCount;

    Statement stmt;

    @Test
    public void testAppendLoggingEvent() throws SQLException {
        ILoggingEvent event = createLoggingEvent();
        appender.append(event);
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
        ResultSet rs = null;
        rs = stmt.executeQuery(("SELECT * FROM logging_event where EVENT_ID = " + (existingRowCount)));
        if (rs.next()) {
            Assert.assertEquals(event.getTimeStamp(), rs.getLong(TIMESTMP_INDEX));
            Assert.assertEquals(event.getFormattedMessage(), rs.getString(FORMATTED_MESSAGE_INDEX));
            Assert.assertEquals(event.getLoggerName(), rs.getString(LOGGER_NAME_INDEX));
            Assert.assertEquals(event.getLevel().toString(), rs.getString(LEVEL_STRING_INDEX));
            Assert.assertEquals(event.getThreadName(), rs.getString(THREAD_NAME_INDEX));
            Assert.assertEquals(DBHelper.computeReferenceMask(event), rs.getShort(REFERENCE_FLAG_INDEX));
            Assert.assertEquals(String.valueOf(diff), rs.getString(ARG0_INDEX));
            StackTraceElement callerData = event.getCallerData()[0];
            Assert.assertEquals(callerData.getFileName(), rs.getString(CALLER_FILENAME_INDEX));
            Assert.assertEquals(callerData.getClassName(), rs.getString(CALLER_CLASS_INDEX));
            Assert.assertEquals(callerData.getMethodName(), rs.getString(CALLER_METHOD_INDEX));
        } else {
            Assert.fail("No row was inserted in the database");
        }
        rs.close();
    }

    @Test
    public void testAppendThrowable() throws SQLException {
        ILoggingEvent event = createLoggingEvent();
        appender.append(event);
        ResultSet rs = null;
        rs = stmt.executeQuery(("SELECT * FROM LOGGING_EVENT_EXCEPTION where EVENT_ID = " + (existingRowCount)));
        rs.next();
        String expected = "java.lang.Exception: test Ex";
        String firstLine = rs.getString(3);
        Assert.assertTrue((((("[" + firstLine) + "] does not match [") + expected) + "]"), firstLine.contains(expected));
        int i = 0;
        while (rs.next()) {
            expected = event.getThrowableProxy().getStackTraceElementProxyArray()[i].toString();
            String st = rs.getString(3);
            Assert.assertTrue((((("[" + st) + "] does not match [") + expected) + "]"), st.contains(expected));
            i++;
        } 
        Assert.assertTrue((i != 0));
        rs.close();
    }

    @Test
    public void testContextInfo() throws SQLException {
        lc.putProperty("testKey1", "testValue1");
        MDC.put(("k" + (diff)), ("v" + (diff)));
        ILoggingEvent event = createLoggingEvent();
        appender.append(event);
        Statement stmt = connectionSource.getConnection().createStatement();
        ResultSet rs = null;
        rs = stmt.executeQuery(("SELECT * FROM LOGGING_EVENT_PROPERTY  WHERE EVENT_ID = " + (existingRowCount)));
        Map<String, String> map = appender.mergePropertyMaps(event);
        System.out.println(("ma.size=" + (map.size())));
        int i = 0;
        while (rs.next()) {
            String key = rs.getString(2);
            Assert.assertEquals(map.get(key), rs.getString(3));
            i++;
        } 
        Assert.assertTrue(((map.size()) != 0));
        Assert.assertEquals(map.size(), i);
        rs.close();
    }

    @Test
    public void testAppendMultipleEvents() throws SQLException {
        int numEvents = 3;
        for (int i = 0; i < numEvents; i++) {
            ILoggingEvent event = createLoggingEvent();
            appender.append(event);
        }
        Statement stmt = connectionSource.getConnection().createStatement();
        ResultSet rs = null;
        rs = stmt.executeQuery(("SELECT * FROM logging_event WHERE EVENT_ID >=" + (existingRowCount)));
        int count = 0;
        while (rs.next()) {
            count++;
        } 
        Assert.assertEquals(numEvents, count);
        rs.close();
    }
}

