/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.standard;


import ListDatabaseTables.DB_TABLE_COUNT;
import ListDatabaseTables.INCLUDE_COUNT;
import ListDatabaseTables.REFRESH_INTERVAL;
import ListDatabaseTables.REL_SUCCESS;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.dbcp.DBCPService;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 * Unit tests for ListDatabaseTables processor.
 */
public class TestListDatabaseTables {
    TestRunner runner;

    ListDatabaseTables processor;

    private static final String DB_LOCATION = "target/db_ldt";

    @Test
    public void testListTablesNoCount() throws Exception {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_TABLE1");
            stmt.execute("drop table TEST_TABLE2");
        } catch (final SQLException sqle) {
            // Do nothing, may not have existed
        }
        stmt.execute("create table TEST_TABLE1 (id integer not null, val1 integer, val2 integer, constraint my_pk1 primary key (id))");
        stmt.execute("create table TEST_TABLE2 (id integer not null, val1 integer, val2 integer, constraint my_pk2 primary key (id))");
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 2);
        // Already got these tables, shouldn't get them again
        runner.clearTransferState();
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 0);
    }

    @Test
    public void testListTablesWithCount() throws Exception {
        runner.setProperty(INCLUDE_COUNT, "true");
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_TABLE1");
            stmt.execute("drop table TEST_TABLE2");
        } catch (final SQLException sqle) {
            // Do nothing, may not have existed
        }
        stmt.execute("create table TEST_TABLE1 (id integer not null, val1 integer, val2 integer, constraint my_pk1 primary key (id))");
        stmt.execute("insert into TEST_TABLE1 (id, val1, val2) VALUES (0, NULL, 1)");
        stmt.execute("insert into TEST_TABLE1 (id, val1, val2) VALUES (1, 1, 1)");
        stmt.execute("create table TEST_TABLE2 (id integer not null, val1 integer, val2 integer, constraint my_pk2 primary key (id))");
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 2);
        List<MockFlowFile> results = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals("2", results.get(0).getAttribute(DB_TABLE_COUNT));
        Assert.assertEquals("0", results.get(1).getAttribute(DB_TABLE_COUNT));
    }

    @Test
    public void testListTablesAfterRefresh() throws Exception {
        Assume.assumeFalse(isWindowsEnvironment());
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_TABLE1");
            stmt.execute("drop table TEST_TABLE2");
        } catch (final SQLException sqle) {
            // Do nothing, may not have existed
        }
        stmt.execute("create table TEST_TABLE1 (id integer not null, val1 integer, val2 integer, constraint my_pk1 primary key (id))");
        stmt.execute("insert into TEST_TABLE1 (id, val1, val2) VALUES (0, NULL, 1)");
        stmt.execute("insert into TEST_TABLE1 (id, val1, val2) VALUES (1, 1, 1)");
        stmt.execute("create table TEST_TABLE2 (id integer not null, val1 integer, val2 integer, constraint my_pk2 primary key (id))");
        stmt.close();
        runner.setProperty(INCLUDE_COUNT, "true");
        runner.setProperty(REFRESH_INTERVAL, "100 millis");
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 2);
        List<MockFlowFile> results = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals("2", results.get(0).getAttribute(DB_TABLE_COUNT));
        Assert.assertEquals("0", results.get(1).getAttribute(DB_TABLE_COUNT));
        runner.clearTransferState();
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 0);
        // Now wait longer than 100 millis and assert the refresh has happened (the two tables are re-listed)
        Thread.sleep(200);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 2);
    }

    @Test
    public void testListTablesMultipleRefresh() throws Exception {
        Assume.assumeFalse(isWindowsEnvironment());
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_TABLE1");
            stmt.execute("drop table TEST_TABLE2");
        } catch (final SQLException sqle) {
            // Do nothing, may not have existed
        }
        stmt.execute("create table TEST_TABLE1 (id integer not null, val1 integer, val2 integer, constraint my_pk1 primary key (id))");
        stmt.execute("insert into TEST_TABLE1 (id, val1, val2) VALUES (0, NULL, 1)");
        stmt.execute("insert into TEST_TABLE1 (id, val1, val2) VALUES (1, 1, 1)");
        runner.setProperty(INCLUDE_COUNT, "true");
        runner.setProperty(REFRESH_INTERVAL, "200 millis");
        runner.run();
        long startTimer = System.currentTimeMillis();
        runner.assertTransferCount(REL_SUCCESS, 1);
        List<MockFlowFile> results = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals("2", results.get(0).getAttribute(DB_TABLE_COUNT));
        runner.clearTransferState();
        // Add another table immediately, the first table should not be listed again but the second should
        stmt.execute("create table TEST_TABLE2 (id integer not null, val1 integer, val2 integer, constraint my_pk2 primary key (id))");
        stmt.close();
        runner.run();
        long endTimer = System.currentTimeMillis();
        // Expect 1 or 2 tables (whether execution has taken longer than the refresh time)
        runner.assertTransferCount(REL_SUCCESS, ((endTimer - startTimer) > 200 ? 2 : 1));
        results = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(((endTimer - startTimer) > 200 ? "2" : "0"), results.get(0).getAttribute(DB_TABLE_COUNT));
        runner.clearTransferState();
        // Now wait longer than the refresh interval and assert the refresh has happened (i.e. the two tables are re-listed)
        Thread.sleep(500);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 2);
    }

    /**
     * Simple implementation only for ListDatabaseTables processor testing.
     */
    private class DBCPServiceSimpleImpl extends AbstractControllerService implements DBCPService {
        @Override
        public String getIdentifier() {
            return "dbcp";
        }

        @Override
        public Connection getConnection() throws ProcessException {
            try {
                Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
                return DriverManager.getConnection((("jdbc:derby:" + (TestListDatabaseTables.DB_LOCATION)) + ";create=true"));
            } catch (final Exception e) {
                throw new ProcessException(("getConnection failed: " + e));
            }
        }
    }
}

