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
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.db.DriverManagerConnectionSource;
import ch.qos.logback.core.testUtil.RandomUtil;
import ch.qos.logback.core.testUtil.StatusChecker;
import ch.qos.logback.core.util.StatusPrinter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Map;
import org.apache.log4j.MDC;
import org.junit.Assert;
import org.junit.Test;


public class DBAppenderH2Test {
    LoggerContext loggerContext = new LoggerContext();

    Logger logger;

    DBAppender appender;

    DriverManagerConnectionSource connectionSource;

    DBAppenderH2TestFixture dbAppenderH2TestFixture;

    int diff = RandomUtil.getPositiveInt();

    StatusChecker checker = new StatusChecker(loggerContext);

    @Test
    public void testAppendLoggingEvent() throws SQLException {
        ILoggingEvent event = createLoggingEvent();
        appender.append(event);
        StatusPrinter.print(loggerContext);
        Statement stmt = connectionSource.getConnection().createStatement();
        ResultSet rs = null;
        rs = stmt.executeQuery("SELECT * FROM logging_event");
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
        stmt.close();
    }

    @Test
    public void testAppendThrowable() throws SQLException {
        ILoggingEvent event = createLoggingEvent();
        appender.append(event);
        Statement stmt = connectionSource.getConnection().createStatement();
        ResultSet rs = null;
        rs = stmt.executeQuery("SELECT * FROM LOGGING_EVENT_EXCEPTION WHERE EVENT_ID=1");
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
        stmt.close();
    }

    @Test
    public void withNullArgument() throws SQLException {
        ILoggingEvent event = createLoggingEvent("Processing code {}; code type is {}; request date {}; record from date {}", new Object[]{ 1, 2, new Date(), null });
        appender.append(event);
        Statement stmt = connectionSource.getConnection().createStatement();
        ResultSet rs = null;
        rs = stmt.executeQuery("SELECT * FROM logging_event");
        if (rs.next()) {
            Assert.assertEquals(event.getTimeStamp(), rs.getLong(TIMESTMP_INDEX));
            Assert.assertEquals(event.getFormattedMessage(), rs.getString(FORMATTED_MESSAGE_INDEX));
        }
    }

    @Test
    public void testContextInfo() throws SQLException {
        loggerContext.putProperty("testKey1", "testValue1");
        MDC.put(("k" + (diff)), ("v" + (diff)));
        ILoggingEvent event = createLoggingEvent();
        appender.append(event);
        Statement stmt = connectionSource.getConnection().createStatement();
        ResultSet rs = null;
        rs = stmt.executeQuery("SELECT * FROM LOGGING_EVENT_PROPERTY WHERE EVENT_ID=1");
        Map<String, String> map = appender.mergePropertyMaps(event);
        int i = 0;
        while (rs.next()) {
            String key = rs.getString(2);
            Assert.assertEquals(map.get(key), rs.getString(3));
            i++;
        } 
        Assert.assertTrue(((map.size()) != 0));
        Assert.assertEquals(map.size(), i);
        rs.close();
        stmt.close();
    }

    @Test
    public void testAppendMultipleEvents() throws SQLException {
        for (int i = 0; i < 10; i++) {
            ILoggingEvent event = createLoggingEvent();
            appender.append(event);
        }
        Statement stmt = connectionSource.getConnection().createStatement();
        ResultSet rs = null;
        rs = stmt.executeQuery("SELECT * FROM logging_event");
        int count = 0;
        while (rs.next()) {
            count++;
        } 
        Assert.assertEquals(10, count);
        rs.close();
        stmt.close();
    }

    // http://jira.qos.ch/browse/LOGBACK-805
    @Test
    public void emptyCallerDataShouldBeHandledGracefully() {
        LoggingEvent event = createLoggingEvent();
        event.setCallerData(new StackTraceElement[0]);
        appender.append(event);
        event.setCallerData(new StackTraceElement[]{ null });
        appender.append(event);
        checker.assertIsErrorFree();
    }
}

