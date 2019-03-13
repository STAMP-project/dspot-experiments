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


import QueryDatabaseTable.COLUMN_NAMES;
import QueryDatabaseTable.FETCH_SIZE;
import QueryDatabaseTable.MAX_FRAGMENTS;
import QueryDatabaseTable.MAX_ROWS_PER_FLOW_FILE;
import QueryDatabaseTable.MAX_VALUE_COLUMN_NAMES;
import QueryDatabaseTable.OUTPUT_BATCH_SIZE;
import QueryDatabaseTable.REL_SUCCESS;
import QueryDatabaseTable.RESULT_ROW_COUNT;
import QueryDatabaseTable.RESULT_TABLENAME;
import QueryDatabaseTable.SQL_QUERY;
import QueryDatabaseTable.TABLE_NAME;
import QueryDatabaseTable.WHERE_CLAUSE;
import QueryDatabaseTable.dbAdapters;
import Scope.CLUSTER;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.apache.avro.file.DataFileWriter;
import org.apache.nifi.annotation.behavior.Stateful;
import org.apache.nifi.components.state.Scope;
import org.apache.nifi.components.state.StateManager;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.dbcp.DBCPService;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processors.standard.db.DatabaseAdapter;
import org.apache.nifi.processors.standard.db.impl.GenericDatabaseAdapter;
import org.apache.nifi.processors.standard.db.impl.MSSQLDatabaseAdapter;
import org.apache.nifi.processors.standard.db.impl.MySQLDatabaseAdapter;
import org.apache.nifi.processors.standard.db.impl.OracleDatabaseAdapter;
import org.apache.nifi.processors.standard.db.impl.PhoenixDatabaseAdapter;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;

import static AbstractDatabaseFetchProcessor.NAMESPACE_DELIMITER;


/**
 * Unit tests for the QueryDatabaseTable processor
 */
public class QueryDatabaseTableTest {
    QueryDatabaseTableTest.MockQueryDatabaseTable processor;

    private TestRunner runner;

    private static final String DB_LOCATION = "target/db_qdt";

    private DatabaseAdapter dbAdapter;

    private HashMap<String, DatabaseAdapter> origDbAdapters;

    private static final String TABLE_NAME_KEY = "tableName";

    private static final String MAX_ROWS_KEY = "maxRows";

    @Test
    public void testGetQuery() throws Exception {
        String query = processor.getQuery(dbAdapter, "myTable", null, null, null, null);
        Assert.assertEquals("SELECT * FROM myTable", query);
        query = processor.getQuery(dbAdapter, "myTable", "col1,col2", null, null, null);
        Assert.assertEquals("SELECT col1,col2 FROM myTable", query);
        query = processor.getQuery(dbAdapter, "myTable", null, Collections.singletonList("id"), null, null);
        Assert.assertEquals("SELECT * FROM myTable", query);
        Map<String, String> maxValues = new HashMap<>();
        maxValues.put("id", "509");
        StateManager stateManager = runner.getStateManager();
        stateManager.setState(maxValues, CLUSTER);
        processor.putColumnType(processor.getStateKey("mytable", "id", dbAdapter), Types.INTEGER);
        query = processor.getQuery(dbAdapter, "myTable", null, Collections.singletonList("id"), null, stateManager.getState(CLUSTER).toMap());
        Assert.assertEquals("SELECT * FROM myTable WHERE id > 509", query);
        maxValues.put("date_created", "2016-03-07 12:34:56");
        stateManager.setState(maxValues, CLUSTER);
        processor.putColumnType(processor.getStateKey("mytable", "date_created", dbAdapter), Types.TIMESTAMP);
        query = processor.getQuery(dbAdapter, "myTable", null, Arrays.asList("id", "DATE_CREATED"), null, stateManager.getState(CLUSTER).toMap());
        Assert.assertEquals("SELECT * FROM myTable WHERE id > 509 AND DATE_CREATED >= '2016-03-07 12:34:56'", query);
        // Double quotes can be used to escape column and table names with most ANSI compatible database engines.
        maxValues.put("mytable@!@date-created", "2016-03-07 12:34:56");
        stateManager.setState(maxValues, CLUSTER);
        processor.putColumnType(processor.getStateKey("\"myTable\"", "\"DATE-CREATED\"", dbAdapter), Types.TIMESTAMP);
        query = processor.getQuery(dbAdapter, "\"myTable\"", null, Arrays.asList("id", "\"DATE-CREATED\""), null, stateManager.getState(CLUSTER).toMap());
        Assert.assertEquals("SELECT * FROM \"myTable\" WHERE id > 509 AND \"DATE-CREATED\" >= \'2016-03-07 12:34:56\'", query);
        // Back-ticks can be used to escape MySQL column and table names.
        dbAdapter = new MySQLDatabaseAdapter();
        processor.putColumnType(processor.getStateKey("`myTable`", "`DATE-CREATED`", dbAdapter), Types.TIMESTAMP);
        query = processor.getQuery(dbAdapter, "`myTable`", null, Arrays.asList("id", "`DATE-CREATED`"), null, stateManager.getState(CLUSTER).toMap());
        Assert.assertEquals("SELECT * FROM `myTable` WHERE id > 509 AND `DATE-CREATED` >= '2016-03-07 12:34:56'", query);
        // Square brackets can be used to escape Microsoft SQL Server column and table names.
        dbAdapter = new MSSQLDatabaseAdapter();
        processor.putColumnType(processor.getStateKey("[myTable]", "[DATE-CREATED]", dbAdapter), Types.TIMESTAMP);
        query = processor.getQuery(dbAdapter, "[myTable]", null, Arrays.asList("id", "[DATE-CREATED]"), null, stateManager.getState(CLUSTER).toMap());
        Assert.assertEquals("SELECT * FROM [myTable] WHERE id > 509 AND [DATE-CREATED] >= '2016-03-07 12:34:56'", query);
        // Test Oracle strategy
        dbAdapter = new OracleDatabaseAdapter();
        query = processor.getQuery(dbAdapter, "myTable", null, Arrays.asList("id", "DATE_CREATED"), "type = \"CUSTOMER\"", stateManager.getState(CLUSTER).toMap());
        Assert.assertEquals("SELECT * FROM myTable WHERE id > 509 AND DATE_CREATED >= timestamp \'2016-03-07 12:34:56\' AND (type = \"CUSTOMER\")", query);
        // Test time.
        processor.putColumnType((("mytable" + (NAMESPACE_DELIMITER)) + "time_created"), Types.TIME);
        maxValues.clear();
        maxValues.put("id", "509");
        maxValues.put("time_created", "12:34:57");
        maxValues.put("date_created", "2016-03-07 12:34:56");
        stateManager = runner.getStateManager();
        stateManager.clear(CLUSTER);
        stateManager.setState(maxValues, CLUSTER);
        query = processor.getQuery(dbAdapter, "myTable", null, Arrays.asList("id", "DATE_CREATED", "TIME_CREATED"), "type = \"CUSTOMER\"", stateManager.getState(CLUSTER).toMap());
        Assert.assertEquals("SELECT * FROM myTable WHERE id > 509 AND DATE_CREATED >= timestamp \'2016-03-07 12:34:56\' AND TIME_CREATED >= timestamp \'12:34:57\' AND (type = \"CUSTOMER\")", query);
        dbAdapter = new GenericDatabaseAdapter();
        query = processor.getQuery(dbAdapter, "myTable", null, Arrays.asList("id", "DATE_CREATED", "TIME_CREATED"), "type = \"CUSTOMER\"", stateManager.getState(CLUSTER).toMap());
        Assert.assertEquals("SELECT * FROM myTable WHERE id > 509 AND DATE_CREATED >= \'2016-03-07 12:34:56\' AND TIME_CREATED >= \'12:34:57\' AND (type = \"CUSTOMER\")", query);
    }

