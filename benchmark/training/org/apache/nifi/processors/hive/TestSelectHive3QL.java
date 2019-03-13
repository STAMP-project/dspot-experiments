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
package org.apache.nifi.processors.hive;


import ProvenanceEventType.FETCH;
import ProvenanceEventType.FORK;
import ProvenanceEventType.RECEIVE;
import SelectHive3QL.HIVEQL_OUTPUT_FORMAT;
import SelectHive3QL.HIVEQL_SELECT_QUERY;
import SelectHive3QL.MAX_FRAGMENTS;
import SelectHive3QL.MAX_ROWS_PER_FLOW_FILE;
import SelectHive3QL.REL_FAILURE;
import SelectHive3QL.REL_SUCCESS;
import SelectHive3QL.RESULT_ROW_COUNT;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.dbcp.hive.Hive3DBCPService;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestSelectHive3QL {
    private static final Logger LOGGER;

    private static final String MAX_ROWS_KEY = "maxRows";

    private final int NUM_OF_ROWS = 100;

    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.log.nifi.io.nio", "debug");
        System.setProperty("org.slf4j.simpleLogger.log.nifi.processors.hive.SelectHive3QL", "debug");
        System.setProperty("org.slf4j.simpleLogger.log.nifi.processors.hive.TestSelectHive3QL", "debug");
        LOGGER = LoggerFactory.getLogger(TestSelectHive3QL.class);
    }

    private static final String DB_LOCATION = "target/db";

    private static final String QUERY_WITH_EL = "select " + (("  PER.ID as PersonId, PER.NAME as PersonName, PER.CODE as PersonCode" + " from persons PER") + " where PER.ID > ${person.id}");

    private static final String QUERY_WITHOUT_EL = "select " + (("  PER.ID as PersonId, PER.NAME as PersonName, PER.CODE as PersonCode" + " from persons PER") + " where PER.ID > 10");

    private TestRunner runner;

    @Test
    public void testIncomingConnectionWithNoFlowFile() throws InitializationException {
        runner.setIncomingConnection(true);
        runner.setProperty(HIVEQL_SELECT_QUERY, "SELECT * FROM persons");
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
    }

    @Test
    public void testNoIncomingConnection() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        runner.setIncomingConnection(false);
        invokeOnTrigger(TestSelectHive3QL.QUERY_WITHOUT_EL, false, "Avro");
        final List<ProvenanceEventRecord> provenanceEvents = runner.getProvenanceEvents();
        final ProvenanceEventRecord provenance0 = provenanceEvents.get(0);
        Assert.assertEquals(RECEIVE, provenance0.getEventType());
        Assert.assertEquals("jdbc:derby:target/db;create=true", provenance0.getTransitUri());
    }

    @Test
    public void testNoTimeLimit() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        invokeOnTrigger(TestSelectHive3QL.QUERY_WITH_EL, true, "Avro");
        final List<ProvenanceEventRecord> provenanceEvents = runner.getProvenanceEvents();
        Assert.assertEquals(3, provenanceEvents.size());
        final ProvenanceEventRecord provenance0 = provenanceEvents.get(0);
        Assert.assertEquals(FORK, provenance0.getEventType());
        final ProvenanceEventRecord provenance1 = provenanceEvents.get(1);
        Assert.assertEquals(FETCH, provenance1.getEventType());
        Assert.assertEquals("jdbc:derby:target/db;create=true", provenance1.getTransitUri());
        final ProvenanceEventRecord provenance2 = provenanceEvents.get(2);
        Assert.assertEquals(FORK, provenance2.getEventType());
    }

    @Test
    public void testWithNullIntColumn() throws SQLException {
        // remove previous test database, if any
        final File dbLocation = new File(TestSelectHive3QL.DB_LOCATION);
        dbLocation.delete();
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_NULL_INT");
        } catch (final SQLException sqle) {
            // Nothing to do, probably means the table didn't exist
        }
        stmt.execute("create table TEST_NULL_INT (id integer not null, val1 integer, val2 integer, constraint my_pk primary key (id))");
        stmt.execute("insert into TEST_NULL_INT (id, val1, val2) VALUES (0, NULL, 1)");
        stmt.execute("insert into TEST_NULL_INT (id, val1, val2) VALUES (1, 1, 1)");
        runner.setIncomingConnection(false);
        runner.setProperty(HIVEQL_SELECT_QUERY, "SELECT * FROM TEST_NULL_INT");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertAttributeEquals(RESULT_ROW_COUNT, "2");
    }

    @Test
    public void testWithSqlException() throws SQLException {
        // remove previous test database, if any
        final File dbLocation = new File(TestSelectHive3QL.DB_LOCATION);
        dbLocation.delete();
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("drop table TEST_NO_ROWS");
        } catch (final SQLException sqle) {
            // Nothing to do, probably means the table didn't exist
        }
        stmt.execute("create table TEST_NO_ROWS (id integer)");
        runner.setIncomingConnection(false);
        // Try a valid SQL statement that will generate an error (val1 does not exist, e.g.)
        runner.setProperty(HIVEQL_SELECT_QUERY, "SELECT val1 FROM TEST_NO_ROWS");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void invokeOnTriggerExceptionInPreQueriesNoIncomingFlows() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        doOnTrigger(TestSelectHive3QL.QUERY_WITHOUT_EL, false, CSV, "select 'no exception' from persons; select exception from persons", null);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void invokeOnTriggerExceptionInPreQueriesWithIncomingFlows() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        doOnTrigger(TestSelectHive3QL.QUERY_WITHOUT_EL, true, CSV, "select 'no exception' from persons; select exception from persons", null);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void invokeOnTriggerExceptionInPostQueriesNoIncomingFlows() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        doOnTrigger(TestSelectHive3QL.QUERY_WITHOUT_EL, false, CSV, null, "select 'no exception' from persons; select exception from persons");
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void invokeOnTriggerExceptionInPostQueriesWithIncomingFlows() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        doOnTrigger(TestSelectHive3QL.QUERY_WITHOUT_EL, true, CSV, null, "select 'no exception' from persons; select exception from persons");
        // with incoming connections, it should be rolled back
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testWithBadSQL() throws SQLException {
        final String BAD_SQL = "create table TEST_NO_ROWS (id integer)";
        // Test with incoming flow file (it should be routed to failure intact, i.e. same content and no parent)
        runner.setIncomingConnection(true);
        // Try a valid SQL statement that will generate an error (val1 does not exist, e.g.)
        runner.enqueue(BAD_SQL);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        flowFile.assertContentEquals(BAD_SQL);
        flowFile.assertAttributeEquals("parentIds", null);
        runner.clearTransferState();
        // Test with no incoming flow file (an empty flow file is transferred)
        runner.setIncomingConnection(false);
        // Try a valid SQL statement that will generate an error (val1 does not exist, e.g.)
        runner.setProperty(HIVEQL_SELECT_QUERY, BAD_SQL);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        flowFile = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        flowFile.assertContentEquals("");
    }

    @Test
    public void invokeOnTriggerWithCsv() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        invokeOnTrigger(TestSelectHive3QL.QUERY_WITHOUT_EL, false, CSV);
    }

    @Test
    public void invokeOnTriggerWithAvro() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        invokeOnTrigger(TestSelectHive3QL.QUERY_WITHOUT_EL, false, AVRO);
    }

    @Test
    public void invokeOnTriggerWithValidPreQueries() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // should not be 'select'. But Derby driver doesn't support "set param=val" format.
        invokeOnTrigger(TestSelectHive3QL.QUERY_WITHOUT_EL, false, CSV, "select '1' from persons; select '2' from persons", null);
    }

    @Test
    public void invokeOnTriggerWithValidPostQueries() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // should not be 'select'. But Derby driver doesn't support "set param=val" format,
        // so just providing any "compilable" query.
        invokeOnTrigger(TestSelectHive3QL.QUERY_WITHOUT_EL, false, CSV, null, " select \'4\' from persons; \nselect \'5\' from persons");
    }

    @Test
    public void invokeOnTriggerWithValidPrePostQueries() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // should not be 'select'. But Derby driver doesn't support "set param=val" format,
        // so just providing any "compilable" query.
        invokeOnTrigger(TestSelectHive3QL.QUERY_WITHOUT_EL, false, CSV, "select '1' from persons; select '2' from persons", " select \'4\' from persons; \nselect \'5\' from persons");
    }

    @Test
    public void testMaxRowsPerFlowFileAvro() throws IOException, ClassNotFoundException, SQLException, InitializationException {
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
        runner.setIncomingConnection(false);
        runner.setProperty(HIVEQL_SELECT_QUERY, "SELECT * FROM TEST_QUERY_DB_TABLE");
        runner.setProperty(MAX_ROWS_PER_FLOW_FILE, (("${" + (TestSelectHive3QL.MAX_ROWS_KEY)) + "}"));
        runner.setProperty(HIVEQL_OUTPUT_FORMAT, HiveJdbcCommon.AVRO);
        runner.setVariable(TestSelectHive3QL.MAX_ROWS_KEY, "9");
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
    }

    @Test
    public void testParametrizedQuery() throws IOException, ClassNotFoundException, SQLException, InitializationException {
        // load test data to database
        final Connection con = getConnection();
        Statement stmt = con.createStatement();
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
        runner.setIncomingConnection(true);
        runner.setProperty(MAX_ROWS_PER_FLOW_FILE, (("${" + (TestSelectHive3QL.MAX_ROWS_KEY)) + "}"));
        runner.setProperty(HIVEQL_OUTPUT_FORMAT, HiveJdbcCommon.AVRO);
        runner.setVariable(TestSelectHive3QL.MAX_ROWS_KEY, "9");
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("hiveql.args.1.value", "1");
        attributes.put("hiveql.args.1.type", String.valueOf(Types.INTEGER));
        runner.enqueue("SELECT * FROM TEST_QUERY_DB_TABLE WHERE id = ?", attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        // Assert the attributes from the incoming flow file are preserved in the outgoing flow file(s)
        flowFile.assertAttributeEquals("hiveql.args.1.value", "1");
        flowFile.assertAttributeEquals("hiveql.args.1.type", String.valueOf(Types.INTEGER));
        runner.clearTransferState();
    }

    @Test
    public void testMaxRowsPerFlowFileCSV() throws IOException, ClassNotFoundException, SQLException, InitializationException {
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
        runner.setIncomingConnection(true);
        runner.setProperty(MAX_ROWS_PER_FLOW_FILE, (("${" + (TestSelectHive3QL.MAX_ROWS_KEY)) + "}"));
        runner.setProperty(HIVEQL_OUTPUT_FORMAT, HiveJdbcCommon.CSV);
        runner.enqueue("SELECT * FROM TEST_QUERY_DB_TABLE", new HashMap<String, String>() {
            {
                put(TestSelectHive3QL.MAX_ROWS_KEY, "9");
            }
        });
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 12);
        // ensure all but the last file have 9 records (10 lines = 9 records + header) each
        for (int ff = 0; ff < 11; ff++) {
            mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(ff);
            in = new ByteArrayInputStream(mff.toByteArray());
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            Assert.assertEquals(10, br.lines().count());
            mff.assertAttributeExists("fragment.identifier");
            Assert.assertEquals(Integer.toString(ff), mff.getAttribute("fragment.index"));
            Assert.assertEquals("12", mff.getAttribute("fragment.count"));
        }
        // last file should have 1 record (2 lines = 1 record + header)
        mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(11);
        in = new ByteArrayInputStream(mff.toByteArray());
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        Assert.assertEquals(2, br.lines().count());
        mff.assertAttributeExists("fragment.identifier");
        Assert.assertEquals(Integer.toString(11), mff.getAttribute("fragment.index"));
        Assert.assertEquals("12", mff.getAttribute("fragment.count"));
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
        runner.setIncomingConnection(false);
        runner.setProperty(HIVEQL_SELECT_QUERY, "SELECT * FROM TEST_QUERY_DB_TABLE");
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

    /**
     * Simple implementation only for SelectHive3QL processor testing.
     */
    private class DBCPServiceSimpleImpl extends AbstractControllerService implements Hive3DBCPService {
        @Override
        public String getIdentifier() {
            return "dbcp";
        }

        @Override
        public Connection getConnection() throws ProcessException {
            try {
                Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
                return DriverManager.getConnection((("jdbc:derby:" + (TestSelectHive3QL.DB_LOCATION)) + ";create=true"));
            } catch (final Exception e) {
                throw new ProcessException(("getConnection failed: " + e));
            }
        }

        @Override
        public String getConnectionURL() {
            return ("jdbc:derby:" + (TestSelectHive3QL.DB_LOCATION)) + ";create=true";
        }
    }
}

