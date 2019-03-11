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
import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.core.db.DriverManagerConnectionSource;
import ch.qos.logback.core.util.StatusPrinter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.Assert;
import org.junit.Test;


public class DBAppenderHSQLTest {
    static DBAppenderHSQLTestFixture DB_APPENDER_HSQL_TEST_FIXTURE;

    AccessContext context;

    DBAppender appender;

    DriverManagerConnectionSource connectionSource;

    int existingEventTableRowCount;

    Statement stmt;

    @Test
    public void testAppendAccessEvent() throws SQLException {
        setInsertHeadersAndStart(false);
        IAccessEvent event = createAccessEvent();
        appender.append(event);
        Statement stmt = connectionSource.getConnection().createStatement();
        ResultSet rs = null;
        rs = stmt.executeQuery(("SELECT * FROM access_event where EVENT_ID = " + (existingEventTableRowCount)));
        if (rs.next()) {
            Assert.assertEquals(event.getTimeStamp(), rs.getLong(1));
            Assert.assertEquals(event.getRequestURI(), rs.getString(2));
            Assert.assertEquals(event.getRequestURL(), rs.getString(3));
            Assert.assertEquals(event.getRemoteHost(), rs.getString(4));
            Assert.assertEquals(event.getRemoteUser(), rs.getString(5));
            Assert.assertEquals(event.getRemoteAddr(), rs.getString(6));
            Assert.assertEquals(event.getProtocol(), rs.getString(7));
            Assert.assertEquals(event.getMethod(), rs.getString(8));
            Assert.assertEquals(event.getServerName(), rs.getString(9));
            Assert.assertEquals(event.getRequestContent(), rs.getString(10));
        } else {
            Assert.fail("No row was inserted in the database");
        }
        rs.close();
        stmt.close();
    }

    @Test
    public void testCheckNoHeadersAreInserted() throws Exception {
        setInsertHeadersAndStart(false);
        IAccessEvent event = createAccessEvent();
        appender.append(event);
        StatusPrinter.print(context.getStatusManager());
        // Check that no headers were inserted
        Statement stmt = connectionSource.getConnection().createStatement();
        ResultSet rs = null;
        rs = stmt.executeQuery(("SELECT * FROM access_event_header where EVENT_ID = " + (existingEventTableRowCount)));
        Assert.assertFalse(rs.next());
        rs.close();
        stmt.close();
    }

    @Test
    public void testAppendHeaders() throws SQLException {
        setInsertHeadersAndStart(true);
        IAccessEvent event = createAccessEvent();
        appender.append(event);
        Statement stmt = connectionSource.getConnection().createStatement();
        ResultSet rs = null;
        rs = stmt.executeQuery("SELECT * FROM access_event_header");
        String key;
        String value;
        if (!(rs.next())) {
            Assert.fail("There should be results to this query");
        } else {
            key = rs.getString(2);
            value = rs.getString(3);
            Assert.assertNotNull(key);
            Assert.assertNotNull(value);
            Assert.assertEquals(event.getRequestHeader(key), value);
            rs.next();
            key = rs.getString(2);
            value = rs.getString(3);
            Assert.assertNotNull(key);
            Assert.assertNotNull(value);
            Assert.assertEquals(event.getRequestHeader(key), value);
        }
        if (rs.next()) {
            Assert.fail("There should be no more rows available");
        }
        rs.close();
        stmt.close();
    }

    @Test
    public void testAppendMultipleEvents() throws SQLException {
        setInsertHeadersAndStart(false);
        String uri = "testAppendMultipleEvents";
        for (int i = 0; i < 10; i++) {
            IAccessEvent event = createAccessEvent(uri);
            appender.append(event);
        }
        StatusPrinter.print(context);
        Statement stmt = connectionSource.getConnection().createStatement();
        ResultSet rs = null;
        rs = stmt.executeQuery((("SELECT * FROM access_event where requestURI='" + uri) + "'"));
        int count = 0;
        while (rs.next()) {
            count++;
        } 
        Assert.assertEquals(10, count);
        rs.close();
        stmt.close();
    }
}