    @Test
    public void testGetQueryUsingPhoenixAdapter() throws Exception {
        Map<String, String> maxValues = new HashMap<>();
        StateManager stateManager = runner.getStateManager();
        processor.putColumnType((("mytable" + (NAMESPACE_DELIMITER)) + "id"), Types.INTEGER);
        processor.putColumnType((("mytable" + (NAMESPACE_DELIMITER)) + "time_created"), Types.TIME);
        processor.putColumnType((("mytable" + (NAMESPACE_DELIMITER)) + "date_created"), Types.TIMESTAMP);
        maxValues.put("id", "509");
        maxValues.put("time_created", "12:34:57");
        maxValues.put("date_created", "2016-03-07 12:34:56");
        stateManager.setState(maxValues, CLUSTER);
        dbAdapter = new PhoenixDatabaseAdapter();
        String query = processor.getQuery(dbAdapter, "myTable", null, Arrays.asList("id", "DATE_CREATED", "TIME_CREATED"), "type = \"CUSTOMER\"", stateManager.getState(CLUSTER).toMap());
        Assert.assertEquals("SELECT * FROM myTable WHERE id > 509 AND DATE_CREATED >= timestamp \'2016-03-07 12:34:56\' AND TIME_CREATED >= time \'12:34:57\' AND (type = \"CUSTOMER\")", query);
        // Cover the other path
        dbAdapter = new GenericDatabaseAdapter();
        query = processor.getQuery(dbAdapter, "myTable", null, Arrays.asList("id", "DATE_CREATED", "TIME_CREATED"), "type = \"CUSTOMER\"", stateManager.getState(CLUSTER).toMap());
        Assert.assertEquals("SELECT * FROM myTable WHERE id > 509 AND DATE_CREATED >= \'2016-03-07 12:34:56\' AND TIME_CREATED >= \'12:34:57\' AND (type = \"CUSTOMER\")", query);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetQueryNoTable() throws Exception {
        processor.getQuery(dbAdapter, null, null, null, null, null);
    }

    @Test
    public void testAddedRows() throws IOException, ClassNotFoundException, SQLException, InitializationException {
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
        runner.setProperty(MAX_ROWS_PER_FLOW_FILE, "2");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals("TEST_QUERY_DB_TABLE", flowFile.getAttribute(RESULT_TABLENAME));
        Assert.assertEquals(flowFile.getAttribute("maxvalue.id"), "2");
        InputStream in = new ByteArrayInputStream(flowFile.toByteArray());
        runner.setProperty(FETCH_SIZE, "2");
        Assert.assertEquals(2, getNumberOfRecordsFromStream(in));
        flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(1);
        Assert.assertEquals(flowFile.getAttribute("maxvalue.id"), "2");
        in = new ByteArrayInputStream(flowFile.toByteArray());
        Assert.assertEquals(1, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Run again, this time no flowfiles/rows should be transferred
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        runner.clearTransferState();
        // Remove Max Rows Per Flow File
        runner.setProperty(MAX_ROWS_PER_FLOW_FILE, "0");
        // Add a new row with a higher ID and run, one flowfile with one new row should be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (3, 'Mary West', 15.0, '2000-01-01 03:23:34.234')");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals(flowFile.getAttribute("maxvalue.id"), "3");
        in = new ByteArrayInputStream(flowFile.toByteArray());
        Assert.assertEquals(1, getNumberOfRecordsFromStream(in));
        // Sanity check - run again, this time no flowfiles/rows should be transferred
        runner.clearTransferState();
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        runner.clearTransferState();
        // Add timestamp as a max value column name
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "id, created_on");
        // Add a new row with a higher ID and run, one flow file will be transferred because no max value for the timestamp has been stored
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (4, 'Marty Johnson', 15.0, '2011-01-01 03:23:34.234')");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals(flowFile.getAttribute("maxvalue.id"), "4");
        Assert.assertEquals(flowFile.getAttribute("maxvalue.created_on"), "2011-01-01 03:23:34.234");
        in = new ByteArrayInputStream(flowFile.toByteArray());
        Assert.assertEquals(1, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Add a new row with a higher ID but lower timestamp and run, no flow file will be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (5, 'NO NAME', 15.0, '2001-01-01 03:23:34.234')");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        runner.clearTransferState();
        // Add a new row with a higher ID and run, one flow file will be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (6, 'Mr. NiFi', 1.0, '2012-01-01 03:23:34.234')");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertEquals(1, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Set name as the max value column name (and clear the state), all rows should be returned since the max value for name has not been set
        runner.getStateManager().clear(CLUSTER);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "name");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertEquals(7, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Add a new row with a "higher" name than the max but lower than "NULL" (to test that null values are skipped), one flow file will be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (7, 'NULK', 1.0, '2012-01-01 03:23:34.234')");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertEquals(1, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Set scale as the max value column name (and clear the state), all rows should be returned since the max value for name has not been set
        runner.getStateManager().clear(CLUSTER);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "scale");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertEquals(8, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Add a new row with a higher value for scale than the max, one flow file will be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (8, 'NULK', 100.0, '2012-01-01 03:23:34.234')");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertEquals(1, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Set scale as the max value column name (and clear the state), all rows should be returned since the max value for name has not been set
        runner.getStateManager().clear(CLUSTER);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "bignum");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertEquals(9, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Add a new row with a higher value for scale than the max, one flow file will be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on, bignum) VALUES (9, 'Alice Bob', 100.0, '2012-01-01 03:23:34.234', 1)");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertEquals(1, getNumberOfRecordsFromStream(in));
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
        runner.setProperty(MAX_ROWS_PER_FLOW_FILE, "2");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals("TEST_QUERY_DB_TABLE", flowFile.getAttribute(RESULT_TABLENAME));
        Assert.assertEquals(flowFile.getAttribute("maxvalue.id"), "2");
        InputStream in = new ByteArrayInputStream(flowFile.toByteArray());
        runner.setProperty(FETCH_SIZE, "2");
        Assert.assertEquals(2, getNumberOfRecordsFromStream(in));
        flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(1);
        Assert.assertEquals(flowFile.getAttribute("maxvalue.id"), "2");
        in = new ByteArrayInputStream(flowFile.toByteArray());
        Assert.assertEquals(1, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Populate a second table and set
        stmt.execute("create table TEST_QUERY_DB_TABLE2 (id integer not null, name varchar(100), scale float, created_on timestamp, bignum bigint default 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE2 (id, name, scale, created_on) VALUES (0, 'Joe Smith', 1.0, '1962-09-23 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE2 (id, name, scale, created_on) VALUES (1, 'Carrie Jones', 5.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE2 (id, name, scale, created_on) VALUES (2, NULL, 2.0, '2010-01-01 00:00:00')");
        runner.setProperty(TABLE_NAME, "TEST_QUERY_DB_TABLE2");
        runner.setProperty(MAX_ROWS_PER_FLOW_FILE, "0");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals("TEST_QUERY_DB_TABLE2", flowFile.getAttribute(RESULT_TABLENAME));
        Assert.assertEquals(flowFile.getAttribute("maxvalue.id"), "2");
        in = new ByteArrayInputStream(flowFile.toByteArray());
        Assert.assertEquals(3, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Add a new row with a higher ID and run, one flowfile with one new row should be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE2 (id, name, scale, created_on) VALUES (3, 'Mary West', 15.0, '2000-01-01 03:23:34.234')");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals(flowFile.getAttribute("maxvalue.id"), "3");
        in = new ByteArrayInputStream(flowFile.toByteArray());
        Assert.assertEquals(1, getNumberOfRecordsFromStream(in));
        // Sanity check - run again, this time no flowfiles/rows should be transferred
        runner.clearTransferState();
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        runner.clearTransferState();
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
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Assert.assertEquals("2", runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).getAttribute(RESULT_ROW_COUNT));
        runner.clearTransferState();
        // Add a new row in the same bucket
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (2, 0)");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Assert.assertEquals("1", runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).getAttribute(RESULT_ROW_COUNT));
        runner.clearTransferState();
        // Add a new row in a new bucket
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (3, 1)");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Assert.assertEquals("1", runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).getAttribute(RESULT_ROW_COUNT));
        runner.clearTransferState();
        // Add a new row in an old bucket, it should not be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (4, 0)");
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 0);
        // Add a new row in the second bucket, only the new row should be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, bucket) VALUES (5, 1)");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Assert.assertEquals("1", runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).getAttribute(RESULT_ROW_COUNT));
        runner.clearTransferState();
    }

    @Test
    public void testTimestampNanos() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, name varchar(100), scale float, created_on timestamp, bignum bigint default 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (1, 'Carrie Jones', 5.0, '2000-01-01 03:23:34.000123456')");
        runner.setProperty(TABLE_NAME, "TEST_QUERY_DB_TABLE");
        runner.setIncomingConnection(false);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "created_on");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        InputStream in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertEquals(1, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Run again, this time no flowfiles/rows should be transferred
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        runner.clearTransferState();
        // Add a new row with a lower timestamp (but same millisecond value), no flow file should be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (3, 'Mary West', 15.0, '2000-01-01 03:23:34.000')");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        runner.clearTransferState();
        // Add a new row with a higher timestamp, one flow file should be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (3, 'Mary West', 15.0, '2000-01-01 03:23:34.0003')");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertEquals(1, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Run again, this time no flowfiles/rows should be transferred
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        runner.clearTransferState();
    }

