/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.jdbc.test;


import ExecConstants.OUTPUT_FORMAT_OPTION;
import ExecConstants.RETURN_RESULT_SET_FOR_DDL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import org.apache.drill.categories.JdbcTest;
import org.apache.drill.jdbc.JdbcTestBase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(JdbcTest.class)
public class TestJdbcQuery extends JdbcTestQueryBase {
    private static final Logger logger = LoggerFactory.getLogger(TestJdbcQuery.class);

    // DRILL-3635
    @Test
    public void testFix3635() throws Exception {
        // When we run a CTAS query, executeQuery() should return after the table has been flushed to disk even though we
        // didn't yet receive a terminal message. To test this, we run CTAS then immediately run a query on the newly
        // created table.
        final String tableName = "dfs.tmp.`testDDLs`";
        try (Connection conn = JdbcTestBase.connect()) {
            Statement s = conn.createStatement();
            s.executeQuery(String.format("CREATE TABLE %s AS SELECT * FROM cp.`employee.json`", tableName));
        }
        JdbcTestQueryBase.testQuery(String.format("SELECT * FROM %s LIMIT 1", tableName));
    }

    @Test
    public void testCast() throws Exception {
        JdbcTestQueryBase.testQuery("select R_REGIONKEY, cast(R_NAME as varchar(15)) as region, cast(R_COMMENT as varchar(255)) as comment from dfs.`sample-data/region.parquet`");
    }

    @Test
    public void testCharLiteral() throws Exception {
        JdbcTestQueryBase.testQuery("select 'test literal' from INFORMATION_SCHEMA.`TABLES` LIMIT 1");
    }

    @Test
    public void testVarCharLiteral() throws Exception {
        JdbcTestQueryBase.testQuery("select cast('test literal' as VARCHAR) from INFORMATION_SCHEMA.`TABLES` LIMIT 1");
    }

    @Test
    public void testLikeNotLike() throws Exception {
        JdbcTestBase.withNoDefaultSchema().sql(("SELECT TABLE_NAME, COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " + "WHERE TABLE_NAME NOT LIKE 'C%' AND COLUMN_NAME LIKE 'TABLE_%E'")).returns(("TABLE_NAME=TABLES; COLUMN_NAME=TABLE_NAME\n" + ("TABLE_NAME=TABLES; COLUMN_NAME=TABLE_TYPE\n" + "TABLE_NAME=VIEWS; COLUMN_NAME=TABLE_NAME\n")));
    }

    @Test
    public void testSimilarNotSimilar() throws Exception {
        JdbcTestBase.withNoDefaultSchema().sql(("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.`TABLES` " + "WHERE TABLE_NAME SIMILAR TO '%(H|I)E%' AND TABLE_NAME NOT SIMILAR TO 'C%' ORDER BY TABLE_NAME")).returns(("TABLE_NAME=SCHEMATA\n" + "TABLE_NAME=VIEWS\n"));
    }

    @Test
    public void testIntegerLiteral() throws Exception {
        JdbcTestBase.withNoDefaultSchema().sql("select substring('asd' from 1 for 2) from INFORMATION_SCHEMA.`TABLES` limit 1").returns("EXPR$0=as\n");
    }

    @Test
    public void testNullOpForNullableType() throws Exception {
        JdbcTestBase.withNoDefaultSchema().sql("SELECT * FROM cp.`test_null_op.json` WHERE intType IS NULL AND varCharType IS NOT NULL").returns("intType=null; varCharType=val2");
    }

    @Test
    public void testNullOpForNonNullableType() throws Exception {
        // output of (intType IS NULL) is a non-nullable type
        JdbcTestBase.withNoDefaultSchema().sql(("SELECT * FROM cp.`test_null_op.json` " + "WHERE (intType IS NULL) IS NULL AND (varCharType IS NOT NULL) IS NOT NULL")).returns("");
    }

    @Test
    public void testTrueOpForNullableType() throws Exception {
        JdbcTestBase.withNoDefaultSchema().sql("SELECT data FROM cp.`test_true_false_op.json` WHERE booleanType IS TRUE").returns("data=set to true");
        JdbcTestBase.withNoDefaultSchema().sql("SELECT data FROM cp.`test_true_false_op.json` WHERE booleanType IS FALSE").returns("data=set to false");
        JdbcTestBase.withNoDefaultSchema().sql("SELECT data FROM cp.`test_true_false_op.json` WHERE booleanType IS NOT TRUE").returns(("data=set to false\n" + "data=not set"));
        JdbcTestBase.withNoDefaultSchema().sql("SELECT data FROM cp.`test_true_false_op.json` WHERE booleanType IS NOT FALSE").returns(("data=set to true\n" + "data=not set"));
    }

