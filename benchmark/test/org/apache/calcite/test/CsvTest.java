/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.test;


import CsvSchemaFactory.INSTANCE;
import SqlToRelConverter.DEFAULT_IN_SUB_QUERY_THRESHOLD;
import com.google.common.collect.ImmutableMap;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.util.TestUtil;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test of the Calcite adapter for CSV.
 */
public class CsvTest {
    /**
     * Tests an inline schema with a non-existent directory.
     */
    @Test
    public void testBadDirectory() throws SQLException {
        Properties info = new Properties();
        info.put("model", ("inline:" + (((((((((((("{\n" + "  version: \'1.0\',\n") + "   schemas: [\n") + "     {\n") + "       type: \'custom\',\n") + "       name: \'bad\',\n") + "       factory: \'org.apache.calcite.adapter.csv.CsvSchemaFactory\',\n") + "       operand: {\n") + "         directory: \'/does/not/exist\'\n") + "       }\n") + "     }\n") + "   ]\n") + "}")));
        Connection connection = DriverManager.getConnection("jdbc:calcite:", info);
        // must print "directory ... not found" to stdout, but not fail
        ResultSet tables = connection.getMetaData().getTables(null, null, null, null);
        tables.next();
        tables.close();
        connection.close();
    }

    /**
     * Reads from a table.
     */
    @Test
    public void testSelect() throws SQLException {
        sql("model", "select * from EMPS").ok();
    }

    @Test
    public void testSelectSingleProjectGz() throws SQLException {
        sql("smart", "select name from EMPS").ok();
    }

