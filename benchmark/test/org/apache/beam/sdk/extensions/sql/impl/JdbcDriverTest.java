/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.extensions.sql.impl;


import Duration.ZERO;
import JdbcDriver.CONNECT_STRING_PREFIX;
import Schema.FieldType;
import Schema.FieldType.DATETIME;
import Schema.FieldType.INT32;
import Schema.FieldType.INT64;
import Schema.FieldType.STRING;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.beam.sdk.extensions.sql.meta.provider.ReadOnlyTableProvider;
import org.apache.beam.sdk.extensions.sql.meta.provider.test.TestBoundedTable;
import org.apache.beam.sdk.extensions.sql.meta.provider.test.TestTableProvider;
import org.apache.beam.sdk.extensions.sql.meta.provider.test.TestUnboundedTable;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.util.ReleaseInfo;
import org.apache.beam.sdk.values.Row;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.schema.SchemaPlus;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static JdbcDriver.CONNECT_STRING_PREFIX;


/**
 * Test for {@link JdbcDriver}.
 */
public class JdbcDriverTest {
    public static final DateTime FIRST_DATE = new DateTime(1);

    private static final Schema BASIC_SCHEMA = Schema.builder().addNullableField("id", INT64).addNullableField("name", STRING).build();

    private static final Schema COMPLEX_SCHEMA = Schema.builder().addNullableField("description", STRING).addNullableField("nestedRow", FieldType.row(JdbcDriverTest.BASIC_SCHEMA)).build();

    private static final ReadOnlyTableProvider BOUNDED_TABLE = new ReadOnlyTableProvider("test", ImmutableMap.of("test", TestBoundedTable.of(INT32, "id", STRING, "name").addRows(1, "first")));

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testDriverManager_getDriver() throws Exception {
        Driver driver = DriverManager.getDriver(CONNECT_STRING_PREFIX);
        Assert.assertTrue((driver instanceof JdbcDriver));
    }

    @Test
    public void testDriverManager_simple() throws Exception {
        Connection connection = DriverManager.getConnection(CONNECT_STRING_PREFIX);
        Statement statement = connection.createStatement();
        // SELECT 1 is a special case and does not reach the parser
        Assert.assertTrue(statement.execute("SELECT 1"));
    }

    /**
     * Tests that the userAgent is set in the pipeline options of the connection.
     */
    @Test
    public void testDriverManager_defaultUserAgent() throws Exception {
        Connection connection = DriverManager.getConnection(CONNECT_STRING_PREFIX);
        SchemaPlus rootSchema = getRootSchema();
        BeamCalciteSchema beamSchema = ((BeamCalciteSchema) (CalciteSchema.from(rootSchema.getSubSchema("beam")).schema));
        Map<String, String> pipelineOptions = beamSchema.getPipelineOptions();
        Assert.assertThat(pipelineOptions.get("userAgent"), Matchers.containsString("BeamSQL"));
    }

    /**
     * Tests that userAgent is set.
     */
    @Test
    public void testDriverManager_hasUserAgent() throws Exception {
        JdbcConnection connection = ((JdbcConnection) (DriverManager.getConnection(CONNECT_STRING_PREFIX)));
        BeamCalciteSchema schema = connection.getCurrentBeamSchema();
        Assert.assertThat(schema.getPipelineOptions().get("userAgent"), Matchers.equalTo(("BeamSQL/" + (ReleaseInfo.getReleaseInfo().getVersion()))));
    }

    /**
     * Tests that userAgent can be overridden on the querystring.
     */
    @Test
    public void testDriverManager_setUserAgent() throws Exception {
        Connection connection = DriverManager.getConnection(((CONNECT_STRING_PREFIX) + "beam.userAgent=Secret Agent"));
        SchemaPlus rootSchema = getRootSchema();
        BeamCalciteSchema beamSchema = ((BeamCalciteSchema) (CalciteSchema.from(rootSchema.getSubSchema("beam")).schema));
        Map<String, String> pipelineOptions = beamSchema.getPipelineOptions();
        Assert.assertThat(pipelineOptions.get("userAgent"), Matchers.equalTo("Secret Agent"));
    }