    @Test
    public void testTrueOpForNonNullableType() throws Exception {
        // Output of IS TRUE (and others) is a Non-nullable type
        JdbcTestBase.withNoDefaultSchema().sql("SELECT data FROM cp.`test_true_false_op.json` WHERE (booleanType IS TRUE) IS TRUE").returns("data=set to true");
        JdbcTestBase.withNoDefaultSchema().sql("SELECT data FROM cp.`test_true_false_op.json` WHERE (booleanType IS FALSE) IS FALSE").returns(("data=set to true\n" + "data=not set"));
        JdbcTestBase.withNoDefaultSchema().sql("SELECT data FROM cp.`test_true_false_op.json` WHERE (booleanType IS NOT TRUE) IS NOT TRUE").returns("data=set to true");
        JdbcTestBase.withNoDefaultSchema().sql("SELECT data FROM cp.`test_true_false_op.json` WHERE (booleanType IS NOT FALSE) IS NOT FALSE").returns(("data=set to true\n" + "data=not set"));
    }

    @Test
    public void testDateTimeAccessors() throws Exception {
        JdbcTestBase.withNoDefaultSchema().withConnection(new org.apache.drill.shaded.guava.com.google.common.base.Function<Connection, Void>() {
            public Void apply(Connection connection) {
                try {
                    final Statement statement = connection.createStatement();
                    // show tables on view
                    final ResultSet resultSet = statement.executeQuery(("select date '2008-2-23', time '12:23:34', timestamp '2008-2-23 12:23:34.456', " + ((("interval '1' year, interval '2' day, " + "date_add(date '2008-2-23', interval '1 10:20:30' day to second), ") + "date_add(date '2010-2-23', 1) ") + "from cp.`employee.json` limit 1")));
                    resultSet.next();
                    final Date date = resultSet.getDate(1);
                    final Time time = resultSet.getTime(2);
                    final Timestamp ts = resultSet.getTimestamp(3);
                    final String intervalYear = resultSet.getString(4);
                    final String intervalDay = resultSet.getString(5);
                    final Timestamp ts1 = resultSet.getTimestamp(6);
                    final Date date1 = resultSet.getDate(7);
                    final Timestamp result = Timestamp.valueOf("2008-2-24 10:20:30");
                    final Date result1 = Date.valueOf("2010-2-24");
                    Assert.assertEquals(ts1, result);
                    Assert.assertEquals(date1, result1);
                    // TODO:  Purge nextUntilEnd(...) and calls when remaining fragment
                    // race conditions are fixed (not just DRILL-2245 fixes).
                    // nextUntilEnd(resultSet);
                    statement.close();
                    return null;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void testVerifyMetadata() throws Exception {
        JdbcTestBase.withNoDefaultSchema().withConnection(new org.apache.drill.shaded.guava.com.google.common.base.Function<Connection, Void>() {
            public Void apply(Connection connection) {
                try {
                    final Statement statement = connection.createStatement();
                    // show files
                    final ResultSet resultSet = statement.executeQuery("select timestamp '2008-2-23 12:23:23', date '2001-01-01' from cp.`employee.json` limit 1");
                    Assert.assertEquals(Types.TIMESTAMP, resultSet.getMetaData().getColumnType(1));
                    Assert.assertEquals(Types.DATE, resultSet.getMetaData().getColumnType(2));
                    TestJdbcQuery.logger.debug(JdbcTestBase.toString(resultSet));
                    resultSet.close();
                    statement.close();
                    return null;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void testCaseWithNoElse() throws Exception {
        JdbcTestBase.withNoDefaultSchema().sql(("SELECT employee_id, CASE WHEN employee_id < 100 THEN first_name END from cp.`employee.json` " + "WHERE employee_id = 99 OR employee_id = 100")).returns(("employee_id=99; EXPR$1=Elizabeth\n" + "employee_id=100; EXPR$1=null\n"));
    }

    @Test
    public void testCaseWithElse() throws Exception {
        JdbcTestBase.withNoDefaultSchema().sql(("SELECT employee_id, CASE WHEN employee_id < 100 THEN first_name ELSE 'Test' END from cp.`employee.json` " + "WHERE employee_id = 99 OR employee_id = 100")).returns(("employee_id=99; EXPR$1=Elizabeth\n" + "employee_id=100; EXPR$1=Test"));
    }

    @Test
    public void testCaseWith2ThensAndNoElse() throws Exception {
        JdbcTestBase.withNoDefaultSchema().sql(("SELECT employee_id, CASE WHEN employee_id < 100 THEN first_name WHEN employee_id = 100 THEN last_name END " + ("from cp.`employee.json` " + "WHERE employee_id = 99 OR employee_id = 100 OR employee_id = 101"))).returns(("employee_id=99; EXPR$1=Elizabeth\n" + ("employee_id=100; EXPR$1=Hunt\n" + "employee_id=101; EXPR$1=null")));
    }

    @Test
    public void testCaseWith2ThensAndElse() throws Exception {
        JdbcTestBase.withNoDefaultSchema().sql(("SELECT employee_id, CASE WHEN employee_id < 100 THEN first_name WHEN employee_id = 100 THEN last_name ELSE 'Test' END " + ("from cp.`employee.json` " + "WHERE employee_id = 99 OR employee_id = 100 OR employee_id = 101"))).returns(("employee_id=99; EXPR$1=Elizabeth\n" + ("employee_id=100; EXPR$1=Hunt\n" + "employee_id=101; EXPR$1=Test\n")));
    }

    @Test
    public void testAggWithDrillFunc() throws Exception {
        JdbcTestBase.withNoDefaultSchema().sql(("SELECT extract(year from max(to_timestamp(hire_date, 'yyyy-MM-dd HH:mm:SS.SSS' ))) as MAX_YEAR " + "from cp.`employee.json` ")).returns("MAX_YEAR=1998\n");
    }

    @Test
    public void testLeftRightReplace() throws Exception {
        JdbcTestBase.withNoDefaultSchema().sql(("SELECT `left`('abcdef', 2) as LEFT_STR, `right`('abcdef', 2) as RIGHT_STR, `replace`('abcdef', 'ab', 'zz') as REPLACE_STR " + "from cp.`employee.json` limit 1")).returns(("LEFT_STR=ab; " + ("RIGHT_STR=ef; " + "REPLACE_STR=zzcdef\n")));
    }

    @Test
    public void testLengthUTF8VarCharInput() throws Exception {
        JdbcTestBase.withNoDefaultSchema().sql(("select length('Sheri', 'UTF8') as L_UTF8 " + "from cp.`employee.json` where employee_id = 1")).returns("L_UTF8=5\n");
    }

    @Test
    public void testTimeIntervalAddOverflow() throws Exception {
        JdbcTestBase.withNoDefaultSchema().sql(("select extract(hour from (interval '10 20' day to hour + time '10:00:00')) as TIME_INT_ADD " + "from cp.`employee.json` where employee_id = 1")).returns("TIME_INT_ADD=6\n");
    }

    // DRILL-1051
    @Test
    public void testOldDateTimeJulianCalendar() throws Exception {
        // Should work with any timezone
        JdbcTestBase.withNoDefaultSchema().sql(("select cast(to_timestamp('1581-12-01 23:32:01', 'yyyy-MM-dd HH:mm:ss') as date) as `DATE`, " + (("to_timestamp('1581-12-01 23:32:01', 'yyyy-MM-dd HH:mm:ss') as `TIMESTAMP`, " + "cast(to_timestamp('1581-12-01 23:32:01', 'yyyy-MM-dd HH:mm:ss') as time) as `TIME` ") + "from (VALUES(1))"))).returns("DATE=1581-12-01; TIMESTAMP=1581-12-01 23:32:01.0; TIME=23:32:01");
    }

    // DRILL-1051
    @Test
    public void testOldDateTimeLocalMeanTime() throws Exception {
        // Should work with any timezone
        JdbcTestBase.withNoDefaultSchema().sql(("select cast(to_timestamp('1883-11-16 01:32:01', 'yyyy-MM-dd HH:mm:ss') as date) as `DATE`, " + (("to_timestamp('1883-11-16 01:32:01', 'yyyy-MM-dd HH:mm:ss') as `TIMESTAMP`, " + "cast(to_timestamp('1883-11-16 01:32:01', 'yyyy-MM-dd HH:mm:ss') as time) as `TIME` ") + "from (VALUES(1))"))).returns("DATE=1883-11-16; TIMESTAMP=1883-11-16 01:32:01.0; TIME=01:32:01");
    }

    // DRILL-5792
    @Test
    public void testConvertFromInEmptyInputSql() throws Exception {
        JdbcTestBase.withNoDefaultSchema().sql("SELECT CONVERT_FROM(columns[1], 'JSON') as col1 from cp.`empty.csv`").returns("");
    }

    @Test
    public void testResultSetIsNotReturnedSet() throws Exception {
        try (Connection conn = JdbcTestBase.connect();Statement s = conn.createStatement()) {
            s.execute(String.format("SET `%s` = false", RETURN_RESULT_SET_FOR_DDL));
            // Set any option
            s.execute(String.format("SET `%s` = 'json'", OUTPUT_FORMAT_OPTION));
            Assert.assertNull("No result", s.getResultSet());
        }
    }

    @Test
    public void testResultSetIsNotReturnedCTAS() throws Exception {
        String tableName = "dfs.tmp.`ctas`";
        try (Connection conn = JdbcTestBase.connect();Statement s = conn.createStatement()) {
            s.execute(String.format("SET `%s` = false", RETURN_RESULT_SET_FOR_DDL));
            s.execute(String.format("CREATE TABLE %s AS SELECT * FROM cp.`employee.json`", tableName));
            Assert.assertNull("No result", s.getResultSet());
        } finally {
            TestJdbcQuery.execute("DROP TABLE IF EXISTS %s", tableName);
        }
    }

    @Test
    public void testResultSetIsNotReturnedCreateView() throws Exception {
        String viewName = "dfs.tmp.`cv`";
        try (Connection conn = JdbcTestBase.connect();Statement s = conn.createStatement()) {
            s.execute(String.format("SET `%s` = false", RETURN_RESULT_SET_FOR_DDL));
            s.execute(String.format("CREATE VIEW %s AS SELECT * FROM cp.`employee.json`", viewName));
            Assert.assertNull("No result", s.getResultSet());
        } finally {
            TestJdbcQuery.execute("DROP VIEW IF EXISTS %s", viewName);
        }
    }

    @Test
    public void testResultSetIsNotReturnedDropTable() throws Exception {
        String tableName = "dfs.tmp.`dt`";
        try (Connection conn = JdbcTestBase.connect();Statement s = conn.createStatement()) {
            s.execute(String.format("SET `%s` = false", RETURN_RESULT_SET_FOR_DDL));
            s.execute(String.format("CREATE TABLE %s AS SELECT * FROM cp.`employee.json`", tableName));
            s.execute(String.format("DROP TABLE %s", tableName));
            Assert.assertNull("No result", s.getResultSet());
        }
    }

    @Test
    public void testResultSetIsNotReturnedDropView() throws Exception {
        String viewName = "dfs.tmp.`dv`";
        try (Connection conn = JdbcTestBase.connect();Statement stmt = conn.createStatement()) {
            stmt.execute(String.format("SET `%s` = false", RETURN_RESULT_SET_FOR_DDL));
            stmt.execute(String.format("CREATE VIEW %s AS SELECT * FROM cp.`employee.json`", viewName));
            stmt.execute(String.format("DROP VIEW %s", viewName));
            Assert.assertNull("No result", stmt.getResultSet());
        }
    }

    @Test
    public void testResultSetIsNotReturnedUse() throws Exception {
        try (Connection conn = JdbcTestBase.connect();Statement s = conn.createStatement()) {
            s.execute(String.format("SET `%s` = false", RETURN_RESULT_SET_FOR_DDL));
            s.execute("USE dfs.tmp");
            Assert.assertNull("No result", s.getResultSet());
        }
    }

    @Test
    public void testResultSetIsNotReturnedRefreshMetadata() throws Exception {
        String tableName = "dfs.tmp.`rm`";
        try (Connection conn = JdbcTestBase.connect();Statement s = conn.createStatement()) {
            s.execute(String.format("SET `%s` = false", RETURN_RESULT_SET_FOR_DDL));
            s.execute(String.format("CREATE TABLE %s AS SELECT * FROM cp.`employee.json`", tableName));
            s.execute(String.format("REFRESH TABLE METADATA %s", tableName));
            Assert.assertNull("No result", s.getResultSet());
        }
    }
}

