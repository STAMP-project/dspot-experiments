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
package org.apache.hive.jdbc;


import ClassicTableTypes.TABLE;
import ClassicTableTypes.VIEW;
import ConfVars.HIVE_SUPPORT_CONCURRENCY;
import ConfVars.HIVE_TXN_MANAGER;
import DfsProcessor.DFS_RESULT_HEADER;
import ErrorMsg.REPL_DATABASE_IS_NOT_SOURCE_OF_REPLICATION;
import ErrorMsg.REPL_LOAD_PATH_NOT_FOUND;
import HiveConf.ConfVars.HIVE_SERVER2_TABLE_TYPE_MAPPING;
import HiveConf.ConfVars.HIVE_VECTORIZATION_ENABLED;
import HiveConf.ConfVars.METASTOREPWD.varname;
import TableType.EXTERNAL_TABLE;
import TableType.MANAGED_TABLE;
import TableType.VIRTUAL_VIEW;
import TableTypeMappings.CLASSIC;
import TableTypeMappings.HIVE;
import com.google.common.collect.ImmutableSet;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.common.type.HiveIntervalDayTime;
import org.apache.hadoop.hive.common.type.HiveIntervalYearMonth;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.exec.ExplainTask;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.ql.stats.StatsUtils;
import org.apache.hive.common.util.HiveVersionInfo;
import org.apache.hive.jdbc.Utils.JdbcConnectionParams;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hive.service.cli.operation.ClassicTableTypeMapping;
import org.apache.hive.service.cli.operation.HiveTableTypeMapping;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.sql.Date.valueOf;


/**
 * TestJdbcDriver2
 * This class tests the JDBC API for HiveServer2 via an embedded HiveServer2 instance
 */
public class TestJdbcDriver2 {
    private static final Logger LOG = LoggerFactory.getLogger(TestJdbcDriver2.class);

    private static final String driverName = "org.apache.hive.jdbc.HiveDriver";

    private static final String testDbName = "testjdbcdriver";

    private static final String defaultDbName = "default";

    private static final String tableName = "testjdbcdrivertbl";

    private static final String tableNameWithPk = "pktable";

    private static final String tableComment = "Simple table";

    private static final String viewName = "testjdbcdriverview";

    private static final String viewComment = "Simple view";

    private static final String partitionedTableName = "testjdbcdriverparttbl";

    private static final String partitionedColumnName = "partcoljdbc";

    private static final String partitionedColumnValue = "20090619";

    private static final String partitionedTableComment = "Partitioned table";

    private static final String dataTypeTableName = "testjdbcdriverdatatypetbl";

    private static final String dataTypeTableComment = "Table with many column data types";

    private static final String externalTableName = "testjdbcdriverexttbl";

    private static final String externalTableComment = "An external table";

    private static HiveConf conf;

    private static String dataFileDir;

    private static Path dataFilePath;

    private static int dataFileRowCount;

    private static Path dataTypeDataFilePath;

    // Creating a new connection is expensive, so we'll reuse this object
    private static Connection con;

    private static final float floatCompareDelta = 1.0E-4F;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testExceucteUpdateCounts() throws Exception {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        stmt.execute((("set " + (HIVE_SUPPORT_CONCURRENCY.varname)) + "=true"));
        stmt.execute((("set " + (HIVE_TXN_MANAGER.varname)) + "=org.apache.hadoop.hive.ql.lockmgr.DbTxnManager"));
        stmt.execute(("create table transactional_crud (a int, b int) stored as orc " + "tblproperties('transactional'='true', 'transactional_properties'='default')"));
        int count = stmt.executeUpdate("insert into transactional_crud values(1,2),(3,4),(5,6)");
        Assert.assertEquals("Statement insert", 3, count);
        count = stmt.executeUpdate("update transactional_crud set b = 17 where a <= 3");
        Assert.assertEquals("Statement update", 2, count);
        count = stmt.executeUpdate("delete from transactional_crud where b = 6");
        Assert.assertEquals("Statement delete", 1, count);
        stmt.close();
        PreparedStatement pStmt = TestJdbcDriver2.con.prepareStatement("update transactional_crud set b = ? where a = ? or a = ?");
        pStmt.setInt(1, 15);
        pStmt.setInt(2, 1);
        pStmt.setInt(3, 3);
        count = pStmt.executeUpdate();
        Assert.assertEquals("2 row PreparedStatement update", 2, count);
        pStmt.setInt(1, 19);
        pStmt.setInt(2, 3);
        pStmt.setInt(3, 3);
        count = pStmt.executeUpdate();
        Assert.assertEquals("1 row PreparedStatement update", 1, count);
    }

    /**
     * Tests malformed JDBC URL
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testBadURL() throws Exception {
        checkBadUrl("jdbc:hive2://localhost:10000;principal=test");
        checkBadUrl(("jdbc:hive2://localhost:10000;" + "principal=hive/HiveServer2Host@YOUR-REALM.COM"));
        checkBadUrl("jdbc:hive2://localhost:10000test");
    }

    /**
     * Tests setting a custom fetch size for the RPC call
     *
     * @throws SQLException
     * 		
     */
    @Test
    public void testURLWithFetchSize() throws SQLException {
        Connection con = TestJdbcDriver2.getConnection(((TestJdbcDriver2.testDbName) + ";fetchSize=1234"));
        Statement stmt = con.createStatement();
        Assert.assertEquals(stmt.getFetchSize(), 1234);
        stmt.close();
        con.close();
    }

