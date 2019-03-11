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
package org.apache.beam.sdk.io.jdbc;


import JdbcIO.DataSourceConfiguration;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.io.common.DatabaseTestHelper;
import org.apache.beam.sdk.io.common.TestRow;
import org.apache.beam.sdk.testing.ExpectedLogs;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.derby.drda.NetworkServerControl;
import org.apache.derby.jdbc.ClientDataSource;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test on the JdbcIO.
 */
public class JdbcIOTest implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(JdbcIOTest.class);

    public static final int EXPECTED_ROW_COUNT = 1000;

    public static final String BACKOFF_TABLE = "UT_WRITE_BACKOFF";

    private static NetworkServerControl derbyServer;

    private static ClientDataSource dataSource;

    private static int port;

    private static String readTableName;

    @Rule
    public final transient TestPipeline pipeline = TestPipeline.create();

    @Rule
    public final transient ExpectedLogs expectedLogs = ExpectedLogs.none(JdbcIO.class);

    @Test
    public void testDataSourceConfigurationDataSource() throws Exception {
        JdbcIO.DataSourceConfiguration config = DataSourceConfiguration.create(JdbcIOTest.dataSource);
        try (Connection conn = config.buildDatasource().getConnection()) {
            Assert.assertTrue(conn.isValid(0));
        }
    }

    @Test
    public void testDataSourceConfigurationDriverAndUrl() throws Exception {
        JdbcIO.DataSourceConfiguration config = DataSourceConfiguration.create("org.apache.derby.jdbc.ClientDriver", (("jdbc:derby://localhost:" + (JdbcIOTest.port)) + "/target/beam"));
        try (Connection conn = config.buildDatasource().getConnection()) {
            Assert.assertTrue(conn.isValid(0));
        }
    }

    @Test
    public void testDataSourceConfigurationUsernameAndPassword() throws Exception {
        String username = "sa";
        String password = "sa";
        JdbcIO.DataSourceConfiguration config = DataSourceConfiguration.create("org.apache.derby.jdbc.ClientDriver", (("jdbc:derby://localhost:" + (JdbcIOTest.port)) + "/target/beam")).withUsername(username).withPassword(password);
        try (Connection conn = config.buildDatasource().getConnection()) {
            Assert.assertTrue(conn.isValid(0));
        }
    }

    @Test
    public void testDataSourceConfigurationNullPassword() throws Exception {
        String username = "sa";
        String password = null;
        JdbcIO.DataSourceConfiguration config = DataSourceConfiguration.create("org.apache.derby.jdbc.ClientDriver", (("jdbc:derby://localhost:" + (JdbcIOTest.port)) + "/target/beam")).withUsername(username).withPassword(password);
        try (Connection conn = config.buildDatasource().getConnection()) {
            Assert.assertTrue(conn.isValid(0));
        }
    }

    @Test
    public void testDataSourceConfigurationNullUsernameAndPassword() throws Exception {
        String username = null;
        String password = null;
        JdbcIO.DataSourceConfiguration config = DataSourceConfiguration.create("org.apache.derby.jdbc.ClientDriver", (("jdbc:derby://localhost:" + (JdbcIOTest.port)) + "/target/beam")).withUsername(username).withPassword(password);
        try (Connection conn = config.buildDatasource().getConnection()) {
            Assert.assertTrue(conn.isValid(0));
        }
    }

    @Test
    public void testRead() throws Exception {
        PCollection<TestRow> rows = pipeline.apply(JdbcIO.<TestRow>read().withFetchSize(12).withDataSourceConfiguration(DataSourceConfiguration.create(JdbcIOTest.dataSource)).withQuery(("select name,id from " + (JdbcIOTest.readTableName))).withRowMapper(new JdbcTestHelper.CreateTestRowOfNameAndId()).withCoder(SerializableCoder.of(TestRow.class)));
        PAssert.thatSingleton(rows.apply("Count All", Count.globally())).isEqualTo(((long) (JdbcIOTest.EXPECTED_ROW_COUNT)));
        Iterable<TestRow> expectedValues = TestRow.getExpectedValues(0, JdbcIOTest.EXPECTED_ROW_COUNT);
        PAssert.that(rows).containsInAnyOrder(expectedValues);
        pipeline.run();
    }

    @Test
    public void testReadWithSingleStringParameter() throws Exception {
        PCollection<TestRow> rows = pipeline.apply(JdbcIO.<TestRow>read().withDataSourceConfiguration(DataSourceConfiguration.create(JdbcIOTest.dataSource)).withQuery(String.format("select name,id from %s where name = ?", JdbcIOTest.readTableName)).withStatementPreparator(( preparedStatement) -> preparedStatement.setString(1, TestRow.getNameForSeed(1))).withRowMapper(new JdbcTestHelper.CreateTestRowOfNameAndId()).withCoder(SerializableCoder.of(TestRow.class)));
        PAssert.thatSingleton(rows.apply("Count All", Count.globally())).isEqualTo(1L);
        Iterable<TestRow> expectedValues = Collections.singletonList(TestRow.fromSeed(1));
        PAssert.that(rows).containsInAnyOrder(expectedValues);
        pipeline.run();
    }

    @Test
    public void testWrite() throws Exception {
        final long rowsToAdd = 1000L;
        String tableName = DatabaseTestHelper.getTestTableName("UT_WRITE");
        DatabaseTestHelper.createTable(JdbcIOTest.dataSource, tableName);
        try {
            ArrayList<KV<Integer, String>> data = new ArrayList<>();
            for (int i = 0; i < rowsToAdd; i++) {
                KV<Integer, String> kv = KV.of(i, "Test");
                data.add(kv);
            }
            pipeline.apply(Create.of(data)).apply(JdbcIO.<KV<Integer, String>>write().withDataSourceConfiguration(DataSourceConfiguration.create("org.apache.derby.jdbc.ClientDriver", (("jdbc:derby://localhost:" + (JdbcIOTest.port)) + "/target/beam"))).withStatement(String.format("insert into %s values(?, ?)", tableName)).withBatchSize(10L).withPreparedStatementSetter(( element, statement) -> {
                statement.setInt(1, element.getKey());
                statement.setString(2, element.getValue());
            }));
            pipeline.run();
            try (Connection connection = JdbcIOTest.dataSource.getConnection()) {
                try (Statement statement = connection.createStatement()) {
                    try (ResultSet resultSet = statement.executeQuery(("select count(*) from " + tableName))) {
                        resultSet.next();
                        int count = resultSet.getInt(1);
                        Assert.assertEquals(JdbcIOTest.EXPECTED_ROW_COUNT, count);
                    }
                }
            }
        } finally {
            DatabaseTestHelper.deleteTable(JdbcIOTest.dataSource, tableName);
        }
    }

    @Test
    public void testWriteWithBackoff() throws Exception {
        String tableName = DatabaseTestHelper.getTestTableName("UT_WRITE_BACKOFF");
        DatabaseTestHelper.createTable(JdbcIOTest.dataSource, tableName);
        // lock table
        Connection connection = JdbcIOTest.dataSource.getConnection();
        Statement lockStatement = connection.createStatement();
        lockStatement.execute((("ALTER TABLE " + tableName) + " LOCKSIZE TABLE"));
        lockStatement.execute((("LOCK TABLE " + tableName) + " IN EXCLUSIVE MODE"));
        // start a first transaction
        connection.setAutoCommit(false);
        PreparedStatement insertStatement = connection.prepareStatement((("insert into " + tableName) + " values(?, ?)"));
        insertStatement.setInt(1, 1);
        insertStatement.setString(2, "TEST");
        insertStatement.execute();
        // try to write to this table
        pipeline.apply(Create.of(Collections.singletonList(KV.of(1, "TEST")))).apply(JdbcIO.<KV<Integer, String>>write().withDataSourceConfiguration(DataSourceConfiguration.create("org.apache.derby.jdbc.ClientDriver", (("jdbc:derby://localhost:" + (JdbcIOTest.port)) + "/target/beam"))).withStatement(String.format("insert into %s values(?, ?)", tableName)).withRetryStrategy(((JdbcIO.RetryStrategy) (( e) -> {
            return "XJ208".equals(e.getSQLState());// we fake a deadlock with a lock here

        }))).withPreparedStatementSetter(( element, statement) -> {
            statement.setInt(1, element.getKey());
            statement.setString(2, element.getValue());
        }));
        // starting a thread to perform the commit later, while the pipeline is running into the backoff
        Thread commitThread = new Thread(() -> {
            try {
                Thread.sleep(10000);
                connection.commit();
            } catch (Exception e) {
                // nothing to do
            }
        });
        commitThread.start();
        pipeline.run();
        commitThread.join();
        // we verify the the backoff has been called thanks to the log message
        expectedLogs.verifyWarn("Deadlock detected, retrying");
        try (Connection readConnection = JdbcIOTest.dataSource.getConnection()) {
            try (Statement statement = readConnection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(("select count(*) from " + tableName))) {
                    resultSet.next();
                    int count = resultSet.getInt(1);
                    // here we have the record inserted by the first transaction (by hand), and a second one
                    // inserted by the pipeline
                    Assert.assertEquals(2, count);
                }
            }
        }
    }

    @Test
    public void testWriteWithEmptyPCollection() throws Exception {
        pipeline.apply(Create.empty(KvCoder.of(VarIntCoder.of(), StringUtf8Coder.of()))).apply(JdbcIO.<KV<Integer, String>>write().withDataSourceConfiguration(DataSourceConfiguration.create("org.apache.derby.jdbc.ClientDriver", (("jdbc:derby://localhost:" + (JdbcIOTest.port)) + "/target/beam"))).withStatement("insert into BEAM values(?, ?)").withPreparedStatementSetter(( element, statement) -> {
            statement.setInt(1, element.getKey());
            statement.setString(2, element.getValue());
        }));
        pipeline.run();
    }
}