    /**
     * Tests that unknown pipeline options are passed verbatim from the JDBC URI.
     */
    @Test
    public void testDriverManager_pipelineOptionsPlumbing() throws Exception {
        Connection connection = DriverManager.getConnection(((CONNECT_STRING_PREFIX) + "beam.foo=baz;beam.foobizzle=mahshizzle;other=smother"));
        SchemaPlus rootSchema = getRootSchema();
        BeamCalciteSchema beamSchema = ((BeamCalciteSchema) (CalciteSchema.from(rootSchema.getSubSchema("beam")).schema));
        Map<String, String> pipelineOptions = beamSchema.getPipelineOptions();
        Assert.assertThat(pipelineOptions.get("foo"), Matchers.equalTo("baz"));
        Assert.assertThat(pipelineOptions.get("foobizzle"), Matchers.equalTo("mahshizzle"));
        Assert.assertThat(pipelineOptions.get("other"), Matchers.nullValue());
    }

    @Test
    public void testDriverManager_parse() throws Exception {
        Connection connection = DriverManager.getConnection(CONNECT_STRING_PREFIX);
        Statement statement = connection.createStatement();
        Assert.assertTrue(statement.execute("SELECT 'beam'"));
    }

    @Test
    public void testDriverManager_ddl() throws Exception {
        Connection connection = DriverManager.getConnection(CONNECT_STRING_PREFIX);
        // Ensure no tables
        final DatabaseMetaData metadata = connection.getMetaData();
        ResultSet resultSet = metadata.getTables(null, null, null, new String[]{ "TABLE" });
        Assert.assertFalse(resultSet.next());
        // create external tables
        Statement statement = connection.createStatement();
        Assert.assertEquals(0, statement.executeUpdate("CREATE EXTERNAL TABLE test (id INTEGER) TYPE 'text'"));
        // Ensure table test
        resultSet = metadata.getTables(null, null, null, new String[]{ "TABLE" });
        Assert.assertTrue(resultSet.next());
        Assert.assertEquals("test", resultSet.getString("TABLE_NAME"));
        Assert.assertFalse(resultSet.next());
        Assert.assertEquals(0, statement.executeUpdate("DROP TABLE test"));
        // Ensure no tables
        resultSet = metadata.getTables(null, null, null, new String[]{ "TABLE" });
        Assert.assertFalse(resultSet.next());
    }

    @Test
    public void testSelectsFromExistingTable() throws Exception {
        TestTableProvider tableProvider = new TestTableProvider();
        Connection connection = JdbcDriver.connect(tableProvider);
        connection.createStatement().executeUpdate("CREATE EXTERNAL TABLE person (id BIGINT, name VARCHAR) TYPE 'test'");
        tableProvider.addRows("person", row(1L, "aaa"), row(2L, "bbb"));
        ResultSet selectResult = connection.createStatement().executeQuery("SELECT id, name FROM person");
        List<Row> resultRows = readResultSet(selectResult).stream().map(( values) -> values.stream().collect(toRow(JdbcDriverTest.BASIC_SCHEMA))).collect(Collectors.toList());
        Assert.assertThat(resultRows, Matchers.containsInAnyOrder(row(1L, "aaa"), row(2L, "bbb")));
    }

    @Test
    public void testTimestampWithDefaultTimezone() throws Exception {
        TestTableProvider tableProvider = new TestTableProvider();
        Connection connection = JdbcDriver.connect(tableProvider);
        // A table with one TIMESTAMP column
        Schema schema = Schema.builder().addDateTimeField("ts").build();
        connection.createStatement().executeUpdate("CREATE EXTERNAL TABLE test (ts TIMESTAMP) TYPE 'test'");
        ReadableInstant july1 = ISODateTimeFormat.dateTimeParser().parseDateTime("2018-07-01T01:02:03Z");
        tableProvider.addRows("test", Row.withSchema(schema).addValue(july1).build());
        ResultSet selectResult = connection.createStatement().executeQuery(String.format("SELECT ts FROM test"));
        selectResult.next();
        Timestamp ts = selectResult.getTimestamp(1);
        Assert.assertThat(String.format("Wrote %s to a table, but got back %s", ISODateTimeFormat.basicDateTime().print(july1), ISODateTimeFormat.basicDateTime().print(ts.getTime())), ts.getTime(), Matchers.equalTo(july1.getMillis()));
    }