    /**
     * Test running parallel queries (with parallel queries disabled).
     * Should be serialized in the order of execution.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSerializedExecution() throws Exception {
        HiveStatement stmt1 = ((HiveStatement) (TestJdbcDriver2.con.createStatement()));
        HiveStatement stmt2 = ((HiveStatement) (TestJdbcDriver2.con.createStatement()));
        stmt1.execute("SET hive.driver.parallel.compilation=false");
        stmt1.execute("SET hive.server2.async.exec.async.compile=false");
        stmt2.execute("SET hive.driver.parallel.compilation=false");
        stmt2.execute("SET hive.server2.async.exec.async.compile=false");
        stmt1.execute((("create temporary function sleepMsUDF as '" + (TestJdbcDriver2.SleepMsUDF.class.getName())) + "'"));
        stmt1.execute("create table test_ser_1(i int)");
        stmt1.executeAsync((("insert into test_ser_1 select sleepMsUDF(under_col, 500) from " + (TestJdbcDriver2.tableName)) + " limit 1"));
        boolean isResultSet = stmt2.executeAsync("select * from test_ser_1");
        Assert.assertTrue(isResultSet);
        ResultSet rs = stmt2.getResultSet();
        Assert.assertTrue(rs.next());
        Assert.assertFalse(rs.next());
        stmt1.close();
        stmt2.close();
    }

    @Test
    public void testParentReferences() throws Exception {
        /* Test parent references from Statement */
        Statement s = TestJdbcDriver2.con.createStatement();
        ResultSet rs = s.executeQuery(("SELECT * FROM " + (TestJdbcDriver2.dataTypeTableName)));
        Assert.assertTrue(((s.getConnection()) == (TestJdbcDriver2.con)));
        Assert.assertTrue(((rs.getStatement()) == s));
        rs.close();
        s.close();
        /* Test parent references from PreparedStatement */
        PreparedStatement ps = TestJdbcDriver2.con.prepareStatement(("SELECT * FROM " + (TestJdbcDriver2.dataTypeTableName)));
        rs = ps.executeQuery();
        Assert.assertTrue(((ps.getConnection()) == (TestJdbcDriver2.con)));
        Assert.assertTrue(((rs.getStatement()) == ps));
        rs.close();
        ps.close();
        /* Test DatabaseMetaData queries which do not have a parent Statement */
        DatabaseMetaData md = TestJdbcDriver2.con.getMetaData();
        Assert.assertTrue(((md.getConnection()) == (TestJdbcDriver2.con)));
        rs = md.getCatalogs();
        Assert.assertNull(rs.getStatement());
        rs.close();
        rs = md.getColumns(null, null, null, null);
        Assert.assertNull(rs.getStatement());
        rs.close();
        rs = md.getFunctions(null, null, null);
        Assert.assertNull(rs.getStatement());
        rs.close();
        rs = md.getImportedKeys(null, null, null);
        Assert.assertNull(rs.getStatement());
        rs.close();
        rs = md.getPrimaryKeys(null, TestJdbcDriver2.testDbName, TestJdbcDriver2.tableName);
        Assert.assertNull(rs.getStatement());
        rs.close();
        rs = md.getProcedureColumns(null, null, null, null);
        Assert.assertNull(rs.getStatement());
        rs.close();
        rs = md.getProcedures(null, null, null);
        Assert.assertNull(rs.getStatement());
        rs.close();
        rs = md.getSchemas();
        Assert.assertNull(rs.getStatement());
        rs.close();
        rs = md.getTableTypes();
        Assert.assertNull(rs.getStatement());
        rs.close();
        rs = md.getTables(null, null, null, null);
        Assert.assertNull(rs.getStatement());
        rs.close();
        rs = md.getTypeInfo();
        Assert.assertNull(rs.getStatement());
        rs.close();
    }

    @Test
    public void testDataTypes2() throws Exception {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        ResultSet res = stmt.executeQuery(("select c5, c1 from " + (TestJdbcDriver2.dataTypeTableName)));
        ResultSetMetaData meta = res.getMetaData();
        // row 1
        Assert.assertTrue(res.next());
        // skip the last (partitioning) column since it is always non-null
        for (int i = 1; i < (meta.getColumnCount()); i++) {
            Assert.assertNull(res.getObject(i));
        }
        stmt.close();
    }

    @Test
    public void testErrorDiag() throws SQLException {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        // verify syntax error
        try {
            stmt.executeQuery(("select from " + (TestJdbcDriver2.dataTypeTableName)));
            Assert.fail("SQLException is expected");
        } catch (SQLException e) {
            Assert.assertEquals("42000", e.getSQLState());
        }
        // verify table not fuond error
        try {
            stmt.executeQuery("select * from nonTable");
            Assert.fail("SQLException is expected");
        } catch (SQLException e) {
            Assert.assertEquals("42S02", e.getSQLState());
        }
        // verify invalid column error
        try {
            stmt.executeQuery(("select zzzz from " + (TestJdbcDriver2.dataTypeTableName)));
            Assert.fail("SQLException is expected");
        } catch (SQLException e) {
            Assert.assertEquals("42000", e.getSQLState());
        }
        stmt.close();
    }

    /**
     * verify 'explain ...' resultset
     *
     * @throws SQLException
     * 		
     */
    @Test
    public void testExplainStmt() throws SQLException {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        ResultSet res = stmt.executeQuery(((("explain select c1, c2, c3, c4, c5 as a, c6, c7, c8, c9, c10, c11, c12, " + "c1*2, sentences(null, null, null) as b, c23 from ") + (TestJdbcDriver2.dataTypeTableName)) + " limit 1"));
        ResultSetMetaData md = res.getMetaData();
        // only one result column
        Assert.assertEquals(md.getColumnCount(), 1);
        // verify the column name
        Assert.assertEquals(md.getColumnLabel(1), ExplainTask.EXPL_COLUMN_NAME);
        // verify that there is data in the resultset
        Assert.assertTrue("Nothing returned explain", res.next());
        stmt.close();
    }

    @Test
    public void testPrepareStatement() {
        String sql = ((((("FROM (SELECT 1 FROM " + (TestJdbcDriver2.tableName)) + " where   'not?param?not?param' <> 'not_param??not_param' and ?=? ") + " and 1=? and 2=? and 3.0=? and 4.0=? and \'test\\\'string\"\'=? and 5=? and ?=? ") + " and date '2012-01-01' = date ?") + " and timestamp '2012-04-22 09:00:00.123456789' = timestamp ?") + " ) t SELECT '2011-03-25' ddate,'China',true bv, 10 num LIMIT 1";
        // executed twice: once with the typed ps setters, once with the generic setObject
        try {
            try (PreparedStatement ps = createPreapredStatementUsingSetXXX(sql);ResultSet res = ps.executeQuery()) {
                assertPreparedStatementResultAsExpected(res);
            }
            try (PreparedStatement ps = createPreapredStatementUsingSetObject(sql);ResultSet res = ps.executeQuery()) {
                assertPreparedStatementResultAsExpected(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }
        // set nothing for prepared sql
        Exception expectedException = null;
        try (PreparedStatement ps = TestJdbcDriver2.con.prepareStatement(sql);ResultSet ignored = ps.executeQuery()) {
        } catch (Exception e) {
            expectedException = e;
        }
        Assert.assertNotNull("Execute the un-setted sql statement should throw exception", expectedException);
        // set some of parameters for prepared sql, not all of them.
        expectedException = null;
        try (PreparedStatement ps = TestJdbcDriver2.con.prepareStatement(sql)) {
            ps.setBoolean(1, true);
            ps.setBoolean(2, true);
            try (ResultSet ignored = ps.executeQuery()) {
            }
        } catch (Exception e) {
            expectedException = e;
        }
        Assert.assertNotNull("Execute the invalid setted sql statement should throw exception", expectedException);
        // set the wrong type parameters for prepared sql.
        expectedException = null;
        try (PreparedStatement ps = TestJdbcDriver2.con.prepareStatement(sql)) {
            // wrong type here
            ps.setString(1, "wrong");
            try (ResultSet res = ps.executeQuery()) {
                Assert.assertFalse("ResultSet was not empty", res.next());
            }
        } catch (Exception e) {
            expectedException = e;
        }
        Assert.assertNotNull("Execute the invalid setted sql statement should throw exception", expectedException);
        // setObject to the yet unknown type java.util.Date
        expectedException = null;
        try (PreparedStatement ps = TestJdbcDriver2.con.prepareStatement(sql)) {
            ps.setObject(1, new Date());
            try (ResultSet ignored = ps.executeQuery()) {
            }
        } catch (Exception e) {
            expectedException = e;
        }
        Assert.assertNotNull("Setting to an unknown type should throw an exception", expectedException);
    }

    @Test
    public void testPrepareStatementWithSetBinaryStream() throws SQLException {
        PreparedStatement stmt = TestJdbcDriver2.con.prepareStatement((("select under_col from " + (TestJdbcDriver2.tableName)) + " where value=?"));
        stmt.setBinaryStream(1, new ByteArrayInputStream("'val_238' or under_col <> 0".getBytes()));
        ResultSet res = stmt.executeQuery();
        Assert.assertFalse(res.next());
    }

    @Test
    public void testPrepareStatementWithSetString() throws SQLException {
        PreparedStatement stmt = TestJdbcDriver2.con.prepareStatement((("select under_col from " + (TestJdbcDriver2.tableName)) + " where value=?"));
        stmt.setString(1, "val_238\\\' or under_col <> 0 --");
        ResultSet res = stmt.executeQuery();
        Assert.assertFalse(res.next());
        stmt.setString(1, "anyStringHere\\\' or 1=1 --");
        res = stmt.executeQuery();
        Assert.assertFalse(res.next());
    }

    /**
     * Execute non-select statements using execute() and executeUpdated() APIs
     * of PreparedStatement interface
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testExecutePreparedStatement() throws Exception {
        String key = "testKey";
        String val1 = "val1";
        String val2 = "val2";
        PreparedStatement ps = TestJdbcDriver2.con.prepareStatement((("set " + key) + " = ?"));
        // execute() of Prepared statement
        ps.setString(1, val1);
        ps.execute();
        verifyConfValue(TestJdbcDriver2.con, key, val1);
        // executeUpdate() of Prepared statement
        ps.clearParameters();
        ps.setString(1, val2);
        ps.executeUpdate();
        verifyConfValue(TestJdbcDriver2.con, key, val2);
    }

    @Test
    public void testSetOnConnection() throws Exception {
        Connection connection = TestJdbcDriver2.getConnection(((TestJdbcDriver2.testDbName) + "?conf1=conf2;conf3=conf4#var1=var2;var3=var4"));
        try {
            verifyConfValue(connection, "conf1", "conf2");
            verifyConfValue(connection, "conf3", "conf4");
            verifyConfValue(connection, "var1", "var2");
            verifyConfValue(connection, "var3", "var4");
        } catch (Exception e) {
            connection.close();
        }
    }

    @Test
    public final void testSelectAll() throws Exception {
        doTestSelectAll(TestJdbcDriver2.tableName, (-1), (-1));// tests not setting maxRows (return all)

        doTestSelectAll(TestJdbcDriver2.tableName, 0, (-1));// tests setting maxRows to 0 (return all)

    }

    @Test
    public final void testSelectAllFromView() throws Exception {
        doTestSelectAll(TestJdbcDriver2.viewName, (-1), (-1));// tests not setting maxRows (return all)

        doTestSelectAll(TestJdbcDriver2.viewName, 0, (-1));// tests setting maxRows to 0 (return all)

    }

    @Test
    public final void testSelectAllPartioned() throws Exception {
        doTestSelectAll(TestJdbcDriver2.partitionedTableName, (-1), (-1));// tests not setting maxRows

        // (return all)
        doTestSelectAll(TestJdbcDriver2.partitionedTableName, 0, (-1));// tests setting maxRows to 0

        // (return all)
    }

    @Test
    public final void testSelectAllMaxRows() throws Exception {
        doTestSelectAll(TestJdbcDriver2.tableName, 100, (-1));
    }

    @Test
    public final void testSelectAllFetchSize() throws Exception {
        doTestSelectAll(TestJdbcDriver2.tableName, 100, 20);
    }

    @Test
    public void testNullType() throws Exception {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        try {
            ResultSet res = stmt.executeQuery(("select null from " + (TestJdbcDriver2.dataTypeTableName)));
            Assert.assertTrue(res.next());
            Assert.assertNull(res.getObject(1));
        } finally {
            stmt.close();
        }
    }

    // executeQuery should always throw a SQLException,
    // when it executes a non-ResultSet query (like create)
    @Test
    public void testExecuteQueryException() throws Exception {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        try {
            stmt.executeQuery("create table test_t2 (under_col int, value string)");
            Assert.fail("Expecting SQLException");
        } catch (SQLException e) {
            System.out.println(("Caught an expected SQLException: " + (e.getMessage())));
        } finally {
            stmt.close();
        }
    }

    @Test
    public void testNullResultSet() throws Exception {
        List<String> setupQueries = new ArrayList<String>();
        String testQuery;
        Statement stmt = TestJdbcDriver2.con.createStatement();
        // -select- should return a ResultSet
        testQuery = ("select * from " + (TestJdbcDriver2.tableName)) + " limit 5";
        checkResultSetExpected(stmt, setupQueries, testQuery, true);
        setupQueries.clear();
        // -create- should not return a ResultSet
        setupQueries.add("drop table test_t1");
        testQuery = "create table test_t1 (under_col int, value string)";
        checkResultSetExpected(stmt, setupQueries, testQuery, false);
        setupQueries.clear();
        // -create table as select- should not return a ResultSet
        setupQueries.add("drop table test_t1");
        testQuery = ("create table test_t1 as select * from " + (TestJdbcDriver2.tableName)) + " limit 5";
        checkResultSetExpected(stmt, setupQueries, testQuery, false);
        setupQueries.clear();
        // -insert table as select- should not return a ResultSet
        setupQueries.add("drop table test_t1");
        setupQueries.add("create table test_t1 (under_col int, value string)");
        testQuery = ("insert into table test_t1 select under_col, value from " + (TestJdbcDriver2.tableName)) + " limit 5";
        checkResultSetExpected(stmt, setupQueries, testQuery, false);
        setupQueries.clear();
        stmt.close();
    }

    @Test
    public void testCloseResultSet() throws Exception {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        // execute query, ignore exception if any
        ResultSet res = stmt.executeQuery(("select * from " + (TestJdbcDriver2.tableName)));
        // close ResultSet, ignore exception if any
        res.close();
        // A statement should be open even after ResultSet#close
        Assert.assertFalse(stmt.isClosed());
        // A Statement#cancel after ResultSet#close should be a no-op
        try {
            stmt.cancel();
        } catch (SQLException e) {
            failWithExceptionMsg(e);
        }
        stmt.close();
        stmt = TestJdbcDriver2.con.createStatement();
        // execute query, ignore exception if any
        res = stmt.executeQuery(("select * from " + (TestJdbcDriver2.tableName)));
        // close ResultSet, ignore exception if any
        res.close();
        // A Statement#execute after ResultSet#close should be fine too
        try {
            stmt.executeQuery(("select * from " + (TestJdbcDriver2.tableName)));
        } catch (SQLException e) {
            failWithExceptionMsg(e);
        }
        // A Statement#close after ResultSet#close should close the statement
        stmt.close();
        Assert.assertTrue(stmt.isClosed());
    }

    @Test
    public void testDataTypes() throws Exception {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        ResultSet res = stmt.executeQuery(("select * from " + (TestJdbcDriver2.dataTypeTableName)));
        ResultSetMetaData meta = res.getMetaData();
        // row 1
        Assert.assertTrue(res.next());
        // skip the last (partitioning) column since it is always non-null
        for (int i = 1; i < (meta.getColumnCount()); i++) {
            Assert.assertNull((("Column " + i) + " should be null"), res.getObject(i));
        }
        // getXXX returns 0 for numeric types, false for boolean and null for other
        Assert.assertEquals(0, res.getInt(1));
        Assert.assertEquals(false, res.getBoolean(2));
        Assert.assertEquals(0.0, res.getDouble(3), TestJdbcDriver2.floatCompareDelta);
        Assert.assertEquals(null, res.getString(4));
        Assert.assertEquals(null, res.getString(5));
        Assert.assertEquals(null, res.getString(6));
        Assert.assertEquals(null, res.getString(7));
        Assert.assertEquals(null, res.getString(8));
        Assert.assertEquals(0, res.getByte(9));
        Assert.assertEquals(0, res.getShort(10));
        Assert.assertEquals(0.0F, res.getFloat(11), TestJdbcDriver2.floatCompareDelta);
        Assert.assertEquals(0L, res.getLong(12));
        Assert.assertEquals(null, res.getString(13));
        Assert.assertEquals(null, res.getString(14));
        Assert.assertEquals(null, res.getString(15));
        Assert.assertEquals(null, res.getString(16));
        Assert.assertEquals(null, res.getString(17));
        Assert.assertEquals(null, res.getString(18));
        Assert.assertEquals(null, res.getString(19));
        Assert.assertEquals(null, res.getString(20));
        Assert.assertEquals(null, res.getDate(20));
        Assert.assertEquals(null, res.getString(21));
        Assert.assertEquals(null, res.getString(22));
        // row 2
        Assert.assertTrue(res.next());
        Assert.assertEquals((-1), res.getInt(1));
        Assert.assertEquals(false, res.getBoolean(2));
        Assert.assertEquals((-1.1), res.getDouble(3), TestJdbcDriver2.floatCompareDelta);
        Assert.assertEquals("", res.getString(4));
        Assert.assertEquals("[]", res.getString(5));
        Assert.assertEquals("{}", res.getString(6));
        Assert.assertEquals("{}", res.getString(7));
        Assert.assertEquals("{\"r\":null,\"s\":null,\"t\":null}", res.getString(8));
        Assert.assertEquals((-1), res.getByte(9));
        Assert.assertEquals((-1), res.getShort(10));
        Assert.assertEquals((-1.0F), res.getFloat(11), TestJdbcDriver2.floatCompareDelta);
        Assert.assertEquals((-1), res.getLong(12));
        Assert.assertEquals("[]", res.getString(13));
        Assert.assertEquals("{}", res.getString(14));
        Assert.assertEquals("{\"r\":null,\"s\":null}", res.getString(15));
        Assert.assertEquals("[]", res.getString(16));
        Assert.assertEquals(null, res.getString(17));
        Assert.assertEquals(null, res.getTimestamp(17));
        Assert.assertEquals(null, res.getBigDecimal(18));
        Assert.assertEquals(null, res.getString(19));
        Assert.assertEquals(null, res.getString(20));
        Assert.assertEquals(null, res.getDate(20));
        Assert.assertEquals(null, res.getString(21));
        Assert.assertEquals(null, res.getString(22));
        Assert.assertEquals(null, res.getString(23));
        // row 3
        Assert.assertTrue(res.next());
        Assert.assertEquals(1, res.getInt(1));
        Assert.assertEquals(true, res.getBoolean(2));
        Assert.assertEquals(1.1, res.getDouble(3), TestJdbcDriver2.floatCompareDelta);
        Assert.assertEquals("1", res.getString(4));
        Assert.assertEquals("[1,2]", res.getString(5));
        Assert.assertEquals("{1:\"x\",2:\"y\"}", res.getString(6));
        Assert.assertEquals("{\"k\":\"v\"}", res.getString(7));
        Assert.assertEquals("{\"r\":\"a\",\"s\":9,\"t\":2.2}", res.getString(8));
        Assert.assertEquals(1, res.getByte(9));
        Assert.assertEquals(1, res.getShort(10));
        Assert.assertEquals(1.0F, res.getFloat(11), TestJdbcDriver2.floatCompareDelta);
        Assert.assertEquals(1, res.getLong(12));
        Assert.assertEquals("[[\"a\",\"b\"],[\"c\",\"d\"]]", res.getString(13));
        Assert.assertEquals("{1:{11:12,13:14},2:{21:22}}", res.getString(14));
        Assert.assertEquals("{\"r\":1,\"s\":{\"a\":2,\"b\":\"x\"}}", res.getString(15));
        Assert.assertEquals("[{\"m\":{},\"n\":1},{\"m\":{\"a\":\"b\",\"c\":\"d\"},\"n\":2}]", res.getString(16));
        Assert.assertEquals("2012-04-22 09:00:00.123456789", res.getString(17));
        Assert.assertEquals("2012-04-22 09:00:00.123456789", res.getTimestamp(17).toString());
        Assert.assertEquals("123456789.1234560", res.getBigDecimal(18).toString());
        Assert.assertEquals("abcd", res.getString(19));
        Assert.assertEquals("2013-01-01", res.getString(20));
        Assert.assertEquals("2013-01-01", res.getDate(20).toString());
        Assert.assertEquals("abc123", res.getString(21));
        Assert.assertEquals("abc123         ", res.getString(22));
        byte[] bytes = "X'01FF'".getBytes("UTF-8");
        InputStream resultSetInputStream = res.getBinaryStream(23);
        int len = bytes.length;
        byte[] b = new byte[len];
        resultSetInputStream.read(b, 0, len);
        for (int i = 0; i < len; i++) {
            Assert.assertEquals(bytes[i], b[i]);
        }
        // test getBoolean rules on non-boolean columns
        Assert.assertEquals(true, res.getBoolean(1));
        Assert.assertEquals(true, res.getBoolean(4));
        // test case sensitivity
        Assert.assertFalse(meta.isCaseSensitive(1));
        Assert.assertFalse(meta.isCaseSensitive(2));
        Assert.assertFalse(meta.isCaseSensitive(3));
        Assert.assertTrue(meta.isCaseSensitive(4));
        // no more rows
        Assert.assertFalse(res.next());
        stmt.close();
    }

    @Test
    public void testIntervalTypes() throws Exception {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        // Since interval types not currently supported as table columns, need to create them
        // as expressions.
        ResultSet res = stmt.executeQuery((("select case when c17 is null then null else interval '1' year end as col1," + " c17 -  c17 as col2 from ") + (TestJdbcDriver2.dataTypeTableName)));
        ResultSetMetaData meta = res.getMetaData();
        Assert.assertEquals("col1", meta.getColumnLabel(1));
        Assert.assertEquals(Types.OTHER, meta.getColumnType(1));
        Assert.assertEquals("interval_year_month", meta.getColumnTypeName(1));
        Assert.assertEquals(11, meta.getColumnDisplaySize(1));
        Assert.assertEquals(11, meta.getPrecision(1));
        Assert.assertEquals(0, meta.getScale(1));
        Assert.assertEquals(HiveIntervalYearMonth.class.getName(), meta.getColumnClassName(1));
        Assert.assertEquals("col2", meta.getColumnLabel(2));
        Assert.assertEquals(Types.OTHER, meta.getColumnType(2));
        Assert.assertEquals("interval_day_time", meta.getColumnTypeName(2));
        Assert.assertEquals(29, meta.getColumnDisplaySize(2));
        Assert.assertEquals(29, meta.getPrecision(2));
        Assert.assertEquals(0, meta.getScale(2));
        Assert.assertEquals(HiveIntervalDayTime.class.getName(), meta.getColumnClassName(2));
        // row 1 - results should be null
        Assert.assertTrue(res.next());
        // skip the last (partitioning) column since it is always non-null
        for (int i = 1; i < (meta.getColumnCount()); i++) {
            Assert.assertNull((("Column " + i) + " should be null"), res.getObject(i));
        }
        // row 2 - results should be null
        Assert.assertTrue(res.next());
        for (int i = 1; i < (meta.getColumnCount()); i++) {
            Assert.assertNull((("Column " + i) + " should be null"), res.getObject(i));
        }
        // row 3
        Assert.assertTrue(res.next());
        Assert.assertEquals("1-0", res.getString(1));
        Assert.assertEquals(1, getYears());
        Assert.assertEquals("0 00:00:00.000000000", res.getString(2));
        Assert.assertEquals(0, getDays());
        stmt.close();
    }

    @Test
    public void testErrorMessages() throws SQLException {
        String invalidSyntaxSQLState = "42000";
        // These tests inherently cause exceptions to be written to the test output
        // logs. This is undesirable, since you it might appear to someone looking
        // at the test output logs as if something is failing when it isn't.
        // Not sure how to get around that.
        doTestErrorCase(("SELECTT * FROM " + (TestJdbcDriver2.tableName)), "cannot recognize input near 'SELECTT' '*' 'FROM'", invalidSyntaxSQLState, 40000);
        doTestErrorCase("SELECT * FROM some_table_that_does_not_exist", "Table not found", "42S02", 10001);
        doTestErrorCase("drop table some_table_that_does_not_exist", "Table not found", "42S02", 10001);
        doTestErrorCase(("SELECT invalid_column FROM " + (TestJdbcDriver2.tableName)), "Invalid table alias or column reference", invalidSyntaxSQLState, 10004);
        doTestErrorCase(("SELECT invalid_function(under_col) FROM " + (TestJdbcDriver2.tableName)), "Invalid function", invalidSyntaxSQLState, 10011);
        // TODO: execute errors like this currently don't return good error
        // codes and messages. This should be fixed.
        doTestErrorCase((("create table " + (TestJdbcDriver2.tableName)) + " (key int, value string)"), "FAILED: Execution Error, return code 1 from org.apache.hadoop.hive.ql.exec.DDLTask", "08S01", 1);
    }

    @Test
    public void testShowTables() throws SQLException {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        Assert.assertNotNull("Statement is null", stmt);
        ResultSet res = stmt.executeQuery("show tables");
        boolean testTableExists = false;
        while (res.next()) {
            Assert.assertNotNull("table name is null in result set", res.getString(1));
            if (TestJdbcDriver2.tableName.equalsIgnoreCase(res.getString(1))) {
                testTableExists = true;
            }
        } 
        Assert.assertTrue((("table name " + (TestJdbcDriver2.tableName)) + " not found in SHOW TABLES result set"), testTableExists);
        stmt.close();
    }

    @Test
    public void testShowTablesInDb() throws SQLException {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        Assert.assertNotNull("Statement is null", stmt);
        String tableNameInDbUnique = (TestJdbcDriver2.tableName) + "_unique";
        String fullTestTableName = StatsUtils.getFullyQualifiedTableName(TestJdbcDriver2.testDbName, tableNameInDbUnique);
        // create a table with a unique name in testDb
        stmt.execute(("drop table if exists " + fullTestTableName));
        stmt.execute((((("create table " + fullTestTableName) + " (under_col int comment 'the under column', value string) comment '") + (TestJdbcDriver2.tableComment)) + "'"));
        ResultSet res = stmt.executeQuery(("show tables in " + (TestJdbcDriver2.testDbName)));
        boolean testTableExists = false;
        while (res.next()) {
            Assert.assertNotNull("table name is null in result set", res.getString(1));
            if (tableNameInDbUnique.equalsIgnoreCase(res.getString(1))) {
                testTableExists = true;
            }
        } 
        Assert.assertTrue((("table name " + tableNameInDbUnique) + " not found in SHOW TABLES result set"), testTableExists);
        stmt.execute(("drop table if exists " + fullTestTableName));
        stmt.close();
    }

    @Test
    public void testInvalidShowTables() throws SQLException {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        Assert.assertNotNull("Statement is null", stmt);
        // show tables <dbname> is in invalid show tables syntax. Hive does not return
        // any tables in this case
        ResultSet res = stmt.executeQuery(("show tables " + (TestJdbcDriver2.testDbName)));
        Assert.assertFalse(res.next());
        stmt.close();
    }

    @Test
    public void testMetaDataGetTables() throws SQLException {
        getTablesTest(ImmutableSet.of(TABLE.toString()), VIEW.toString());
    }

    @Test
    public void testMetaDataGetTablesHive() throws SQLException {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        stmt.execute(((("set " + (HIVE_SERVER2_TABLE_TYPE_MAPPING.varname)) + " = ") + (HIVE.toString())));
        getTablesTest(ImmutableSet.of(MANAGED_TABLE.toString(), EXTERNAL_TABLE.toString()), VIRTUAL_VIEW.toString());
    }

    @Test
    public void testMetaDataGetTablesClassic() throws SQLException {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        stmt.execute(((("set " + (HIVE_SERVER2_TABLE_TYPE_MAPPING.varname)) + " = ") + (CLASSIC.toString())));
        stmt.close();
        getTablesTest(ImmutableSet.of(TABLE.toString()), VIEW.toString());
    }

    @Test
    public void testMetaDataGetExternalTables() throws SQLException {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        stmt.execute(((("set " + (HIVE_SERVER2_TABLE_TYPE_MAPPING.varname)) + " = ") + (HIVE.toString())));
        stmt.close();
        ResultSet rs = TestJdbcDriver2.con.getMetaData().getTables(TestJdbcDriver2.testDbName, null, null, new String[]{ EXTERNAL_TABLE.toString() });
        ResultSetMetaData resMeta = rs.getMetaData();
        Assert.assertEquals(10, resMeta.getColumnCount());
        Assert.assertEquals("TABLE_CAT", resMeta.getColumnName(1));
        Assert.assertEquals("TABLE_SCHEM", resMeta.getColumnName(2));
        Assert.assertEquals("TABLE_NAME", resMeta.getColumnName(3));
        Assert.assertEquals("TABLE_TYPE", resMeta.getColumnName(4));
        Assert.assertEquals("REMARKS", resMeta.getColumnName(5));
        rs.next();
        String resultDbName = rs.getString("TABLE_SCHEM");
        Assert.assertEquals(resultDbName, TestJdbcDriver2.testDbName);
        String resultTableName = rs.getString("TABLE_NAME");
        Assert.assertEquals(resultTableName, TestJdbcDriver2.externalTableName.toLowerCase());
        String resultTableComment = rs.getString("REMARKS");
        Assert.assertTrue("Missing comment on the table.", ((resultTableComment.length()) > 0));
        String tableType = rs.getString("TABLE_TYPE");
        Assert.assertEquals(EXTERNAL_TABLE.toString(), tableType);
        Assert.assertFalse("Unexpected table", rs.next());
    }

    @Test
    public void testMetaDataGetTypeInfo() throws SQLException {
        HiveBaseResultSet rs = ((HiveBaseResultSet) (TestJdbcDriver2.con.getMetaData().getTypeInfo()));
        Set<String> typeInfos = new HashSet<String>();
        typeInfos.add("BOOLEAN");
        typeInfos.add("TINYINT");
        typeInfos.add("SMALLINT");
        typeInfos.add("INT");
        typeInfos.add("BIGINT");
        typeInfos.add("FLOAT");
        typeInfos.add("DOUBLE");
        typeInfos.add("STRING");
        typeInfos.add("TIMESTAMP");
        typeInfos.add("BINARY");
        typeInfos.add("DECIMAL");
        typeInfos.add("ARRAY");
        typeInfos.add("MAP");
        typeInfos.add("STRUCT");
        typeInfos.add("UNIONTYPE");
        int cnt = 0;
        while (rs.next()) {
            String typeInfo = rs.getString("TYPE_NAME");
            Assert.assertEquals("Get by index different from get by name", rs.getString(1), typeInfo);
            typeInfos.remove(typeInfo);
            cnt++;
        } 
        rs.close();
        Assert.assertEquals("Incorrect typeInfo count.", 0, typeInfos.size());
        Assert.assertTrue("Found less typeInfos than we test for.", (cnt >= (typeInfos.size())));
    }

    @Test
    public void testMetaDataGetCatalogs() throws SQLException {
        ResultSet rs = TestJdbcDriver2.con.getMetaData().getCatalogs();
        ResultSetMetaData resMeta = rs.getMetaData();
        Assert.assertEquals(1, resMeta.getColumnCount());
        Assert.assertEquals("TABLE_CAT", resMeta.getColumnName(1));
        Assert.assertFalse(rs.next());
    }

    @Test
    public void testMetaDataGetSchemas() throws SQLException {
        ResultSet rs = TestJdbcDriver2.con.getMetaData().getSchemas();
        ResultSetMetaData resMeta = rs.getMetaData();
        Assert.assertEquals(2, resMeta.getColumnCount());
        Assert.assertEquals("TABLE_SCHEM", resMeta.getColumnName(1));
        Assert.assertEquals("TABLE_CATALOG", resMeta.getColumnName(2));
        Assert.assertTrue(rs.next());
        Assert.assertEquals("default", rs.getString(1));
        Assert.assertTrue(rs.next());
        Assert.assertEquals(TestJdbcDriver2.testDbName, rs.getString(1));
        Assert.assertFalse(rs.next());
        rs.close();
    }

    // test default table types returned in
    // Connection.getMetaData().getTableTypes()
    @Test
    public void testMetaDataGetTableTypes() throws SQLException {
        metaDataGetTableTypeTest(new ClassicTableTypeMapping().getTableTypeNames());
    }

    // test default table types returned in
    // Connection.getMetaData().getTableTypes() when type config is set to "HIVE"
    @Test
    public void testMetaDataGetHiveTableTypes() throws SQLException {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        stmt.execute(((("set " + (HIVE_SERVER2_TABLE_TYPE_MAPPING.varname)) + " = ") + (HIVE.toString())));
        stmt.close();
        metaDataGetTableTypeTest(new HiveTableTypeMapping().getTableTypeNames());
    }

    // test default table types returned in
    // Connection.getMetaData().getTableTypes() when type config is set to "CLASSIC"
    @Test
    public void testMetaDataGetClassicTableTypes() throws SQLException {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        stmt.execute(((("set " + (HIVE_SERVER2_TABLE_TYPE_MAPPING.varname)) + " = ") + (CLASSIC.toString())));
        stmt.close();
        metaDataGetTableTypeTest(new ClassicTableTypeMapping().getTableTypeNames());
    }

    @Test
    public void testMetaDataGetColumns() throws SQLException {
        Map<String[], Integer> tests = new HashMap<String[], Integer>();
        tests.put(new String[]{ "testjdbcdrivertbl", null }, 2);
        tests.put(new String[]{ "%jdbcdrivertbl", null }, 2);
        tests.put(new String[]{ "%jdbcdrivertbl%", "under\\_col" }, 1);
        tests.put(new String[]{ "%jdbcdrivertbl%", "under\\_co_" }, 1);
        tests.put(new String[]{ "%jdbcdrivertbl%", "under_col" }, 1);
        tests.put(new String[]{ "%jdbcdrivertbl%", "und%" }, 1);
        tests.put(new String[]{ "%jdbcdrivertbl%", "%" }, 2);
        tests.put(new String[]{ "%jdbcdrivertbl%", "_%" }, 2);
        for (String[] checkPattern : tests.keySet()) {
            ResultSet rs = TestJdbcDriver2.con.getMetaData().getColumns(null, TestJdbcDriver2.testDbName, checkPattern[0], checkPattern[1]);
            // validate the metadata for the getColumns result set
            ResultSetMetaData rsmd = rs.getMetaData();
            Assert.assertEquals("TABLE_CAT", rsmd.getColumnName(1));
            int cnt = 0;
            while (rs.next()) {
                String columnname = rs.getString("COLUMN_NAME");
                int ordinalPos = rs.getInt("ORDINAL_POSITION");
                switch (cnt) {
                    case 0 :
                        Assert.assertEquals("Wrong column name found", "under_col", columnname);
                        Assert.assertEquals("Wrong ordinal position found", ordinalPos, 1);
                        break;
                    case 1 :
                        Assert.assertEquals("Wrong column name found", "value", columnname);
                        Assert.assertEquals("Wrong ordinal position found", ordinalPos, 2);
                        break;
                    default :
                        break;
                }
                cnt++;
            } 
            rs.close();
            Assert.assertEquals("Found less columns then we test for.", tests.get(checkPattern).intValue(), cnt);
        }
    }

    /**
     * Validate the Metadata for the result set of a metadata getColumns call.
     */
    @Test
    public void testMetaDataGetColumnsMetaData() throws SQLException {
        ResultSet rs = TestJdbcDriver2.con.getMetaData().getColumns(null, null, "testhivejdbcdrivertable", null);
        ResultSetMetaData rsmd = rs.getMetaData();
        Assert.assertEquals("TABLE_CAT", rsmd.getColumnName(1));
        Assert.assertEquals(Types.VARCHAR, rsmd.getColumnType(1));
        Assert.assertEquals(Integer.MAX_VALUE, rsmd.getColumnDisplaySize(1));
        Assert.assertEquals("ORDINAL_POSITION", rsmd.getColumnName(17));
        Assert.assertEquals(Types.INTEGER, rsmd.getColumnType(17));
        Assert.assertEquals(11, rsmd.getColumnDisplaySize(17));
    }

    @Test
    public void testDescribeTable() throws SQLException {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        Assert.assertNotNull("Statement is null", stmt);
        ResultSet res = stmt.executeQuery(("describe " + (TestJdbcDriver2.tableName)));
        res.next();
        Assert.assertEquals("Column name 'under_col' not found", "under_col", res.getString(1));
        Assert.assertEquals("Column type 'under_col' for column under_col not found", "int", res.getString(2));
        res.next();
        Assert.assertEquals("Column name 'value' not found", "value", res.getString(1));
        Assert.assertEquals("Column type 'string' for column key not found", "string", res.getString(2));
        Assert.assertFalse("More results found than expected", res.next());
        stmt.close();
    }

    @Test
    public void testShowColumns() throws SQLException {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        Assert.assertNotNull("Statement is null", stmt);
        ResultSet res = stmt.executeQuery(("show columns in " + (TestJdbcDriver2.tableName)));
        res.next();
        Assert.assertEquals("Column name 'under_col' not found", "under_col", res.getString(1));
        res.next();
        Assert.assertEquals("Column name 'value' not found", "value", res.getString(1));
        Assert.assertFalse("More results found than expected", res.next());
        stmt.close();
    }

    @Test
    public void testDatabaseMetaData() throws SQLException {
        DatabaseMetaData meta = TestJdbcDriver2.con.getMetaData();
        Assert.assertEquals("Apache Hive", meta.getDatabaseProductName());
        String[] keywords = meta.getSQLKeywords().toLowerCase().split(",");
        // Check a random one. These can change w/Hive versions.
        boolean found = false;
        for (String keyword : keywords) {
            found = "limit".equals(keyword);
            if (found)
                break;

        }
        Assert.assertTrue(found);
        Assert.assertEquals(HiveVersionInfo.getVersion(), meta.getDatabaseProductVersion());
        Assert.assertEquals(System.getProperty("hive.version"), meta.getDatabaseProductVersion());
        Assert.assertTrue(("verifying hive version pattern. got " + (meta.getDatabaseProductVersion())), Pattern.matches("\\d+\\.\\d+\\.\\d+.*", meta.getDatabaseProductVersion()));
        Assert.assertEquals(DatabaseMetaData.sqlStateSQL99, meta.getSQLStateType());
        Assert.assertFalse(meta.supportsCatalogsInTableDefinitions());
        Assert.assertTrue(meta.supportsSchemasInTableDefinitions());
        Assert.assertTrue(meta.supportsSchemasInDataManipulation());
        Assert.assertFalse(meta.supportsMultipleResultSets());
        Assert.assertFalse(meta.supportsStoredProcedures());
        Assert.assertTrue(meta.supportsAlterTableWithAddColumn());
        // -1 indicates malformed version.
        Assert.assertTrue(((meta.getDatabaseMajorVersion()) > (-1)));
        Assert.assertTrue(((meta.getDatabaseMinorVersion()) > (-1)));
    }

    @Test
    public void testClientInfo() throws SQLException {
        DatabaseMetaData meta = TestJdbcDriver2.con.getMetaData();
        ResultSet res = meta.getClientInfoProperties();
        try {
            Assert.assertTrue(res.next());
            Assert.assertEquals("ApplicationName", res.getString(1));
            Assert.assertEquals(1000, res.getInt("MAX_LEN"));
            Assert.assertFalse(res.next());
        } catch (Exception e) {
            String msg = "Unexpected exception: " + e;
            TestJdbcDriver2.LOG.info(msg, e);
            Assert.fail(msg);
        }
        Connection conn = TestJdbcDriver2.getConnection("");
        try {
            conn.setClientInfo("ApplicationName", "test");
            Assert.assertEquals("test", conn.getClientInfo("ApplicationName"));
        } finally {
            conn.close();
        }
    }

    @Test
    public void testResultSetColumnNameCaseInsensitive() throws SQLException {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        ResultSet res;
        res = stmt.executeQuery((("select c1 from " + (TestJdbcDriver2.dataTypeTableName)) + " limit 1"));
        try {
            int count = 0;
            while (res.next()) {
                res.findColumn("c1");
                res.findColumn("C1");
                count++;
            } 
            Assert.assertEquals(count, 1);
        } catch (Exception e) {
            String msg = "Unexpected exception: " + e;
            TestJdbcDriver2.LOG.info(msg, e);
            Assert.fail(msg);
        }
        res = stmt.executeQuery((("select c1 C1 from " + (TestJdbcDriver2.dataTypeTableName)) + " limit 1"));
        try {
            int count = 0;
            while (res.next()) {
                res.findColumn("c1");
                res.findColumn("C1");
                count++;
            } 
            Assert.assertEquals(count, 1);
        } catch (Exception e) {
            String msg = "Unexpected exception: " + e;
            TestJdbcDriver2.LOG.info(msg, e);
            Assert.fail(msg);
        }
        stmt.close();
    }

    @Test
    public void testResultSetMetaData() throws SQLException {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        stmt.execute((("set " + (HIVE_VECTORIZATION_ENABLED.varname)) + "=false"));
        ResultSet res = stmt.executeQuery(((("select c1, c2, c3, c4, c5 as a, c6, c7, c8, c9, c10, c11, c12, " + "c1*2, sentences(null, null, null) as b, c17, c18, c20, c21, c22, c23, null as null_val from ") + (TestJdbcDriver2.dataTypeTableName)) + " limit 1"));
        ResultSetMetaData meta = res.getMetaData();
        ResultSet colRS = TestJdbcDriver2.con.getMetaData().getColumns(null, null, TestJdbcDriver2.dataTypeTableName.toLowerCase(), null);
        Assert.assertEquals(21, meta.getColumnCount());
        Assert.assertTrue(colRS.next());
        Assert.assertEquals("c1", meta.getColumnName(1));
        Assert.assertEquals(Types.INTEGER, meta.getColumnType(1));
        Assert.assertEquals("int", meta.getColumnTypeName(1));
        Assert.assertEquals(11, meta.getColumnDisplaySize(1));
        Assert.assertEquals(10, meta.getPrecision(1));
        Assert.assertEquals(0, meta.getScale(1));
        Assert.assertEquals("c1", colRS.getString("COLUMN_NAME"));
        Assert.assertEquals(Types.INTEGER, colRS.getInt("DATA_TYPE"));
        Assert.assertEquals("int", colRS.getString("TYPE_NAME").toLowerCase());
        Assert.assertEquals(meta.getPrecision(1), colRS.getInt("COLUMN_SIZE"));
        Assert.assertEquals(meta.getScale(1), colRS.getInt("DECIMAL_DIGITS"));
        Assert.assertTrue(colRS.next());
        Assert.assertEquals("c2", meta.getColumnName(2));
        Assert.assertEquals("boolean", meta.getColumnTypeName(2));
        Assert.assertEquals(Types.BOOLEAN, meta.getColumnType(2));
        Assert.assertEquals(1, meta.getColumnDisplaySize(2));
        Assert.assertEquals(1, meta.getPrecision(2));
        Assert.assertEquals(0, meta.getScale(2));
        Assert.assertEquals("c2", colRS.getString("COLUMN_NAME"));
        Assert.assertEquals(Types.BOOLEAN, colRS.getInt("DATA_TYPE"));
        Assert.assertEquals("boolean", colRS.getString("TYPE_NAME").toLowerCase());
        Assert.assertEquals(meta.getScale(2), colRS.getInt("DECIMAL_DIGITS"));
        Assert.assertTrue(colRS.next());
        Assert.assertEquals("c3", meta.getColumnName(3));
        Assert.assertEquals(Types.DOUBLE, meta.getColumnType(3));
        Assert.assertEquals("double", meta.getColumnTypeName(3));
        Assert.assertEquals(25, meta.getColumnDisplaySize(3));
        Assert.assertEquals(15, meta.getPrecision(3));
        Assert.assertEquals(15, meta.getScale(3));
        Assert.assertEquals("c3", colRS.getString("COLUMN_NAME"));
        Assert.assertEquals(Types.DOUBLE, colRS.getInt("DATA_TYPE"));
        Assert.assertEquals("double", colRS.getString("TYPE_NAME").toLowerCase());
        Assert.assertEquals(meta.getPrecision(3), colRS.getInt("COLUMN_SIZE"));
        Assert.assertEquals(meta.getScale(3), colRS.getInt("DECIMAL_DIGITS"));
        Assert.assertTrue(colRS.next());
        Assert.assertEquals("c4", meta.getColumnName(4));
        Assert.assertEquals(Types.VARCHAR, meta.getColumnType(4));
        Assert.assertEquals("string", meta.getColumnTypeName(4));
        Assert.assertEquals(Integer.MAX_VALUE, meta.getColumnDisplaySize(4));
        Assert.assertEquals(Integer.MAX_VALUE, meta.getPrecision(4));
        Assert.assertEquals(0, meta.getScale(4));
        Assert.assertEquals("c4", colRS.getString("COLUMN_NAME"));
        Assert.assertEquals(Types.VARCHAR, colRS.getInt("DATA_TYPE"));
        Assert.assertEquals("string", colRS.getString("TYPE_NAME").toLowerCase());
        Assert.assertEquals(meta.getPrecision(4), colRS.getInt("COLUMN_SIZE"));
        Assert.assertEquals(meta.getScale(4), colRS.getInt("DECIMAL_DIGITS"));
        Assert.assertTrue(colRS.next());
        Assert.assertEquals("a", meta.getColumnName(5));
        Assert.assertEquals(Types.ARRAY, meta.getColumnType(5));
        Assert.assertEquals("array", meta.getColumnTypeName(5));
        Assert.assertEquals(Integer.MAX_VALUE, meta.getColumnDisplaySize(5));
        Assert.assertEquals(Integer.MAX_VALUE, meta.getPrecision(5));
        Assert.assertEquals(0, meta.getScale(5));
        Assert.assertEquals("c5", colRS.getString("COLUMN_NAME"));
        Assert.assertEquals(Types.ARRAY, colRS.getInt("DATA_TYPE"));
        Assert.assertEquals("array<int>", colRS.getString("TYPE_NAME").toLowerCase());
        Assert.assertTrue(colRS.next());
        Assert.assertEquals("c6", meta.getColumnName(6));
        Assert.assertEquals(Types.JAVA_OBJECT, meta.getColumnType(6));
        Assert.assertEquals("map", meta.getColumnTypeName(6));
        Assert.assertEquals(Integer.MAX_VALUE, meta.getColumnDisplaySize(6));
        Assert.assertEquals(Integer.MAX_VALUE, meta.getPrecision(6));
        Assert.assertEquals(0, meta.getScale(6));
        Assert.assertEquals("c6", colRS.getString("COLUMN_NAME"));
        Assert.assertEquals(Types.JAVA_OBJECT, colRS.getInt("DATA_TYPE"));
        Assert.assertEquals("map<int,string>", colRS.getString("TYPE_NAME").toLowerCase());
        Assert.assertTrue(colRS.next());
        Assert.assertEquals("c7", meta.getColumnName(7));
        Assert.assertEquals(Types.JAVA_OBJECT, meta.getColumnType(7));
        Assert.assertEquals("map", meta.getColumnTypeName(7));
        Assert.assertEquals(Integer.MAX_VALUE, meta.getColumnDisplaySize(7));
        Assert.assertEquals(Integer.MAX_VALUE, meta.getPrecision(7));
        Assert.assertEquals(0, meta.getScale(7));
        Assert.assertEquals("c7", colRS.getString("COLUMN_NAME"));
        Assert.assertEquals(Types.JAVA_OBJECT, colRS.getInt("DATA_TYPE"));
        Assert.assertEquals("map<string,string>", colRS.getString("TYPE_NAME").toLowerCase());
        Assert.assertTrue(colRS.next());
        Assert.assertEquals("c8", meta.getColumnName(8));
        Assert.assertEquals(Types.STRUCT, meta.getColumnType(8));
        Assert.assertEquals("struct", meta.getColumnTypeName(8));
        Assert.assertEquals(Integer.MAX_VALUE, meta.getColumnDisplaySize(8));
        Assert.assertEquals(Integer.MAX_VALUE, meta.getPrecision(8));
        Assert.assertEquals(0, meta.getScale(8));
        Assert.assertEquals("c8", colRS.getString("COLUMN_NAME"));
        Assert.assertEquals(Types.STRUCT, colRS.getInt("DATA_TYPE"));
        Assert.assertEquals("struct<r:string,s:int,t:double>", colRS.getString("TYPE_NAME").toLowerCase());
        Assert.assertTrue(colRS.next());
        Assert.assertEquals("c9", meta.getColumnName(9));
        Assert.assertEquals(Types.TINYINT, meta.getColumnType(9));
        Assert.assertEquals("tinyint", meta.getColumnTypeName(9));
        Assert.assertEquals(4, meta.getColumnDisplaySize(9));
        Assert.assertEquals(3, meta.getPrecision(9));
        Assert.assertEquals(0, meta.getScale(9));
        Assert.assertEquals("c9", colRS.getString("COLUMN_NAME"));
        Assert.assertEquals(Types.TINYINT, colRS.getInt("DATA_TYPE"));
        Assert.assertEquals("tinyint", colRS.getString("TYPE_NAME").toLowerCase());
        Assert.assertEquals(meta.getPrecision(9), colRS.getInt("COLUMN_SIZE"));
        Assert.assertEquals(meta.getScale(9), colRS.getInt("DECIMAL_DIGITS"));
        Assert.assertTrue(colRS.next());
        Assert.assertEquals("c10", meta.getColumnName(10));
        Assert.assertEquals(Types.SMALLINT, meta.getColumnType(10));
        Assert.assertEquals("smallint", meta.getColumnTypeName(10));
        Assert.assertEquals(6, meta.getColumnDisplaySize(10));
        Assert.assertEquals(5, meta.getPrecision(10));
        Assert.assertEquals(0, meta.getScale(10));
        Assert.assertEquals("c10", colRS.getString("COLUMN_NAME"));
        Assert.assertEquals(Types.SMALLINT, colRS.getInt("DATA_TYPE"));
        Assert.assertEquals("smallint", colRS.getString("TYPE_NAME").toLowerCase());
        Assert.assertEquals(meta.getPrecision(10), colRS.getInt("COLUMN_SIZE"));
        Assert.assertEquals(meta.getScale(10), colRS.getInt("DECIMAL_DIGITS"));
        Assert.assertTrue(colRS.next());
        Assert.assertEquals("c11", meta.getColumnName(11));
        Assert.assertEquals(Types.FLOAT, meta.getColumnType(11));
        Assert.assertEquals("float", meta.getColumnTypeName(11));
        Assert.assertEquals(24, meta.getColumnDisplaySize(11));
        Assert.assertEquals(7, meta.getPrecision(11));
        Assert.assertEquals(7, meta.getScale(11));
        Assert.assertEquals("c11", colRS.getString("COLUMN_NAME"));
        Assert.assertEquals(Types.FLOAT, colRS.getInt("DATA_TYPE"));
        Assert.assertEquals("float", colRS.getString("TYPE_NAME").toLowerCase());
        Assert.assertEquals(meta.getPrecision(11), colRS.getInt("COLUMN_SIZE"));
        Assert.assertEquals(meta.getScale(11), colRS.getInt("DECIMAL_DIGITS"));
        Assert.assertTrue(colRS.next());
        Assert.assertEquals("c12", meta.getColumnName(12));
        Assert.assertEquals(Types.BIGINT, meta.getColumnType(12));
        Assert.assertEquals("bigint", meta.getColumnTypeName(12));
        Assert.assertEquals(20, meta.getColumnDisplaySize(12));
        Assert.assertEquals(19, meta.getPrecision(12));
        Assert.assertEquals(0, meta.getScale(12));
        Assert.assertEquals("c12", colRS.getString("COLUMN_NAME"));
        Assert.assertEquals(Types.BIGINT, colRS.getInt("DATA_TYPE"));
        Assert.assertEquals("bigint", colRS.getString("TYPE_NAME").toLowerCase());
        Assert.assertEquals(meta.getPrecision(12), colRS.getInt("COLUMN_SIZE"));
        Assert.assertEquals(meta.getScale(12), colRS.getInt("DECIMAL_DIGITS"));
        Assert.assertEquals("_c12", meta.getColumnName(13));
        Assert.assertEquals(Types.INTEGER, meta.getColumnType(13));
        Assert.assertEquals("int", meta.getColumnTypeName(13));
        Assert.assertEquals(11, meta.getColumnDisplaySize(13));
        Assert.assertEquals(10, meta.getPrecision(13));
        Assert.assertEquals(0, meta.getScale(13));
        Assert.assertEquals("b", meta.getColumnName(14));
        Assert.assertEquals(Types.ARRAY, meta.getColumnType(14));
        Assert.assertEquals("array", meta.getColumnTypeName(14));
        Assert.assertEquals(Integer.MAX_VALUE, meta.getColumnDisplaySize(14));
        Assert.assertEquals(Integer.MAX_VALUE, meta.getPrecision(14));
        Assert.assertEquals(0, meta.getScale(14));
        // Move the result of getColumns() forward to match the columns of the query
        Assert.assertTrue(colRS.next());// c13

        Assert.assertTrue(colRS.next());// c14

        Assert.assertTrue(colRS.next());// c15

        Assert.assertTrue(colRS.next());// c16

        Assert.assertTrue(colRS.next());// c17

        Assert.assertEquals("c17", meta.getColumnName(15));
        Assert.assertEquals(Types.TIMESTAMP, meta.getColumnType(15));
        Assert.assertEquals("timestamp", meta.getColumnTypeName(15));
        Assert.assertEquals(29, meta.getColumnDisplaySize(15));
        Assert.assertEquals(29, meta.getPrecision(15));
        Assert.assertEquals(9, meta.getScale(15));
        Assert.assertEquals("c17", colRS.getString("COLUMN_NAME"));
        Assert.assertEquals(Types.TIMESTAMP, colRS.getInt("DATA_TYPE"));
        Assert.assertEquals("timestamp", colRS.getString("TYPE_NAME").toLowerCase());
        Assert.assertEquals(meta.getPrecision(15), colRS.getInt("COLUMN_SIZE"));
        Assert.assertEquals(meta.getScale(15), colRS.getInt("DECIMAL_DIGITS"));
        Assert.assertTrue(colRS.next());
        Assert.assertEquals("c18", meta.getColumnName(16));
        Assert.assertEquals(Types.DECIMAL, meta.getColumnType(16));
        Assert.assertEquals("decimal", meta.getColumnTypeName(16));
        Assert.assertEquals(18, meta.getColumnDisplaySize(16));
        Assert.assertEquals(16, meta.getPrecision(16));
        Assert.assertEquals(7, meta.getScale(16));
        Assert.assertEquals("c18", colRS.getString("COLUMN_NAME"));
        Assert.assertEquals(Types.DECIMAL, colRS.getInt("DATA_TYPE"));
        Assert.assertEquals("decimal", colRS.getString("TYPE_NAME").toLowerCase());
        Assert.assertEquals(meta.getPrecision(16), colRS.getInt("COLUMN_SIZE"));
        Assert.assertEquals(meta.getScale(16), colRS.getInt("DECIMAL_DIGITS"));
        Assert.assertTrue(colRS.next());// skip c19, since not selected by query

        Assert.assertTrue(colRS.next());
        Assert.assertEquals("c20", meta.getColumnName(17));
        Assert.assertEquals(Types.DATE, meta.getColumnType(17));
        Assert.assertEquals("date", meta.getColumnTypeName(17));
        Assert.assertEquals(10, meta.getColumnDisplaySize(17));
        Assert.assertEquals(10, meta.getPrecision(17));
        Assert.assertEquals(0, meta.getScale(17));
        Assert.assertEquals("c20", colRS.getString("COLUMN_NAME"));
        Assert.assertEquals(Types.DATE, colRS.getInt("DATA_TYPE"));
        Assert.assertEquals("date", colRS.getString("TYPE_NAME").toLowerCase());
        Assert.assertEquals(meta.getPrecision(17), colRS.getInt("COLUMN_SIZE"));
        Assert.assertEquals(meta.getScale(17), colRS.getInt("DECIMAL_DIGITS"));
        Assert.assertTrue(colRS.next());
        Assert.assertEquals("c21", meta.getColumnName(18));
        Assert.assertEquals(Types.VARCHAR, meta.getColumnType(18));
        Assert.assertEquals("varchar", meta.getColumnTypeName(18));
        // varchar columns should have correct display size/precision
        Assert.assertEquals(20, meta.getColumnDisplaySize(18));
        Assert.assertEquals(20, meta.getPrecision(18));
        Assert.assertEquals(0, meta.getScale(18));
        Assert.assertEquals("c21", colRS.getString("COLUMN_NAME"));
        Assert.assertEquals(Types.VARCHAR, colRS.getInt("DATA_TYPE"));
        Assert.assertEquals("varchar", colRS.getString("TYPE_NAME").toLowerCase());
        Assert.assertEquals(meta.getPrecision(18), colRS.getInt("COLUMN_SIZE"));
        Assert.assertEquals(meta.getScale(18), colRS.getInt("DECIMAL_DIGITS"));
        Assert.assertTrue(colRS.next());
        Assert.assertEquals("c22", meta.getColumnName(19));
        Assert.assertEquals(Types.CHAR, meta.getColumnType(19));
        Assert.assertEquals("char", meta.getColumnTypeName(19));
        // char columns should have correct display size/precision
        Assert.assertEquals(15, meta.getColumnDisplaySize(19));
        Assert.assertEquals(15, meta.getPrecision(19));
        Assert.assertEquals(0, meta.getScale(19));
        Assert.assertEquals("c22", colRS.getString("COLUMN_NAME"));
        Assert.assertEquals(Types.CHAR, colRS.getInt("DATA_TYPE"));
        Assert.assertEquals("char", colRS.getString("TYPE_NAME").toLowerCase());
        Assert.assertEquals(meta.getPrecision(19), colRS.getInt("COLUMN_SIZE"));
        Assert.assertEquals(meta.getScale(19), colRS.getInt("DECIMAL_DIGITS"));
        Assert.assertTrue(colRS.next());
        Assert.assertEquals("c23", meta.getColumnName(20));
        Assert.assertEquals(Types.BINARY, meta.getColumnType(20));
        Assert.assertEquals("binary", meta.getColumnTypeName(20));
        Assert.assertEquals(Integer.MAX_VALUE, meta.getColumnDisplaySize(20));
        Assert.assertEquals(Integer.MAX_VALUE, meta.getPrecision(20));
        Assert.assertEquals(0, meta.getScale(20));
        Assert.assertTrue(colRS.next());
        Assert.assertEquals("null_val", meta.getColumnName(21));
        Assert.assertEquals(Types.NULL, meta.getColumnType(21));
        Assert.assertEquals("void", meta.getColumnTypeName(21));
        Assert.assertEquals(4, meta.getColumnDisplaySize(21));
        Assert.assertEquals(0, meta.getPrecision(21));
        Assert.assertEquals(0, meta.getScale(21));
        for (int i = 1; i <= (meta.getColumnCount()); i++) {
            Assert.assertFalse(meta.isAutoIncrement(i));
            Assert.assertFalse(meta.isCurrency(i));
            Assert.assertEquals(ResultSetMetaData.columnNullable, meta.isNullable(i));
        }
        stmt.close();
    }

    @Test
    public void testResultSetMetaDataDuplicateColumnNames() throws SQLException {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        ResultSet res = stmt.executeQuery((("select c1 as c2_1, c2, c1*2 from " + (TestJdbcDriver2.dataTypeTableName)) + " limit 1"));
        ResultSetMetaData meta = res.getMetaData();
        ResultSet colRS = TestJdbcDriver2.con.getMetaData().getColumns(null, null, TestJdbcDriver2.dataTypeTableName.toLowerCase(), null);
        Assert.assertEquals(3, meta.getColumnCount());
        Assert.assertTrue(colRS.next());
        Assert.assertEquals("c2_1", meta.getColumnName(1));
        Assert.assertTrue(colRS.next());
        Assert.assertEquals("c2", meta.getColumnName(2));
        Assert.assertTrue(colRS.next());
        Assert.assertEquals("_c2", meta.getColumnName(3));
        stmt.close();
    }

    @Test
    public void testResultSetRowProperties() throws SQLException {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        ResultSet res = stmt.executeQuery((("select * from " + (TestJdbcDriver2.dataTypeTableName)) + " limit 1"));
        Assert.assertFalse(res.rowDeleted());
        Assert.assertFalse(res.rowInserted());
        Assert.assertFalse(res.rowUpdated());
    }

    // [url] [host] [port] [db]
    private static final String[][] URL_PROPERTIES = new String[][]{ // binary mode
    // For embedded mode, the JDBC uri is of the form:
    // jdbc:hive2:///dbName;sess_var_list?hive_conf_list#hive_var_list
    // and does not contain host:port string.
    // As a result port is parsed to '-1' per the Java URI conventions
    new String[]{ "jdbc:hive2://", "", "", "default" }, new String[]{ "jdbc:hive2://localhost:10001/default", "localhost", "10001", "default" }, new String[]{ "jdbc:hive2://localhost/notdefault", "localhost", "10000", "notdefault" }, new String[]{ "jdbc:hive2://foo:1243", "foo", "1243", "default" }, // http mode
    new String[]{ "jdbc:hive2://server:10002/db;user=foo;password=bar?" + ("hive.server2.transport.mode=http;" + "hive.server2.thrift.http.path=hs2"), "server", "10002", "db" } };

    @Test
    public void testDriverProperties() throws SQLException {
        HiveDriver driver = new HiveDriver();
        for (String[] testValues : TestJdbcDriver2.URL_PROPERTIES) {
            DriverPropertyInfo[] dpi = driver.getPropertyInfo(testValues[0], null);
            Assert.assertEquals("unexpected DriverPropertyInfo array size", 3, dpi.length);
            TestJdbcDriver2.assertDpi(dpi[0], "HOST", testValues[1]);
            TestJdbcDriver2.assertDpi(dpi[1], "PORT", testValues[2]);
            TestJdbcDriver2.assertDpi(dpi[2], "DBNAME", testValues[3]);
        }
    }

    private static final String[][] HTTP_URL_PROPERTIES = new String[][]{ new String[]{ "jdbc:hive2://server:10002/db;user=foo;password=bar;transportMode=http;httpPath=hs2", "server", "10002", "db", "http", "hs2" }, new String[]{ "jdbc:hive2://server:10000/testdb;user=foo;password=bar;transportMode=binary;httpPath=", "server", "10000", "testdb", "binary", "" } };

    @Test
    public void testParseUrlHttpMode() throws SQLException, JdbcUriParseException, ZooKeeperHiveClientException {
        new HiveDriver();
        for (String[] testValues : TestJdbcDriver2.HTTP_URL_PROPERTIES) {
            JdbcConnectionParams params = Utils.parseURL(testValues[0], new Properties());
            Assert.assertEquals(params.getHost(), testValues[1]);
            Assert.assertEquals(params.getPort(), Integer.parseInt(testValues[2]));
            Assert.assertEquals(params.getDbName(), testValues[3]);
            Assert.assertEquals(params.getSessionVars().get("transportMode"), testValues[4]);
            Assert.assertEquals(params.getSessionVars().get("httpPath"), testValues[5]);
        }
    }

    /**
     * validate schema generated by "set" command
     *
     * @throws SQLException
     * 		
     */
    @Test
    public void testSetCommand() throws SQLException {
        // execute set command
        String sql = "set -v";
        Statement stmt = TestJdbcDriver2.con.createStatement();
        ResultSet res = stmt.executeQuery(sql);
        // Validate resultset columns
        ResultSetMetaData md = res.getMetaData();
        Assert.assertEquals(1, md.getColumnCount());
        Assert.assertEquals(SET_COLUMN_NAME, md.getColumnLabel(1));
        // check if there is data in the resultset
        int numLines = 0;
        while (res.next()) {
            numLines++;
            String rline = res.getString(1);
            Assert.assertFalse(("set output must not contain hidden variables such as the metastore password:" + rline), ((rline.contains(varname)) && (!(rline.contains(HiveConf.ConfVars.HIVE_CONF_HIDDEN_LIST.varname)))));
            // the only conf allowed to have the metastore pwd keyname is the hidden list configuration
            // value
        } 
        Assert.assertTrue("Nothing returned by set -v", (numLines > 0));
        res.close();
        stmt.close();
    }

    /**
     * Validate error on closed resultset
     *
     * @throws SQLException
     * 		
     */
    @Test
    public void testPostClose() throws SQLException {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        ResultSet res = stmt.executeQuery(("select * from " + (TestJdbcDriver2.tableName)));
        Assert.assertNotNull("ResultSet is null", res);
        res.close();
        try {
            res.getInt(1);
            Assert.fail("Expected SQLException");
        } catch (SQLException e) {
        }
        try {
            res.getMetaData();
            Assert.fail("Expected SQLException");
        } catch (SQLException e) {
        }
        try {
            res.setFetchSize(10);
            Assert.fail("Expected SQLException");
        } catch (SQLException e) {
        }
        stmt.close();
    }

    /* The JDBC spec says when you have duplicate column names,
    the first one should be returned.
     */
    @Test
    public void testDuplicateColumnNameOrder() throws SQLException {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        ResultSet rs = stmt.executeQuery(("SELECT 1 AS a, 2 AS a from " + (TestJdbcDriver2.tableName)));
        Assert.assertTrue(rs.next());
        Assert.assertEquals(1, rs.getInt("a"));
        rs.close();
        stmt.close();
    }

    /**
     * Test bad args to getXXX()
     *
     * @throws SQLException
     * 		
     */
    @Test
    public void testOutOfBoundCols() throws SQLException {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        ResultSet res = stmt.executeQuery(("select * from " + (TestJdbcDriver2.tableName)));
        // row 1
        Assert.assertTrue(res.next());
        try {
            res.getInt(200);
        } catch (SQLException e) {
        }
        try {
            res.getInt("zzzz");
        } catch (SQLException e) {
        }
        stmt.close();
    }

    /**
     * Verify selecting using builtin UDFs
     *
     * @throws SQLException
     * 		
     */
    @Test
    public void testBuiltInUDFCol() throws SQLException {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        ResultSet res = stmt.executeQuery((("select c12, bin(c12) from " + (TestJdbcDriver2.dataTypeTableName)) + " where c1=1"));
        ResultSetMetaData md = res.getMetaData();
        Assert.assertEquals(md.getColumnCount(), 2);// only one result column

        Assert.assertEquals(md.getColumnLabel(2), "_c1");// verify the system generated column name

        Assert.assertTrue(res.next());
        Assert.assertEquals(res.getLong(1), 1);
        Assert.assertEquals(res.getString(2), "1");
        res.close();
        stmt.close();
    }

    /**
     * Verify selecting named expression columns
     *
     * @throws SQLException
     * 		
     */
    @Test
    public void testExprCol() throws SQLException {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        ResultSet res = stmt.executeQuery((("select c1+1 as col1, length(c4) as len from " + (TestJdbcDriver2.dataTypeTableName)) + " where c1=1"));
        ResultSetMetaData md = res.getMetaData();
        Assert.assertEquals(md.getColumnCount(), 2);// only one result column

        Assert.assertEquals(md.getColumnLabel(1), "col1");// verify the column name

        Assert.assertEquals(md.getColumnLabel(2), "len");// verify the column name

        Assert.assertTrue(res.next());
        Assert.assertEquals(res.getInt(1), 2);
        Assert.assertEquals(res.getInt(2), 1);
        res.close();
        stmt.close();
    }

    /**
     * test getProcedureColumns()
     *
     * @throws SQLException
     * 		
     */
    @Test
    public void testProcCols() throws SQLException {
        DatabaseMetaData dbmd = TestJdbcDriver2.con.getMetaData();
        Assert.assertNotNull(dbmd);
        // currently getProcedureColumns always returns an empty resultset for Hive
        ResultSet res = dbmd.getProcedureColumns(null, null, null, null);
        ResultSetMetaData md = res.getMetaData();
        Assert.assertEquals(md.getColumnCount(), 20);
        Assert.assertFalse(res.next());
    }

    /**
     * test testProccedures()
     *
     * @throws SQLException
     * 		
     */
    @Test
    public void testProccedures() throws SQLException {
        DatabaseMetaData dbmd = TestJdbcDriver2.con.getMetaData();
        Assert.assertNotNull(dbmd);
        // currently testProccedures always returns an empty resultset for Hive
        ResultSet res = dbmd.getProcedures(null, null, null);
        ResultSetMetaData md = res.getMetaData();
        Assert.assertEquals(md.getColumnCount(), 9);
        Assert.assertFalse(res.next());
    }

    /**
     * test getPrimaryKeys()
     *
     * @throws SQLException
     * 		
     */
    @Test
    public void testPrimaryKeys() throws SQLException {
        DatabaseMetaData dbmd = TestJdbcDriver2.con.getMetaData();
        Assert.assertNotNull(dbmd);
        ResultSet res = dbmd.getPrimaryKeys(null, TestJdbcDriver2.testDbName, TestJdbcDriver2.tableName);
        ResultSetMetaData md = res.getMetaData();
        Assert.assertEquals(md.getColumnCount(), 6);
        Assert.assertFalse(res.next());
    }

    /**
     * test testPrimaryKeysNotNull()
     *
     * @throws SQLException
     * 		
     */
    @Test
    public void testPrimaryKeysNotNull() throws SQLException {
        DatabaseMetaData dbmd = TestJdbcDriver2.con.getMetaData();
        Assert.assertNotNull(dbmd);
        ResultSet rs = dbmd.getColumns(null, TestJdbcDriver2.testDbName, TestJdbcDriver2.tableNameWithPk, "%");
        int index = 0;
        while (rs.next()) {
            int nullableInt = rs.getInt("NULLABLE");
            String isNullable = rs.getString("IS_NULLABLE");
            if (index == 0) {
                Assert.assertEquals(nullableInt, 0);
                Assert.assertEquals(isNullable, "NO");
            } else
                if (index == 1) {
                    Assert.assertEquals(nullableInt, 1);
                    Assert.assertEquals(isNullable, "YES");
                } else {
                    throw new SQLException("Unexpected column.");
                }

            index++;
        } 
        rs.close();
    }

    /**
     * test getImportedKeys()
     *
     * @throws SQLException
     * 		
     */
    @Test
    public void testImportedKeys() throws SQLException {
        DatabaseMetaData dbmd = TestJdbcDriver2.con.getMetaData();
        Assert.assertNotNull(dbmd);
        // currently getImportedKeys always returns an empty resultset for Hive
        ResultSet res = dbmd.getImportedKeys(null, null, null);
        ResultSetMetaData md = res.getMetaData();
        Assert.assertEquals(md.getColumnCount(), 14);
        Assert.assertFalse(res.next());
    }

    /**
     * If the Driver implementation understands the URL, it will return a Connection object;
     * otherwise it returns null
     */
    @Test
    public void testInvalidURL() throws Exception {
        HiveDriver driver = new HiveDriver();
        Connection conn = driver.connect("jdbc:derby://localhost:10000/default", new Properties());
        Assert.assertNull(conn);
    }

    /**
     * Test the cursor repositioning to start of resultset
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFetchFirstQuery() throws Exception {
        execFetchFirst((("select c4, c1 from " + (TestJdbcDriver2.dataTypeTableName)) + " order by c1"), "c4", false);
        execFetchFirst((("select c4, c1 from " + (TestJdbcDriver2.dataTypeTableName)) + " order by c1"), "c4", true);
    }

    /**
     * Test the cursor repositioning to start of resultset from non-mr query
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFetchFirstNonMR() throws Exception {
        execFetchFirst(("select * from " + (TestJdbcDriver2.dataTypeTableName)), (((TestJdbcDriver2.dataTypeTableName.toLowerCase()) + ".") + "c4"), false);
    }

    /**
     * Test for cursor repositioning to start of resultset for non-sql commands
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFetchFirstSetCmds() throws Exception {
        execFetchFirst("set -v", SET_COLUMN_NAME, false);
    }

    /**
     * Test for cursor repositioning to start of resultset for non-sql commands
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFetchFirstDfsCmds() throws Exception {
        String wareHouseDir = TestJdbcDriver2.conf.get(HiveConf.ConfVars.METASTOREWAREHOUSE.varname);
        execFetchFirst(("dfs -ls " + wareHouseDir), DFS_RESULT_HEADER, false);
    }

    /**
     * Negative Test for cursor repositioning to start of resultset
     * Verify unsupported JDBC resultset attributes
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testUnsupportedFetchTypes() throws Exception {
        try {
            TestJdbcDriver2.con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Assert.fail("createStatement with TYPE_SCROLL_SENSITIVE should fail");
        } catch (SQLException e) {
            Assert.assertEquals("HYC00", e.getSQLState().trim());
        }
        try {
            TestJdbcDriver2.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            Assert.fail("createStatement with CONCUR_UPDATABLE should fail");
        } catch (SQLException e) {
            Assert.assertEquals("HYC00", e.getSQLState().trim());
        }
    }

    /**
     * Negative Test for cursor repositioning to start of resultset
     * Verify unsupported JDBC resultset methods
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFetchFirstError() throws Exception {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        ResultSet res = stmt.executeQuery(("select * from " + (TestJdbcDriver2.tableName)));
        try {
            res.beforeFirst();
            Assert.fail("beforeFirst() should fail for normal resultset");
        } catch (SQLException e) {
            Assert.assertEquals("Method not supported for TYPE_FORWARD_ONLY resultset", e.getMessage());
        }
        stmt.close();
    }

    @Test
    public void testShowGrant() throws SQLException {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        stmt.execute((("grant select on table " + (TestJdbcDriver2.dataTypeTableName)) + " to user hive_test_user"));
        stmt.execute(("show grant user hive_test_user on table " + (TestJdbcDriver2.dataTypeTableName)));
        ResultSet res = stmt.getResultSet();
        Assert.assertTrue(res.next());
        Assert.assertEquals(TestJdbcDriver2.testDbName, res.getString(1));
        Assert.assertEquals(TestJdbcDriver2.dataTypeTableName, res.getString(2));
        Assert.assertEquals("", res.getString(3));// partition

        Assert.assertEquals("", res.getString(4));// column

        Assert.assertEquals("hive_test_user", res.getString(5));
        Assert.assertEquals("USER", res.getString(6));
        Assert.assertEquals("SELECT", res.getString(7));
        Assert.assertEquals(false, res.getBoolean(8));// grant option

        Assert.assertEquals((-1), res.getLong(9));
        Assert.assertNotNull(res.getString(10));// grantor

        Assert.assertFalse(res.next());
        res.close();
        stmt.close();
    }

    @Test
    public void testShowRoleGrant() throws SQLException {
        Statement stmt = TestJdbcDriver2.con.createStatement();
        // drop role. ignore error.
        try {
            stmt.execute("drop role role1");
        } catch (Exception ex) {
            TestJdbcDriver2.LOG.warn(("Ignoring error during drop role: " + ex));
        }
        stmt.execute("create role role1");
        stmt.execute("grant role role1 to user hive_test_user");
        stmt.execute("show role grant user hive_test_user");
        ResultSet res = stmt.getResultSet();
        Assert.assertTrue(res.next());
        Assert.assertEquals("public", res.getString(1));
        Assert.assertTrue(res.next());
        Assert.assertEquals("role1", res.getString(1));
        res.close();
        stmt.close();
    }

    /**
     * Useful for modifying outer class context from anonymous inner class
     */
    public static interface Holder<T> {
        public void set(T obj);

        public T get();
    }

    /**
     * Tests for query cancellation
     */
    @Test
    public void testCancelQueryNotRun() throws Exception {
        try (final Statement stmt = TestJdbcDriver2.con.createStatement()) {
            System.out.println("Cancel the Statement without running query ...");
            stmt.cancel();
            System.out.println("Executing query: ");
            stmt.executeQuery(" show databases");
        }
    }

    @Test
    public void testCancelQueryFinished() throws Exception {
        try (final Statement stmt = TestJdbcDriver2.con.createStatement()) {
            System.out.println("Executing query: ");
            stmt.executeQuery(" show databases");
            System.out.println("Cancel the Statement after running query ...");
            stmt.cancel();
        }
    }

    @Test
    public void testCancelQueryErrored() throws Exception {
        final Statement stmt = TestJdbcDriver2.con.createStatement();
        try {
            System.out.println("Executing query: ");
            stmt.executeQuery("list dbs");
            Assert.fail("Expecting SQLException");
        } catch (SQLException e) {
            // No-op
        }
        // Cancel the query
        System.out.println("Cancel the Statement ...");
        stmt.cancel();
        stmt.close();
    }

    /**
     * Test the cancellation of a query that is running.
     * We spawn 2 threads - one running the query and
     * the other attempting to cancel.
     * We're using a dummy udf to simulate a query,
     * that runs for a sufficiently long time.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testQueryCancel() throws Exception {
        String udfName = TestJdbcDriver2.SleepMsUDF.class.getName();
        Statement stmt1 = TestJdbcDriver2.con.createStatement();
        stmt1.execute((("create temporary function sleepMsUDF as '" + udfName) + "'"));
        stmt1.close();
        final Statement stmt = TestJdbcDriver2.con.createStatement();
        // Thread executing the query
        Thread tExecute = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Executing query: ");
                    // The test table has 500 rows, so total query time should be ~ 500*500ms
                    stmt.executeQuery(((((("select sleepMsUDF(t1.under_col, 1) as u0, t1.under_col as u1, " + "t2.under_col as u2 from ") + (TestJdbcDriver2.tableName)) + " t1 join ") + (TestJdbcDriver2.tableName)) + " t2 on t1.under_col = t2.under_col"));
                    Assert.fail("Expecting SQLException");
                } catch (SQLException e) {
                    // This thread should throw an exception
                    Assert.assertNotNull(e);
                    System.out.println(e.toString());
                }
            }
        });
        // Thread cancelling the query
        Thread tCancel = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Sleep for 100ms
                    Thread.sleep(100);
                    System.out.println("Cancelling query: ");
                    stmt.cancel();
                } catch (Exception e) {
                    // No-op
                }
            }
        });
        tExecute.start();
        tCancel.start();
        tExecute.join();
        tCancel.join();
        stmt.close();
    }

    @Test
    public void testQueryCancelTwice() throws Exception {
        String udfName = TestJdbcDriver2.SleepMsUDF.class.getName();
        Statement stmt1 = TestJdbcDriver2.con.createStatement();
        stmt1.execute((("create temporary function sleepMsUDF as '" + udfName) + "'"));
        stmt1.close();
        final Statement stmt = TestJdbcDriver2.con.createStatement();
        // Thread executing the query
        Thread tExecute = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Executing query: ");
                    // The test table has 500 rows, so total query time should be ~ 500*500ms
                    stmt.executeQuery(((((("select sleepMsUDF(t1.under_col, 1) as u0, t1.under_col as u1, " + "t2.under_col as u2 from ") + (TestJdbcDriver2.tableName)) + " t1 join ") + (TestJdbcDriver2.tableName)) + " t2 on t1.under_col = t2.under_col"));
                    Assert.fail("Expecting SQLException");
                } catch (SQLException e) {
                    // This thread should throw an exception
                    Assert.assertNotNull(e);
                    System.out.println(e.toString());
                }
            }
        });
        // Thread cancelling the query
        Thread tCancel = new Thread(new Runnable() {
            @Override
            public void run() {
                // 1st Cancel
                try {
                    // Sleep for 100ms
                    Thread.sleep(100);
                    System.out.println("Cancelling query: ");
                    stmt.cancel();
                } catch (Exception e) {
                    // No-op
                }
                // 2nd cancel
                try {
                    // Sleep for 5ms and cancel again
                    Thread.sleep(5);
                    System.out.println("Cancelling query again: ");
                    stmt.cancel();
                } catch (Exception e) {
                    // No-op
                }
            }
        });
        tExecute.start();
        tCancel.start();
        tExecute.join();
        tCancel.join();
        stmt.close();
    }

    @Test
    public void testQueryTimeout() throws Exception {
        String udfName = TestJdbcDriver2.SleepMsUDF.class.getName();
        Statement stmt1 = TestJdbcDriver2.con.createStatement();
        stmt1.execute((("create temporary function sleepMsUDF as '" + udfName) + "'"));
        stmt1.close();
        Statement stmt = TestJdbcDriver2.con.createStatement();
        // Test a query where timeout kicks in
        // Set query timeout to 1 second
        stmt.setQueryTimeout(1);
        System.err.println("Executing query: ");
        try {
            // The test table has 500 rows, so total query time should be ~ 2500ms
            stmt.executeQuery(((((("select sleepMsUDF(t1.under_col, 5) as u0, t1.under_col as u1, " + "t2.under_col as u2 from ") + (TestJdbcDriver2.tableName)) + " t1 join ") + (TestJdbcDriver2.tableName)) + " t2 on t1.under_col = t2.under_col"));
            Assert.fail("Expecting SQLTimeoutException");
        } catch (SQLTimeoutException e) {
            Assert.assertNotNull(e);
            System.err.println(e.toString());
        } catch (SQLException e) {
            Assert.fail(("Expecting SQLTimeoutException, but got SQLException: " + e));
            e.printStackTrace();
        }
        // Test a query where timeout does not kick in. Set it to 5s;
        // show tables should be faster than that
        stmt.setQueryTimeout(5);
        try {
            stmt.executeQuery("show tables");
        } catch (SQLException e) {
            Assert.fail(("Unexpected SQLException: " + e));
            e.printStackTrace();
        }
        stmt.close();
    }

    /**
     * Test the non-null value of the Yarn ATS GUID.
     * We spawn 2 threads - one running the query and
     * the other attempting to read the ATS GUID.
     * We're using a dummy udf to simulate a query,
     * that runs for a sufficiently long time.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testYarnATSGuid() throws Exception {
        String udfName = TestJdbcDriver2.SleepMsUDF.class.getName();
        Statement stmt1 = TestJdbcDriver2.con.createStatement();
        stmt1.execute((("create temporary function sleepMsUDF as '" + udfName) + "'"));
        stmt1.close();
        final Statement stmt = TestJdbcDriver2.con.createStatement();
        final TestJdbcDriver2.Holder<Boolean> yarnATSGuidSet = new TestJdbcDriver2.Holder<Boolean>() {
            public Boolean b = false;

            public void set(Boolean b) {
                this.b = b;
            }

            public Boolean get() {
                return this.b;
            }
        };
        // Thread executing the query
        Thread tExecute = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // The test table has 500 rows, so total query time should be ~ 500*1ms
                    System.out.println("Executing query: ");
                    stmt.executeQuery(((((("select sleepMsUDF(t1.under_col, 1) as u0, t1.under_col as u1, " + "t2.under_col as u2 from ") + (TestJdbcDriver2.tableName)) + " t1 join ") + (TestJdbcDriver2.tableName)) + " t2 on t1.under_col = t2.under_col"));
                } catch (SQLException e) {
                    // No op
                }
            }
        });
        // Thread reading the ATS GUID
        Thread tGuid = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String atsGuid = getYarnATSGuid();
                    if (atsGuid != null) {
                        yarnATSGuidSet.set(true);
                        System.out.println(("Yarn ATS GUID: " + atsGuid));
                        return;
                    } else {
                        yarnATSGuidSet.set(false);
                        System.out.println("No Yarn ATS GUID yet");
                    }
                } 
            }
        });
        tExecute.start();
        tGuid.start();
        tExecute.join();
        tGuid.interrupt();
        if (!(yarnATSGuidSet.get())) {
            Assert.fail("Failed to set the YARN ATS Guid");
        }
        stmt.close();
    }

    // A udf which sleeps for some number of ms to simulate a long running query
    public static class SleepMsUDF extends UDF {
        public Integer evaluate(final Integer value, final Integer ms) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                // No-op
            }
            return value;
        }
    }

    /**
     * Loads data from a table containing non-ascii value column
     * Runs a query and compares the return value
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNonAsciiReturnValues() throws Exception {
        String nonAsciiTableName = "nonAsciiTable";
        String nonAsciiString = "Gar?u K?kaku kid?tai";
        Path nonAsciiFilePath = new Path(TestJdbcDriver2.dataFileDir, "non_ascii_tbl.txt");
        Statement stmt = TestJdbcDriver2.con.createStatement();
        stmt.execute("set hive.support.concurrency = false");
        // Create table
        stmt.execute(((("create table " + nonAsciiTableName) + " (key int, value string) ") + "row format delimited fields terminated by '|'"));
        // Load data
        stmt.execute(((("load data local inpath '" + (nonAsciiFilePath.toString())) + "' into table ") + nonAsciiTableName));
        ResultSet rs = stmt.executeQuery((("select value from " + nonAsciiTableName) + " limit 1"));
        while (rs.next()) {
            String resultValue = rs.getString(1);
            Assert.assertTrue(resultValue.equalsIgnoreCase(nonAsciiString));
        } 
        // Drop table, ignore error.
        try {
            stmt.execute(("drop table " + nonAsciiTableName));
        } catch (Exception ex) {
            // no-op
        }
        stmt.close();
    }

    /**
     * Test getting query log method in Jdbc
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetQueryLog() throws Exception {
        // Prepare
        String[] expectedLogs = new String[]{ "Compiling command", "Completed compiling command", "Starting Semantic Analysis", "Semantic Analysis Completed", "Executing command", "Completed executing command" };
        String sql = "select count(*) from " + (TestJdbcDriver2.tableName);
        // Verify the fetched log (from the beginning of log file)
        HiveStatement stmt = ((HiveStatement) (TestJdbcDriver2.con.createStatement()));
        Assert.assertNotNull("Statement is null", stmt);
        stmt.executeQuery(sql);
        List<String> logs = stmt.getQueryLog(false, 10000);
        stmt.close();
        verifyFetchedLog(logs, expectedLogs);
        // Verify the fetched log (incrementally)
        final HiveStatement statement = ((HiveStatement) (TestJdbcDriver2.con.createStatement()));
        Assert.assertNotNull("Statement is null", statement);
        statement.setFetchSize(10000);
        final List<String> incrementalLogs = new ArrayList<String>();
        Runnable logThread = new Runnable() {
            @Override
            public void run() {
                while (statement.hasMoreLogs()) {
                    try {
                        incrementalLogs.addAll(statement.getQueryLog());
                        Thread.sleep(500);
                    } catch (SQLException e) {
                        TestJdbcDriver2.LOG.error(("Failed getQueryLog. Error message: " + (e.getMessage())));
                        Assert.fail("error in getting log thread");
                    } catch (InterruptedException e) {
                        TestJdbcDriver2.LOG.error(("Getting log thread is interrupted. Error message: " + (e.getMessage())));
                        Assert.fail("error in getting log thread");
                    }
                } 
            }
        };
        Thread thread = new Thread(logThread);
        thread.setDaemon(true);
        thread.start();
        statement.executeQuery(sql);
        thread.interrupt();
        thread.join(10000);
        // fetch remaining logs
        List<String> remainingLogs;
        do {
            remainingLogs = statement.getQueryLog();
            incrementalLogs.addAll(remainingLogs);
        } while ((remainingLogs.size()) > 0 );
        statement.close();
        verifyFetchedLog(incrementalLogs, expectedLogs);
    }

    /**
     * Test getting query log when HS2 disable logging.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetQueryLogOnDisabledLog() throws Exception {
        Statement setStmt = TestJdbcDriver2.con.createStatement();
        setStmt.execute("set hive.server2.logging.operation.enabled = false");
        String sql = "select count(*) from " + (TestJdbcDriver2.tableName);
        HiveStatement stmt = ((HiveStatement) (TestJdbcDriver2.con.createStatement()));
        Assert.assertNotNull("Statement is null", stmt);
        stmt.executeQuery(sql);
        List<String> logs = stmt.getQueryLog(false, 10);
        stmt.close();
        Assert.assertTrue(((logs.size()) == 0));
        setStmt.execute("set hive.server2.logging.operation.enabled = true");
        setStmt.close();
    }

    @Test
    public void testPrepareSetDate() throws Exception {
        try {
            String sql = ("select * from " + (TestJdbcDriver2.dataTypeTableName)) + " where c20 = ?";
            PreparedStatement ps = TestJdbcDriver2.con.prepareStatement(sql);
            java.sql.Date dtValue = valueOf("2013-01-01");
            ps.setDate(1, dtValue);
            ResultSet res = ps.executeQuery();
            Assert.assertTrue(res.next());
            Assert.assertEquals("2013-01-01", res.getString(20));
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }
    }

    @Test
    public void testPrepareSetTimestamp() throws SQLException, ParseException {
        String sql = String.format("SELECT * FROM %s WHERE c17 = ?", TestJdbcDriver2.dataTypeTableName);
        try (PreparedStatement ps = TestJdbcDriver2.con.prepareStatement(sql)) {
            Timestamp timestamp = Timestamp.valueOf("2012-04-22 09:00:00.123456789");
            ps.setTimestamp(1, timestamp);
            // Ensure we find the single row which matches our timestamp (where field 1 has value 1)
            try (ResultSet resultSet = ps.executeQuery()) {
                Assert.assertTrue(resultSet.next());
                Assert.assertEquals(1, resultSet.getInt(1));
                Assert.assertFalse(resultSet.next());
            }
        }
    }

    @Test
    public void testAutoCommit() throws Exception {
        TestJdbcDriver2.con.clearWarnings();
        TestJdbcDriver2.con.setAutoCommit(true);
        Assert.assertNull(TestJdbcDriver2.con.getWarnings());
        TestJdbcDriver2.con.setAutoCommit(false);
        SQLWarning warning = TestJdbcDriver2.con.getWarnings();
        Assert.assertNotNull(warning);
        Assert.assertEquals("Hive does not support autoCommit=false", warning.getMessage());
        Assert.assertNull(warning.getNextWarning());
        TestJdbcDriver2.con.clearWarnings();
    }

    @Test
    public void setAutoCommitOnClosedConnection() throws Exception {
        Connection mycon = TestJdbcDriver2.getConnection("");
        try {
            mycon.setAutoCommit(true);
            mycon.close();
            thrown.expect(SQLException.class);
            thrown.expectMessage("Connection is closed");
            mycon.setAutoCommit(true);
        } finally {
            mycon.close();
        }
    }

    /**
     * Test {@link HiveStatement#executeAsync(String)} for a select query
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSelectExecAsync() throws Exception {
        HiveStatement stmt = ((HiveStatement) (TestJdbcDriver2.con.createStatement()));
        testSelect(stmt);
        stmt.close();
    }

    @Test
    public void testSelectExecAsync2() throws Exception {
        HiveStatement stmt = ((HiveStatement) (TestJdbcDriver2.con.createStatement()));
        stmt.execute("SET hive.driver.parallel.compilation=true");
        stmt.execute("SET hive.server2.async.exec.async.compile=true");
        testSelect(stmt);
        stmt.close();
    }

    @Test
    public void testSelectExecAsync3() throws Exception {
        HiveStatement stmt = ((HiveStatement) (TestJdbcDriver2.con.createStatement()));
        stmt.execute("SET hive.driver.parallel.compilation=true");
        stmt.execute("SET hive.server2.async.exec.async.compile=false");
        testSelect(stmt);
        stmt.close();
    }

    /**
     * Test {@link HiveStatement#executeAsync(String)} for a create table
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCreateTableExecAsync() throws Exception {
        HiveStatement stmt = ((HiveStatement) (TestJdbcDriver2.con.createStatement()));
        String tblName = "testCreateTableExecAsync";
        boolean isResulSet = stmt.executeAsync((("create table " + tblName) + " (col1 int , col2 string)"));
        Assert.assertFalse(isResulSet);
        // HiveStatement#getUpdateCount blocks until the async query is complete
        stmt.getUpdateCount();
        DatabaseMetaData metadata = TestJdbcDriver2.con.getMetaData();
        ResultSet tablesMetadata = metadata.getTables(null, null, "%", null);
        boolean tblFound = false;
        while (tablesMetadata.next()) {
            String tableName = tablesMetadata.getString(3);
            if (tableName.equalsIgnoreCase(tblName)) {
                tblFound = true;
            }
        } 
        if (!tblFound) {
            Assert.fail("Unable to create table using executeAsync");
        }
        stmt.execute(("drop table " + tblName));
        stmt.close();
    }

    @Test
    public void testReplErrorScenarios() throws Exception {
        HiveStatement stmt = ((HiveStatement) (TestJdbcDriver2.con.createStatement()));
        try {
            // source of replication not set
            stmt.execute("repl dump default");
        } catch (SQLException e) {
            Assert.assertTrue(((e.getErrorCode()) == (REPL_DATABASE_IS_NOT_SOURCE_OF_REPLICATION.getErrorCode())));
        }
        try {
            // invalid load path
            stmt.execute("repl load default1 from '/tmp/junk'");
        } catch (SQLException e) {
            Assert.assertTrue(((e.getErrorCode()) == (REPL_LOAD_PATH_NOT_FOUND.getErrorCode())));
        }
        stmt.close();
    }

    /**
     * Test {@link HiveStatement#executeAsync(String)} for an insert overwrite into a table
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testInsertOverwriteExecAsync() throws Exception {
        HiveStatement stmt = ((HiveStatement) (TestJdbcDriver2.con.createStatement()));
        testInsertOverwrite(stmt);
        stmt.close();
    }

    @Test
    public void testInsertOverwriteExecAsync2() throws Exception {
        HiveStatement stmt = ((HiveStatement) (TestJdbcDriver2.con.createStatement()));
        stmt.execute("SET hive.driver.parallel.compilation=true");
        stmt.execute("SET hive.server2.async.exec.async.compile=true");
        testInsertOverwrite(stmt);
        stmt.close();
    }

    @Test
    public void testInsertOverwriteExecAsync3() throws Exception {
        HiveStatement stmt = ((HiveStatement) (TestJdbcDriver2.con.createStatement()));
        stmt.execute("SET hive.driver.parallel.compilation=true");
        stmt.execute("SET hive.server2.async.exec.async.compile=false");
        testInsertOverwrite(stmt);
        stmt.close();
    }

    @Test
    public void testGetQueryId() throws Exception {
        HiveStatement stmt = ((HiveStatement) (TestJdbcDriver2.con.createStatement()));
        HiveStatement stmt1 = ((HiveStatement) (TestJdbcDriver2.con.createStatement()));
        stmt.executeAsync("create database query_id_test with dbproperties ('repl.source.for' = '1, 2, 3')");
        String queryId = stmt.getQueryId();
        Assert.assertFalse(queryId.isEmpty());
        stmt.getUpdateCount();
        stmt1.executeAsync("repl status query_id_test with ('hive.query.id' = 'hiveCustomTag')");
        String queryId1 = stmt1.getQueryId();
        Assert.assertFalse("hiveCustomTag".equals(queryId1));
        Assert.assertFalse(queryId.equals(queryId1));
        Assert.assertFalse(queryId1.isEmpty());
        stmt1.getUpdateCount();
        stmt.executeAsync(("select count(*) from " + (TestJdbcDriver2.dataTypeTableName)));
        queryId = stmt.getQueryId();
        Assert.assertFalse("hiveCustomTag".equals(queryId));
        Assert.assertFalse(queryId.isEmpty());
        Assert.assertFalse(queryId.equals(queryId1));
        stmt.getUpdateCount();
        stmt.execute("drop database query_id_test");
        stmt.close();
        stmt1.close();
    }

    // Test that opening a JDBC connection to a non-existent database throws a HiveSQLException
    @Test(expected = HiveSQLException.class)
    public void testConnectInvalidDatabase() throws SQLException {
        DriverManager.getConnection("jdbc:hive2:///databasedoesnotexist", "", "");
    }
}