    @Test
    public void testWithNullIntColumn() throws SQLException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_NULL_INT");
        } catch (final SQLException sqle) {
            // Ignore, usually due to Derby not having DROP TABLE IF EXISTS
        }
        stmt.execute("create table TEST_NULL_INT (id integer not null, val1 integer, val2 integer, constraint my_pk primary key (id))");
        stmt.execute("insert into TEST_NULL_INT (id, val1, val2) VALUES (0, NULL, 1)");
        stmt.execute("insert into TEST_NULL_INT (id, val1, val2) VALUES (1, 1, 1)");
        runner.setIncomingConnection(false);
        runner.setProperty(TABLE_NAME, "TEST_NULL_INT");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertAttributeEquals(RESULT_ROW_COUNT, "2");
    }

    @Test
    public void testWithRuntimeException() throws SQLException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_NULL_INT");
        } catch (final SQLException sqle) {
            // Ignore, usually due to Derby not having DROP TABLE IF EXISTS
        }
        stmt.execute("create table TEST_NULL_INT (id integer not null, val1 integer, val2 integer, constraint my_pk primary key (id))");
        stmt.execute("insert into TEST_NULL_INT (id, val1, val2) VALUES (0, NULL, 1)");
        stmt.execute("insert into TEST_NULL_INT (id, val1, val2) VALUES (1, 1, 1)");
        runner.setIncomingConnection(false);
        runner.setProperty(TABLE_NAME, "TEST_NULL_INT");
        runner.setProperty(AbstractDatabaseFetchProcessor.MAX_VALUE_COLUMN_NAMES, "id");
        dbAdapters.put(dbAdapter.getName(), new GenericDatabaseAdapter() {
            @Override
            public String getName() {
                throw new DataFileWriter.AppendWriteException(null);
            }
        });
        runner.run();
        Assert.assertTrue(runner.getFlowFilesForRelationship(REL_SUCCESS).isEmpty());
    }

    @Test
    public void testWithSqlException() throws SQLException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_NO_ROWS");
        } catch (final SQLException sqle) {
            // Ignore, usually due to Derby not having DROP TABLE IF EXISTS
        }
        stmt.execute("create table TEST_NO_ROWS (id integer)");
        runner.setIncomingConnection(false);
        // Try a valid SQL statement that will generate an error (val1 does not exist, e.g.)
        runner.setProperty(TABLE_NAME, "TEST_NO_ROWS");
        runner.setProperty(COLUMN_NAMES, "val1");
        runner.run();
        Assert.assertTrue(runner.getFlowFilesForRelationship(REL_SUCCESS).isEmpty());
    }

    @Test
    public void testOutputBatchSize() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        InputStream in;
        MockFlowFile mff;
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, name varchar(100), scale float, created_on timestamp, bignum bigint default 0)");
        int rowCount = 0;
        // Create larger row set
        for (int batch = 0; batch < 100; batch++) {
            stmt.execute((("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (" + rowCount) + ", 'Joe Smith', 1.0, '1962-09-23 03:23:34.234')"));
            rowCount++;
        }
        runner.setProperty(TABLE_NAME, "TEST_QUERY_DB_TABLE");
        runner.setIncomingConnection(false);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "ID");
        runner.setProperty(MAX_ROWS_PER_FLOW_FILE, (("${" + (QueryDatabaseTableTest.MAX_ROWS_KEY)) + "}"));
        runner.setVariable(QueryDatabaseTableTest.MAX_ROWS_KEY, "7");
        runner.setProperty(OUTPUT_BATCH_SIZE, "${outputBatchSize}");
        runner.setVariable("outputBatchSize", "4");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 15);
        // Ensure all but the last file have 7 records each
        for (int ff = 0; ff < 14; ff++) {
            mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(ff);
            in = new ByteArrayInputStream(mff.toByteArray());
            Assert.assertEquals(7, getNumberOfRecordsFromStream(in));
            mff.assertAttributeExists("fragment.identifier");
            Assert.assertEquals(Integer.toString(ff), mff.getAttribute("fragment.index"));
            // No fragment.count set for flow files sent when Output Batch Size is set
            Assert.assertNull(mff.getAttribute("fragment.count"));
        }
        // Last file should have 2 records
        mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(14);
        in = new ByteArrayInputStream(mff.toByteArray());
        Assert.assertEquals(2, getNumberOfRecordsFromStream(in));
        mff.assertAttributeExists("fragment.identifier");
        Assert.assertEquals(Integer.toString(14), mff.getAttribute("fragment.index"));
        // No fragment.count set for flow files sent when Output Batch Size is set
        Assert.assertNull(mff.getAttribute("fragment.count"));
    }

    @Test
    public void testMaxRowsPerFlowFile() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        InputStream in;
        MockFlowFile mff;
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, name varchar(100), scale float, created_on timestamp, bignum bigint default 0)");
        int rowCount = 0;
        // create larger row set
        for (int batch = 0; batch < 100; batch++) {
            stmt.execute((("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (" + rowCount) + ", 'Joe Smith', 1.0, '1962-09-23 03:23:34.234')"));
            rowCount++;
        }
        runner.setProperty(TABLE_NAME, "TEST_QUERY_DB_TABLE");
        runner.setIncomingConnection(false);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "ID");
        runner.setProperty(MAX_ROWS_PER_FLOW_FILE, (("${" + (QueryDatabaseTableTest.MAX_ROWS_KEY)) + "}"));
        runner.setVariable(QueryDatabaseTableTest.MAX_ROWS_KEY, "9");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 12);
        // ensure all but the last file have 9 records each
        for (int ff = 0; ff < 11; ff++) {
            mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(ff);
            in = new ByteArrayInputStream(mff.toByteArray());
            Assert.assertEquals(9, getNumberOfRecordsFromStream(in));
            mff.assertAttributeExists("fragment.identifier");
            Assert.assertEquals(Integer.toString(ff), mff.getAttribute("fragment.index"));
            Assert.assertEquals("12", mff.getAttribute("fragment.count"));
        }
        // last file should have 1 record
        mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(11);
        in = new ByteArrayInputStream(mff.toByteArray());
        Assert.assertEquals(1, getNumberOfRecordsFromStream(in));
        mff.assertAttributeExists("fragment.identifier");
        Assert.assertEquals(Integer.toString(11), mff.getAttribute("fragment.index"));
        Assert.assertEquals("12", mff.getAttribute("fragment.count"));
        runner.clearTransferState();
        // Run again, this time no flowfiles/rows should be transferred
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        runner.clearTransferState();
        // Run again, this time should be a single partial flow file
        for (int batch = 0; batch < 5; batch++) {
            stmt.execute((("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (" + rowCount) + ", 'Joe Smith', 1.0, '1962-09-23 03:23:34.234')"));
            rowCount++;
        }
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        in = new ByteArrayInputStream(mff.toByteArray());
        mff.assertAttributeExists("fragment.identifier");
        Assert.assertEquals(Integer.toString(0), mff.getAttribute("fragment.index"));
        Assert.assertEquals("1", mff.getAttribute("fragment.count"));
        Assert.assertEquals(5, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Run again, this time should be a full batch and a partial
        for (int batch = 0; batch < 14; batch++) {
            stmt.execute((("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (" + rowCount) + ", 'Joe Smith', 1.0, '1962-09-23 03:23:34.234')"));
            rowCount++;
        }
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertEquals(9, getNumberOfRecordsFromStream(in));
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(1).toByteArray());
        Assert.assertEquals(5, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Run again with a cleaned state. Should get all rows split into batches
        int ffCount = ((int) (Math.ceil((rowCount / 9.0))));
        runner.getStateManager().clear(CLUSTER);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, ffCount);
        // ensure all but the last file have 9 records each
        for (int ff = 0; ff < (ffCount - 1); ff++) {
            in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(ff).toByteArray());
            Assert.assertEquals(9, getNumberOfRecordsFromStream(in));
        }
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get((ffCount - 1)).toByteArray());
        Assert.assertEquals((rowCount % 9), getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
    }

    @Test
    public void testMaxRowsPerFlowFileWithMaxFragments() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        InputStream in;
        MockFlowFile mff;
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, name varchar(100), scale float, created_on timestamp, bignum bigint default 0)");
        int rowCount = 0;
        // create larger row set
        for (int batch = 0; batch < 100; batch++) {
            stmt.execute((("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (" + rowCount) + ", 'Joe Smith', 1.0, '1962-09-23 03:23:34.234')"));
            rowCount++;
        }
        runner.setProperty(TABLE_NAME, "TEST_QUERY_DB_TABLE");
        runner.setIncomingConnection(false);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "ID");
        runner.setProperty(MAX_ROWS_PER_FLOW_FILE, "9");
        Integer maxFragments = 3;
        runner.setProperty(MAX_FRAGMENTS, maxFragments.toString());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, maxFragments);
        for (int i = 0; i < maxFragments; i++) {
            mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(i);
            in = new ByteArrayInputStream(mff.toByteArray());
            Assert.assertEquals(9, getNumberOfRecordsFromStream(in));
            mff.assertAttributeExists("fragment.identifier");
            Assert.assertEquals(Integer.toString(i), mff.getAttribute("fragment.index"));
            Assert.assertEquals(maxFragments.toString(), mff.getAttribute("fragment.count"));
        }
        runner.clearTransferState();
    }

    @Test
    public void testInitialMaxValue() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        InputStream in;
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, name varchar(100), scale float, created_on timestamp, bignum bigint default 0)");
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        int rowCount = 0;
        // create larger row set
        for (int batch = 0; batch < 10; batch++) {
            stmt.execute((((("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (" + rowCount) + ", 'Joe Smith', 1.0, '") + (dateFormat.format(cal.getTime().getTime()))) + "')"));
            rowCount++;
            cal.add(Calendar.MINUTE, 1);
        }
        runner.setProperty(TABLE_NAME, (("${" + (QueryDatabaseTableTest.TABLE_NAME_KEY)) + "}"));
        runner.setVariable(QueryDatabaseTableTest.TABLE_NAME_KEY, "TEST_QUERY_DB_TABLE");
        runner.setIncomingConnection(false);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "created_on");
        cal.setTimeInMillis(0);
        cal.add(Calendar.MINUTE, 5);
        runner.setProperty("initial.maxvalue.CREATED_ON", dateFormat.format(cal.getTime().getTime()));
        // Initial run with no previous state. Should get only last 4 records
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertEquals(4, getNumberOfRecordsFromStream(in));
        runner.getStateManager().assertStateEquals((("test_query_db_table" + (NAMESPACE_DELIMITER)) + "created_on"), "1970-01-01 00:09:00.0", CLUSTER);
        runner.clearTransferState();
        // Run again, this time no flowfiles/rows should be transferred
        // Validate Max Value doesn't change also
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        runner.getStateManager().assertStateEquals((("test_query_db_table" + (NAMESPACE_DELIMITER)) + "created_on"), "1970-01-01 00:09:00.0", CLUSTER);
        runner.clearTransferState();
    }

    @Test
    public void testInitialMaxValueWithEL() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        InputStream in;
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, name varchar(100), scale float, created_on timestamp, bignum bigint default 0)");
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        int rowCount = 0;
        // create larger row set
        for (int batch = 0; batch < 10; batch++) {
            stmt.execute((((("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (" + rowCount) + ", 'Joe Smith', 1.0, '") + (dateFormat.format(cal.getTime().getTime()))) + "')"));
            rowCount++;
            cal.add(Calendar.MINUTE, 1);
        }
        runner.setProperty(TABLE_NAME, (("${" + (QueryDatabaseTableTest.TABLE_NAME_KEY)) + "}"));
        runner.setVariable(QueryDatabaseTableTest.TABLE_NAME_KEY, "TEST_QUERY_DB_TABLE");
        runner.setIncomingConnection(false);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "created_on");
        cal.setTimeInMillis(0);
        cal.add(Calendar.MINUTE, 5);
        runner.setProperty("initial.maxvalue.CREATED_ON", "${created.on}");
        runner.setVariable("created.on", dateFormat.format(cal.getTime().getTime()));
        // Initial run with no previous state. Should get only last 4 records
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertEquals(4, getNumberOfRecordsFromStream(in));
        runner.getStateManager().assertStateEquals((("test_query_db_table" + (NAMESPACE_DELIMITER)) + "created_on"), "1970-01-01 00:09:00.0", CLUSTER);
        runner.clearTransferState();
        // Run again, this time no flowfiles/rows should be transferred
        // Validate Max Value doesn't change also
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        runner.getStateManager().assertStateEquals((("test_query_db_table" + (NAMESPACE_DELIMITER)) + "created_on"), "1970-01-01 00:09:00.0", CLUSTER);
        runner.clearTransferState();
        // Append a new row, expect 1 flowfile one row
        cal.setTimeInMillis(0);
        cal.add(Calendar.MINUTE, rowCount);
        stmt.execute((((("insert into TEST_QUERY_DB_TABLE (id, name, scale, created_on) VALUES (" + rowCount) + ", 'Joe Smith', 1.0, '") + (dateFormat.format(cal.getTime().getTime()))) + "')"));
        rowCount++;
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertEquals(1, getNumberOfRecordsFromStream(in));
        runner.getStateManager().assertStateEquals((("test_query_db_table" + (NAMESPACE_DELIMITER)) + "created_on"), "1970-01-01 00:10:00.0", CLUSTER);
        runner.clearTransferState();
    }

    @Test
    public void testAddedRowsCustomWhereClause() throws IOException, ClassNotFoundException, SQLException, InitializationException {
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
        runner.setProperty(WHERE_CLAUSE, "type = 'male'");
        runner.setIncomingConnection(false);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "ID");
        runner.setProperty(MAX_ROWS_PER_FLOW_FILE, "2");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals("TEST_QUERY_DB_TABLE", flowFile.getAttribute(RESULT_TABLENAME));
        Assert.assertEquals(flowFile.getAttribute("maxvalue.id"), "0");
        InputStream in = new ByteArrayInputStream(flowFile.toByteArray());
        runner.setProperty(FETCH_SIZE, "2");
        Assert.assertEquals(1, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Run again, this time no flowfiles/rows should be transferred
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        runner.clearTransferState();
        // Remove Max Rows Per Flow File
        runner.setProperty(MAX_ROWS_PER_FLOW_FILE, "0");
        // Add a new row with a higher ID and run, one flowfile with one new row should be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on) VALUES (3, 'female', 'Mary West', 15.0, '2000-01-01 03:23:34.234')");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        runner.clearTransferState();
        // Sanity check - run again, this time no flowfiles/rows should be transferred
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        runner.clearTransferState();
        // Add timestamp as a max value column name
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "id, created_on");
        // Add a new row with a higher ID and run, one flow file will be transferred because no max value for the timestamp has been stored
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on) VALUES (4, 'male', 'Marty Johnson', 15.0, '2011-01-01 03:23:34.234')");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals(flowFile.getAttribute("maxvalue.id"), "4");
        Assert.assertEquals(flowFile.getAttribute("maxvalue.created_on"), "2011-01-01 03:23:34.234");
        in = new ByteArrayInputStream(flowFile.toByteArray());
        Assert.assertEquals(1, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Add a new row with a higher ID but lower timestamp and run, no flow file will be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on) VALUES (5, 'male', 'NO NAME', 15.0, '2001-01-01 03:23:34.234')");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        runner.clearTransferState();
        // Add a new row with a higher ID and run, one flow file will be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on) VALUES (6, 'male', 'Mr. NiFi', 1.0, '2012-01-01 03:23:34.234')");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertEquals(1, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Set name as the max value column name (and clear the state), all rows should be returned since the max value for name has not been set
        runner.getStateManager().clear(CLUSTER);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "name");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertEquals(4, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Add a new row with a "higher" name than the max but lower than "NULL" (to test that null values are skipped), one flow file will be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on) VALUES (7, 'male', 'NULK', 1.0, '2012-01-01 03:23:34.234')");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertEquals(1, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Set scale as the max value column name (and clear the state), all rows should be returned since the max value for name has not been set
        runner.getStateManager().clear(CLUSTER);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "scale");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertEquals(5, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Add a new row with a higher value for scale than the max, one flow file will be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on) VALUES (8, 'male', 'NULK', 100.0, '2012-01-01 03:23:34.234')");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertEquals(1, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Set scale as the max value column name (and clear the state), all rows should be returned since the max value for name has not been set
        runner.getStateManager().clear(CLUSTER);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "bignum");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertEquals(6, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Add a new row with a higher value for scale than the max, one flow file will be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on, bignum) VALUES (9, 'female', 'Alice Bob', 100.0, '2012-01-01 03:23:34.234', 1)");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        runner.clearTransferState();
    }

    @Test
    public void testCustomSQL() throws IOException, SQLException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        try {
            stmt.execute("drop table TYPE_LIST");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, type varchar(20), name varchar(100), scale float, created_on timestamp, bignum bigint default 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on) VALUES (0, 'male', 'Joe Smith', 1.0, '1962-09-23 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on) VALUES (1, 'female', 'Carrie Jones', 5.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on) VALUES (2, NULL, NULL, 2.0, '2010-01-01 00:00:00')");
        stmt.execute("create table TYPE_LIST (type_id integer not null, type varchar(20), descr varchar(255))");
        stmt.execute("insert into TYPE_LIST (type_id, type,descr) VALUES (0, 'male', 'Man')");
        stmt.execute("insert into TYPE_LIST (type_id, type,descr) VALUES (1, 'female', 'Woman')");
        stmt.execute("insert into TYPE_LIST (type_id, type,descr) VALUES (2, '', 'Unspecified')");
        runner.setProperty(TABLE_NAME, "TEST_QUERY_DB_TABLE");
        runner.setProperty(SQL_QUERY, "SELECT id, b.type as gender, b.descr, name, scale, created_on, bignum FROM TEST_QUERY_DB_TABLE a INNER JOIN TYPE_LIST b ON (a.type=b.type)");
        runner.setProperty(WHERE_CLAUSE, "gender = 'male'");
        runner.setIncomingConnection(false);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "ID");
        runner.setProperty(MAX_ROWS_PER_FLOW_FILE, "2");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals("TEST_QUERY_DB_TABLE", flowFile.getAttribute(RESULT_TABLENAME));
        Assert.assertEquals(flowFile.getAttribute("maxvalue.id"), "0");
        InputStream in = new ByteArrayInputStream(flowFile.toByteArray());
        runner.setProperty(FETCH_SIZE, "2");
        Assert.assertEquals(1, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Run again, this time no flowfiles/rows should be transferred
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        runner.clearTransferState();
        // Remove Max Rows Per Flow File
        runner.setProperty(MAX_ROWS_PER_FLOW_FILE, "0");
        // Add a new row with a higher ID and run, one flowfile with one new row should be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on) VALUES (3, 'female', 'Mary West', 15.0, '2000-01-01 03:23:34.234')");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        runner.clearTransferState();
        // Sanity check - run again, this time no flowfiles/rows should be transferred
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        runner.clearTransferState();
        // Add timestamp as a max value column name
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "id, created_on");
        // Add a new row with a higher ID and run, one flow file will be transferred because no max value for the timestamp has been stored
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on) VALUES (4, 'male', 'Marty Johnson', 15.0, '2011-01-01 03:23:34.234')");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals(flowFile.getAttribute("maxvalue.id"), "4");
        Assert.assertEquals(flowFile.getAttribute("maxvalue.created_on"), "2011-01-01 03:23:34.234");
        in = new ByteArrayInputStream(flowFile.toByteArray());
        Assert.assertEquals(1, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Add a new row with a higher ID but lower timestamp and run, no flow file will be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on) VALUES (5, 'male', 'NO NAME', 15.0, '2001-01-01 03:23:34.234')");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        runner.clearTransferState();
        // Add a new row with a higher ID and run, one flow file will be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on) VALUES (6, 'male', 'Mr. NiFi', 1.0, '2012-01-01 03:23:34.234')");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertEquals(1, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Set name as the max value column name (and clear the state), all rows should be returned since the max value for name has not been set
        runner.getStateManager().clear(CLUSTER);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "name");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertEquals(4, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Add a new row with a "higher" name than the max but lower than "NULL" (to test that null values are skipped), one flow file will be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on) VALUES (7, 'male', 'NULK', 1.0, '2012-01-01 03:23:34.234')");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertEquals(1, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Set scale as the max value column name (and clear the state), all rows should be returned since the max value for name has not been set
        runner.getStateManager().clear(CLUSTER);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "scale");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertEquals(5, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Add a new row with a higher value for scale than the max, one flow file will be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on) VALUES (8, 'male', 'NULK', 100.0, '2012-01-01 03:23:34.234')");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertEquals(1, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Set scale as the max value column name (and clear the state), all rows should be returned since the max value for name has not been set
        runner.getStateManager().clear(CLUSTER);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "bignum");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        in = new ByteArrayInputStream(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertEquals(6, getNumberOfRecordsFromStream(in));
        runner.clearTransferState();
        // Add a new row with a higher value for scale than the max, one flow file will be transferred
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on, bignum) VALUES (9, 'female', 'Alice Bob', 100.0, '2012-01-01 03:23:34.234', 1)");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        runner.clearTransferState();
    }

    @Test(expected = AssertionError.class)
    public void testMissingColumn() throws IOException, ClassNotFoundException, SQLException, ProcessException, InitializationException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_QUERY_DB_TABLE");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        try {
            stmt.execute("drop table TYPE_LIST");
        } catch (final SQLException sqle) {
            // Ignore this error, probably a "table does not exist" since Derby doesn't yet support DROP IF EXISTS [DERBY-4842]
        }
        stmt.execute("create table TEST_QUERY_DB_TABLE (id integer not null, type varchar(20), name varchar(100), scale float, created_on timestamp, bignum bigint default 0)");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on) VALUES (0, 'male', 'Joe Smith', 1.0, '1962-09-23 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on) VALUES (1, 'female', 'Carrie Jones', 5.0, '2000-01-01 03:23:34.234')");
        stmt.execute("insert into TEST_QUERY_DB_TABLE (id, type, name, scale, created_on) VALUES (2, NULL, NULL, 2.0, '2010-01-01 00:00:00')");
        stmt.execute("create table TYPE_LIST (type_id integer not null, type varchar(20), descr varchar(255))");
        stmt.execute("insert into TYPE_LIST (type_id, type,descr) VALUES (0, 'male', 'Man')");
        stmt.execute("insert into TYPE_LIST (type_id, type,descr) VALUES (1, 'female', 'Woman')");
        stmt.execute("insert into TYPE_LIST (type_id, type,descr) VALUES (2, '', 'Unspecified')");
        runner.setProperty(TABLE_NAME, "TYPE_LIST");
        runner.setProperty(SQL_QUERY, "SELECT b.type, b.descr, name, scale, created_on, bignum FROM TEST_QUERY_DB_TABLE a INNER JOIN TYPE_LIST b ON (a.type=b.type)");
        runner.setProperty(WHERE_CLAUSE, "type = 'male'");
        runner.setIncomingConnection(false);
        runner.setProperty(MAX_VALUE_COLUMN_NAMES, "ID");
        runner.setProperty(MAX_ROWS_PER_FLOW_FILE, "2");
        runner.run();
    }

    @Test
    public void testWithExceptionAfterSomeRowsProcessed() throws SQLException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_NULL_INT");
        } catch (final SQLException sqle) {
            // Ignore, usually due to Derby not having DROP TABLE IF EXISTS
        }
        stmt.execute("create table TEST_NULL_INT (id integer not null, val1 integer, val2 integer, constraint my_pk primary key (id))");
        stmt.execute("insert into TEST_NULL_INT (id, val1, val2) VALUES (1, NULL, 1)");
        stmt.execute("insert into TEST_NULL_INT (id, val1, val2) VALUES (2, 1, 1)");
        runner.setIncomingConnection(false);
        runner.setProperty(TABLE_NAME, "TEST_NULL_INT");
        runner.setProperty(AbstractDatabaseFetchProcessor.MAX_VALUE_COLUMN_NAMES, "id");
        // Override adapter with one that fails after the first row is processed
        dbAdapters.put(dbAdapter.getName(), new GenericDatabaseAdapter() {
            boolean fail = false;

            @Override
            public String getName() {
                if (!(fail)) {
                    fail = true;
                    return super.getName();
                }
                throw new DataFileWriter.AppendWriteException(null);
            }
        });
        runner.run();
        Assert.assertTrue(runner.getFlowFilesForRelationship(REL_SUCCESS).isEmpty());
        // State should not have been updated
        runner.getStateManager().assertStateNotSet("test_null_int@!@id", CLUSTER);
        // Restore original (working) adapter and run again
        dbAdapters.put(dbAdapter.getName(), dbAdapter);
        runner.run();
        Assert.assertFalse(runner.getFlowFilesForRelationship(REL_SUCCESS).isEmpty());
        runner.getStateManager().assertStateEquals("test_null_int@!@id", "2", CLUSTER);
    }

    /**
     * Simple implementation only for QueryDatabaseTable processor testing.
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
                return DriverManager.getConnection((("jdbc:derby:" + (QueryDatabaseTableTest.DB_LOCATION)) + ";create=true"));
            } catch (final Exception e) {
                throw new ProcessException(("getConnection failed: " + e));
            }
        }
    }

    @Stateful(scopes = Scope.CLUSTER, description = "Mock for QueryDatabaseTable processor")
    private static class MockQueryDatabaseTable extends QueryDatabaseTable {
        void putColumnType(String colName, Integer colType) {
            columnTypeMap.put(colName, colType);
        }
    }
}

