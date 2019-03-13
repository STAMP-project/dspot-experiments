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
package org.eclipse.jetty.server.session;


import JDBCSessionDataStore.SessionTableSchema;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * SessionTableSchemaTest
 *
 * Test the SessionTableSchema behaviour when the database treats "" as a NULL,
 * like Oracle does.
 */
public class SessionTableSchemaTest {
    DatabaseAdaptor _da;

    SessionTableSchema _tableSchema;

    @Test
    public void testLoad() throws Exception {
        // set up the db
        _da.initialize();
        _tableSchema.prepareTables();
        // insert a fake session at the root context
        JdbcTestHelper.insertSession("1234", "/", "0.0.0.0");
        // test if it can be seen
        try (Connection con = _da.getConnection()) {
            // make a root context
            ContextHandler handler = new ContextHandler();
            handler.setContextPath("/");
            SessionContext sc = new SessionContext("0", handler.getServletContext());
            // test the load statement
            PreparedStatement s = _tableSchema.getLoadStatement(con, "1234", sc);
            ResultSet rs = s.executeQuery();
            Assertions.assertTrue(rs.next());
        }
    }

    @Test
    public void testExists() throws Exception {
        // set up the db
        _da.initialize();
        _tableSchema.prepareTables();
        // insert a fake session at the root context
        JdbcTestHelper.insertSession("1234", "/", "0.0.0.0");
        // test if it can be seen
        try (Connection con = _da.getConnection()) {
            ContextHandler handler = new ContextHandler();
            handler.setContextPath("/");
            SessionContext sc = new SessionContext("0", handler.getServletContext());
            PreparedStatement s = _tableSchema.getCheckSessionExistsStatement(con, sc);
            s.setString(1, "1234");
            ResultSet rs = s.executeQuery();
            Assertions.assertTrue(rs.next());
        }
    }

    @Test
    public void testDelete() throws Exception {
        // set up the db
        _da.initialize();
        _tableSchema.prepareTables();
        // insert a fake session at the root context
        JdbcTestHelper.insertSession("1234", "/", "0.0.0.0");
        // test if it can be deleted
        try (Connection con = _da.getConnection()) {
            ContextHandler handler = new ContextHandler();
            handler.setContextPath("/");
            SessionContext sc = new SessionContext("0", handler.getServletContext());
            PreparedStatement s = _tableSchema.getDeleteStatement(con, "1234", sc);
            Assertions.assertEquals(1, s.executeUpdate());
            Assertions.assertFalse(JdbcTestHelper.existsInSessionTable("1234", false));
        }
    }

    @Test
    public void testExpired() throws Exception {
        // set up the db
        _da.initialize();
        _tableSchema.prepareTables();
        // insert a fake session at the root context
        JdbcTestHelper.insertSession("1234", "/", "0.0.0.0");
        try (Connection con = _da.getConnection()) {
            ContextHandler handler = new ContextHandler();
            handler.setContextPath("/");
            SessionContext sc = new SessionContext("0", handler.getServletContext());
            PreparedStatement s = _tableSchema.getExpiredSessionsStatement(con, sc.getCanonicalContextPath(), sc.getVhost(), ((System.currentTimeMillis()) + 100L));
            ResultSet rs = s.executeQuery();
            Assertions.assertTrue(rs.next());
            Assertions.assertEquals("1234", rs.getString(1));
        }
    }

    @Test
    public void testMyExpiredSessions() throws Exception {
        // set up the db
        _da.initialize();
        _tableSchema.prepareTables();
        // insert a fake session at the root context
        JdbcTestHelper.insertSession("1234", "/", "0.0.0.0");
        try (Connection con = _da.getConnection()) {
            ContextHandler handler = new ContextHandler();
            handler.setContextPath("/");
            SessionContext sc = new SessionContext("0", handler.getServletContext());
            PreparedStatement s = _tableSchema.getMyExpiredSessionsStatement(con, sc, ((System.currentTimeMillis()) + 100L));
            ResultSet rs = s.executeQuery();
            Assertions.assertTrue(rs.next());
            Assertions.assertEquals("1234", rs.getString(1));
        }
    }

    @Test
    public void testUpdate() throws Exception {
        // set up the db
        _da.initialize();
        _tableSchema.prepareTables();
        // insert a fake session at the root context
        JdbcTestHelper.insertSession("1234", "/", "0.0.0.0");
        try (Connection con = _da.getConnection()) {
            ContextHandler handler = new ContextHandler();
            handler.setContextPath("/");
            SessionContext sc = new SessionContext("0", handler.getServletContext());
            PreparedStatement s = _tableSchema.getUpdateStatement(con, "1234", sc);
            s.setString(1, "0");// should be my node id

            s.setLong(2, System.currentTimeMillis());
            s.setLong(3, System.currentTimeMillis());
            s.setLong(4, System.currentTimeMillis());
            s.setLong(5, System.currentTimeMillis());
            s.setLong(6, 2000L);
            byte[] bytes = new byte[3];
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            s.setBinaryStream(7, bais, bytes.length);// attribute map as blob

            Assertions.assertEquals(1, s.executeUpdate());
        }
    }
}

