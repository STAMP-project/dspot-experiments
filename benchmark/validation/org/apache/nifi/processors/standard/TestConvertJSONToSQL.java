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


import ConvertJSONToSQL.CONNECTION_POOL;
import ConvertJSONToSQL.FAIL_UNMATCHED_COLUMN;
import ConvertJSONToSQL.FAIL_UNMATCHED_FIELD;
import ConvertJSONToSQL.IGNORE_UNMATCHED_FIELD;
import ConvertJSONToSQL.QUOTED_IDENTIFIERS;
import ConvertJSONToSQL.QUOTED_TABLE_IDENTIFIER;
import ConvertJSONToSQL.REL_FAILURE;
import ConvertJSONToSQL.REL_ORIGINAL;
import ConvertJSONToSQL.REL_SQL;
import ConvertJSONToSQL.SQL_PARAM_ATTR_PREFIX;
import ConvertJSONToSQL.STATEMENT_TYPE;
import ConvertJSONToSQL.TABLE_NAME;
import ConvertJSONToSQL.UNMATCHED_COLUMN_BEHAVIOR;
import ConvertJSONToSQL.UNMATCHED_FIELD_BEHAVIOR;
import ConvertJSONToSQL.UPDATE_KEY;
import ConvertJSONToSQL.WARNING_UNMATCHED_COLUMN;
import PutSQL.OBTAIN_GENERATED_KEYS;
import PutSQL.REL_SUCCESS;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.dbcp.DBCPService;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TestConvertJSONToSQL {
    static String createPersons = "CREATE TABLE PERSONS (id integer primary key, name varchar(100), code integer)";

    static String createDifferentTypes = "CREATE TABLE DIFTYPES (id integer primary key, b boolean, f float, dbl double, dcml decimal, d date)";

    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();

    /**
     * Setting up Connection pooling is expensive operation.
     * So let's do this only once and reuse MockDBCPService in each test.
     */
    protected static DBCPService service;

    @Test
    public void testInsert() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "INSERT");
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-1.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "1");
        runner.assertTransferCount(REL_SQL, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SQL).get(0);
        out.assertAttributeEquals("sql.args.1.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.1.value", "1");
        out.assertAttributeEquals("sql.args.2.type", String.valueOf(Types.VARCHAR));
        out.assertAttributeEquals("sql.args.2.value", "Mark");
        out.assertAttributeEquals("sql.args.3.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.3.value", "48");
        out.assertContentEquals("INSERT INTO PERSONS (ID, NAME, CODE) VALUES (?, ?, ?)");
    }

    @Test
    public void testInsertQuotedIdentifiers() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "INSERT");
        runner.setProperty(QUOTED_IDENTIFIERS, "true");
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-1.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "1");
        runner.assertTransferCount(REL_SQL, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SQL).get(0);
        out.assertAttributeEquals("sql.args.1.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.1.value", "1");
        out.assertAttributeEquals("sql.args.2.type", String.valueOf(Types.VARCHAR));
        out.assertAttributeEquals("sql.args.2.value", "Mark");
        out.assertAttributeEquals("sql.args.3.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.3.value", "48");
        out.assertContentEquals("INSERT INTO PERSONS (\"ID\", \"NAME\", \"CODE\") VALUES (?, ?, ?)");
    }

    @Test
    public void testInsertQuotedTableIdentifier() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "INSERT");
        runner.setProperty(QUOTED_TABLE_IDENTIFIER, "true");
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-1.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "1");
        runner.assertTransferCount(REL_SQL, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SQL).get(0);
        out.assertAttributeEquals("sql.args.1.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.1.value", "1");
        out.assertAttributeEquals("sql.args.2.type", String.valueOf(Types.VARCHAR));
        out.assertAttributeEquals("sql.args.2.value", "Mark");
        out.assertAttributeEquals("sql.args.3.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.3.value", "48");
        out.assertContentEquals("INSERT INTO \"PERSONS\" (ID, NAME, CODE) VALUES (?, ?, ?)");
    }

    @Test
    public void testInsertWithNullValue() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "INSERT");
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-with-null-code.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "1");
        runner.assertTransferCount(REL_SQL, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SQL).get(0);
        out.assertAttributeEquals("sql.args.1.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.1.value", "1");
        out.assertAttributeEquals("sql.args.2.type", String.valueOf(Types.VARCHAR));
        out.assertAttributeEquals("sql.args.2.value", "Mark");
        out.assertAttributeEquals("sql.args.3.type", String.valueOf(Types.INTEGER));
        out.assertAttributeNotExists("sql.args.3.value");
        out.assertContentEquals("INSERT INTO PERSONS (ID, NAME, CODE) VALUES (?, ?, ?)");
    }

    @Test
    public void testInsertBoolToInteger() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "INSERT");
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-with-bool.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "1");
        runner.assertTransferCount(REL_SQL, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SQL).get(0);
        out.assertAttributeEquals("sql.args.1.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.1.value", "1");
        out.assertAttributeEquals("sql.args.2.type", String.valueOf(Types.VARCHAR));
        out.assertAttributeEquals("sql.args.2.value", "Bool");
        out.assertAttributeEquals("sql.args.3.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.3.value", "1");
        out.assertContentEquals("INSERT INTO PERSONS (ID, NAME, CODE) VALUES (?, ?, ?)");
    }

    @Test
    public void testUpdateWithNullValue() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "UPDATE");
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-with-null-code.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "1");
        runner.assertTransferCount(REL_SQL, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SQL).get(0);
        out.assertAttributeEquals("sql.args.1.type", String.valueOf(Types.VARCHAR));
        out.assertAttributeEquals("sql.args.1.value", "Mark");
        out.assertAttributeEquals("sql.args.2.type", String.valueOf(Types.INTEGER));
        out.assertAttributeNotExists("sql.args.2.value");
        out.assertAttributeEquals("sql.args.3.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.3.value", "1");
        out.assertContentEquals("UPDATE PERSONS SET NAME = ?, CODE = ? WHERE ID = ?");
    }

    @Test
    public void testUpdateQuotedTableIdentifier() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "UPDATE");
        runner.setProperty(QUOTED_TABLE_IDENTIFIER, "true");
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-with-null-code.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "1");
        runner.assertTransferCount(REL_SQL, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SQL).get(0);
        out.assertAttributeEquals("sql.args.1.type", String.valueOf(Types.VARCHAR));
        out.assertAttributeEquals("sql.args.1.value", "Mark");
        out.assertAttributeEquals("sql.args.2.type", String.valueOf(Types.INTEGER));
        out.assertAttributeNotExists("sql.args.2.value");
        out.assertAttributeEquals("sql.args.3.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.3.value", "1");
        out.assertContentEquals("UPDATE \"PERSONS\" SET NAME = ?, CODE = ? WHERE ID = ?");
    }

    @Test
    public void testMultipleInserts() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "INSERT");
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/persons.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "5");
        runner.assertTransferCount(REL_SQL, 5);
        final List<MockFlowFile> mffs = runner.getFlowFilesForRelationship(REL_SQL);
        for (final MockFlowFile mff : mffs) {
            mff.assertContentEquals("INSERT INTO PERSONS (ID, NAME, CODE) VALUES (?, ?, ?)");
            for (int i = 1; i <= 3; i++) {
                mff.assertAttributeExists((("sql.args." + i) + ".type"));
                mff.assertAttributeExists((("sql.args." + i) + ".value"));
            }
        }
    }

    @Test
    public void testMultipleInsertsQuotedIdentifiers() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "INSERT");
        runner.setProperty(QUOTED_IDENTIFIERS, "true");
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/persons.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "5");
        runner.assertTransferCount(REL_SQL, 5);
        final List<MockFlowFile> mffs = runner.getFlowFilesForRelationship(REL_SQL);
        for (final MockFlowFile mff : mffs) {
            mff.assertContentEquals("INSERT INTO PERSONS (\"ID\", \"NAME\", \"CODE\") VALUES (?, ?, ?)");
            for (int i = 1; i <= 3; i++) {
                mff.assertAttributeExists((("sql.args." + i) + ".type"));
                mff.assertAttributeExists((("sql.args." + i) + ".value"));
            }
        }
    }

    @Test
    public void testUpdateBasedOnPrimaryKey() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "UPDATE");
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-1.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "1");
        runner.assertTransferCount(REL_SQL, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SQL).get(0);
        out.assertAttributeEquals("sql.args.1.type", String.valueOf(Types.VARCHAR));
        out.assertAttributeEquals("sql.args.1.value", "Mark");
        out.assertAttributeEquals("sql.args.2.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.2.value", "48");
        out.assertAttributeEquals("sql.args.3.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.3.value", "1");
        out.assertContentEquals("UPDATE PERSONS SET NAME = ?, CODE = ? WHERE ID = ?");
    }

    @Test
    public void testUpdateBasedOnPrimaryKeyQuotedIdentifier() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "UPDATE");
        runner.setProperty(QUOTED_IDENTIFIERS, "true");
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-1.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "1");
        runner.assertTransferCount(REL_SQL, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SQL).get(0);
        out.assertAttributeEquals("sql.args.1.type", String.valueOf(Types.VARCHAR));
        out.assertAttributeEquals("sql.args.1.value", "Mark");
        out.assertAttributeEquals("sql.args.2.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.2.value", "48");
        out.assertAttributeEquals("sql.args.3.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.3.value", "1");
        out.assertContentEquals("UPDATE PERSONS SET \"NAME\" = ?, \"CODE\" = ? WHERE \"ID\" = ?");
    }

    @Test
    public void testUnmappedFieldBehavior() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "INSERT");
        runner.setProperty(UNMATCHED_FIELD_BEHAVIOR, IGNORE_UNMATCHED_FIELD.getValue());
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-with-extra-field.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "1");
        runner.assertTransferCount(REL_SQL, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SQL).get(0);
        out.assertContentEquals("INSERT INTO PERSONS (ID, NAME, CODE) VALUES (?, ?, ?)");
        runner.clearTransferState();
        runner.setProperty(UNMATCHED_FIELD_BEHAVIOR, FAIL_UNMATCHED_FIELD.getValue());
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-with-extra-field.json"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testUpdateBasedOnUpdateKey() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "UPDATE");
        runner.setProperty(UPDATE_KEY, "code");
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-1.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "1");
        runner.assertTransferCount(REL_SQL, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SQL).get(0);
        out.assertAttributeEquals("sql.args.1.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.1.value", "1");
        out.assertAttributeEquals("sql.args.2.type", String.valueOf(Types.VARCHAR));
        out.assertAttributeEquals("sql.args.2.value", "Mark");
        out.assertAttributeEquals("sql.args.3.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.3.value", "48");
        out.assertContentEquals("UPDATE PERSONS SET ID = ?, NAME = ? WHERE CODE = ?");
    }

    @Test
    public void testUpdateBasedOnUpdateKeyQuotedIdentifier() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "UPDATE");
        runner.setProperty(UPDATE_KEY, "code");
        runner.setProperty(QUOTED_IDENTIFIERS, "true");
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-1.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "1");
        runner.assertTransferCount(REL_SQL, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SQL).get(0);
        out.assertAttributeEquals("sql.args.1.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.1.value", "1");
        out.assertAttributeEquals("sql.args.2.type", String.valueOf(Types.VARCHAR));
        out.assertAttributeEquals("sql.args.2.value", "Mark");
        out.assertAttributeEquals("sql.args.3.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.3.value", "48");
        out.assertContentEquals("UPDATE PERSONS SET \"ID\" = ?, \"NAME\" = ? WHERE \"CODE\" = ?");
    }

    @Test
    public void testUpdateBasedOnCompoundUpdateKey() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "UPDATE");
        runner.setProperty(UPDATE_KEY, "name,  code");
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-1.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile originalFlowFile = runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0);
        originalFlowFile.assertAttributeExists(FRAGMENT_ID.key());
        originalFlowFile.assertAttributeEquals(FRAGMENT_COUNT.key(), "1");
        runner.assertTransferCount(REL_SQL, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SQL).get(0);
        out.assertAttributeEquals("sql.args.1.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.1.value", "1");
        out.assertAttributeEquals("sql.args.2.type", String.valueOf(Types.VARCHAR));
        out.assertAttributeEquals("sql.args.2.value", "Mark");
        out.assertAttributeEquals("sql.args.3.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.3.value", "48");
        out.assertContentEquals("UPDATE PERSONS SET ID = ? WHERE NAME = ? AND CODE = ?");
    }

    @Test
    public void testUpdateWithMissingFieldBasedOnCompoundUpdateKey() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "UPDATE");
        runner.setProperty(UPDATE_KEY, "name,  code");
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-without-code.json"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testUpdateWithMalformedJson() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "UPDATE");
        runner.setProperty(UPDATE_KEY, "name,  code");
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/malformed-person-extra-comma.json"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testInsertWithMissingField() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "INSERT");
        runner.setProperty(UPDATE_KEY, "name,  code");
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-without-id.json"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testInsertWithMissingColumnFail() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        try (final Connection conn = TestConvertJSONToSQL.service.getConnection()) {
            try (final Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE TABLE PERSONS3 (id integer, name varchar(100), code integer, generated_key integer primary key)");
            }
        }
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS3");
        runner.setProperty(STATEMENT_TYPE, "INSERT");
        runner.setProperty(UNMATCHED_COLUMN_BEHAVIOR, FAIL_UNMATCHED_COLUMN);
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-1.json"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }// End testInsertWithMissingColumnFail()


    @Test
    public void testInsertWithMissingColumnWarning() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        try (final Connection conn = TestConvertJSONToSQL.service.getConnection()) {
            try (final Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE TABLE PERSONS2 (id integer, name varchar(100), code integer, generated_key integer primary key)");
            }
        }
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS2");
        runner.setProperty(STATEMENT_TYPE, "INSERT");
        runner.setProperty(UNMATCHED_COLUMN_BEHAVIOR, WARNING_UNMATCHED_COLUMN);
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-1.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "1");
        runner.assertTransferCount(REL_SQL, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SQL).get(0);
        out.assertAttributeEquals("sql.args.1.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.1.value", "1");
        out.assertAttributeEquals("sql.args.2.type", String.valueOf(Types.VARCHAR));
        out.assertAttributeEquals("sql.args.2.value", "Mark");
        out.assertAttributeEquals("sql.args.3.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.3.value", "48");
        out.assertContentEquals("INSERT INTO PERSONS2 (ID, NAME, CODE) VALUES (?, ?, ?)");
    }// End testInsertWithMissingColumnWarning()


    @Test
    public void testInsertWithMissingColumnIgnore() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "INSERT");
        runner.setProperty(UNMATCHED_COLUMN_BEHAVIOR, "Ignore Unmatched Columns");
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-1.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "1");
        runner.assertTransferCount(REL_SQL, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SQL).get(0);
        out.assertAttributeEquals("sql.args.1.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.1.value", "1");
        out.assertAttributeEquals("sql.args.2.type", String.valueOf(Types.VARCHAR));
        out.assertAttributeEquals("sql.args.2.value", "Mark");
        out.assertAttributeEquals("sql.args.3.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.3.value", "48");
        out.assertContentEquals("INSERT INTO PERSONS (ID, NAME, CODE) VALUES (?, ?, ?)");
    }// End testInsertWithMissingColumnIgnore()


    @Test
    public void testUpdateWithMissingColumnFail() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "UPDATE");
        runner.setProperty(UPDATE_KEY, "name,  code, extra");
        runner.setProperty(UNMATCHED_COLUMN_BEHAVIOR, FAIL_UNMATCHED_COLUMN);
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-1.json"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }// End testUpdateWithMissingColumnFail()


    @Test
    public void testUpdateWithMissingColumnWarning() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "UPDATE");
        runner.setProperty(UPDATE_KEY, "name,  code, extra");
        runner.setProperty(UNMATCHED_COLUMN_BEHAVIOR, WARNING_UNMATCHED_COLUMN);
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-1.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "1");
        runner.assertTransferCount(REL_SQL, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SQL).get(0);
        out.assertAttributeEquals("sql.args.1.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.1.value", "1");
        out.assertAttributeEquals("sql.args.2.type", String.valueOf(Types.VARCHAR));
        out.assertAttributeEquals("sql.args.2.value", "Mark");
        out.assertAttributeEquals("sql.args.3.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.3.value", "48");
        out.assertContentEquals("UPDATE PERSONS SET ID = ? WHERE NAME = ? AND CODE = ?");
    }// End testUpdateWithMissingColumnWarning()


    @Test
    public void testUpdateWithMissingColumnIgnore() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "UPDATE");
        runner.setProperty(UPDATE_KEY, "name,  code, extra");
        runner.setProperty(UNMATCHED_COLUMN_BEHAVIOR, "Ignore Unmatched Columns");
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-1.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "1");
        runner.assertTransferCount(REL_SQL, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SQL).get(0);
        out.assertAttributeEquals("sql.args.1.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.1.value", "1");
        out.assertAttributeEquals("sql.args.2.type", String.valueOf(Types.VARCHAR));
        out.assertAttributeEquals("sql.args.2.value", "Mark");
        out.assertAttributeEquals("sql.args.3.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.3.value", "48");
        out.assertContentEquals("UPDATE PERSONS SET ID = ? WHERE NAME = ? AND CODE = ?");
    }// End testUpdateWithMissingColumnIgnore()


    /**
     * Test create correct SQL String representation of value.
     *  Use PutSQL processor to verify converted value can be used and don't fail.
     */
    @Test
    public void testCreateSqlStringValue() throws IOException, SQLException, ProcessException, InitializationException, JsonGenerationException, JsonMappingException {
        final TestRunner putSqlRunner = TestRunners.newTestRunner(PutSQL.class);
        final AtomicInteger id = new AtomicInteger(20);
        putSqlRunner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        putSqlRunner.enableControllerService(TestConvertJSONToSQL.service);
        putSqlRunner.setProperty(OBTAIN_GENERATED_KEYS, "false");
        putSqlRunner.setProperty(PutSQL.CONNECTION_POOL, "dbcp");
        String tableName = "DIFTYPES";
        ObjectMapper mapper = new ObjectMapper();
        ResultSet colrs = null;
        try (final Connection conn = TestConvertJSONToSQL.service.getConnection()) {
            try (final Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(TestConvertJSONToSQL.createDifferentTypes);
            }
            colrs = conn.getMetaData().getColumns(null, null, tableName, "%");
            while (colrs.next()) {
                final int sqlType = colrs.getInt("DATA_TYPE");
                final int colSize = colrs.getInt("COLUMN_SIZE");
                switch (sqlType) {
                    case Types.BOOLEAN :
                        String json = mapper.writeValueAsString("true");
                        JsonNode fieldNode = mapper.readTree(json);
                        String booleanString = ConvertJSONToSQL.createSqlStringValue(fieldNode, colSize, sqlType);
                        Assert.assertEquals("true", booleanString);
                        Map<String, String> attributes = new HashMap<>();
                        attributes.put("sql.args.1.type", String.valueOf(sqlType));
                        attributes.put("sql.args.1.value", booleanString);
                        byte[] data = (("INSERT INTO DIFTYPES (ID, B) VALUES (" + (id.incrementAndGet())) + ", ?)").getBytes();
                        putSqlRunner.enqueue(data, attributes);
                        putSqlRunner.run();
                        List<MockFlowFile> failed = putSqlRunner.getFlowFilesForRelationship(PutSQL.REL_FAILURE);
                        putSqlRunner.assertTransferCount(REL_SUCCESS, 1);
                        putSqlRunner.assertTransferCount(PutSQL.REL_FAILURE, 0);
                        putSqlRunner.clearTransferState();
                        break;
                    case Types.FLOAT :
                    case Types.DOUBLE :
                        json = mapper.writeValueAsString("78895654.6575");
                        fieldNode = mapper.readTree(json);
                        String numberString = ConvertJSONToSQL.createSqlStringValue(fieldNode, colSize, sqlType);
                        Assert.assertEquals("78895654.6575", numberString);
                        attributes = new HashMap<>();
                        attributes.put("sql.args.1.type", String.valueOf(sqlType));
                        attributes.put("sql.args.1.value", numberString);
                        data = (("INSERT INTO DIFTYPES (ID, dbl) VALUES (" + (id.incrementAndGet())) + ", ?)").getBytes();
                        putSqlRunner.enqueue(data, attributes);
                        putSqlRunner.run();
                        failed = putSqlRunner.getFlowFilesForRelationship(PutSQL.REL_FAILURE);
                        putSqlRunner.assertTransferCount(REL_SUCCESS, 1);
                        putSqlRunner.assertTransferCount(PutSQL.REL_FAILURE, 0);
                        putSqlRunner.clearTransferState();
                        break;
                    default :
                        break;
                }
            } 
        }
    }

    @Test
    public void testDelete() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "DELETE");
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-1.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "1");
        runner.assertTransferCount(REL_SQL, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SQL).get(0);
        out.assertAttributeEquals("sql.args.1.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.1.value", "1");
        out.assertAttributeEquals("sql.args.2.type", String.valueOf(Types.VARCHAR));
        out.assertAttributeEquals("sql.args.2.value", "Mark");
        out.assertAttributeEquals("sql.args.3.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.3.value", "48");
        out.assertContentEquals("DELETE FROM PERSONS WHERE ID = ? AND NAME = ? AND CODE = ?");
    }

    @Test
    public void testDeleteQuotedIdentifiers() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "DELETE");
        runner.setProperty(QUOTED_IDENTIFIERS, "true");
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-1.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "1");
        runner.assertTransferCount(REL_SQL, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SQL).get(0);
        out.assertAttributeEquals("sql.args.1.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.1.value", "1");
        out.assertAttributeEquals("sql.args.2.type", String.valueOf(Types.VARCHAR));
        out.assertAttributeEquals("sql.args.2.value", "Mark");
        out.assertAttributeEquals("sql.args.3.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.3.value", "48");
        out.assertContentEquals("DELETE FROM PERSONS WHERE \"ID\" = ? AND \"NAME\" = ? AND \"CODE\" = ?");
    }

    @Test
    public void testDeleteQuotedTableIdentifier() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "DELETE");
        runner.setProperty(QUOTED_TABLE_IDENTIFIER, "true");
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-1.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "1");
        runner.assertTransferCount(REL_SQL, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SQL).get(0);
        out.assertAttributeEquals("sql.args.1.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.1.value", "1");
        out.assertAttributeEquals("sql.args.2.type", String.valueOf(Types.VARCHAR));
        out.assertAttributeEquals("sql.args.2.value", "Mark");
        out.assertAttributeEquals("sql.args.3.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.3.value", "48");
        out.assertContentEquals("DELETE FROM \"PERSONS\" WHERE ID = ? AND NAME = ? AND CODE = ?");
    }

    @Test
    public void testDeleteWithNullValue() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "DELETE");
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-with-null-code.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "1");
        runner.assertTransferCount(REL_SQL, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SQL).get(0);
        out.assertAttributeEquals("sql.args.1.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("sql.args.1.value", "1");
        out.assertAttributeEquals("sql.args.2.type", String.valueOf(Types.VARCHAR));
        out.assertAttributeEquals("sql.args.2.value", "Mark");
        out.assertAttributeEquals("sql.args.3.type", String.valueOf(Types.INTEGER));
        out.assertAttributeNotExists("sql.args.3.value");
        out.assertContentEquals("DELETE FROM PERSONS WHERE ID = ? AND NAME = ? AND CODE = ?");
    }

    @Test
    public void testAttributePrefix() throws IOException, SQLException, ProcessException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertJSONToSQL.class);
        runner.addControllerService("dbcp", TestConvertJSONToSQL.service);
        runner.enableControllerService(TestConvertJSONToSQL.service);
        runner.setProperty(CONNECTION_POOL, "dbcp");
        runner.setProperty(TABLE_NAME, "PERSONS");
        runner.setProperty(STATEMENT_TYPE, "INSERT");
        runner.setProperty(QUOTED_IDENTIFIERS, "true");
        runner.setProperty(SQL_PARAM_ATTR_PREFIX, "hiveql");
        runner.enqueue(Paths.get("src/test/resources/TestConvertJSONToSQL/person-1.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "1");
        runner.assertTransferCount(REL_SQL, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SQL).get(0);
        out.assertAttributeEquals("hiveql.args.1.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("hiveql.args.1.value", "1");
        out.assertAttributeEquals("hiveql.args.2.type", String.valueOf(Types.VARCHAR));
        out.assertAttributeEquals("hiveql.args.2.value", "Mark");
        out.assertAttributeEquals("hiveql.args.3.type", String.valueOf(Types.INTEGER));
        out.assertAttributeEquals("hiveql.args.3.value", "48");
        out.assertContentEquals("INSERT INTO PERSONS (\"ID\", \"NAME\", \"CODE\") VALUES (?, ?, ?)");
    }

    /**
     * Simple implementation only for testing purposes
     */
    private static class MockDBCPService extends AbstractControllerService implements DBCPService {
        private final String dbLocation;

        public MockDBCPService(final String dbLocation) {
            this.dbLocation = dbLocation;
        }

        @Override
        public String getIdentifier() {
            return "dbcp";
        }

        @Override
        public Connection getConnection() throws ProcessException {
            try {
                Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
                final Connection con = DriverManager.getConnection((("jdbc:derby:" + (dbLocation)) + ";create=true"));
                return con;
            } catch (final Exception e) {
                e.printStackTrace();
                throw new ProcessException(("getConnection failed: " + e));
            }
        }
    }
}