    @Test
    public void testSelectSingleProject() throws SQLException {
        sql("smart", "select name from DEPTS").ok();
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-898">[CALCITE-898]
     * Type inference multiplying Java long by SQL INTEGER</a>.
     */
    @Test
    public void testSelectLongMultiplyInteger() throws SQLException {
        final String sql = "select empno * 3 as e3\n" + "from long_emps where empno = 100";
        sql("bug", sql).checking(( resultSet) -> {
            try {
                Assert.assertThat(resultSet.next(), CoreMatchers.is(true));
                Long o = ((Long) (resultSet.getObject(1)));
                Assert.assertThat(o, CoreMatchers.is(300L));
                Assert.assertThat(resultSet.next(), CoreMatchers.is(false));
            } catch (SQLException e) {
                throw TestUtil.rethrow(e);
            }
        }).ok();
    }

    @Test
    public void testCustomTable() throws SQLException {
        sql("model-with-custom-table", "select * from CUSTOM_TABLE.EMPS").ok();
    }

    @Test
    public void testPushDownProjectDumb() throws SQLException {
        // rule does not fire, because we're using 'dumb' tables in simple model
        final String sql = "explain plan for select * from EMPS";
        final String expected = "PLAN=EnumerableInterpreter\n" + "  BindableTableScan(table=[[SALES, EMPS]])\n";
        sql("model", sql).returns(expected).ok();
    }

    @Test
    public void testPushDownProject() throws SQLException {
        final String sql = "explain plan for select * from EMPS";
        final String expected = "PLAN=CsvTableScan(table=[[SALES, EMPS]], " + "fields=[[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]])\n";
        sql("smart", sql).returns(expected).ok();
    }

    @Test
    public void testPushDownProject2() throws SQLException {
        sql("smart", "explain plan for select name, empno from EMPS").returns("PLAN=CsvTableScan(table=[[SALES, EMPS]], fields=[[1, 0]])\n").ok();
        // make sure that it works...
        sql("smart", "select name, empno from EMPS").returns("NAME=Fred; EMPNO=100", "NAME=Eric; EMPNO=110", "NAME=John; EMPNO=110", "NAME=Wilma; EMPNO=120", "NAME=Alice; EMPNO=130").ok();
    }

    @Test
    public void testPushDownProjectAggregate() throws SQLException {
        final String sql = "explain plan for\n" + "select gender, count(*) from EMPS group by gender";
        final String expected = "PLAN=" + ("EnumerableAggregate(group=[{0}], EXPR$1=[COUNT()])\n" + "  CsvTableScan(table=[[SALES, EMPS]], fields=[[3]])\n");
        sql("smart", sql).returns(expected).ok();
    }

    @Test
    public void testPushDownProjectAggregateWithFilter() throws SQLException {
        final String sql = "explain plan for\n" + "select max(empno) from EMPS where gender='F'";
        final String expected = "PLAN=" + ((("EnumerableAggregate(group=[{}], EXPR$0=[MAX($0)])\n" + "  EnumerableCalc(expr#0..1=[{inputs}], expr#2=['F':VARCHAR], ") + "expr#3=[=($t1, $t2)], proj#0..1=[{exprs}], $condition=[$t3])\n") + "    CsvTableScan(table=[[SALES, EMPS]], fields=[[0, 3]])\n");
        sql("smart", sql).returns(expected).ok();
    }

    @Test
    public void testPushDownProjectAggregateNested() throws SQLException {
        final String sql = "explain plan for\n" + ((((("select gender, max(qty)\n" + "from (\n") + "  select name, gender, count(*) qty\n") + "  from EMPS\n") + "  group by name, gender) t\n") + "group by gender");
        final String expected = "PLAN=" + (("EnumerableAggregate(group=[{1}], EXPR$1=[MAX($2)])\n" + "  EnumerableAggregate(group=[{0, 1}], QTY=[COUNT()])\n") + "    CsvTableScan(table=[[SALES, EMPS]], fields=[[1, 3]])\n");
        sql("smart", sql).returns(expected).ok();
    }

    @Test
    public void testFilterableSelect() throws SQLException {
        sql("filterable-model", "select name from EMPS").ok();
    }

    @Test
    public void testFilterableSelectStar() throws SQLException {
        sql("filterable-model", "select * from EMPS").ok();
    }

    /**
     * Filter that can be fully handled by CsvFilterableTable.
     */
    @Test
    public void testFilterableWhere() throws SQLException {
        final String sql = "select empno, gender, name from EMPS where name = 'John'";
        sql("filterable-model", sql).returns("EMPNO=110; GENDER=M; NAME=John").ok();
    }

    /**
     * Filter that can be partly handled by CsvFilterableTable.
     */
    @Test
    public void testFilterableWhere2() throws SQLException {
        final String sql = "select empno, gender, name from EMPS\n" + " where gender = 'F' and empno > 125";
        sql("filterable-model", sql).returns("EMPNO=130; GENDER=F; NAME=Alice").ok();
    }

    @Test
    public void testJson() throws SQLException {
        final String sql = "select _MAP[\'id\'] as id,\n" + ((" _MAP[\'title\'] as title,\n" + " CHAR_LENGTH(CAST(_MAP[\'title\'] AS VARCHAR(30))) as len\n") + " from \"archers\"\n");
        sql("bug", sql).returns("ID=19990101; TITLE=Tractor trouble.; LEN=16", "ID=19990103; TITLE=Charlie's surprise.; LEN=19").ok();
    }

    @Test
    public void testJoinOnString() throws SQLException {
        final String sql = "select * from emps\n" + "join depts on emps.name = depts.name";
        sql("smart", sql).ok();
    }

    @Test
    public void testWackyColumns() throws SQLException {
        final String sql = "select * from wacky_column_names where false";
        sql("bug", sql).returns().ok();
        final String sql2 = "select \"joined at\", \"naME\"\n" + ("from wacky_column_names\n" + "where \"2gender\" = \'F\'");
        sql("bug", sql2).returns("joined at=2005-09-07; naME=Wilma", "joined at=2007-01-01; naME=Alice").ok();
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-1754">[CALCITE-1754]
     * In Csv adapter, convert DATE and TIME values to int, and TIMESTAMP values
     * to long</a>.
     */
    @Test
    public void testGroupByTimestampAdd() throws SQLException {
        final String sql = "select count(*) as c,\n" + ("  {fn timestampadd(SQL_TSI_DAY, 1, JOINEDAT) } as t\n" + "from EMPS group by {fn timestampadd(SQL_TSI_DAY, 1, JOINEDAT ) } ");
        sql("model", sql).returnsUnordered("C=1; T=1996-08-04", "C=1; T=2002-05-04", "C=1; T=2005-09-08", "C=1; T=2007-01-02", "C=1; T=2001-01-02").ok();
        final String sql2 = "select count(*) as c,\n" + ("  {fn timestampadd(SQL_TSI_MONTH, 1, JOINEDAT) } as t\n" + "from EMPS group by {fn timestampadd(SQL_TSI_MONTH, 1, JOINEDAT ) } ");
        sql("model", sql2).returnsUnordered("C=1; T=2002-06-03", "C=1; T=2005-10-07", "C=1; T=2007-02-01", "C=1; T=2001-02-01", "C=1; T=1996-09-03").ok();
    }

    @Test
    public void testUnionGroupByWithoutGroupKey() {
        final String sql = "select count(*) as c1 from EMPS group by NAME\n" + ("union\n" + "select count(*) as c1 from EMPS group by NAME");
        sql("model", sql).ok();
    }

    @Test
    public void testBoolean() {
        sql("smart", "select empno, slacker from emps where slacker").returns("EMPNO=100; SLACKER=true").ok();
    }

    @Test
    public void testReadme() throws SQLException {
        final String sql = "SELECT d.name, COUNT(*) cnt" + ((" FROM emps AS e" + " JOIN depts AS d ON e.deptno = d.deptno") + " GROUP BY d.name");
        sql("smart", sql).returns("NAME=Sales; CNT=1", "NAME=Marketing; CNT=2").ok();
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-824">[CALCITE-824]
     * Type inference when converting IN clause to semijoin</a>.
     */
    @Test
    public void testInToSemiJoinWithCast() throws SQLException {
        // Note that the IN list needs at least 20 values to trigger the rewrite
        // to a semijoin. Try it both ways.
        final String sql = "SELECT e.name\n" + ("FROM emps AS e\n" + "WHERE cast(e.empno as bigint) in ");
        final int threshold = SqlToRelConverter.DEFAULT_IN_SUB_QUERY_THRESHOLD;
        sql("smart", (sql + (range(130, (threshold - 5))))).returns("NAME=Alice").ok();
        sql("smart", (sql + (range(130, threshold)))).returns("NAME=Alice").ok();
        sql("smart", (sql + (range(130, (threshold + 1000))))).returns("NAME=Alice").ok();
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-1051">[CALCITE-1051]
     * Underflow exception due to scaling IN clause literals</a>.
     */
    @Test
    public void testInToSemiJoinWithoutCast() throws SQLException {
        final String sql = ("SELECT e.name\n" + ("FROM emps AS e\n" + "WHERE e.empno in ")) + (range(130, DEFAULT_IN_SUB_QUERY_THRESHOLD));
        sql("smart", sql).returns("NAME=Alice").ok();
    }

    @Test
    public void testDateType() throws SQLException {
        Properties info = new Properties();
        info.put("model", jsonPath("bug"));
        try (Connection connection = DriverManager.getConnection("jdbc:calcite:", info)) {
            ResultSet res = connection.getMetaData().getColumns(null, null, "DATE", "JOINEDAT");
            res.next();
            Assert.assertEquals(res.getInt("DATA_TYPE"), Types.DATE);
            res = connection.getMetaData().getColumns(null, null, "DATE", "JOINTIME");
            res.next();
            Assert.assertEquals(res.getInt("DATA_TYPE"), Types.TIME);
            res = connection.getMetaData().getColumns(null, null, "DATE", "JOINTIMES");
            res.next();
            Assert.assertEquals(res.getInt("DATA_TYPE"), Types.TIMESTAMP);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select \"JOINEDAT\", \"JOINTIME\", \"JOINTIMES\" from \"DATE\" where EMPNO = 100");
            resultSet.next();
            // date
            Assert.assertEquals(Date.class, resultSet.getDate(1).getClass());
            Assert.assertEquals(Date.valueOf("1996-08-03"), resultSet.getDate(1));
            // time
            Assert.assertEquals(Time.class, resultSet.getTime(2).getClass());
            Assert.assertEquals(Time.valueOf("00:01:02"), resultSet.getTime(2));
            // timestamp
            Assert.assertEquals(Timestamp.class, resultSet.getTimestamp(3).getClass());
            Assert.assertEquals(Timestamp.valueOf("1996-08-03 00:01:02"), resultSet.getTimestamp(3));
        }
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-1072">[CALCITE-1072]
     * CSV adapter incorrectly parses TIMESTAMP values after noon</a>.
     */
    @Test
    public void testDateType2() throws SQLException {
        Properties info = new Properties();
        info.put("model", jsonPath("bug"));
        try (Connection connection = DriverManager.getConnection("jdbc:calcite:", info)) {
            Statement statement = connection.createStatement();
            final String sql = "select * from \"DATE\"\n" + "where EMPNO >= 140 and EMPNO < 200";
            ResultSet resultSet = statement.executeQuery(sql);
            int n = 0;
            while (resultSet.next()) {
                ++n;
                final int empId = resultSet.getInt(1);
                final String date = resultSet.getString(2);
                final String time = resultSet.getString(3);
                final String timestamp = resultSet.getString(4);
                Assert.assertThat(date, CoreMatchers.is("2015-12-31"));
                switch (empId) {
                    case 140 :
                        Assert.assertThat(time, CoreMatchers.is("07:15:56"));
                        Assert.assertThat(timestamp, CoreMatchers.is("2015-12-31 07:15:56"));
                        break;
                    case 150 :
                        Assert.assertThat(time, CoreMatchers.is("13:31:21"));
                        Assert.assertThat(timestamp, CoreMatchers.is("2015-12-31 13:31:21"));
                        break;
                    default :
                        throw new AssertionError();
                }
            } 
            Assert.assertThat(n, CoreMatchers.is(2));
            resultSet.close();
            statement.close();
        }
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-1673">[CALCITE-1673]
     * Query with ORDER BY or GROUP BY on TIMESTAMP column throws
     * CompileException</a>.
     */
    @Test
    public void testTimestampGroupBy() throws SQLException {
        Properties info = new Properties();
        info.put("model", jsonPath("bug"));
        // Use LIMIT to ensure that results are deterministic without ORDER BY
        final String sql = "select \"EMPNO\", \"JOINTIMES\"\n" + ("from (select * from \"DATE\" limit 1)\n" + "group by \"EMPNO\",\"JOINTIMES\"");
        try (Connection connection = DriverManager.getConnection("jdbc:calcite:", info);Statement statement = connection.createStatement();ResultSet resultSet = statement.executeQuery(sql)) {
            Assert.assertThat(resultSet.next(), CoreMatchers.is(true));
            final Timestamp timestamp = resultSet.getTimestamp(2);
            Assert.assertThat(timestamp, CoreMatchers.isA(Timestamp.class));
            // Note: This logic is time zone specific, but the same time zone is
            // used in the CSV adapter and this test, so they should cancel out.
            Assert.assertThat(timestamp, CoreMatchers.is(Timestamp.valueOf("1996-08-03 00:01:02.0")));
        }
    }

    /**
     * As {@link #testTimestampGroupBy()} but with ORDER BY.
     */
    @Test
    public void testTimestampOrderBy() throws SQLException {
        Properties info = new Properties();
        info.put("model", jsonPath("bug"));
        final String sql = "select \"EMPNO\",\"JOINTIMES\" from \"DATE\"\n" + "order by \"JOINTIMES\"";
        try (Connection connection = DriverManager.getConnection("jdbc:calcite:", info);Statement statement = connection.createStatement();ResultSet resultSet = statement.executeQuery(sql)) {
            Assert.assertThat(resultSet.next(), CoreMatchers.is(true));
            final Timestamp timestamp = resultSet.getTimestamp(2);
            Assert.assertThat(timestamp, CoreMatchers.is(Timestamp.valueOf("1996-08-03 00:01:02")));
        }
    }

    /**
     * As {@link #testTimestampGroupBy()} but with ORDER BY as well as GROUP
     * BY.
     */
    @Test
    public void testTimestampGroupByAndOrderBy() throws SQLException {
        Properties info = new Properties();
        info.put("model", jsonPath("bug"));
        final String sql = "select \"EMPNO\", \"JOINTIMES\" from \"DATE\"\n" + "group by \"EMPNO\",\"JOINTIMES\" order by \"JOINTIMES\"";
        try (Connection connection = DriverManager.getConnection("jdbc:calcite:", info);Statement statement = connection.createStatement();ResultSet resultSet = statement.executeQuery(sql)) {
            Assert.assertThat(resultSet.next(), CoreMatchers.is(true));
            final Timestamp timestamp = resultSet.getTimestamp(2);
            Assert.assertThat(timestamp, CoreMatchers.is(Timestamp.valueOf("1996-08-03 00:01:02")));
        }
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-1031">[CALCITE-1031]
     * In prepared statement, CsvScannableTable.scan is called twice</a>. To see
     * the bug, place a breakpoint in CsvScannableTable.scan, and note that it is
     * called twice. It should only be called once.
     */
    @Test
    public void testPrepared() throws SQLException {
        final Properties properties = new Properties();
        properties.setProperty("caseSensitive", "true");
        try (Connection connection = DriverManager.getConnection("jdbc:calcite:", properties)) {
            final CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
            final Schema schema = INSTANCE.create(calciteConnection.getRootSchema(), null, ImmutableMap.of("directory", resourcePath("sales"), "flavor", "scannable"));
            calciteConnection.getRootSchema().add("TEST", schema);
            final String sql = "select * from \"TEST\".\"DEPTS\" where \"NAME\" = ?";
            final PreparedStatement statement2 = calciteConnection.prepareStatement(sql);
            statement2.setString(1, "Sales");
            final ResultSet resultSet1 = statement2.executeQuery();
            Consumer<ResultSet> expect = CsvTest.expect("DEPTNO=10; NAME=Sales");
            expect.accept(resultSet1);
        }
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-1054">[CALCITE-1054]
     * NPE caused by wrong code generation for Timestamp fields</a>.
     */
    @Test
    public void testFilterOnNullableTimestamp() throws Exception {
        Properties info = new Properties();
        info.put("model", jsonPath("bug"));
        try (Connection connection = DriverManager.getConnection("jdbc:calcite:", info)) {
            final Statement statement = connection.createStatement();
            // date
            final String sql1 = "select JOINEDAT from \"DATE\"\n" + ("where JOINEDAT < {d \'2000-01-01\'}\n" + "or JOINEDAT >= {d '2017-01-01'}");
            final ResultSet joinedAt = statement.executeQuery(sql1);
            Assert.assertThat(joinedAt.next(), CoreMatchers.is(true));
            Assert.assertThat(joinedAt.getDate(1), CoreMatchers.is(Date.valueOf("1996-08-03")));
            // time
            final String sql2 = "select JOINTIME from \"DATE\"\n" + ("where JOINTIME >= {t \'07:00:00\'}\n" + "and JOINTIME < {t '08:00:00'}");
            final ResultSet joinTime = statement.executeQuery(sql2);
            Assert.assertThat(joinTime.next(), CoreMatchers.is(true));
            Assert.assertThat(joinTime.getTime(1), CoreMatchers.is(Time.valueOf("07:15:56")));
            // timestamp
            final String sql3 = "select JOINTIMES,\n" + ((((("  {fn timestampadd(SQL_TSI_DAY, 1, JOINTIMES)}\n" + "from \"DATE\"\n") + "where (JOINTIMES >= {ts \'2003-01-01 00:00:00\'}\n") + "and JOINTIMES < {ts \'2006-01-01 00:00:00\'})\n") + "or (JOINTIMES >= {ts \'2003-01-01 00:00:00\'}\n") + "and JOINTIMES < {ts '2007-01-01 00:00:00'})");
            final ResultSet joinTimes = statement.executeQuery(sql3);
            Assert.assertThat(joinTimes.next(), CoreMatchers.is(true));
            Assert.assertThat(joinTimes.getTimestamp(1), CoreMatchers.is(Timestamp.valueOf("2005-09-07 00:00:00")));
            Assert.assertThat(joinTimes.getTimestamp(2), CoreMatchers.is(Timestamp.valueOf("2005-09-08 00:00:00")));
            final String sql4 = "select JOINTIMES, extract(year from JOINTIMES)\n" + "from \"DATE\"";
            final ResultSet joinTimes2 = statement.executeQuery(sql4);
            Assert.assertThat(joinTimes2.next(), CoreMatchers.is(true));
            Assert.assertThat(joinTimes2.getTimestamp(1), CoreMatchers.is(Timestamp.valueOf("1996-08-03 00:01:02")));
        }
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-1118">[CALCITE-1118]
     * NullPointerException in EXTRACT with WHERE ... IN clause if field has null
     * value</a>.
     */
    @Test
    public void testFilterOnNullableTimestamp2() throws Exception {
        Properties info = new Properties();
        info.put("model", jsonPath("bug"));
        try (Connection connection = DriverManager.getConnection("jdbc:calcite:", info)) {
            final Statement statement = connection.createStatement();
            final String sql1 = "select extract(year from JOINTIMES)\n" + ("from \"DATE\"\n" + "where extract(year from JOINTIMES) in (2006, 2007)");
            final ResultSet joinTimes = statement.executeQuery(sql1);
            Assert.assertThat(joinTimes.next(), CoreMatchers.is(true));
            Assert.assertThat(joinTimes.getInt(1), CoreMatchers.is(2007));
            final String sql2 = "select extract(year from JOINTIMES),\n" + (("  count(0) from \"DATE\"\n" + "where extract(year from JOINTIMES) between 2007 and 2016\n") + "group by extract(year from JOINTIMES)");
            final ResultSet joinTimes2 = statement.executeQuery(sql2);
            Assert.assertThat(joinTimes2.next(), CoreMatchers.is(true));
            Assert.assertThat(joinTimes2.getInt(1), CoreMatchers.is(2007));
            Assert.assertThat(joinTimes2.getLong(2), CoreMatchers.is(1L));
            Assert.assertThat(joinTimes2.next(), CoreMatchers.is(true));
            Assert.assertThat(joinTimes2.getInt(1), CoreMatchers.is(2015));
            Assert.assertThat(joinTimes2.getLong(2), CoreMatchers.is(2L));
        }
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-1427">[CALCITE-1427]
     * Code generation incorrect (does not compile) for DATE, TIME and TIMESTAMP
     * fields</a>.
     */
    @Test
    public void testNonNullFilterOnDateType() throws SQLException {
        Properties info = new Properties();
        info.put("model", jsonPath("bug"));
        try (Connection connection = DriverManager.getConnection("jdbc:calcite:", info)) {
            final Statement statement = connection.createStatement();
            // date
            final String sql1 = "select JOINEDAT from \"DATE\"\n" + "where JOINEDAT is not null";
            final ResultSet joinedAt = statement.executeQuery(sql1);
            Assert.assertThat(joinedAt.next(), CoreMatchers.is(true));
            Assert.assertThat(joinedAt.getDate(1).getClass(), CoreMatchers.equalTo(Date.class));
            Assert.assertThat(joinedAt.getDate(1), CoreMatchers.is(Date.valueOf("1996-08-03")));
            // time
            final String sql2 = "select JOINTIME from \"DATE\"\n" + "where JOINTIME is not null";
            final ResultSet joinTime = statement.executeQuery(sql2);
            Assert.assertThat(joinTime.next(), CoreMatchers.is(true));
            Assert.assertThat(joinTime.getTime(1).getClass(), CoreMatchers.equalTo(Time.class));
            Assert.assertThat(joinTime.getTime(1), CoreMatchers.is(Time.valueOf("00:01:02")));
            // timestamp
            final String sql3 = "select JOINTIMES from \"DATE\"\n" + "where JOINTIMES is not null";
            final ResultSet joinTimes = statement.executeQuery(sql3);
            Assert.assertThat(joinTimes.next(), CoreMatchers.is(true));
            Assert.assertThat(joinTimes.getTimestamp(1).getClass(), CoreMatchers.equalTo(Timestamp.class));
            Assert.assertThat(joinTimes.getTimestamp(1), CoreMatchers.is(Timestamp.valueOf("1996-08-03 00:01:02")));
        }
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-1427">[CALCITE-1427]
     * Code generation incorrect (does not compile) for DATE, TIME and TIMESTAMP
     * fields</a>.
     */
    @Test
    public void testGreaterThanFilterOnDateType() throws SQLException {
        Properties info = new Properties();
        info.put("model", jsonPath("bug"));
        try (Connection connection = DriverManager.getConnection("jdbc:calcite:", info)) {
            final Statement statement = connection.createStatement();
            // date
            final String sql1 = "select JOINEDAT from \"DATE\"\n" + "where JOINEDAT > {d '1990-01-01'}";
            final ResultSet joinedAt = statement.executeQuery(sql1);
            Assert.assertThat(joinedAt.next(), CoreMatchers.is(true));
            Assert.assertThat(joinedAt.getDate(1).getClass(), CoreMatchers.equalTo(Date.class));
            Assert.assertThat(joinedAt.getDate(1), CoreMatchers.is(Date.valueOf("1996-08-03")));
            // time
            final String sql2 = "select JOINTIME from \"DATE\"\n" + "where JOINTIME > {t '00:00:00'}";
            final ResultSet joinTime = statement.executeQuery(sql2);
            Assert.assertThat(joinTime.next(), CoreMatchers.is(true));
            Assert.assertThat(joinTime.getTime(1).getClass(), CoreMatchers.equalTo(Time.class));
            Assert.assertThat(joinTime.getTime(1), CoreMatchers.is(Time.valueOf("00:01:02")));
            // timestamp
            final String sql3 = "select JOINTIMES from \"DATE\"\n" + "where JOINTIMES > {ts '1990-01-01 00:00:00'}";
            final ResultSet joinTimes = statement.executeQuery(sql3);
            Assert.assertThat(joinTimes.next(), CoreMatchers.is(true));
            Assert.assertThat(joinTimes.getTimestamp(1).getClass(), CoreMatchers.equalTo(Timestamp.class));
            Assert.assertThat(joinTimes.getTimestamp(1), CoreMatchers.is(Timestamp.valueOf("1996-08-03 00:01:02")));
        }
    }

    /**
     * Receives commands on a queue and executes them on its own thread.
     * Call {@link #close} to terminate.
     *
     * @param <E>
     * 		Result value of commands
     */
    private static class Worker<E> implements AutoCloseable , Runnable {
        /**
         * Queue of commands.
         */
        final BlockingQueue<Callable<E>> queue = new ArrayBlockingQueue<>(5);

        /**
         * Value returned by the most recent command.
         */
        private E v;

        /**
         * Exception thrown by a command or queue wait.
         */
        private Exception e;

        /**
         * The poison pill command.
         */
        final Callable<E> end = () -> null;

        public void run() {
            try {
                for (; ;) {
                    final Callable<E> c = queue.take();
                    if (c == (end)) {
                        return;
                    }
                    this.v = c.call();
                }
            } catch (Exception e) {
                this.e = e;
            }
        }

        public void close() {
            try {
                queue.put(end);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    /**
     * Fluent API to perform test actions.
     */
    private class Fluent {
        private final String model;

        private final String sql;

        private final Consumer<ResultSet> expect;

        Fluent(String model, String sql, Consumer<ResultSet> expect) {
            this.model = model;
            this.sql = sql;
            this.expect = expect;
        }

        /**
         * Runs the test.
         */
        CsvTest.Fluent ok() {
            try {
                checkSql(sql, model, expect);
                return this;
            } catch (SQLException e) {
                throw TestUtil.rethrow(e);
            }
        }

        /**
         * Assigns a function to call to test whether output is correct.
         */
        CsvTest.Fluent checking(Consumer<ResultSet> expect) {
            return new CsvTest.Fluent(model, sql, expect);
        }

        /**
         * Sets the rows that are expected to be returned from the SQL query.
         */
        CsvTest.Fluent returns(String... expectedLines) {
            return checking(CsvTest.expect(expectedLines));
        }

        /**
         * Sets the rows that are expected to be returned from the SQL query,
         * in no particular order.
         */
        CsvTest.Fluent returnsUnordered(String... expectedLines) {
            return checking(CsvTest.expectUnordered(expectedLines));
        }
    }
}

/**
 * End CsvTest.java
 */
