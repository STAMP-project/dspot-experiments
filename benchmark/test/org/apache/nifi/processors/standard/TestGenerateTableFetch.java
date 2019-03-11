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


import GenerateTableFetch.COLUMN_FOR_VALUE_PARTITIONING;
import GenerateTableFetch.COLUMN_NAMES;
import GenerateTableFetch.MAX_VALUE_COLUMN_NAMES;
import GenerateTableFetch.OUTPUT_EMPTY_FLOWFILE_ON_ZERO_RESULTS;
import GenerateTableFetch.PARTITION_SIZE;
import GenerateTableFetch.REL_FAILURE;
import GenerateTableFetch.REL_SUCCESS;
import GenerateTableFetch.TABLE_NAME;
import GenerateTableFetch.WHERE_CLAUSE;
import Scope.CLUSTER;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.components.state.StateManager;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.dbcp.DBCPService;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for the GenerateTableFetch processor.
 */
public class TestGenerateTableFetch {
    TestRunner runner;

    GenerateTableFetch processor;

    TestGenerateTableFetch.DBCPServiceSimpleImpl dbcp;

    private static final String DB_LOCATION = "target/db_gtf";

    @Test
    public void testAddedRows() throws IOException, SQLException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, name varchar(100), scale float, created_on timestamp, bignum bigint default 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (0, 'Joe Smith', 1.0, '1962-09-23 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (1, 'Carrie Jones', 5.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (2, NULL, 2.0, '2010-01-01 00:00:00')");
        runner.setProperty(TABLE_NAME, "TEST_QUERY_DB_TABLE");
        runner.setIncomingConnection(false);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "ID");
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        String query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE ID <= 2 ORDER BY ID FETCH NEXT 10000 ROWS ONLY", query);
        flowFile.assertAttributeEquals(AbstractDatabaseFetchProcessor.FRAGMENT_INDEX, "0");
        flowFile.assertAttributeEquals(AbstractDatabaseFetchProcessor.FRAGMENT_COUNT, "1");
        ResultSet resultSet = stmt.executeQuery(query);
        // Should be three records
        Assert.assertTrue(resultSet.next());
        Assert.assertTrue(resultSet.next());
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        runner.clearTransferState();
        // Run again, this time no flowfiles/rows should be transferred
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 0);
        runner.clearTransferState();
        // Add 3 new rows with a higher ID and run with a partition size of 2. Two flow files should be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (3, 'Mary West', 15.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (4, 'Marty Johnson', 15.0, '2011-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (5, 'Marty Johnson', 15.0, '2011-01-01 03:23:34.234')");
        runner.setProperty(PARTITION_SIZE, "2");
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 2);
        // Check fragment attributes
        List<MockFlowFile> resultFFs = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS);
        MockFlowFile ff1 = resultFFs.get(0);
        MockFlowFile ff2 = resultFFs.get(1);
        Assert.assertEquals(ff1.getAttribute(AbstractDatabaseFetchProcessor.FRAGMENT_ID), ff2.getAttribute(AbstractDatabaseFetchProcessor.FRAGMENT_ID));
        Assert.assertEquals(ff1.getAttribute(AbstractDatabaseFetchProcessor.FRAGMENT_INDEX), "0");
        Assert.assertEquals(ff1.getAttribute(AbstractDatabaseFetchProcessor.FRAGMENT_COUNT), "2");
        Assert.assertEquals(ff2.getAttribute(AbstractDatabaseFetchProcessor.FRAGMENT_INDEX), "1");
        Assert.assertEquals(ff2.getAttribute(AbstractDatabaseFetchProcessor.FRAGMENT_COUNT), "2");
        // Verify first flow file's contents
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE ID > 2 AND ID <= 5 ORDER BY ID FETCH NEXT 2 ROWS ONLY", query);
        resultSet = stmt.executeQuery(query);
        // Should be two records
        Assert.assertTrue(resultSet.next());
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        // Verify second flow file's contents
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(1);
        query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE ID > 2 AND ID <= 5 ORDER BY ID OFFSET 2 ROWS FETCH NEXT 2 ROWS ONLY", query);
        resultSet = stmt.executeQuery(query);
        // Should be one record
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        runner.clearTransferState();
        // Add a new row with a higher ID and run, one flow file will be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (6, 'Mr. NiFi', 1.0, '2012-01-01 03:23:34.234')");
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE ID > 5 AND ID <= 6 ORDER BY ID FETCH NEXT 2 ROWS ONLY", query);
        resultSet = stmt.executeQuery(query);
        // Should be one record
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        runner.clearTransferState();
        // Set name as the max value column name (and clear the state), all rows should be returned since the max value for name has not been set
        runner.getStateManager().clear(CLUSTER);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "name");
        runner.setProperty(COLUMN_NAMES, "id, name, scale, created_on");
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 4);// 7 records with partition size 2 means 4 generated FlowFiles

        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        Assert.assertEquals("SELECT id, name, scale, created_on FROM TEST_QUERY_DB_TABLE WHERE name <= 'Mr. NiFi' ORDER BY name FETCH NEXT 2 ROWS ONLY", new String(flowFile.toByteArray()));
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(1);
        Assert.assertEquals("SELECT id, name, scale, created_on FROM TEST_QUERY_DB_TABLE WHERE name <= 'Mr. NiFi' ORDER BY name OFFSET 2 ROWS FETCH NEXT 2 ROWS ONLY", new String(flowFile.toByteArray()));
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(2);
        Assert.assertEquals("SELECT id, name, scale, created_on FROM TEST_QUERY_DB_TABLE WHERE name <= 'Mr. NiFi' ORDER BY name OFFSET 4 ROWS FETCH NEXT 2 ROWS ONLY", new String(flowFile.toByteArray()));
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(3);
        Assert.assertEquals("SELECT id, name, scale, created_on FROM TEST_QUERY_DB_TABLE WHERE name <= 'Mr. NiFi' ORDER BY name OFFSET 6 ROWS FETCH NEXT 2 ROWS ONLY", new String(flowFile.toByteArray()));
        Assert.assertEquals("TEST_QUERY_DB_TABLE", flowFile.getAttribute("generatetablefetch.tableName"));
        Assert.assertEquals("id, name, scale, created_on", flowFile.getAttribute("generatetablefetch.columnNames"));
        Assert.assertEquals("name <= 'Mr. NiFi'", flowFile.getAttribute("generatetablefetch.whereClause"));
        Assert.assertEquals("name", flowFile.getAttribute("generatetablefetch.maxColumnNames"));
        Assert.assertEquals("2", flowFile.getAttribute("generatetablefetch.limit"));
        Assert.assertEquals("6", flowFile.getAttribute("generatetablefetch.offset"));
        runner.clearTransferState();
    }

    @Test
    public void testAddedRowsTwoTables() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE2");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, name varchar(100), scale float, created_on timestamp, bignum bigint default 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (0, 'Joe Smith', 1.0, '1962-09-23 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (1, 'Carrie Jones', 5.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (2, NULL, 2.0, '2010-01-01 00:00:00')");
        runner.setProperty(TABLE_NAME, "TEST_QUERY_DB_TABLE");
        runner.setIncomingConnection(false);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "ID");
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        String query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE ID <= 2 ORDER BY ID FETCH NEXT 10000 ROWS ONLY", query);
        ResultSet resultSet = stmt.executeQuery(query);
        // Should be three records
        Assert.assertTrue(resultSet.next());
        Assert.assertTrue(resultSet.next());
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        runner.clearTransferState();
        // Run again, this time no flowfiles/rows should be transferred
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 0);
        runner.clearTransferState();
        // Create and populate a new table and re-run
        stmt.execute("create table TEST_QUERY_DB_TABLE2 (id integer not null, name varchar(100), scale float, created_on timestamp, bignum bigint default 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE2 (id, name, scale, created_on) VALUES (0, 'Joe Smith', 1.0, '1962-09-23 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE2 (id, name, scale, created_on) VALUES (1, 'Carrie Jones', 5.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE2 (id, name, scale, created_on) VALUES (2, NULL, 2.0, '2010-01-01 00:00:00')");
        runner.setProperty(TABLE_NAME, "TEST_QUERY_DB_TABLE2");
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE2 WHERE ID <= 2 ORDER BY ID FETCH NEXT 10000 ROWS ONLY", query);
        resultSet = stmt.executeQuery(query);
        // Should be three records
        Assert.assertTrue(resultSet.next());
        Assert.assertTrue(resultSet.next());
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        runner.clearTransferState();
        // Add 3 new rows with a higher ID and run with a partition size of 2. Two flow files should be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE2 (id, name, scale, created_on) VALUES (3, 'Mary West', 15.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE2 (id, name, scale, created_on) VALUES (4, 'Marty Johnson', 15.0, '2011-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE2 (id, name, scale, created_on) VALUES (5, 'Marty Johnson', 15.0, '2011-01-01 03:23:34.234')");
        runner.setProperty(PARTITION_SIZE, "2");
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 2);
        // Verify first flow file's contents
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE2 WHERE ID > 2 AND ID <= 5 ORDER BY ID FETCH NEXT 2 ROWS ONLY", query);
        resultSet = stmt.executeQuery(query);
        // Should be two records
        Assert.assertTrue(resultSet.next());
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        // Verify second flow file's contents
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(1);
        query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE2 WHERE ID > 2 AND ID <= 5 ORDER BY ID OFFSET 2 ROWS FETCH NEXT 2 ROWS ONLY", query);
        resultSet = stmt.executeQuery(query);
        // Should be one record
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        runner.clearTransferState();
    }

    @Test
    public void testAddedRowsRightBounded() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, name varchar(100), scale float, created_on timestamp, bignum bigint default 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (0, 'Joe Smith', 1.0, '1962-09-23 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (1, 'Carrie Jones', 5.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (2, NULL, 2.0, '2010-01-01 00:00:00')");
        runner.setProperty(TABLE_NAME, "TEST_QUERY_DB_TABLE");
        runner.setIncomingConnection(false);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "ID");
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        String query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE ID <= 2 ORDER BY ID FETCH NEXT 10000 ROWS ONLY", query);
        ResultSet resultSet = stmt.executeQuery(query);
        // Should be three records
        Assert.assertTrue(resultSet.next());
        Assert.assertTrue(resultSet.next());
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        runner.clearTransferState();
        // Run again, this time no flowfiles/rows should be transferred
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 0);
        runner.clearTransferState();
        // Add 3 new rows with a higher ID and run with a partition size of 2. Two flow files should be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (3, 'Mary West', 15.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (4, 'Marty Johnson', 15.0, '2011-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (5, 'Marty Johnson', 15.0, '2011-01-01 03:23:34.234')");
        runner.setProperty(PARTITION_SIZE, "2");
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 2);
        // Verify first flow file's contents
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE ID > 2 AND ID <= 5 ORDER BY ID FETCH NEXT 2 ROWS ONLY", query);
        resultSet = stmt.executeQuery(query);
        // Should be two records
        Assert.assertTrue(resultSet.next());
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        // Verify second flow file's contents
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(1);
        query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE ID > 2 AND ID <= 5 ORDER BY ID OFFSET 2 ROWS FETCH NEXT 2 ROWS ONLY", query);
        resultSet = stmt.executeQuery(query);
        // Should be one record
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        runner.clearTransferState();
        // Add a new row with a higher ID and run, one flow file will be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (6, 'Mr. NiFi', 1.0, '2012-01-01 03:23:34.234')");
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE ID > 5 AND ID <= 6 ORDER BY ID FETCH NEXT 2 ROWS ONLY", query);
        resultSet = stmt.executeQuery(query);
        // Should be one record
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        runner.clearTransferState();
        // Set name as the max value column name (and clear the state), all rows should be returned since the max value for name has not been set
        runner.getStateManager().clear(CLUSTER);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "name");
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 4);// 7 records with partition size 2 means 4 generated FlowFiles

        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE name <= 'Mr. NiFi' ORDER BY name FETCH NEXT 2 ROWS ONLY", new String(flowFile.toByteArray()));
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(1);
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE name <= 'Mr. NiFi' ORDER BY name OFFSET 2 ROWS FETCH NEXT 2 ROWS ONLY", new String(flowFile.toByteArray()));
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(2);
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE name <= 'Mr. NiFi' ORDER BY name OFFSET 4 ROWS FETCH NEXT 2 ROWS ONLY", new String(flowFile.toByteArray()));
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(3);
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE name <= 'Mr. NiFi' ORDER BY name OFFSET 6 ROWS FETCH NEXT 2 ROWS ONLY", new String(flowFile.toByteArray()));
        runner.clearTransferState();
    }

    @Test
    public void testAddedRowsTimestampRightBounded() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, name varchar(100), scale float, created_on timestamp, bignum bigint default 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (0, 'Joe Smith', 1.0, '1962-09-23 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (1, 'Carrie Jones', 5.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (2, NULL, 2.0, '2010-01-01 00:00:00')");
        runner.setProperty(TABLE_NAME, "TEST_QUERY_DB_TABLE");
        runner.setIncomingConnection(false);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "created_on");
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        String query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE created_on <= '2010-01-01 00:00:00.0' ORDER BY created_on FETCH NEXT 10000 ROWS ONLY", query);
        ResultSet resultSet = stmt.executeQuery(query);
        // Should be three records
        Assert.assertTrue(resultSet.next());
        Assert.assertTrue(resultSet.next());
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        runner.clearTransferState();
        // Run again, this time no flowfiles/rows should be transferred
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 0);
        runner.clearTransferState();
        // Add 5 new rows, 3 with higher timestamps, 2 with a lower timestamp.
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (3, 'Mary West', 15.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (4, 'Mary West', 15.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (5, 'Marty Johnson', 15.0, '2011-01-01 02:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (6, 'Marty Johnson', 15.0, '2011-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (7, 'James Johnson', 16.0, '2011-01-01 04:23:34.236')");
        runner.setProperty(PARTITION_SIZE, "2");
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 2);
        // Verify first flow file's contents
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        query = new String(flowFile.toByteArray());
        Assert.assertEquals(("SELECT * FROM TEST_QUERY_DB_TABLE WHERE created_on > '2010-01-01 00:00:00.0' AND " + "created_on <= '2011-01-01 04:23:34.236' ORDER BY created_on FETCH NEXT 2 ROWS ONLY"), query);
        resultSet = stmt.executeQuery(query);
        // Should be two records
        Assert.assertTrue(resultSet.next());
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        // Verify second flow file's contents
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(1);
        query = new String(flowFile.toByteArray());
        Assert.assertEquals(("SELECT * FROM TEST_QUERY_DB_TABLE WHERE created_on > '2010-01-01 00:00:00.0' AND " + "created_on <= '2011-01-01 04:23:34.236' ORDER BY created_on OFFSET 2 ROWS FETCH NEXT 2 ROWS ONLY"), query);
        resultSet = stmt.executeQuery(query);
        // Should be one record
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        runner.clearTransferState();
        // Add a new row with a higher created_on and run, one flow file will be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (8, 'Mr. NiFi', 1.0, '2012-01-01 03:23:34.234')");
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE created_on > '2011-01-01 04:23:34.236' AND created_on <= '2012-01-01 03:23:34.234' ORDER BY created_on FETCH NEXT 2 ROWS ONLY", query);
        resultSet = stmt.executeQuery(query);
        // Should be one record
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        runner.clearTransferState();
    }

    @Test
    public void testOnePartition() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, bucket integer not null)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (0, 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (1, 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (2, 0)");
        runner.setProperty(TABLE_NAME, "TEST_QUERY_DB_TABLE");
        runner.setIncomingConnection(false);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "ID");
        // Set partition size to 0 so we can see that the flow file gets all rows
        runner.setProperty(PARTITION_SIZE, "0");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE ID <= 2");
        flowFile.assertAttributeExists("generatetablefetch.limit");
        flowFile.assertAttributeEquals("generatetablefetch.limit", null);
        runner.clearTransferState();
    }

    @Test
    public void testFlowFileGeneratedOnZeroResults() throws SQLException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, bucket integer not null)");
        runner.setProperty(TABLE_NAME, "TEST_QUERY_DB_TABLE");
        runner.setIncomingConnection(false);
        runner.setProperty(COLUMN_NAMES, "ID,BUCKET");
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "ID");
        // Set partition size to 0 so we can see that the flow file gets all rows
        runner.setProperty(PARTITION_SIZE, "1");
        runner.setProperty(OUTPUT_EMPTY_FLOWFILE_ON_ZERO_RESULTS, "false");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        runner.clearTransferState();
        runner.setProperty(OUTPUT_EMPTY_FLOWFILE_ON_ZERO_RESULTS, "true");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals("TEST_QUERY_DB_TABLE", flowFile.getAttribute("generatetablefetch.tableName"));
        Assert.assertEquals("ID,BUCKET", flowFile.getAttribute("generatetablefetch.columnNames"));
        Assert.assertEquals("1=1", flowFile.getAttribute("generatetablefetch.whereClause"));
        Assert.assertEquals("ID", flowFile.getAttribute("generatetablefetch.maxColumnNames"));
        Assert.assertNull(flowFile.getAttribute("generatetablefetch.limit"));
        Assert.assertNull(flowFile.getAttribute("generatetablefetch.offset"));
        Assert.assertEquals("0", flowFile.getAttribute("fragment.index"));
        Assert.assertEquals("0", flowFile.getAttribute("fragment.count"));
    }

    @Test
    public void testMultiplePartitions() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, bucket integer not null)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (0, 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (1, 0)");
        runner.setProperty(TABLE_NAME, "TEST_QUERY_DB_TABLE");
        runner.setIncomingConnection(false);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "ID, BUCKET");
        // Set partition size to 1 so we can compare flow files to records
        runner.setProperty(PARTITION_SIZE, "1");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        runner.clearTransferState();
        // Add a new row in the same bucket
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (2, 0)");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        runner.clearTransferState();
        // Add a new row in a new bucket
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (3, 1)");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        runner.clearTransferState();
        // Add a new row in an old bucket, it should not be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (4, 0)");
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 0);
        // Add a new row in the second bucket, only the new row should be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (5, 1)");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        runner.clearTransferState();
    }

    @Test
    public void testMultiplePartitionsIncomingFlowFiles() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE1");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE1 (id integer not null, bucket integer not null)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE1 (id, bucket) VALUES (0, 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE1 (id, bucket) VALUES (1, 0)");
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE2");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE2 (id integer not null, bucket integer not null)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE2 (id, bucket) VALUES (0, 0)");
        runner.setProperty(TABLE_NAME, "${tableName}");
        runner.setIncomingConnection(true);
        runner.setProperty(PARTITION_SIZE, "${partSize}");
        runner.enqueue("".getBytes(), new HashMap<String, String>() {
            {
                put("tableName", "TEST_QUERY_DB_TABLE1");
                put("partSize", "1");
            }
        });
        runner.enqueue("".getBytes(), new HashMap<String, String>() {
            {
                put("tableName", "TEST_QUERY_DB_TABLE2");
                put("partSize", "2");
            }
        });
        // The table does not exist, expect the original flow file to be routed to failure
        runner.enqueue("".getBytes(), new HashMap<String, String>() {
            {
                put("tableName", "TEST_QUERY_DB_TABLE3");
                put("partSize", "1");
            }
        });
        runner.run(3);
        runner.assertTransferCount(AbstractDatabaseFetchProcessor.REL_SUCCESS, 3);
        // Two records from table 1
        Assert.assertEquals(runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).stream().filter(( ff) -> "TEST_QUERY_DB_TABLE1".equals(ff.getAttribute("tableName"))).count(), 2);
        // One record from table 2
        Assert.assertEquals(runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).stream().filter(( ff) -> "TEST_QUERY_DB_TABLE2".equals(ff.getAttribute("tableName"))).count(), 1);
        // Table 3 doesn't exist, should be routed to failure
        runner.assertTransferCount(REL_FAILURE, 1);
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE1");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE2");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
    }

    @Test
    public void testBackwardsCompatibilityStateKeyStaticTableDynamicMaxValues() throws Exception {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, bucket integer not null)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (0, 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (1, 0)");
        runner.setProperty(TABLE_NAME, "TEST_QUERY_DB_TABLE");
        runner.setIncomingConnection(true);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "${maxValueCol}");
        runner.enqueue("".getBytes(), new HashMap<String, String>() {
            {
                put("maxValueCol", "id");
            }
        });
        // Pre-populate the state with a key for column name (not fully-qualified)
        StateManager stateManager = runner.getStateManager();
        stateManager.setState(new HashMap<String, String>() {
            {
                put("id", "0");
            }
        }, CLUSTER);
        // Pre-populate the column type map with an entry for id (not fully-qualified)
        processor.columnTypeMap.put("id", 4);
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE id > 0 AND id <= 1 ORDER BY id FETCH NEXT 10000 ROWS ONLY", new String(flowFile.toByteArray()));
    }

    @Test
    public void testBackwardsCompatibilityStateKeyDynamicTableDynamicMaxValues() throws Exception {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, bucket integer not null)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (0, 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (1, 0)");
        runner.setProperty(TABLE_NAME, "${tableName}");
        runner.setIncomingConnection(true);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "${maxValueCol}");
        runner.enqueue("".getBytes(), new HashMap<String, String>() {
            {
                put("tableName", "TEST_QUERY_DB_TABLE");
                put("maxValueCol", "id");
            }
        });
        // Pre-populate the state with a key for column name (not fully-qualified)
        StateManager stateManager = runner.getStateManager();
        stateManager.setState(new HashMap<String, String>() {
            {
                put("id", "0");
            }
        }, CLUSTER);
        // Pre-populate the column type map with an entry for id (not fully-qualified)
        processor.columnTypeMap.put("id", 4);
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        // Note there is no WHERE clause here. Because we are using dynamic tables, the old state key/value is not retrieved
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE id <= 1 ORDER BY id FETCH NEXT 10000 ROWS ONLY", new String(flowFile.toByteArray()));
        Assert.assertEquals("TEST_QUERY_DB_TABLE", flowFile.getAttribute("generatetablefetch.tableName"));
        Assert.assertEquals(null, flowFile.getAttribute("generatetablefetch.columnNames"));
        Assert.assertEquals("id <= 1", flowFile.getAttribute("generatetablefetch.whereClause"));
        Assert.assertEquals("id", flowFile.getAttribute("generatetablefetch.maxColumnNames"));
        Assert.assertEquals("10000", flowFile.getAttribute("generatetablefetch.limit"));
        Assert.assertEquals("0", flowFile.getAttribute("generatetablefetch.offset"));
        runner.clearTransferState();
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (2, 0)");
        runner.enqueue("".getBytes(), new HashMap<String, String>() {
            {
                put("tableName", "TEST_QUERY_DB_TABLE");
                put("maxValueCol", "id");
            }
        });
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE id > 1 AND id <= 2 ORDER BY id FETCH NEXT 10000 ROWS ONLY", new String(flowFile.toByteArray()));
        Assert.assertEquals("TEST_QUERY_DB_TABLE", flowFile.getAttribute("generatetablefetch.tableName"));
        Assert.assertEquals(null, flowFile.getAttribute("generatetablefetch.columnNames"));
        Assert.assertEquals("id > 1 AND id <= 2", flowFile.getAttribute("generatetablefetch.whereClause"));
        Assert.assertEquals("id", flowFile.getAttribute("generatetablefetch.maxColumnNames"));
        Assert.assertEquals("10000", flowFile.getAttribute("generatetablefetch.limit"));
        Assert.assertEquals("0", flowFile.getAttribute("generatetablefetch.offset"));
    }

    @Test
    public void testBackwardsCompatibilityStateKeyDynamicTableStaticMaxValues() throws Exception {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, bucket integer not null)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (0, 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (1, 0)");
        runner.setProperty(TABLE_NAME, "${tableName}");
        runner.setIncomingConnection(true);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "id");
        runner.enqueue("".getBytes(), new HashMap<String, String>() {
            {
                put("tableName", "TEST_QUERY_DB_TABLE");
            }
        });
        // Pre-populate the state with a key for column name (not fully-qualified)
        StateManager stateManager = runner.getStateManager();
        stateManager.setState(new HashMap<String, String>() {
            {
                put("id", "0");
            }
        }, CLUSTER);
        // Pre-populate the column type map with an entry for id (not fully-qualified)
        processor.columnTypeMap.put("id", 4);
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        // Note there is no WHERE clause here. Because we are using dynamic tables, the old state key/value is not retrieved
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE id <= 1 ORDER BY id FETCH NEXT 10000 ROWS ONLY", new String(flowFile.toByteArray()));
        runner.clearTransferState();
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (2, 0)");
        runner.enqueue("".getBytes(), new HashMap<String, String>() {
            {
                put("tableName", "TEST_QUERY_DB_TABLE");
                put("maxValueCol", "id");
            }
        });
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE id > 1 AND id <= 2 ORDER BY id FETCH NEXT 10000 ROWS ONLY", new String(flowFile.toByteArray()));
    }

    @Test
    public void testBackwardsCompatibilityStateKeyVariableRegistry() throws Exception {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, bucket integer not null)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (0, 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (1, 0)");
        runner.setProperty(TABLE_NAME, "${tableName}");
        runner.setIncomingConnection(false);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "${maxValueCol}");
        runner.setVariable("tableName", "TEST_QUERY_DB_TABLE");
        runner.setVariable("maxValueCol", "id");
        // Pre-populate the state with a key for column name (not fully-qualified)
        StateManager stateManager = runner.getStateManager();
        stateManager.setState(new HashMap<String, String>() {
            {
                put("id", "0");
            }
        }, CLUSTER);
        // Pre-populate the column type map with an entry for id (not fully-qualified)
        processor.columnTypeMap.put("id", 4);
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        // Note there is no WHERE clause here. Because we are using dynamic tables (i.e. Expression Language,
        // even when not referring to flow file attributes), the old state key/value is not retrieved
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE id <= 1 ORDER BY id FETCH NEXT 10000 ROWS ONLY", new String(flowFile.toByteArray()));
    }

    @Test
    public void testRidiculousRowCount() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        long rowCount = (Long.parseLong(Integer.toString(Integer.MAX_VALUE))) + 100;
        int partitionSize = 1000000;
        int expectedFileCount = ((int) (rowCount / partitionSize)) + 1;
        Connection conn = Mockito.mock(Connection.class);
        Mockito.when(dbcp.getConnection()).thenReturn(conn);
        Statement st = Mockito.mock(Statement.class);
        Mockito.when(conn.createStatement()).thenReturn(st);
        Mockito.doNothing().when(st).close();
        ResultSet rs = Mockito.mock(ResultSet.class);
        Mockito.when(st.executeQuery(ArgumentMatchers.anyString())).thenReturn(rs);
        Mockito.when(rs.next()).thenReturn(true);
        Mockito.when(rs.getInt(1)).thenReturn(((int) (rowCount)));
        Mockito.when(rs.getLong(1)).thenReturn(rowCount);
        final ResultSetMetaData resultSetMetaData = Mockito.mock(ResultSetMetaData.class);
        Mockito.when(rs.getMetaData()).thenReturn(resultSetMetaData);
        Mockito.when(resultSetMetaData.getColumnCount()).thenReturn(2);
        Mockito.when(resultSetMetaData.getTableName(1)).thenReturn("");
        Mockito.when(resultSetMetaData.getColumnType(1)).thenReturn(Types.INTEGER);
        Mockito.when(resultSetMetaData.getColumnName(1)).thenReturn("COUNT");
        Mockito.when(resultSetMetaData.getColumnType(2)).thenReturn(Types.INTEGER);
        Mockito.when(resultSetMetaData.getColumnName(2)).thenReturn("ID");
        Mockito.when(rs.getInt(2)).thenReturn(1000);
        runner.setProperty(TABLE_NAME, "TEST_QUERY_DB_TABLE");
        runner.setIncomingConnection(false);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "ID");
        runner.setProperty(PARTITION_SIZE, Integer.toString(partitionSize));
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, expectedFileCount);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        String query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE 1=1 ORDER BY ID FETCH NEXT 1000000 ROWS ONLY", query);
        runner.clearTransferState();
    }

    @Test
    public void testInitialMaxValue() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, name varchar(100), scale float, created_on timestamp, bignum bigint default 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (0, 'Joe Smith', 1.0, '1962-09-23 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (1, 'Carrie Jones', 5.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (2, NULL, 2.0, '2010-01-01 00:00:00')");
        runner.setProperty(TABLE_NAME, "TEST_QUERY_DB_TABLE");
        runner.setIncomingConnection(false);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "ID");
        runner.setProperty("initial.maxvalue.ID", "1");
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        String query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE ID > 1 AND ID <= 2 ORDER BY ID FETCH NEXT 10000 ROWS ONLY", query);
        ResultSet resultSet = stmt.executeQuery(query);
        // Should be one record (the initial max value skips the first two)
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        runner.clearTransferState();
        // Run again, this time no flowfiles/rows should be transferred
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 0);
        runner.clearTransferState();
        // Add 3 new rows with a higher ID and run with a partition size of 2. Two flow files should be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (3, 'Mary West', 15.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (4, 'Marty Johnson', 15.0, '2011-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (5, 'Marty Johnson', 15.0, '2011-01-01 03:23:34.234')");
        runner.setProperty(PARTITION_SIZE, "2");
        runner.setProperty("initial.maxvalue.ID", "5");// This should have no effect as there is a max value in the processor state

        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 2);
        // Verify first flow file's contents
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE ID > 2 AND ID <= 5 ORDER BY ID FETCH NEXT 2 ROWS ONLY", query);
        resultSet = stmt.executeQuery(query);
        // Should be two records
        Assert.assertTrue(resultSet.next());
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        // Verify second flow file's contents
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(1);
        query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE ID > 2 AND ID <= 5 ORDER BY ID OFFSET 2 ROWS FETCH NEXT 2 ROWS ONLY", query);
        resultSet = stmt.executeQuery(query);
        // Should be one record
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        runner.clearTransferState();
    }

    @Test
    public void testInitialMaxValueWithEL() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, name varchar(100), scale float, created_on timestamp, bignum bigint default 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (0, 'Joe Smith', 1.0, '1962-09-23 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (1, 'Carrie Jones', 5.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (2, NULL, 2.0, '2010-01-01 00:00:00')");
        runner.setProperty(TABLE_NAME, "TEST_QUERY_DB_TABLE");
        runner.setIncomingConnection(false);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "ID");
        runner.setProperty("initial.maxvalue.ID", "${maxval.id}");
        runner.setVariable("maxval.id", "1");
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        String query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE ID > 1 AND ID <= 2 ORDER BY ID FETCH NEXT 10000 ROWS ONLY", query);
        ResultSet resultSet = stmt.executeQuery(query);
        // Should be one record (the initial max value skips the first two)
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        runner.clearTransferState();
        // Run again, this time no flowfiles/rows should be transferred
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 0);
        runner.clearTransferState();
    }

    @Test
    public void testInitialMaxValueWithELAndIncoming() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, name varchar(100), scale float, created_on timestamp, bignum bigint default 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (0, 'Joe Smith', 1.0, '1962-09-23 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (1, 'Carrie Jones', 5.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (2, NULL, 2.0, '2010-01-01 00:00:00')");
        runner.setProperty(TABLE_NAME, "TEST_QUERY_DB_TABLE");
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "ID");
        runner.setProperty("initial.maxvalue.ID", "${maxval.id}");
        Map<String, String> attrs = new HashMap<String, String>() {
            {
                put("maxval.id", "1");
            }
        };
        runner.setIncomingConnection(true);
        runner.enqueue(new byte[0], attrs);
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        String query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE ID > 1 AND ID <= 2 ORDER BY ID FETCH NEXT 10000 ROWS ONLY", query);
        ResultSet resultSet = stmt.executeQuery(query);
        // Should be one record (the initial max value skips the first two)
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        runner.clearTransferState();
        // Run again, this time no flowfiles/rows should be transferred
        runner.enqueue(new byte[0], attrs);
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 0);
        runner.clearTransferState();
    }

    @Test
    public void testInitialMaxValueWithELAndMultipleTables() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, name varchar(100), scale float, created_on timestamp, bignum bigint default 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (0, 'Joe Smith', 1.0, '1962-09-23 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (1, 'Carrie Jones', 5.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (2, NULL, 2.0, '2010-01-01 00:00:00')");
        runner.setProperty(TABLE_NAME, "${table.name}");
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "ID");
        runner.setProperty("initial.maxvalue.ID", "${maxval.id}");
        Map<String, String> attrs = new HashMap<String, String>() {
            {
                put("maxval.id", "1");
                put("table.name", "TEST_QUERY_DB_TABLE");
            }
        };
        runner.setIncomingConnection(true);
        runner.enqueue(new byte[0], attrs);
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        String query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE ID > 1 AND ID <= 2 ORDER BY ID FETCH NEXT 10000 ROWS ONLY", query);
        ResultSet resultSet = stmt.executeQuery(query);
        // Should be one record (the initial max value skips the first two)
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        runner.clearTransferState();
        // Run again, this time no flowfiles/rows should be transferred
        runner.enqueue(new byte[0], attrs);
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 0);
        runner.clearTransferState();
        // Initial Max Value for second table
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE2");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE2 (id integer not null, name varchar(100), scale float, created_on timestamp, bignum bigint default 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE2 (id, name, scale, created_on) VALUES (0, 'Joe Smith', 1.0, '1962-09-23 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE2 (id, name, scale, created_on) VALUES (1, 'Carrie Jones', 5.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE2 (id, name, scale, created_on) VALUES (2, NULL, 2.0, '2010-01-01 00:00:00')");
        attrs.put("table.name", "TEST_QUERY_DB_TABLE2");
        runner.enqueue(new byte[0], attrs);
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE2 WHERE ID > 1 AND ID <= 2 ORDER BY ID FETCH NEXT 10000 ROWS ONLY", query);
        resultSet = stmt.executeQuery(query);
        // Should be one record (the initial max value skips the first two)
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        runner.clearTransferState();
        // Run again, this time no flowfiles/rows should be transferred
        runner.enqueue(new byte[0], attrs);
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 0);
        runner.clearTransferState();
    }

    @Test
    public void testNoDuplicateWithRightBounded() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, name varchar(100), scale float, created_on timestamp, bignum bigint default 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (0, 'Joe Smith', 1.0, '1962-09-23 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (1, 'Carrie Jones', 5.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (2, NULL, 2.0, '2010-01-01 00:00:00')");
        runner.setProperty(TABLE_NAME, "TEST_QUERY_DB_TABLE");
        runner.setIncomingConnection(false);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "ID");
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        String query = new String(flowFile.toByteArray());
        // we now insert a row before the query issued by GFT is actually executed by, let's say, ExecuteSQL processor
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (3, 'Mary West', 15.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (4, 'Marty Johnson', 15.0, '2011-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (5, 'Marty Johnson', 15.0, '2011-01-01 03:23:34.234')");
        ResultSet resultSet = stmt.executeQuery(query);
        int numberRecordsFirstExecution = 0;// Should be three records

        while (resultSet.next()) {
            numberRecordsFirstExecution++;
        } 
        runner.clearTransferState();
        // Run again
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        query = new String(flowFile.toByteArray());
        resultSet = stmt.executeQuery(query);
        int numberRecordsSecondExecution = 0;// Should be three records

        while (resultSet.next()) {
            numberRecordsSecondExecution++;
        } 
        // will fail and will be equal to 9 if right-bounded parameter is set to false.
        Assert.assertEquals((numberRecordsFirstExecution + numberRecordsSecondExecution), 6);
        runner.clearTransferState();
    }

    @Test
    public void testAddedRowsWithCustomWhereClause() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, type varchar(20), name varchar(100), scale float, created_on timestamp, bignum bigint default 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on) VALUES (0, 'male', 'Joe Smith', 1.0, '1962-09-23 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on) VALUES (1, 'female', 'Carrie Jones', 5.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on) VALUES (2, NULL, NULL, 2.0, '2010-01-01 00:00:00')");
        runner.setProperty(TABLE_NAME, "TEST_QUERY_DB_TABLE");
        runner.setProperty(WHERE_CLAUSE, "type = 'male' OR type IS NULL");
        runner.setIncomingConnection(false);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "ID");
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        String query = new String(flowFile.toByteArray());
        Assert.assertEquals(("SELECT * FROM TEST_QUERY_DB_TABLE WHERE (type = 'male' OR type IS NULL)" + " AND ID <= 2 ORDER BY ID FETCH NEXT 10000 ROWS ONLY"), query);
        ResultSet resultSet = stmt.executeQuery(query);
        // Should be two records
        Assert.assertTrue(resultSet.next());
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        runner.clearTransferState();
        // Run again, this time no flowfiles/rows should be transferred
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 0);
        runner.clearTransferState();
        // Add 3 new rows with a higher ID and run with a partition size of 2. Two flow files should be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on) VALUES (3, 'female', 'Mary West', 15.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on) VALUES (4, 'male', 'Marty Johnson', 15.0, '2011-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on) VALUES (5, 'male', 'Marty Johnson', 15.0, '2011-01-01 03:23:34.234')");
        runner.setProperty(PARTITION_SIZE, "1");
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 2);
        // Verify first flow file's contents
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        query = new String(flowFile.toByteArray());
        Assert.assertEquals(("SELECT * FROM TEST_QUERY_DB_TABLE WHERE ID > 2 AND (type = 'male' OR type IS NULL)" + " AND ID <= 5 ORDER BY ID FETCH NEXT 1 ROWS ONLY"), query);
        resultSet = stmt.executeQuery(query);
        // Should be one record
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        // Verify second flow file's contents
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(1);
        query = new String(flowFile.toByteArray());
        Assert.assertEquals(("SELECT * FROM TEST_QUERY_DB_TABLE WHERE ID > 2 AND (type = 'male' OR type IS NULL)" + " AND ID <= 5 ORDER BY ID OFFSET 1 ROWS FETCH NEXT 1 ROWS ONLY"), query);
        resultSet = stmt.executeQuery(query);
        // Should be one record
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        runner.clearTransferState();
        // Add a new row with a higher ID and run, one flow file will be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on) VALUES (6, 'male', 'Mr. NiFi', 1.0, '2012-01-01 03:23:34.234')");
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        query = new String(flowFile.toByteArray());
        Assert.assertEquals(("SELECT * FROM TEST_QUERY_DB_TABLE WHERE ID > 5 AND (type = 'male' OR type IS NULL)" + " AND ID <= 6 ORDER BY ID FETCH NEXT 1 ROWS ONLY"), query);
        resultSet = stmt.executeQuery(query);
        // Should be one record
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        runner.clearTransferState();
        // Set name as the max value column name (and clear the state), all rows should be returned since the max value for name has not been set
        runner.getStateManager().clear(CLUSTER);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "name");
        runner.setProperty(COLUMN_NAMES, "id, type, name, scale, created_on");
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 5);// 5 records with partition size 1 means 5 generated FlowFiles

        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        Assert.assertEquals(("SELECT id, type, name, scale, created_on FROM TEST_QUERY_DB_TABLE WHERE (type = 'male' OR type IS NULL)" + " AND name <= 'Mr. NiFi' ORDER BY name FETCH NEXT 1 ROWS ONLY"), new String(flowFile.toByteArray()));
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(1);
        Assert.assertEquals(("SELECT id, type, name, scale, created_on FROM TEST_QUERY_DB_TABLE WHERE (type = 'male' OR type IS NULL)" + " AND name <= 'Mr. NiFi' ORDER BY name OFFSET 1 ROWS FETCH NEXT 1 ROWS ONLY"), new String(flowFile.toByteArray()));
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(2);
        Assert.assertEquals(("SELECT id, type, name, scale, created_on FROM TEST_QUERY_DB_TABLE WHERE (type = 'male' OR type IS NULL)" + " AND name <= 'Mr. NiFi' ORDER BY name OFFSET 2 ROWS FETCH NEXT 1 ROWS ONLY"), new String(flowFile.toByteArray()));
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(3);
        Assert.assertEquals(("SELECT id, type, name, scale, created_on FROM TEST_QUERY_DB_TABLE WHERE (type = 'male' OR type IS NULL)" + " AND name <= 'Mr. NiFi' ORDER BY name OFFSET 3 ROWS FETCH NEXT 1 ROWS ONLY"), new String(flowFile.toByteArray()));
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(4);
        Assert.assertEquals(("SELECT id, type, name, scale, created_on FROM TEST_QUERY_DB_TABLE WHERE (type = 'male' OR type IS NULL)" + " AND name <= 'Mr. NiFi' ORDER BY name OFFSET 4 ROWS FETCH NEXT 1 ROWS ONLY"), new String(flowFile.toByteArray()));
        Assert.assertEquals("TEST_QUERY_DB_TABLE", flowFile.getAttribute("generatetablefetch.tableName"));
        Assert.assertEquals("id, type, name, scale, created_on", flowFile.getAttribute("generatetablefetch.columnNames"));
        Assert.assertEquals("(type = 'male' OR type IS NULL) AND name <= 'Mr. NiFi'", flowFile.getAttribute("generatetablefetch.whereClause"));
        Assert.assertEquals("name", flowFile.getAttribute("generatetablefetch.maxColumnNames"));
        Assert.assertEquals("1", flowFile.getAttribute("generatetablefetch.limit"));
        Assert.assertEquals("4", flowFile.getAttribute("generatetablefetch.offset"));
        runner.clearTransferState();
    }

    @Test
    public void testColumnTypeMissing() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // Load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, bucket integer not null)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (0, 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (1, 0)");
        runner.setProperty(TABLE_NAME, "TEST_QUERY_DB_TABLE");
        runner.setIncomingConnection(true);
        runner.setProperty(TABLE_NAME, "${tableName}");
        runner.setIncomingConnection(true);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "${maxValueCol}");
        runner.enqueue("".getBytes(), new HashMap<String, String>() {
            {
                put("tableName", "TEST_QUERY_DB_TABLE");
                put("maxValueCol", "id");
            }
        });
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        String query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE id <= 1 ORDER BY id FETCH NEXT 10000 ROWS ONLY", query);
        runner.clearTransferState();
        // Clear columnTypeMap to simulate it's clean after instance reboot
        processor.columnTypeMap.clear();
        // Insert new records
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (2, 0)");
        // Re-launch FlowFile to se if re-cache column type works
        runner.enqueue("".getBytes(), new HashMap<String, String>() {
            {
                put("tableName", "TEST_QUERY_DB_TABLE");
                put("maxValueCol", "id");
            }
        });
        // It should re-cache column type
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE id > 1 AND id <= 2 ORDER BY id FETCH NEXT 10000 ROWS ONLY", query);
        runner.clearTransferState();
    }

    @Test
    public void testMultipleColumnTypeMissing() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // Load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
            stmt.execute("drop table TEST_QUERY_DB_TABLE_2");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        // Create multiple table to invoke processor state stored
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, bucket integer not null)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (1, 0)");
        stmt.execute("create table TEST_QUERY_DB_TABLE_2 (id integer not null, bucket integer not null)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE_2 (id, bucket) VALUES (1, 0)");
        runner.setProperty(TABLE_NAME, "${tableName}");
        runner.setIncomingConnection(true);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "${maxValueCol}");
        runner.enqueue("".getBytes(), new HashMap<String, String>() {
            {
                put("tableName", "TEST_QUERY_DB_TABLE");
                put("maxValueCol", "id");
            }
        });
        runner.enqueue("".getBytes(), new HashMap<String, String>() {
            {
                put("tableName", "TEST_QUERY_DB_TABLE_2");
                put("maxValueCol", "id");
            }
        });
        runner.run(2);
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 2);
        Assert.assertEquals(2, processor.columnTypeMap.size());
        runner.clearTransferState();
        // Remove one element from columnTypeMap to simulate it's re-cache partial state
        Map.Entry<String, Integer> entry = processor.columnTypeMap.entrySet().iterator().next();
        String key = entry.getKey();
        processor.columnTypeMap.remove(key);
        // Insert new records
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (2, 0)");
        // Re-launch FlowFile to se if re-cache column type works
        runner.enqueue("".getBytes(), new HashMap<String, String>() {
            {
                put("tableName", "TEST_QUERY_DB_TABLE");
                put("maxValueCol", "id");
            }
        });
        // It should re-cache column type
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 1);
        Assert.assertEquals(2, processor.columnTypeMap.size());
        runner.clearTransferState();
    }

    @Test
    public void testUseColumnValuesForPartitioning() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, name varchar(100), scale float, created_on timestamp, bignum bigint default 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (10, 'Joe Smith', 1.0, '1962-09-23 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (11, 'Carrie Jones', 5.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (12, NULL, 2.0, '2010-01-01 00:00:00')");
        runner.setProperty(TABLE_NAME, "TEST_QUERY_DB_TABLE");
        runner.setIncomingConnection(false);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "ID");
        runner.setProperty(COLUMN_FOR_VALUE_PARTITIONING, "ID");
        runner.setProperty(PARTITION_SIZE, "2");
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 2);
        // First flow file
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        String query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE ID <= 12 AND ID >= 10 AND ID < 12", query);
        ResultSet resultSet = stmt.executeQuery(query);
        // Should be two records
        Assert.assertTrue(resultSet.next());
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        // Second flow file
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(1);
        query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE ID <= 12 AND ID >= 12 AND ID < 14", query);
        resultSet = stmt.executeQuery(query);
        // Should be one record
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        runner.clearTransferState();
        // Run again, this time no flowfiles/rows should be transferred
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 0);
        runner.clearTransferState();
        // Add 3 new rows with a higher ID and run with a partition size of 2. Three flow files should be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (20, 'Mary West', 15.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (21, 'Marty Johnson', 15.0, '2011-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (24, 'Marty Johnson', 15.0, '2011-01-01 03:23:34.234')");
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 3);
        // Verify first flow file's contents
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE ID > 12 AND ID <= 24 AND ID >= 20 AND ID < 22", query);
        resultSet = stmt.executeQuery(query);
        // Should be two records
        Assert.assertTrue(resultSet.next());
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        // Verify second flow file's contents
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(1);
        query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE ID > 12 AND ID <= 24 AND ID >= 22 AND ID < 24", query);
        resultSet = stmt.executeQuery(query);
        // Should be no records
        Assert.assertFalse(resultSet.next());
        // Verify third flow file's contents
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(2);
        query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE ID > 12 AND ID <= 24 AND ID >= 24 AND ID < 26", query);
        resultSet = stmt.executeQuery(query);
        // Should be one record
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        runner.clearTransferState();
    }

    @Test
    public void testUseColumnValuesForPartitioningNoMaxValueColumn() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, name varchar(100), scale float, created_on timestamp, bignum bigint default 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (10, 'Joe Smith', 1.0, '1962-09-23 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (11, 'Carrie Jones', 5.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (12, NULL, 2.0, '2010-01-01 00:00:00')");
        runner.setProperty(TABLE_NAME, "TEST_QUERY_DB_TABLE");
        runner.setIncomingConnection(false);
        runner.setProperty(COLUMN_FOR_VALUE_PARTITIONING, "ID");
        runner.setProperty(PARTITION_SIZE, "2");
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 2);
        // First flow file
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(0);
        String query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE 1=1 AND ID >= 10 AND ID < 12", query);
        ResultSet resultSet = stmt.executeQuery(query);
        // Should be two records
        Assert.assertTrue(resultSet.next());
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        // Second flow file
        flowFile = runner.getFlowFilesForRelationship(AbstractDatabaseFetchProcessor.REL_SUCCESS).get(1);
        query = new String(flowFile.toByteArray());
        Assert.assertEquals("SELECT * FROM TEST_QUERY_DB_TABLE WHERE ID <= 12 AND ID >= 12", query);
        resultSet = stmt.executeQuery(query);
        // Should be one record
        Assert.assertTrue(resultSet.next());
        Assert.assertFalse(resultSet.next());
        runner.clearTransferState();
        // Run again, the same flowfiles should be transferred as we have no maximum-value column
        runner.run();
        runner.assertAllFlowFilesTransferred(AbstractDatabaseFetchProcessor.REL_SUCCESS, 2);
        runner.clearTransferState();
    }

    /**
     * Simple implementation only for GenerateTableFetch processor testing.
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
                return DriverManager.getConnection((("jdbc:derby:" + (TestGenerateTableFetch.DB_LOCATION)) + ";create=true"));
            } catch (final Exception e) {
                throw new ProcessException(("getConnection failed: " + e));
            }
        }
    }
}

