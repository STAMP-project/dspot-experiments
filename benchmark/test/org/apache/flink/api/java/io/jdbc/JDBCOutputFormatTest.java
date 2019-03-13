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
package org.apache.flink.api.java.io.jdbc;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.apache.flink.types.Row;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link JDBCOutputFormat}.
 */
public class JDBCOutputFormatTest extends JDBCTestBase {
    private JDBCOutputFormat jdbcOutputFormat;

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDriver() throws IOException {
        jdbcOutputFormat = JDBCOutputFormat.buildJDBCOutputFormat().setDrivername("org.apache.derby.jdbc.idontexist").setDBUrl(JDBCTestBase.DB_URL).setQuery(String.format(JDBCTestBase.INSERT_TEMPLATE, JDBCTestBase.INPUT_TABLE)).finish();
        jdbcOutputFormat.open(0, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidURL() throws IOException {
        jdbcOutputFormat = JDBCOutputFormat.buildJDBCOutputFormat().setDrivername(JDBCTestBase.DRIVER_CLASS).setDBUrl("jdbc:der:iamanerror:mory:ebookshop").setQuery(String.format(JDBCTestBase.INSERT_TEMPLATE, JDBCTestBase.INPUT_TABLE)).finish();
        jdbcOutputFormat.open(0, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidQuery() throws IOException {
        jdbcOutputFormat = JDBCOutputFormat.buildJDBCOutputFormat().setDrivername(JDBCTestBase.DRIVER_CLASS).setDBUrl(JDBCTestBase.DB_URL).setQuery("iamnotsql").finish();
        jdbcOutputFormat.open(0, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncompleteConfiguration() throws IOException {
        jdbcOutputFormat = JDBCOutputFormat.buildJDBCOutputFormat().setDrivername(JDBCTestBase.DRIVER_CLASS).setQuery(String.format(JDBCTestBase.INSERT_TEMPLATE, JDBCTestBase.INPUT_TABLE)).finish();
    }

    @Test(expected = RuntimeException.class)
    public void testIncompatibleTypes() throws IOException {
        jdbcOutputFormat = JDBCOutputFormat.buildJDBCOutputFormat().setDrivername(JDBCTestBase.DRIVER_CLASS).setDBUrl(JDBCTestBase.DB_URL).setQuery(String.format(JDBCTestBase.INSERT_TEMPLATE, JDBCTestBase.INPUT_TABLE)).finish();
        jdbcOutputFormat.open(0, 1);
        Row row = new Row(5);
        row.setField(0, 4);
        row.setField(1, "hello");
        row.setField(2, "world");
        row.setField(3, 0.99);
        row.setField(4, "imthewrongtype");
        jdbcOutputFormat.writeRecord(row);
        jdbcOutputFormat.close();
    }

    @Test(expected = RuntimeException.class)
    public void testExceptionOnInvalidType() throws IOException {
        jdbcOutputFormat = JDBCOutputFormat.buildJDBCOutputFormat().setDrivername(JDBCTestBase.DRIVER_CLASS).setDBUrl(JDBCTestBase.DB_URL).setQuery(String.format(JDBCTestBase.INSERT_TEMPLATE, JDBCTestBase.OUTPUT_TABLE)).setSqlTypes(new int[]{ Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.DOUBLE, Types.INTEGER }).finish();
        jdbcOutputFormat.open(0, 1);
        JDBCTestBase.TestEntry entry = JDBCTestBase.TEST_DATA[0];
        Row row = new Row(5);
        row.setField(0, entry.id);
        row.setField(1, entry.title);
        row.setField(2, entry.author);
        row.setField(3, 0L);// use incompatible type (Long instead of Double)

        row.setField(4, entry.qty);
        jdbcOutputFormat.writeRecord(row);
    }

    @Test(expected = RuntimeException.class)
    public void testExceptionOnClose() throws IOException {
        jdbcOutputFormat = JDBCOutputFormat.buildJDBCOutputFormat().setDrivername(JDBCTestBase.DRIVER_CLASS).setDBUrl(JDBCTestBase.DB_URL).setQuery(String.format(JDBCTestBase.INSERT_TEMPLATE, JDBCTestBase.OUTPUT_TABLE)).setSqlTypes(new int[]{ Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.DOUBLE, Types.INTEGER }).finish();
        jdbcOutputFormat.open(0, 1);
        JDBCTestBase.TestEntry entry = JDBCTestBase.TEST_DATA[0];
        Row row = new Row(5);
        row.setField(0, entry.id);
        row.setField(1, entry.title);
        row.setField(2, entry.author);
        row.setField(3, entry.price);
        row.setField(4, entry.qty);
        jdbcOutputFormat.writeRecord(row);
        jdbcOutputFormat.writeRecord(row);// writing the same record twice must yield a unique key violation.

        jdbcOutputFormat.close();
    }

    @Test
    public void testJDBCOutputFormat() throws IOException, SQLException {
        jdbcOutputFormat = JDBCOutputFormat.buildJDBCOutputFormat().setDrivername(JDBCTestBase.DRIVER_CLASS).setDBUrl(JDBCTestBase.DB_URL).setQuery(String.format(JDBCTestBase.INSERT_TEMPLATE, JDBCTestBase.OUTPUT_TABLE)).finish();
        jdbcOutputFormat.open(0, 1);
        for (JDBCTestBase.TestEntry entry : JDBCTestBase.TEST_DATA) {
            jdbcOutputFormat.writeRecord(JDBCOutputFormatTest.toRow(entry));
        }
        jdbcOutputFormat.close();
        try (Connection dbConn = DriverManager.getConnection(JDBCTestBase.DB_URL);PreparedStatement statement = dbConn.prepareStatement(JDBCTestBase.SELECT_ALL_NEWBOOKS);ResultSet resultSet = statement.executeQuery()) {
            int recordCount = 0;
            while (resultSet.next()) {
                Assert.assertEquals(JDBCTestBase.TEST_DATA[recordCount].id, resultSet.getObject("id"));
                Assert.assertEquals(JDBCTestBase.TEST_DATA[recordCount].title, resultSet.getObject("title"));
                Assert.assertEquals(JDBCTestBase.TEST_DATA[recordCount].author, resultSet.getObject("author"));
                Assert.assertEquals(JDBCTestBase.TEST_DATA[recordCount].price, resultSet.getObject("price"));
                Assert.assertEquals(JDBCTestBase.TEST_DATA[recordCount].qty, resultSet.getObject("qty"));
                recordCount++;
            } 
            Assert.assertEquals(JDBCOutputFormatTest.TEST_DATA.length, recordCount);
        }
    }

    @Test
    public void testFlush() throws IOException, SQLException {
        jdbcOutputFormat = JDBCOutputFormat.buildJDBCOutputFormat().setDrivername(JDBCTestBase.DRIVER_CLASS).setDBUrl(JDBCTestBase.DB_URL).setQuery(String.format(JDBCTestBase.INSERT_TEMPLATE, JDBCTestBase.OUTPUT_TABLE_2)).setBatchInterval(3).finish();
        try (Connection dbConn = DriverManager.getConnection(JDBCTestBase.DB_URL);PreparedStatement statement = dbConn.prepareStatement(JDBCTestBase.SELECT_ALL_NEWBOOKS_2)) {
            jdbcOutputFormat.open(0, 1);
            for (int i = 0; i < 2; ++i) {
                jdbcOutputFormat.writeRecord(JDBCOutputFormatTest.toRow(JDBCTestBase.TEST_DATA[i]));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                Assert.assertFalse(resultSet.next());
            }
            jdbcOutputFormat.writeRecord(JDBCOutputFormatTest.toRow(JDBCTestBase.TEST_DATA[2]));
            try (ResultSet resultSet = statement.executeQuery()) {
                int recordCount = 0;
                while (resultSet.next()) {
                    Assert.assertEquals(JDBCTestBase.TEST_DATA[recordCount].id, resultSet.getObject("id"));
                    Assert.assertEquals(JDBCTestBase.TEST_DATA[recordCount].title, resultSet.getObject("title"));
                    Assert.assertEquals(JDBCTestBase.TEST_DATA[recordCount].author, resultSet.getObject("author"));
                    Assert.assertEquals(JDBCTestBase.TEST_DATA[recordCount].price, resultSet.getObject("price"));
                    Assert.assertEquals(JDBCTestBase.TEST_DATA[recordCount].qty, resultSet.getObject("qty"));
                    recordCount++;
                } 
                Assert.assertEquals(3, recordCount);
            }
        } finally {
            jdbcOutputFormat.close();
        }
    }
}