    @Test
    public void testTimestampWithZeroTimezone() throws Exception {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ROOT);
        TestTableProvider tableProvider = new TestTableProvider();
        Connection connection = JdbcDriver.connect(tableProvider);
        // A table with one TIMESTAMP column
        Schema schema = Schema.builder().addDateTimeField("ts").build();
        connection.createStatement().executeUpdate("CREATE EXTERNAL TABLE test (ts TIMESTAMP) TYPE 'test'");
        ReadableInstant july1 = ISODateTimeFormat.dateTimeParser().parseDateTime("2018-07-01T01:02:03Z");
        tableProvider.addRows("test", Row.withSchema(schema).addValue(july1).build());
        ResultSet selectResult = connection.createStatement().executeQuery(String.format("SELECT ts FROM test"));
        selectResult.next();
        Timestamp ts = selectResult.getTimestamp(1, cal);
        Assert.assertThat(String.format("Wrote %s to a table, but got back %s", ISODateTimeFormat.basicDateTime().print(july1), ISODateTimeFormat.basicDateTime().print(ts.getTime())), ts.getTime(), Matchers.equalTo(july1.getMillis()));
    }

    @Test
    public void testSelectsFromExistingComplexTable() throws Exception {
        TestTableProvider tableProvider = new TestTableProvider();
        Connection connection = JdbcDriver.connect(tableProvider);
        connection.createStatement().executeUpdate(("CREATE EXTERNAL TABLE person ( \n" + ((((("description VARCHAR, \n" + "nestedRow ROW< \n") + "              id BIGINT, \n") + "              name VARCHAR> \n") + ") \n") + "TYPE 'test'")));
        tableProvider.addRows("person", row(JdbcDriverTest.COMPLEX_SCHEMA, "description1", row(1L, "aaa")), row(JdbcDriverTest.COMPLEX_SCHEMA, "description2", row(2L, "bbb")));
        ResultSet selectResult = connection.createStatement().executeQuery("SELECT person.nestedRow.id, person.nestedRow.name FROM person");
        List<Row> resultRows = readResultSet(selectResult).stream().map(( values) -> values.stream().collect(toRow(JdbcDriverTest.BASIC_SCHEMA))).collect(Collectors.toList());
        Assert.assertThat(resultRows, Matchers.containsInAnyOrder(row(1L, "aaa"), row(2L, "bbb")));
    }

    @Test
    public void testInsertIntoCreatedTable() throws Exception {
        TestTableProvider tableProvider = new TestTableProvider();
        Connection connection = JdbcDriver.connect(tableProvider);
        connection.createStatement().executeUpdate("CREATE EXTERNAL TABLE person (id BIGINT, name VARCHAR) TYPE 'test'");
        connection.createStatement().executeUpdate("CREATE EXTERNAL TABLE person_src (id BIGINT, name VARCHAR) TYPE 'test'");
        tableProvider.addRows("person_src", row(1L, "aaa"), row(2L, "bbb"));
        connection.createStatement().execute("INSERT INTO person SELECT id, name FROM person_src");
        ResultSet selectResult = connection.createStatement().executeQuery("SELECT id, name FROM person");
        List<Row> resultRows = readResultSet(selectResult).stream().map(( resultValues) -> resultValues.stream().collect(toRow(JdbcDriverTest.BASIC_SCHEMA))).collect(Collectors.toList());
        Assert.assertThat(resultRows, Matchers.containsInAnyOrder(row(1L, "aaa"), row(2L, "bbb")));
    }

    @Test
    public void testInternalConnect_boundedTable() throws Exception {
        CalciteConnection connection = JdbcDriver.connect(JdbcDriverTest.BOUNDED_TABLE);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM test");
        Assert.assertTrue(resultSet.next());
        Assert.assertEquals(1, resultSet.getInt("id"));
        Assert.assertEquals("first", resultSet.getString("name"));
        Assert.assertFalse(resultSet.next());
    }

    @Test
    public void testInternalConnect_bounded_limit() throws Exception {
        ReadOnlyTableProvider tableProvider = new ReadOnlyTableProvider("test", ImmutableMap.of("test", TestBoundedTable.of(INT32, "id", STRING, "name").addRows(1, "first").addRows(1, "second first").addRows(2, "second")));
        CalciteConnection connection = JdbcDriver.connect(tableProvider);
        Statement statement = connection.createStatement();
        ResultSet resultSet1 = statement.executeQuery("SELECT * FROM test LIMIT 5");
        Assert.assertTrue(resultSet1.next());
        Assert.assertTrue(resultSet1.next());
        Assert.assertTrue(resultSet1.next());
        Assert.assertFalse(resultSet1.next());
        Assert.assertFalse(resultSet1.next());
        ResultSet resultSet2 = statement.executeQuery("SELECT * FROM test LIMIT 1");
        Assert.assertTrue(resultSet2.next());
        Assert.assertFalse(resultSet2.next());
        ResultSet resultSet3 = statement.executeQuery("SELECT * FROM test LIMIT 2");
        Assert.assertTrue(resultSet3.next());
        Assert.assertTrue(resultSet3.next());
        Assert.assertFalse(resultSet3.next());
        ResultSet resultSet4 = statement.executeQuery("SELECT * FROM test LIMIT 3");
        Assert.assertTrue(resultSet4.next());
        Assert.assertTrue(resultSet4.next());
        Assert.assertTrue(resultSet4.next());
        Assert.assertFalse(resultSet4.next());
    }

    @Test
    public void testInternalConnect_unbounded_limit() throws Exception {
        ReadOnlyTableProvider tableProvider = new ReadOnlyTableProvider("test", ImmutableMap.of("test", TestUnboundedTable.of(INT32, "order_id", INT32, "site_id", INT32, "price", DATETIME, "order_time").timestampColumnIndex(3).addRows(ZERO, 1, 1, 1, JdbcDriverTest.FIRST_DATE, 1, 2, 6, JdbcDriverTest.FIRST_DATE)));
        CalciteConnection connection = JdbcDriver.connect(tableProvider);
        Statement statement = connection.createStatement();
        ResultSet resultSet1 = statement.executeQuery("SELECT * FROM test LIMIT 1");
        Assert.assertTrue(resultSet1.next());
        Assert.assertFalse(resultSet1.next());
        ResultSet resultSet2 = statement.executeQuery("SELECT * FROM test LIMIT 2");
        Assert.assertTrue(resultSet2.next());
        Assert.assertTrue(resultSet2.next());
        Assert.assertFalse(resultSet2.next());
    }

    @Test
    public void testInternalConnect_setDirectRunner() throws Exception {
        CalciteConnection connection = JdbcDriver.connect(JdbcDriverTest.BOUNDED_TABLE);
        Statement statement = connection.createStatement();
        Assert.assertEquals(0, statement.executeUpdate("SET runner = direct"));
        Assert.assertTrue(statement.execute("SELECT * FROM test"));
    }

    @Test
    public void testInternalConnect_setBogusRunner() throws Exception {
        thrown.expectMessage("Unknown 'runner' specified 'bogus'");
        CalciteConnection connection = JdbcDriver.connect(JdbcDriverTest.BOUNDED_TABLE);
        Statement statement = connection.createStatement();
        Assert.assertEquals(0, statement.executeUpdate("SET runner = bogus"));
        Assert.assertTrue(statement.execute("SELECT * FROM test"));
    }

    @Test
    public void testInternalConnect_resetAll() throws Exception {
        CalciteConnection connection = JdbcDriver.connect(JdbcDriverTest.BOUNDED_TABLE);
        Statement statement = connection.createStatement();
        Assert.assertEquals(0, statement.executeUpdate("SET runner = bogus"));
        Assert.assertEquals(0, statement.executeUpdate("RESET ALL"));
        Assert.assertTrue(statement.execute("SELECT * FROM test"));
    }

    @Test
    public void testInternalConnect_driverManagerDifferentProtocol() throws Exception {
        thrown.expect(SQLException.class);
        thrown.expectMessage("No suitable driver found");
        DriverManager.getConnection("jdbc:baaaaaad");
    }
}

