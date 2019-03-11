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


import AbstractExecuteSQL.DBCP_SERVICE;
import AbstractExecuteSQL.REL_FAILURE;
import AbstractExecuteSQL.REL_SUCCESS;
import AbstractExecuteSQL.RESULT_ERROR_MESSAGE;
import AbstractExecuteSQL.SQL_SELECT_QUERY;
import CoreAttributes.MIME_TYPE;
import ExecuteSQLRecord.MAX_ROWS_PER_FLOW_FILE;
import ExecuteSQLRecord.OUTPUT_BATCH_SIZE;
import ExecuteSQLRecord.RECORD_WRITER_FACTORY;
import ExecuteSQLRecord.RESULTSET_INDEX;
import ExecuteSQLRecord.RESULT_ROW_COUNT;
import ExecuteSQLRecord.SQL_POST_QUERY;
import ExecuteSQLRecord.SQL_PRE_QUERY;
import FragmentAttributes.FRAGMENT_COUNT;
import FragmentAttributes.FRAGMENT_ID;
import FragmentAttributes.FRAGMENT_INDEX;
import ProvenanceEventType.FETCH;
import ProvenanceEventType.FORK;
import ProvenanceEventType.RECEIVE;
import SchemaAccessUtils.INHERIT_RECORD_SCHEMA;
import SchemaAccessUtils.SCHEMA_ACCESS_STRATEGY;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;
import org.apache.nifi.avro.AvroRecordSetWriter;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.dbcp.DBCPService;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.serialization.record.MockRecordWriter;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestExecuteSQLRecord {
    private static final Logger LOGGER;

    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.log.nifi.io.nio", "debug");
        System.setProperty("org.slf4j.simpleLogger.log.nifi.processors.standard.ExecuteSQLRecord", "debug");
        System.setProperty("org.slf4j.simpleLogger.log.nifi.processors.standard.TestExecuteSQLRecord", "debug");
        LOGGER = LoggerFactory.getLogger(TestExecuteSQLRecord.class);
    }

    static final String DB_LOCATION = "target/db";

    static final String QUERY_WITH_EL = "select " + ((((("  PER.ID as PersonId, PER.NAME as PersonName, PER.CODE as PersonCode" + ", PRD.ID as ProductId,PRD.NAME as ProductName,PRD.CODE as ProductCode") + ", REL.ID as RelId,    REL.NAME as RelName,    REL.CODE as RelCode") + ", ROW_NUMBER() OVER () as rownr ") + " from persons PER, products PRD, relationships REL") + " where PER.ID = ${person.id}");

    static final String QUERY_WITHOUT_EL = "select " + ((((("  PER.ID as PersonId, PER.NAME as PersonName, PER.CODE as PersonCode" + ", PRD.ID as ProductId,PRD.NAME as ProductName,PRD.CODE as ProductCode") + ", REL.ID as RelId,    REL.NAME as RelName,    REL.CODE as RelCode") + ", ROW_NUMBER() OVER () as rownr ") + " from persons PER, products PRD, relationships REL") + " where PER.ID = 10");

    static final String QUERY_WITHOUT_EL_WITH_PARAMS = "select " + ((((("  PER.ID as PersonId, PER.NAME as PersonName, PER.CODE as PersonCode" + ", PRD.ID as ProductId,PRD.NAME as ProductName,PRD.CODE as ProductCode") + ", REL.ID as RelId,    REL.NAME as RelName,    REL.CODE as RelCode") + ", ROW_NUMBER() OVER () as rownr ") + " from persons PER, products PRD, relationships REL") + " where PER.ID < ? AND REL.ID < ?");

    private TestRunner runner;

    @Test
    public void testIncomingConnectionWithNoFlowFile() throws InitializationException {
        runner.setIncomingConnection(true);
        runner.setProperty(SQL_SELECT_QUERY, "SELECT * FROM persons");
        MockRecordWriter recordWriter = new MockRecordWriter(null, true, (-1));
        runner.addControllerService("writer", recordWriter);
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.enableControllerService(recordWriter);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
    }

    @Test
    public void testIncomingConnectionWithNoFlowFileAndNoQuery() throws InitializationException {
        runner.setIncomingConnection(true);
        MockRecordWriter recordWriter = new MockRecordWriter(null, true, (-1));
        runner.addControllerService("writer", recordWriter);
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.enableControllerService(recordWriter);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
    }

    @Test(expected = AssertionError.class)
    public void testNoIncomingConnectionAndNoQuery() throws InitializationException {
        runner.setIncomingConnection(false);
        runner.run();
    }

    @Test
    public void testNoIncomingConnection() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        runner.setIncomingConnection(false);
        invokeOnTriggerRecords(null, TestExecuteSQLRecord.QUERY_WITHOUT_EL, false, null, true);
        Assert.assertEquals(RECEIVE, runner.getProvenanceEvents().get(0).getEventType());
    }

    @Test
    public void testSelectQueryInFlowFile() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        invokeOnTriggerRecords(null, TestExecuteSQLRecord.QUERY_WITHOUT_EL, true, null, false);
        Assert.assertEquals(FORK, runner.getProvenanceEvents().get(0).getEventType());
        Assert.assertEquals(FETCH, runner.getProvenanceEvents().get(1).getEventType());
    }

    @Test
    public void testWithOutputBatching() throws SQLException, InitializationException {
        // remove previous test database, if any
        final File dbLocation = new File(TestExecuteSQLRecord.DB_LOCATION);
        dbLocation.delete();
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_NULL_INT");
        } catch (final SQLException sqle) {
        }
        stmt.execute("create table TEST_NULL_INT (id integer not null, val1 integer, val2 integer, constraint my_pk primary key (id))");
        for (int i = 0; i < 1000; i++) {
            stmt.execute((("insert into TEST_NULL_INT (id, val1, val2) VALUES (" + i) + ", 1, 1)"));
        }
        MockRecordWriter recordWriter = new MockRecordWriter(null, true, (-1));
        runner.addControllerService("writer", recordWriter);
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.enableControllerService(recordWriter);
        runner.setIncomingConnection(false);
        runner.setProperty(MAX_ROWS_PER_FLOW_FILE, "5");
        runner.setProperty(OUTPUT_BATCH_SIZE, "5");
        runner.setProperty(ExecuteSQLRecord.SQL_SELECT_QUERY, "SELECT * FROM TEST_NULL_INT");
        runner.run();
        runner.assertAllFlowFilesTransferred(ExecuteSQLRecord.REL_SUCCESS, 200);
        runner.assertAllFlowFilesContainAttribute(ExecuteSQLRecord.REL_SUCCESS, FRAGMENT_INDEX.key());
        runner.assertAllFlowFilesContainAttribute(ExecuteSQLRecord.REL_SUCCESS, FRAGMENT_ID.key());
        MockFlowFile firstFlowFile = runner.getFlowFilesForRelationship(ExecuteSQLRecord.REL_SUCCESS).get(0);
        firstFlowFile.assertAttributeEquals(RESULT_ROW_COUNT, "5");
        firstFlowFile.assertAttributeNotExists(FRAGMENT_COUNT.key());
        firstFlowFile.assertAttributeEquals(FRAGMENT_INDEX.key(), "0");
        firstFlowFile.assertAttributeEquals(RESULTSET_INDEX, "0");
        MockFlowFile lastFlowFile = runner.getFlowFilesForRelationship(ExecuteSQLRecord.REL_SUCCESS).get(199);
        lastFlowFile.assertAttributeEquals(RESULT_ROW_COUNT, "5");
        lastFlowFile.assertAttributeEquals(FRAGMENT_INDEX.key(), "199");
        lastFlowFile.assertAttributeEquals(RESULTSET_INDEX, "0");
    }

    @Test
    public void testWithOutputBatchingAndIncomingFlowFile() throws SQLException, InitializationException {
        // remove previous test database, if any
        final File dbLocation = new File(TestExecuteSQLRecord.DB_LOCATION);
        dbLocation.delete();
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_NULL_INT");
        } catch (final SQLException sqle) {
        }
        stmt.execute("create table TEST_NULL_INT (id integer not null, val1 integer, val2 integer, constraint my_pk primary key (id))");
        for (int i = 0; i < 1000; i++) {
            stmt.execute((("insert into TEST_NULL_INT (id, val1, val2) VALUES (" + i) + ", 1, 1)"));
        }
        MockRecordWriter recordWriter = new MockRecordWriter(null, true, (-1));
        runner.addControllerService("writer", recordWriter);
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.enableControllerService(recordWriter);
        runner.setIncomingConnection(true);
        runner.setProperty(MAX_ROWS_PER_FLOW_FILE, "5");
        runner.setProperty(OUTPUT_BATCH_SIZE, "1");
        runner.enqueue("SELECT * FROM TEST_NULL_INT");
        runner.run();
        runner.assertAllFlowFilesTransferred(ExecuteSQLRecord.REL_SUCCESS, 200);
        runner.assertAllFlowFilesContainAttribute(ExecuteSQLRecord.REL_SUCCESS, FRAGMENT_INDEX.key());
        runner.assertAllFlowFilesContainAttribute(ExecuteSQLRecord.REL_SUCCESS, FRAGMENT_ID.key());
        MockFlowFile firstFlowFile = runner.getFlowFilesForRelationship(ExecuteSQLRecord.REL_SUCCESS).get(0);
        firstFlowFile.assertAttributeEquals(RESULT_ROW_COUNT, "5");
        firstFlowFile.assertAttributeNotExists(FRAGMENT_COUNT.key());
        firstFlowFile.assertAttributeEquals(FRAGMENT_INDEX.key(), "0");
        firstFlowFile.assertAttributeEquals(RESULTSET_INDEX, "0");
        MockFlowFile lastFlowFile = runner.getFlowFilesForRelationship(ExecuteSQLRecord.REL_SUCCESS).get(199);
        lastFlowFile.assertAttributeEquals(RESULT_ROW_COUNT, "5");
        lastFlowFile.assertAttributeEquals(FRAGMENT_INDEX.key(), "199");
        lastFlowFile.assertAttributeEquals(RESULTSET_INDEX, "0");
    }

    @Test
    public void testMaxRowsPerFlowFile() throws Exception {
        // remove previous test database, if any
        final File dbLocation = new File(TestExecuteSQLRecord.DB_LOCATION);
        dbLocation.delete();
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_NULL_INT");
        } catch (final SQLException sqle) {
        }
        stmt.execute("create table TEST_NULL_INT (id integer not null, val1 integer, val2 integer, constraint my_pk primary key (id))");
        for (int i = 0; i < 1000; i++) {
            stmt.execute((("insert into TEST_NULL_INT (id, val1, val2) VALUES (" + i) + ", 1, 1)"));
        }
        runner.setIncomingConnection(false);
        runner.setProperty(AbstractExecuteSQL.MAX_ROWS_PER_FLOW_FILE, "5");
        runner.setProperty(AbstractExecuteSQL.OUTPUT_BATCH_SIZE, "0");
        runner.setProperty(SQL_SELECT_QUERY, "SELECT * FROM TEST_NULL_INT");
        MockRecordWriter recordWriter = new MockRecordWriter(null, true, (-1));
        runner.addControllerService("writer", recordWriter);
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.enableControllerService(recordWriter);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 200);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertAllFlowFilesContainAttribute(REL_SUCCESS, FRAGMENT_INDEX.key());
        runner.assertAllFlowFilesContainAttribute(REL_SUCCESS, FRAGMENT_ID.key());
        runner.assertAllFlowFilesContainAttribute(REL_SUCCESS, FRAGMENT_COUNT.key());
        MockFlowFile firstFlowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        firstFlowFile.assertAttributeEquals(AbstractExecuteSQL.RESULT_ROW_COUNT, "5");
        firstFlowFile.assertAttributeEquals("record.count", "5");
        firstFlowFile.assertAttributeEquals(MIME_TYPE.key(), "text/plain");// MockRecordWriter has text/plain MIME type

        firstFlowFile.assertAttributeEquals(FRAGMENT_INDEX.key(), "0");
        firstFlowFile.assertAttributeEquals(AbstractExecuteSQL.RESULTSET_INDEX, "0");
        MockFlowFile lastFlowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(199);
        lastFlowFile.assertAttributeEquals(AbstractExecuteSQL.RESULT_ROW_COUNT, "5");
        lastFlowFile.assertAttributeEquals("record.count", "5");
        lastFlowFile.assertAttributeEquals(MIME_TYPE.key(), "text/plain");// MockRecordWriter has text/plain MIME type

        lastFlowFile.assertAttributeEquals(FRAGMENT_INDEX.key(), "199");
        lastFlowFile.assertAttributeEquals(AbstractExecuteSQL.RESULTSET_INDEX, "0");
    }

    @Test
    public void testInsertStatementCreatesFlowFile() throws Exception {
        // remove previous test database, if any
        final File dbLocation = new File(TestExecuteSQLRecord.DB_LOCATION);
        dbLocation.delete();
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_NULL_INT");
        } catch (final SQLException sqle) {
        }
        stmt.execute("create table TEST_NULL_INT (id integer not null, val1 integer, val2 integer, constraint my_pk primary key (id))");
        runner.setIncomingConnection(false);
        runner.setProperty(SQL_SELECT_QUERY, "insert into TEST_NULL_INT (id, val1, val2) VALUES (0, NULL, 1)");
        MockRecordWriter recordWriter = new MockRecordWriter(null, true, (-1));
        runner.addControllerService("writer", recordWriter);
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.enableControllerService(recordWriter);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertAttributeEquals(AbstractExecuteSQL.RESULT_ROW_COUNT, "0");
    }

    @Test
    public void testWriteLOBsToAvro() throws Exception {
        final DBCPService dbcp = new TestExecuteSQLRecord.DBCPServiceSimpleImpl("h2");
        final Map<String, String> dbcpProperties = new HashMap<>();
        runner = TestRunners.newTestRunner(ExecuteSQLRecord.class);
        runner.addControllerService("dbcp", dbcp, dbcpProperties);
        runner.enableControllerService(dbcp);
        runner.setProperty(DBCP_SERVICE, "dbcp");
        // remove previous test database, if any
        final File dbLocation = new File(TestExecuteSQLRecord.DB_LOCATION);
        dbLocation.delete();
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_NULL_INT");
        } catch (final SQLException sqle) {
        }
        stmt.execute(("create table TEST_NULL_INT (id integer not null, val1 integer, val2 integer, image blob(1K), words clob(1K), " + "natwords nclob(1K), constraint my_pk primary key (id))"));
        stmt.execute(("insert into TEST_NULL_INT (id, val1, val2, image, words, natwords) VALUES (0, NULL, 1, CAST (X'DEADBEEF' AS BLOB), " + "CAST ('Hello World' AS CLOB), CAST ('I am an NCLOB' AS NCLOB))"));
        runner.setIncomingConnection(false);
        runner.setProperty(SQL_SELECT_QUERY, "select * from TEST_NULL_INT");
        AvroRecordSetWriter recordWriter = new AvroRecordSetWriter();
        runner.addControllerService("writer", recordWriter);
        runner.setProperty(recordWriter, SCHEMA_ACCESS_STRATEGY, INHERIT_RECORD_SCHEMA);
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.enableControllerService(recordWriter);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertAttributeEquals(AbstractExecuteSQL.RESULT_ROW_COUNT, "1");
        ByteArrayInputStream bais = new ByteArrayInputStream(flowFile.toByteArray());
        final DataFileStream<GenericRecord> dataFileStream = new DataFileStream(bais, new org.apache.avro.generic.GenericDatumReader());
        final Schema avroSchema = dataFileStream.getSchema();
        GenericData.setStringType(avroSchema, GenericData.StringType.String);
        final GenericRecord avroRecord = dataFileStream.next();
        Object imageObj = avroRecord.get("IMAGE");
        Assert.assertNotNull(imageObj);
        Assert.assertTrue((imageObj instanceof ByteBuffer));
        Assert.assertArrayEquals(new byte[]{ ((byte) (222)), ((byte) (173)), ((byte) (190)), ((byte) (239)) }, ((ByteBuffer) (imageObj)).array());
        Object wordsObj = avroRecord.get("WORDS");
        Assert.assertNotNull(wordsObj);
        Assert.assertTrue((wordsObj instanceof Utf8));
        Assert.assertEquals("Hello World", wordsObj.toString());
        Object natwordsObj = avroRecord.get("NATWORDS");
        Assert.assertNotNull(natwordsObj);
        Assert.assertTrue((natwordsObj instanceof Utf8));
        Assert.assertEquals("I am an NCLOB", natwordsObj.toString());
    }

    @Test
    public void testNoRowsStatementCreatesEmptyFlowFile() throws Exception {
        // remove previous test database, if any
        final File dbLocation = new File(TestExecuteSQLRecord.DB_LOCATION);
        dbLocation.delete();
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_NULL_INT");
        } catch (final SQLException sqle) {
        }
        stmt.execute("create table TEST_NULL_INT (id integer not null, val1 integer, val2 integer, constraint my_pk primary key (id))");
        runner.setIncomingConnection(true);
        runner.setProperty(ExecuteSQLRecord.SQL_SELECT_QUERY, "select * from TEST_NULL_INT");
        MockRecordWriter recordWriter = new MockRecordWriter(null, true, (-1));
        runner.addControllerService("writer", recordWriter);
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.enableControllerService(recordWriter);
        runner.enqueue("Hello".getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(ExecuteSQLRecord.REL_SUCCESS, 1);
        MockFlowFile firstFlowFile = runner.getFlowFilesForRelationship(ExecuteSQLRecord.REL_SUCCESS).get(0);
        firstFlowFile.assertAttributeEquals(RESULT_ROW_COUNT, "0");
        firstFlowFile.assertContentEquals("");
    }

    @Test
    public void testWithSqlException() throws Exception {
        // remove previous test database, if any
        final File dbLocation = new File(TestExecuteSQLRecord.DB_LOCATION);
        dbLocation.delete();
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_NO_ROWS");
        } catch (final SQLException sqle) {
        }
        stmt.execute("create table TEST_NO_ROWS (id integer)");
        runner.setIncomingConnection(false);
        // Try a valid SQL statement that will generate an error (val1 does not exist, e.g.)
        runner.setProperty(SQL_SELECT_QUERY, "SELECT val1 FROM TEST_NO_ROWS");
        MockRecordWriter recordWriter = new MockRecordWriter(null, true, (-1));
        runner.addControllerService("writer", recordWriter);
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.enableControllerService(recordWriter);
        runner.run();
        // No incoming flow file containing a query, and an exception causes no outbound flowfile.
        // There should be no flow files on either relationship
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 0);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testWithSqlExceptionErrorProcessingResultSet() throws Exception {
        DBCPService dbcp = Mockito.mock(DBCPService.class);
        Connection conn = Mockito.mock(Connection.class);
        Mockito.when(dbcp.getConnection(ArgumentMatchers.any(Map.class))).thenReturn(conn);
        Mockito.when(dbcp.getIdentifier()).thenReturn("mockdbcp");
        PreparedStatement statement = Mockito.mock(PreparedStatement.class);
        Mockito.when(conn.prepareStatement(ArgumentMatchers.anyString())).thenReturn(statement);
        Mockito.when(statement.execute()).thenReturn(true);
        ResultSet rs = Mockito.mock(ResultSet.class);
        Mockito.when(statement.getResultSet()).thenReturn(rs);
        // Throw an exception the first time you access the ResultSet, this is after the flow file to hold the results has been created.
        Mockito.when(rs.getMetaData()).thenThrow(new SQLException("test execute statement failed"));
        runner.addControllerService("mockdbcp", dbcp, new HashMap());
        runner.enableControllerService(dbcp);
        runner.setProperty(DBCP_SERVICE, "mockdbcp");
        MockRecordWriter recordWriter = new MockRecordWriter(null, true, (-1));
        runner.addControllerService("writer", recordWriter);
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.enableControllerService(recordWriter);
        runner.setIncomingConnection(true);
        runner.enqueue("SELECT 1");
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.assertTransferCount(REL_SUCCESS, 0);
        // Assert exception message has been put to flow file attribute
        MockFlowFile failedFlowFile = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        Assert.assertEquals("java.sql.SQLException: test execute statement failed", failedFlowFile.getAttribute(RESULT_ERROR_MESSAGE));
    }

    @Test
    public void testPreQuery() throws Exception {
        // remove previous test database, if any
        final File dbLocation = new File(TestExecuteSQLRecord.DB_LOCATION);
        dbLocation.delete();
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_NULL_INT");
        } catch (final SQLException sqle) {
        }
        stmt.execute("create table TEST_NULL_INT (id integer not null, val1 integer, val2 integer, constraint my_pk primary key (id))");
        stmt.execute("insert into TEST_NULL_INT values(1,2,3)");
        runner.setIncomingConnection(true);
        runner.setProperty(SQL_PRE_QUERY, "CALL SYSCS_UTIL.SYSCS_SET_RUNTIMESTATISTICS(1);CALL SYSCS_UTIL.SYSCS_SET_STATISTICS_TIMING(1)");
        runner.setProperty(ExecuteSQLRecord.SQL_SELECT_QUERY, "select * from TEST_NULL_INT");
        MockRecordWriter recordWriter = new MockRecordWriter(null, true, (-1));
        runner.addControllerService("writer", recordWriter);
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.enableControllerService(recordWriter);
        runner.enqueue("test".getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(ExecuteSQLRecord.REL_SUCCESS, 1);
        MockFlowFile firstFlowFile = runner.getFlowFilesForRelationship(ExecuteSQLRecord.REL_SUCCESS).get(0);
        firstFlowFile.assertAttributeEquals(RESULT_ROW_COUNT, "1");
    }

    @Test
    public void testPostQuery() throws Exception {
        // remove previous test database, if any
        final File dbLocation = new File(TestExecuteSQLRecord.DB_LOCATION);
        dbLocation.delete();
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_NULL_INT");
        } catch (final SQLException sqle) {
        }
        stmt.execute("create table TEST_NULL_INT (id integer not null, val1 integer, val2 integer, constraint my_pk primary key (id))");
        stmt.execute("insert into TEST_NULL_INT values(1,2,3)");
        runner.setIncomingConnection(true);
        runner.setProperty(SQL_PRE_QUERY, "CALL SYSCS_UTIL.SYSCS_SET_RUNTIMESTATISTICS(1);CALL SYSCS_UTIL.SYSCS_SET_STATISTICS_TIMING(1)");
        runner.setProperty(ExecuteSQLRecord.SQL_SELECT_QUERY, "select * from TEST_NULL_INT");
        runner.setProperty(SQL_POST_QUERY, "CALL SYSCS_UTIL.SYSCS_SET_RUNTIMESTATISTICS(0);CALL SYSCS_UTIL.SYSCS_SET_STATISTICS_TIMING(0)");
        MockRecordWriter recordWriter = new MockRecordWriter(null, true, (-1));
        runner.addControllerService("writer", recordWriter);
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.enableControllerService(recordWriter);
        runner.enqueue("test".getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(ExecuteSQLRecord.REL_SUCCESS, 1);
        MockFlowFile firstFlowFile = runner.getFlowFilesForRelationship(ExecuteSQLRecord.REL_SUCCESS).get(0);
        firstFlowFile.assertAttributeEquals(RESULT_ROW_COUNT, "1");
    }

    @Test
    public void testPreQueryFail() throws Exception {
        // remove previous test database, if any
        final File dbLocation = new File(TestExecuteSQLRecord.DB_LOCATION);
        dbLocation.delete();
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_NULL_INT");
        } catch (final SQLException sqle) {
        }
        stmt.execute("create table TEST_NULL_INT (id integer not null, val1 integer, val2 integer, constraint my_pk primary key (id))");
        runner.setIncomingConnection(true);
        // Simulate failure by not provide parameter
        runner.setProperty(SQL_PRE_QUERY, "CALL SYSCS_UTIL.SYSCS_SET_RUNTIMESTATISTICS()");
        runner.setProperty(ExecuteSQLRecord.SQL_SELECT_QUERY, "select * from TEST_NULL_INT");
        MockRecordWriter recordWriter = new MockRecordWriter(null, true, (-1));
        runner.addControllerService("writer", recordWriter);
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.enableControllerService(recordWriter);
        runner.enqueue("test".getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(ExecuteSQLRecord.REL_FAILURE, 1);
    }

    @Test
    public void testPostQueryFail() throws Exception {
        // remove previous test database, if any
        final File dbLocation = new File(TestExecuteSQLRecord.DB_LOCATION);
        dbLocation.delete();
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_NULL_INT");
        } catch (final SQLException sqle) {
        }
        stmt.execute("create table TEST_NULL_INT (id integer not null, val1 integer, val2 integer, constraint my_pk primary key (id))");
        runner.setIncomingConnection(true);
        runner.setProperty(SQL_PRE_QUERY, "CALL SYSCS_UTIL.SYSCS_SET_RUNTIMESTATISTICS(1);CALL SYSCS_UTIL.SYSCS_SET_STATISTICS_TIMING(1)");
        runner.setProperty(ExecuteSQLRecord.SQL_SELECT_QUERY, "select * from TEST_NULL_INT");
        // Simulate failure by not provide parameter
        runner.setProperty(SQL_POST_QUERY, "CALL SYSCS_UTIL.SYSCS_SET_RUNTIMESTATISTICS()");
        MockRecordWriter recordWriter = new MockRecordWriter(null, true, (-1));
        runner.addControllerService("writer", recordWriter);
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.enableControllerService(recordWriter);
        runner.enqueue("test".getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(ExecuteSQLRecord.REL_FAILURE, 1);
        MockFlowFile firstFlowFile = runner.getFlowFilesForRelationship(ExecuteSQLRecord.REL_FAILURE).get(0);
        firstFlowFile.assertContentEquals("test");
    }

    /**
     * Simple implementation only for ExecuteSQL processor testing.
     */
    class DBCPServiceSimpleImpl extends AbstractControllerService implements DBCPService {
        private final String type;

        public DBCPServiceSimpleImpl(String type) {
            this.type = type;
        }

        @Override
        public String getIdentifier() {
            return "dbcp";
        }

        @Override
        public Connection getConnection() throws ProcessException {
            try {
                final Connection con;
                if ("h2".equalsIgnoreCase(type)) {
                    con = DriverManager.getConnection(("jdbc:h2:file:" + "./target/testdb7"));
                } else {
                    Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
                    con = DriverManager.getConnection((("jdbc:derby:" + (TestExecuteSQLRecord.DB_LOCATION)) + ";create=true"));
                }
                return con;
            } catch (final Exception e) {
                throw new ProcessException(("getConnection failed: " + e));
            }
        }
    }
}

