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


import ConfVars.HIVE_SERVER2_BUILTIN_UDF_BLACKLIST;
import ConfVars.HIVE_SERVER2_BUILTIN_UDF_WHITELIST;
import HiveConf.ConfVars.DOWNLOADED_RESOURCES_DIR;
import HiveConf.ConfVars.HIVE_SERVER2_THRIFT_HTTP_REQUEST_HEADER_SIZE;
import HiveConf.ConfVars.HIVE_SERVER2_THRIFT_HTTP_RESPONSE_HEADER_SIZE;
import HiveConf.ConfVars.HIVE_SERVER2_THRIFT_RESULTSET_MAX_FETCH_SIZE;
import HiveConf.ConfVars.LOCALSCRATCHDIR;
import HiveConf.ConfVars.SCRATCHDIR;
import HiveConf.ConfVars.SCRATCHDIRPERMISSION;
import java.net.URI;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.exec.FunctionRegistry;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.apache.hive.service.cli.HiveSQLException;
import org.junit.Assert;
import org.junit.Test;


public class TestJdbcWithMiniHS2 {
    private static MiniHS2 miniHS2 = null;

    private static String dataFileDir;

    private static Path kvDataFilePath;

    private static final String tmpDir = System.getProperty("test.tmp.dir");

    private static final String testDbName = "testjdbcminihs2";

    private static final String defaultDbName = "default";

    private static final String tableName = "testjdbcminihs2tbl";

    private static final String tableComment = "Simple table";

    private static Connection conDefault = null;

    private static Connection conTestDb = null;

    private static String testUdfClassName = "org.apache.hadoop.hive.contrib.udf.example.UDFExampleAdd";

    @Test
    public void testConnection() throws Exception {
        Statement stmt = TestJdbcWithMiniHS2.conTestDb.createStatement();
        ResultSet res = stmt.executeQuery((("select * from " + (TestJdbcWithMiniHS2.tableName)) + " limit 5"));
        Assert.assertTrue(res.next());
        res.close();
        stmt.close();
    }

    @Test
    public void testParallelCompilation() throws Exception {
        Statement stmt = TestJdbcWithMiniHS2.conTestDb.createStatement();
        stmt.execute("set hive.driver.parallel.compilation=true");
        stmt.execute("set hive.server2.async.exec.async.compile=true");
        stmt.close();
        TestJdbcWithMiniHS2.startConcurrencyTest(TestJdbcWithMiniHS2.conTestDb, TestJdbcWithMiniHS2.tableName, 10);
        Connection conn = TestJdbcWithMiniHS2.getConnection(TestJdbcWithMiniHS2.testDbName);
        TestJdbcWithMiniHS2.startConcurrencyTest(conn, TestJdbcWithMiniHS2.tableName, 10);
        conn.close();
    }

    @Test
    public void testParallelCompilation2() throws Exception {
        Statement stmt = TestJdbcWithMiniHS2.conTestDb.createStatement();
        stmt.execute("set hive.driver.parallel.compilation=false");
        stmt.execute("set hive.server2.async.exec.async.compile=true");
        stmt.close();
        TestJdbcWithMiniHS2.startConcurrencyTest(TestJdbcWithMiniHS2.conTestDb, TestJdbcWithMiniHS2.tableName, 10);
        Connection conn = TestJdbcWithMiniHS2.getConnection(TestJdbcWithMiniHS2.testDbName);
        TestJdbcWithMiniHS2.startConcurrencyTest(conn, TestJdbcWithMiniHS2.tableName, 10);
        conn.close();
    }

    @Test
    public void testParallelCompilation3() throws Exception {
        Statement stmt = TestJdbcWithMiniHS2.conTestDb.createStatement();
        stmt.execute("set hive.driver.parallel.compilation=true");
        stmt.execute("set hive.server2.async.exec.async.compile=true");
        stmt.close();
        Connection conn = TestJdbcWithMiniHS2.getConnection(TestJdbcWithMiniHS2.testDbName);
        stmt = conn.createStatement();
        stmt.execute("set hive.driver.parallel.compilation=true");
        stmt.execute("set hive.server2.async.exec.async.compile=true");
        stmt.close();
        int poolSize = 100;
        SynchronousQueue<Runnable> executorQueue1 = new SynchronousQueue<Runnable>();
        ExecutorService workers1 = new ThreadPoolExecutor(1, poolSize, 20, TimeUnit.SECONDS, executorQueue1);
        SynchronousQueue<Runnable> executorQueue2 = new SynchronousQueue<Runnable>();
        ExecutorService workers2 = new ThreadPoolExecutor(1, poolSize, 20, TimeUnit.SECONDS, executorQueue2);
        List<Future<Boolean>> list1 = TestJdbcWithMiniHS2.startTasks(workers1, TestJdbcWithMiniHS2.conTestDb, TestJdbcWithMiniHS2.tableName, 10);
        List<Future<Boolean>> list2 = TestJdbcWithMiniHS2.startTasks(workers2, conn, TestJdbcWithMiniHS2.tableName, 10);
        TestJdbcWithMiniHS2.finishTasks(list1, workers1);
        TestJdbcWithMiniHS2.finishTasks(list2, workers2);
        conn.close();
    }

    @Test
    public void testParallelCompilation4() throws Exception {
        Statement stmt = TestJdbcWithMiniHS2.conTestDb.createStatement();
        stmt.execute("set hive.driver.parallel.compilation=true");
        stmt.execute("set hive.server2.async.exec.async.compile=false");
        stmt.close();
        Connection conn = TestJdbcWithMiniHS2.getConnection(TestJdbcWithMiniHS2.testDbName);
        stmt = conn.createStatement();
        stmt.execute("set hive.driver.parallel.compilation=true");
        stmt.execute("set hive.server2.async.exec.async.compile=false");
        stmt.close();
        int poolSize = 100;
        SynchronousQueue<Runnable> executorQueue1 = new SynchronousQueue<Runnable>();
        ExecutorService workers1 = new ThreadPoolExecutor(1, poolSize, 20, TimeUnit.SECONDS, executorQueue1);
        SynchronousQueue<Runnable> executorQueue2 = new SynchronousQueue<Runnable>();
        ExecutorService workers2 = new ThreadPoolExecutor(1, poolSize, 20, TimeUnit.SECONDS, executorQueue2);
        List<Future<Boolean>> list1 = TestJdbcWithMiniHS2.startTasks(workers1, TestJdbcWithMiniHS2.conTestDb, TestJdbcWithMiniHS2.tableName, 10);
        List<Future<Boolean>> list2 = TestJdbcWithMiniHS2.startTasks(workers2, conn, TestJdbcWithMiniHS2.tableName, 10);
        TestJdbcWithMiniHS2.finishTasks(list1, workers1);
        TestJdbcWithMiniHS2.finishTasks(list2, workers2);
        conn.close();
    }

    @Test
    public void testConcurrentStatements() throws Exception {
        TestJdbcWithMiniHS2.startConcurrencyTest(TestJdbcWithMiniHS2.conTestDb, TestJdbcWithMiniHS2.tableName, 50);
    }

    static class JDBCTask implements Callable<Boolean> {
        private String showsql = "show tables";

        private String querysql;

        private int seq = 0;

        Connection con = null;

        Statement stmt = null;

        ResultSet res = null;

        JDBCTask(Connection con, int seq, String tblName) {
            this.con = con;
            this.seq = seq;
            querysql = "SELECT count(value) FROM " + tblName;
        }

        public Boolean call() throws SQLException {
            int mod = (seq) % 10;
            try {
                if (mod < 2) {
                    String name = con.getMetaData().getDatabaseProductName();
                } else
                    if (mod < 5) {
                        stmt = con.createStatement();
                        res = stmt.executeQuery(querysql);
                        while (res.next()) {
                            res.getInt(1);
                        } 
                    } else
                        if (mod < 7) {
                            res = con.getMetaData().getSchemas();
                            if (res.next()) {
                                res.getString(1);
                            }
                        } else {
                            stmt = con.createStatement();
                            res = stmt.executeQuery(showsql);
                            if (res.next()) {
                                res.getString(1);
                            }
                        }


                return new Boolean(true);
            } finally {
                try {
                    if ((res) != null) {
                        res.close();
                        res = null;
                    }
                    if ((stmt) != null) {
                        stmt.close();
                        stmt = null;
                    }
                } catch (SQLException sqle1) {
                }
            }
        }
    }

    /**
     * This test is to connect to any database without using the command "Use <<DB>>"
     *  1) connect to default database.
     *  2) Create a new DB test_default.
     *  3) Connect to test_default database.
     *  4) Connect and create table under test_default_test.
     *  5) Connect and display all tables.
     *  6) Connect to default database and shouldn't find table test_default_test.
     *  7) Connect and drop test_default_test.
     *  8) drop test_default database.
     */
    @Test
    public void testURIDatabaseName() throws Exception {
        String jdbcUri = TestJdbcWithMiniHS2.miniHS2.getJdbcURL().substring(0, TestJdbcWithMiniHS2.miniHS2.getJdbcURL().indexOf("default"));
        Connection conn = TestJdbcWithMiniHS2.getConnection((jdbcUri + "default"), System.getProperty("user.name"), "bar");
        String dbName = "test_connection_non_default_db";
        String tableInNonDefaultSchema = "table_in_non_default_schema";
        Statement stmt = conn.createStatement();
        stmt.execute(("create database  if not exists " + dbName));
        stmt.close();
        conn.close();
        conn = TestJdbcWithMiniHS2.getConnection((jdbcUri + dbName), System.getProperty("user.name"), "bar");
        stmt = conn.createStatement();
        boolean expected = stmt.execute(((" create table " + tableInNonDefaultSchema) + " (x int)"));
        stmt.close();
        conn.close();
        conn = TestJdbcWithMiniHS2.getConnection((jdbcUri + dbName), System.getProperty("user.name"), "bar");
        stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery("show tables");
        boolean testTableExists = false;
        while (res.next()) {
            Assert.assertNotNull("table name is null in result set", res.getString(1));
            if (tableInNonDefaultSchema.equalsIgnoreCase(res.getString(1))) {
                testTableExists = true;
            }
        } 
        Assert.assertTrue((("table name  " + tableInNonDefaultSchema) + "   found in SHOW TABLES result set"), testTableExists);
        stmt.close();
        conn.close();
        conn = TestJdbcWithMiniHS2.getConnection((jdbcUri + "default"), System.getProperty("user.name"), "bar");
        stmt = conn.createStatement();
        res = stmt.executeQuery("show tables");
        testTableExists = false;
        while (res.next()) {
            Assert.assertNotNull("table name is null in result set", res.getString(1));
            if (tableInNonDefaultSchema.equalsIgnoreCase(res.getString(1))) {
                testTableExists = true;
            }
        } 
        Assert.assertFalse((("table name " + tableInNonDefaultSchema) + "  NOT  found in SHOW TABLES result set"), testTableExists);
        stmt.close();
        conn.close();
        conn = TestJdbcWithMiniHS2.getConnection((jdbcUri + dbName), System.getProperty("user.name"), "bar");
        stmt = conn.createStatement();
        stmt.execute("set hive.support.concurrency = false");
        res = stmt.executeQuery("show tables");
        stmt.execute(" drop table if exists table_in_non_default_schema");
        expected = stmt.execute(("DROP DATABASE " + dbName));
        stmt.close();
        conn.close();
        conn = TestJdbcWithMiniHS2.getConnection((jdbcUri + "default"), System.getProperty("user.name"), "bar");
        stmt = conn.createStatement();
        res = stmt.executeQuery("show tables");
        testTableExists = false;
        while (res.next()) {
            Assert.assertNotNull("table name is null in result set", res.getString(1));
            if (tableInNonDefaultSchema.equalsIgnoreCase(res.getString(1))) {
                testTableExists = true;
            }
        } 
        // test URI with no dbName
        conn = TestJdbcWithMiniHS2.getConnection(jdbcUri, System.getProperty("user.name"), "bar");
        verifyCurrentDB("default", conn);
        conn.close();
        conn = TestJdbcWithMiniHS2.getConnection((jdbcUri + ";"), System.getProperty("user.name"), "bar");
        verifyCurrentDB("default", conn);
        conn.close();
        conn = TestJdbcWithMiniHS2.getConnection((jdbcUri + ";/foo=bar;foo1=bar1"), System.getProperty("user.name"), "bar");
        verifyCurrentDB("default", conn);
        conn.close();
    }

    @Test
    public void testConnectionSchemaAPIs() throws Exception {
        /**
         * get/set Schema are new in JDK7 and not available in java.sql.Connection in JDK6. Hence the
         * test uses HiveConnection object to call these methods so that test will run with older JDKs
         */
        HiveConnection hiveConn = ((HiveConnection) (TestJdbcWithMiniHS2.conDefault));
        Assert.assertEquals(TestJdbcWithMiniHS2.defaultDbName, hiveConn.getSchema());
        Statement stmt = TestJdbcWithMiniHS2.conDefault.createStatement();
        stmt.execute(("USE " + (TestJdbcWithMiniHS2.testDbName)));
        Assert.assertEquals(TestJdbcWithMiniHS2.testDbName, hiveConn.getSchema());
        stmt.execute(("USE " + (TestJdbcWithMiniHS2.defaultDbName)));
        Assert.assertEquals(TestJdbcWithMiniHS2.defaultDbName, hiveConn.getSchema());
        hiveConn.setSchema(TestJdbcWithMiniHS2.defaultDbName);
        Assert.assertEquals(TestJdbcWithMiniHS2.defaultDbName, hiveConn.getSchema());
        hiveConn.setSchema(TestJdbcWithMiniHS2.defaultDbName);
        Assert.assertEquals(TestJdbcWithMiniHS2.defaultDbName, hiveConn.getSchema());
        Assert.assertTrue(hiveConn.getCatalog().isEmpty());
        hiveConn.setCatalog("foo");
        Assert.assertTrue(hiveConn.getCatalog().isEmpty());
    }

    /**
     * This method tests whether while creating a new connection, the config
     * variables specified in the JDBC URI are properly set for the connection.
     * This is a test for HiveConnection#configureConnection.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNewConnectionConfiguration() throws Exception {
        // Set some conf parameters
        String hiveConf = "hive.cli.print.header=true;hive.server2.async.exec.shutdown.timeout=20;" + "hive.server2.async.exec.threads=30;hive.server2.thrift.max.worker.threads=15";
        // Set some conf vars
        String hiveVar = "stab=salesTable;icol=customerID";
        String jdbcUri = ((((TestJdbcWithMiniHS2.miniHS2.getJdbcURL()) + "?") + hiveConf) + "#") + hiveVar;
        // Open a new connection with these conf & vars
        Connection con1 = DriverManager.getConnection(jdbcUri);
        // Execute "set" command and retrieve values for the conf & vars specified above
        // Assert values retrieved
        Statement stmt = con1.createStatement();
        // Verify that the property has been properly set while creating the
        // connection above
        verifyConfProperty(stmt, "hive.cli.print.header", "true");
        verifyConfProperty(stmt, "hive.server2.async.exec.shutdown.timeout", "20");
        verifyConfProperty(stmt, "hive.server2.async.exec.threads", "30");
        verifyConfProperty(stmt, "hive.server2.thrift.max.worker.threads", "15");
        verifyConfProperty(stmt, "stab", "salesTable");
        verifyConfProperty(stmt, "icol", "customerID");
        stmt.close();
        con1.close();
    }

    @Test
    public void testMetadataQueriesWithSerializeThriftInTasks() throws Exception {
        Statement stmt = TestJdbcWithMiniHS2.conTestDb.createStatement();
        setSerializeInTasksInConf(stmt);
        ResultSet rs = stmt.executeQuery("show tables");
        Assert.assertTrue(rs.next());
        stmt.execute(("describe " + (TestJdbcWithMiniHS2.tableName)));
        stmt.execute(("explain select * from " + (TestJdbcWithMiniHS2.tableName)));
        // Note: by closing stmt object, we are also reverting any session specific config changes.
        stmt.close();
    }

    @Test
    public void testSelectThriftSerializeInTasks() throws Exception {
        Statement stmt = TestJdbcWithMiniHS2.conTestDb.createStatement();
        setSerializeInTasksInConf(stmt);
        stmt.execute("set hive.compute.query.using.stats=false");
        stmt.execute("drop table if exists testSelectThriftOrders");
        stmt.execute("drop table if exists testSelectThriftCustomers");
        stmt.execute("create table testSelectThriftOrders (orderid int, orderdate string, customerid int)");
        stmt.execute("create table testSelectThriftCustomers (customerid int, customername string, customercountry string)");
        stmt.execute(("insert into testSelectThriftOrders values (1, '2015-09-09', 123), " + "(2, '2015-10-10', 246), (3, '2015-11-11', 356)"));
        stmt.execute(("insert into testSelectThriftCustomers values (123, 'David', 'America'), " + "(246, 'John', 'Canada'), (356, 'Mary', 'CostaRica')"));
        ResultSet countOrders = stmt.executeQuery("select count(*) from testSelectThriftOrders");
        while (countOrders.next()) {
            Assert.assertEquals(3, countOrders.getInt(1));
        } 
        ResultSet maxOrders = stmt.executeQuery("select max(customerid) from testSelectThriftCustomers");
        while (maxOrders.next()) {
            Assert.assertEquals(356, maxOrders.getInt(1));
        } 
        stmt.execute("drop table testSelectThriftOrders");
        stmt.execute("drop table testSelectThriftCustomers");
        stmt.close();
    }

    // Test that jdbc does not allow shell commands starting with "!".
    @Test
    public void testBangCommand() throws Exception {
        try (Statement stmt = TestJdbcWithMiniHS2.conTestDb.createStatement()) {
            stmt.execute("!ls --l");
            Assert.fail("statement should fail, allowing this would be bad security");
        } catch (HiveSQLException e) {
            Assert.assertTrue(e.getMessage().contains("cannot recognize input near '!'"));
        }
    }

    @Test
    public void testJoinThriftSerializeInTasks() throws Exception {
        Statement stmt = TestJdbcWithMiniHS2.conTestDb.createStatement();
        setSerializeInTasksInConf(stmt);
        stmt.execute("drop table if exists testThriftJoinOrders");
        stmt.execute("drop table if exists testThriftJoinCustomers");
        stmt.execute("create table testThriftJoinOrders (orderid int, orderdate string, customerid int)");
        stmt.execute("create table testThriftJoinCustomers (customerid int, customername string, customercountry string)");
        stmt.execute(("insert into testThriftJoinOrders values (1, '2015-09-09', 123), (2, '2015-10-10', 246), " + "(3, '2015-11-11', 356)"));
        stmt.execute(("insert into testThriftJoinCustomers values (123, 'David', 'America'), " + "(246, 'John', 'Canada'), (356, 'Mary', 'CostaRica')"));
        ResultSet joinResultSet = stmt.executeQuery(("select testThriftJoinOrders.orderid, testThriftJoinCustomers.customername " + ("from testThriftJoinOrders inner join testThriftJoinCustomers where " + "testThriftJoinOrders.customerid=testThriftJoinCustomers.customerid")));
        Map<Integer, String> expectedResult = new HashMap<Integer, String>();
        expectedResult.put(1, "David");
        expectedResult.put(2, "John");
        expectedResult.put(3, "Mary");
        for (int i = 1; i < 4; i++) {
            Assert.assertTrue(joinResultSet.next());
            Assert.assertEquals(joinResultSet.getString(2), expectedResult.get(i));
        }
        stmt.execute("drop table testThriftJoinOrders");
        stmt.execute("drop table testThriftJoinCustomers");
        stmt.close();
    }

    @Test
    public void testEmptyResultsetThriftSerializeInTasks() throws Exception {
        Statement stmt = TestJdbcWithMiniHS2.conTestDb.createStatement();
        setSerializeInTasksInConf(stmt);
        stmt.execute("drop table if exists testThriftSerializeShow1");
        stmt.execute("drop table if exists testThriftSerializeShow2");
        stmt.execute("create table testThriftSerializeShow1 (a int)");
        stmt.execute("create table testThriftSerializeShow2 (b int)");
        stmt.execute("insert into testThriftSerializeShow1 values (1)");
        stmt.execute("insert into testThriftSerializeShow2 values (2)");
        ResultSet rs = stmt.executeQuery(("select * from testThriftSerializeShow1 inner join " + "testThriftSerializeShow2 where testThriftSerializeShow1.a=testThriftSerializeShow2.b"));
        Assert.assertTrue((!(rs.next())));
        stmt.execute("drop table testThriftSerializeShow1");
        stmt.execute("drop table testThriftSerializeShow2");
        stmt.close();
    }

    @Test
    public void testFloatCast2DoubleThriftSerializeInTasks() throws Exception {
        Statement stmt = TestJdbcWithMiniHS2.conTestDb.createStatement();
        setSerializeInTasksInConf(stmt);
        stmt.execute("drop table if exists testThriftSerializeShow1");
        stmt.execute("drop table if exists testThriftSerializeShow2");
        stmt.execute("create table testThriftSerializeShow1 (a float)");
        stmt.execute("create table testThriftSerializeShow2 (b double)");
        stmt.execute("insert into testThriftSerializeShow1 values (1.1), (2.2), (3.3)");
        stmt.execute("insert into testThriftSerializeShow2 values (2.2), (3.3), (4.4)");
        ResultSet rs = stmt.executeQuery(("select * from testThriftSerializeShow1 inner join " + "testThriftSerializeShow2 where testThriftSerializeShow1.a=testThriftSerializeShow2.b"));
        Assert.assertTrue((!(rs.next())));
        stmt.execute("drop table testThriftSerializeShow1");
        stmt.execute("drop table testThriftSerializeShow2");
        stmt.close();
    }

    @Test
    public void testEnableThriftSerializeInTasks() throws Exception {
        Statement stmt = TestJdbcWithMiniHS2.conTestDb.createStatement();
        stmt.execute("drop table if exists testThriftSerializeShow1");
        stmt.execute("drop table if exists testThriftSerializeShow2");
        stmt.execute("create table testThriftSerializeShow1 (a int)");
        stmt.execute("create table testThriftSerializeShow2 (b int)");
        stmt.execute("insert into testThriftSerializeShow1 values (1)");
        stmt.execute("insert into testThriftSerializeShow2 values (2)");
        ResultSet rs = stmt.executeQuery(("select * from testThriftSerializeShow1 inner join " + "testThriftSerializeShow2 where testThriftSerializeShow1.a=testThriftSerializeShow2.b"));
        Assert.assertTrue((!(rs.next())));
        unsetSerializeInTasksInConf(stmt);
        rs = stmt.executeQuery(("select * from testThriftSerializeShow1 inner join " + "testThriftSerializeShow2 where testThriftSerializeShow1.a=testThriftSerializeShow2.b"));
        Assert.assertTrue((!(rs.next())));
        setSerializeInTasksInConf(stmt);
        rs = stmt.executeQuery(("select * from testThriftSerializeShow1 inner join " + "testThriftSerializeShow2 where testThriftSerializeShow1.a=testThriftSerializeShow2.b"));
        Assert.assertTrue((!(rs.next())));
        stmt.execute("drop table testThriftSerializeShow1");
        stmt.execute("drop table testThriftSerializeShow2");
        stmt.close();
    }

    /**
     * Tests the creation of the 3 scratch dirs: hdfs, local, downloaded resources (which is also local).
     * 1. Test with doAs=false: open a new JDBC session and verify the presence of directories/permissions
     * 2. Test with doAs=true: open a new JDBC session and verify the presence of directories/permissions
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSessionScratchDirs() throws Exception {
        // Stop HiveServer2
        TestJdbcWithMiniHS2.stopMiniHS2();
        HiveConf conf = new HiveConf();
        String userName;
        Path scratchDirPath;
        // Set a custom prefix for hdfs scratch dir path
        conf.set("hive.exec.scratchdir", ((TestJdbcWithMiniHS2.tmpDir) + "/hs2"));
        // Set a scratch dir permission
        String fsPermissionStr = "700";
        conf.set("hive.scratch.dir.permission", fsPermissionStr);
        // Start an instance of HiveServer2 which uses miniMR
        TestJdbcWithMiniHS2.startMiniHS2(conf);
        // 1. Test with doAs=false
        String sessionConf = "hive.server2.enable.doAs=false";
        userName = System.getProperty("user.name");
        Connection conn = TestJdbcWithMiniHS2.getConnection(TestJdbcWithMiniHS2.miniHS2.getJdbcURL(TestJdbcWithMiniHS2.testDbName, sessionConf), userName, "password");
        // FS
        FileSystem fs = TestJdbcWithMiniHS2.miniHS2.getLocalFS();
        FsPermission expectedFSPermission = new FsPermission(HiveConf.getVar(conf, SCRATCHDIRPERMISSION));
        // Verify scratch dir paths and permission
        // HDFS scratch dir
        scratchDirPath = new Path((((HiveConf.getVar(conf, SCRATCHDIR)) + "/") + userName));
        verifyScratchDir(conf, fs, scratchDirPath, expectedFSPermission, userName, false);
        // Local scratch dir
        scratchDirPath = new Path(HiveConf.getVar(conf, LOCALSCRATCHDIR));
        verifyScratchDir(conf, fs, scratchDirPath, expectedFSPermission, userName, true);
        // Downloaded resources dir
        scratchDirPath = new Path(HiveConf.getVar(conf, DOWNLOADED_RESOURCES_DIR));
        verifyScratchDir(conf, fs, scratchDirPath, expectedFSPermission, userName, true);
        conn.close();
        // 2. Test with doAs=true
        sessionConf = "hive.server2.enable.doAs=true";
        // Test for user "neo"
        userName = "neo";
        conn = TestJdbcWithMiniHS2.getConnection(TestJdbcWithMiniHS2.miniHS2.getJdbcURL(TestJdbcWithMiniHS2.testDbName, sessionConf), userName, "the-one");
        // Verify scratch dir paths and permission
        // HDFS scratch dir
        scratchDirPath = new Path((((HiveConf.getVar(conf, SCRATCHDIR)) + "/") + userName));
        verifyScratchDir(conf, fs, scratchDirPath, expectedFSPermission, userName, false);
        // Local scratch dir
        scratchDirPath = new Path(HiveConf.getVar(conf, LOCALSCRATCHDIR));
        verifyScratchDir(conf, fs, scratchDirPath, expectedFSPermission, userName, true);
        // Downloaded resources dir
        scratchDirPath = new Path(HiveConf.getVar(conf, DOWNLOADED_RESOURCES_DIR));
        verifyScratchDir(conf, fs, scratchDirPath, expectedFSPermission, userName, true);
        conn.close();
        // Restore original state
        TestJdbcWithMiniHS2.restoreMiniHS2AndConnections();
    }

    /**
     * Test UDF whitelist
     * - verify default value
     * - verify udf allowed with default whitelist
     * - verify udf allowed with specific whitelist
     * - verify udf disallowed when not in whitelist
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testUdfWhiteBlackList() throws Exception {
        HiveConf testConf = new HiveConf();
        Assert.assertTrue(testConf.getVar(HIVE_SERVER2_BUILTIN_UDF_WHITELIST).isEmpty());
        // verify that udf in default whitelist can be executed
        Statement stmt = TestJdbcWithMiniHS2.conDefault.createStatement();
        stmt.executeQuery("SELECT substr('foobar', 4) ");
        stmt.close();
        // setup whitelist
        TestJdbcWithMiniHS2.stopMiniHS2();
        Set<String> funcNames = FunctionRegistry.getFunctionNames();
        funcNames.remove("reflect");
        String funcNameStr = "";
        for (String funcName : funcNames) {
            funcNameStr += "," + funcName;
        }
        funcNameStr = funcNameStr.substring(1);// remove ',' at begining

        testConf.setVar(HIVE_SERVER2_BUILTIN_UDF_WHITELIST, funcNameStr);
        TestJdbcWithMiniHS2.startMiniHS2(testConf);
        Connection conn = TestJdbcWithMiniHS2.getConnection(TestJdbcWithMiniHS2.miniHS2.getJdbcURL(TestJdbcWithMiniHS2.testDbName), System.getProperty("user.name"), "bar");
        stmt = conn.createStatement();
        // verify that udf in whitelist can be executed
        stmt.executeQuery("SELECT substr('foobar', 3) ");
        // verify that udf not in whitelist fails
        try {
            stmt.executeQuery("SELECT reflect('java.lang.String', 'valueOf', 1) ");
            Assert.fail("reflect() udf invocation should fail");
        } catch (SQLException e) {
            // expected
        }
        conn.close();
        // Restore original state
        TestJdbcWithMiniHS2.restoreMiniHS2AndConnections();
    }

    /**
     * Test UDF blacklist
     *   - verify default value
     *   - verify udfs allowed with default blacklist
     *   - verify udf disallowed when in blacklist
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testUdfBlackList() throws Exception {
        HiveConf testConf = new HiveConf();
        Assert.assertTrue(testConf.getVar(HIVE_SERVER2_BUILTIN_UDF_BLACKLIST).isEmpty());
        Statement stmt = TestJdbcWithMiniHS2.conDefault.createStatement();
        // verify that udf in default whitelist can be executed
        stmt.executeQuery("SELECT substr('foobar', 4) ");
        TestJdbcWithMiniHS2.stopMiniHS2();
        testConf.setVar(HIVE_SERVER2_BUILTIN_UDF_BLACKLIST, "reflect");
        TestJdbcWithMiniHS2.startMiniHS2(testConf);
        Connection conn = TestJdbcWithMiniHS2.getConnection(TestJdbcWithMiniHS2.miniHS2.getJdbcURL(TestJdbcWithMiniHS2.testDbName), System.getProperty("user.name"), "bar");
        stmt = conn.createStatement();
        try {
            stmt.executeQuery("SELECT reflect('java.lang.String', 'valueOf', 1) ");
            Assert.fail("reflect() udf invocation should fail");
        } catch (SQLException e) {
            // expected
        }
        conn.close();
        // Restore original state
        TestJdbcWithMiniHS2.restoreMiniHS2AndConnections();
    }

    /**
     * Test UDF blacklist overrides whitelist
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testUdfBlackListOverride() throws Exception {
        TestJdbcWithMiniHS2.stopMiniHS2();
        // setup whitelist
        HiveConf testConf = new HiveConf();
        Set<String> funcNames = FunctionRegistry.getFunctionNames();
        String funcNameStr = "";
        for (String funcName : funcNames) {
            funcNameStr += "," + funcName;
        }
        funcNameStr = funcNameStr.substring(1);// remove ',' at begining

        testConf.setVar(HIVE_SERVER2_BUILTIN_UDF_WHITELIST, funcNameStr);
        testConf.setVar(HIVE_SERVER2_BUILTIN_UDF_BLACKLIST, "reflect");
        TestJdbcWithMiniHS2.startMiniHS2(testConf);
        Connection conn = TestJdbcWithMiniHS2.getConnection(TestJdbcWithMiniHS2.miniHS2.getJdbcURL(TestJdbcWithMiniHS2.testDbName), System.getProperty("user.name"), "bar");
        Statement stmt = conn.createStatement();
        // verify that udf in black list fails even though it's included in whitelist
        try {
            stmt.executeQuery("SELECT reflect('java.lang.String', 'valueOf', 1) ");
            Assert.fail("reflect() udf invocation should fail");
        } catch (SQLException e) {
            // expected
        }
        conn.close();
        // Restore original state
        TestJdbcWithMiniHS2.restoreMiniHS2AndConnections();
    }

    /**
     * Tests the creation of the root hdfs scratch dir, which should be writable by all.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRootScratchDir() throws Exception {
        // Stop HiveServer2
        TestJdbcWithMiniHS2.stopMiniHS2();
        HiveConf conf = new HiveConf();
        String userName;
        Path scratchDirPath;
        conf.set("hive.exec.scratchdir", ((TestJdbcWithMiniHS2.tmpDir) + "/hs2"));
        // Start an instance of HiveServer2 which uses miniMR
        TestJdbcWithMiniHS2.startMiniHS2(conf);
        userName = System.getProperty("user.name");
        Connection conn = TestJdbcWithMiniHS2.getConnection(TestJdbcWithMiniHS2.miniHS2.getJdbcURL(TestJdbcWithMiniHS2.testDbName), userName, "password");
        // FS
        FileSystem fs = TestJdbcWithMiniHS2.miniHS2.getLocalFS();
        FsPermission expectedFSPermission = new FsPermission(((short) (475)));
        // Verify scratch dir paths and permission
        // HDFS scratch dir
        scratchDirPath = new Path(HiveConf.getVar(conf, SCRATCHDIR));
        verifyScratchDir(conf, fs, scratchDirPath, expectedFSPermission, userName, false);
        conn.close();
        // Test with multi-level scratch dir path
        // Stop HiveServer2
        TestJdbcWithMiniHS2.stopMiniHS2();
        conf.set("hive.exec.scratchdir", ((TestJdbcWithMiniHS2.tmpDir) + "/level1/level2/level3"));
        TestJdbcWithMiniHS2.startMiniHS2(conf);
        conn = TestJdbcWithMiniHS2.getConnection(TestJdbcWithMiniHS2.miniHS2.getJdbcURL(TestJdbcWithMiniHS2.testDbName), userName, "password");
        scratchDirPath = new Path(HiveConf.getVar(conf, SCRATCHDIR));
        verifyScratchDir(conf, fs, scratchDirPath, expectedFSPermission, userName, false);
        conn.close();
        // Restore original state
        TestJdbcWithMiniHS2.restoreMiniHS2AndConnections();
    }

    /**
     * Test for http header size
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHttpHeaderSize() throws Exception {
        // Stop HiveServer2
        TestJdbcWithMiniHS2.stopMiniHS2();
        HiveConf conf = new HiveConf();
        conf.setIntVar(HIVE_SERVER2_THRIFT_HTTP_REQUEST_HEADER_SIZE, 1024);
        conf.setIntVar(HIVE_SERVER2_THRIFT_HTTP_RESPONSE_HEADER_SIZE, 1024);
        TestJdbcWithMiniHS2.startMiniHS2(conf, true);
        // Username and password are added to the http request header.
        // We will test the reconfiguration of the header size by changing the password length.
        String userName = "userName";
        String password = StringUtils.leftPad("*", 100);
        Connection conn = null;
        // This should go fine, since header should be less than the configured header size
        try {
            conn = TestJdbcWithMiniHS2.getConnection(TestJdbcWithMiniHS2.miniHS2.getJdbcURL(TestJdbcWithMiniHS2.testDbName), userName, password);
        } catch (Exception e) {
            Assert.fail(("Not expecting exception: " + e));
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        // This should fail with given HTTP response code 431 in error message, since header is more
        // than the configured the header size
        password = StringUtils.leftPad("*", 2000);
        Exception headerException = null;
        try {
            conn = null;
            conn = TestJdbcWithMiniHS2.getConnection(TestJdbcWithMiniHS2.miniHS2.getJdbcURL(TestJdbcWithMiniHS2.testDbName), userName, password);
        } catch (Exception e) {
            headerException = e;
        } finally {
            if (conn != null) {
                conn.close();
            }
            Assert.assertTrue("Header exception should be thrown", (headerException != null));
            Assert.assertTrue(("Incorrect HTTP Response:" + (headerException.getMessage())), headerException.getMessage().contains("HTTP Response code: 431"));
        }
        // Stop HiveServer2 to increase header size
        TestJdbcWithMiniHS2.stopMiniHS2();
        conf.setIntVar(HIVE_SERVER2_THRIFT_HTTP_REQUEST_HEADER_SIZE, 3000);
        conf.setIntVar(HIVE_SERVER2_THRIFT_HTTP_RESPONSE_HEADER_SIZE, 3000);
        TestJdbcWithMiniHS2.startMiniHS2(conf);
        // This should now go fine, since we increased the configured header size
        try {
            conn = null;
            conn = TestJdbcWithMiniHS2.getConnection(TestJdbcWithMiniHS2.miniHS2.getJdbcURL(TestJdbcWithMiniHS2.testDbName), userName, password);
        } catch (Exception e) {
            Assert.fail(("Not expecting exception: " + e));
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        // Restore original state
        TestJdbcWithMiniHS2.restoreMiniHS2AndConnections();
    }

    /**
     * Tests that DataNucleus' NucleusContext.classLoaderResolverMap clears cached class objects
     * (& hence doesn't leak classloaders) on closing any session
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAddJarDataNucleusUnCaching() throws Exception {
        Path jarFilePath = getHiveContribJarPath();
        // We need a new connection object as we'll check the cache size after connection close
        Connection conn = TestJdbcWithMiniHS2.getConnection(TestJdbcWithMiniHS2.miniHS2.getJdbcURL(TestJdbcWithMiniHS2.testDbName), System.getProperty("user.name"), "password");
        Statement stmt = conn.createStatement();
        int mapSizeAfterClose;
        // Add the jar file
        stmt.execute(("ADD JAR " + (jarFilePath.toString())));
        // Create a temporary function using the jar
        stmt.execute((("CREATE TEMPORARY FUNCTION add_func AS '" + (TestJdbcWithMiniHS2.testUdfClassName)) + "'"));
        ResultSet res = stmt.executeQuery("DESCRIBE FUNCTION add_func");
        checkForNotExist(res);
        // Execute the UDF
        stmt.execute((("SELECT add_func(int_col, 1) from " + (TestJdbcWithMiniHS2.tableName)) + " limit 1"));
        // Close the connection
        conn.close();
        mapSizeAfterClose = getNucleusClassLoaderResolverMapSize();
        System.out.println(("classLoaderResolverMap size after connection close: " + mapSizeAfterClose));
        // Cache size should be 0 now
        Assert.assertTrue(("Failed; NucleusContext classLoaderResolverMap size: " + mapSizeAfterClose), (mapSizeAfterClose == 0));
    }

    /**
     * Tests ADD JAR uses Hives ReflectionUtil.CONSTRUCTOR_CACHE
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAddJarConstructorUnCaching() throws Exception {
        // This test assumes the hive-contrib JAR has been built as part of the Hive build.
        // Also dependent on the UDFExampleAdd class within that JAR.
        setReflectionUtilCache();
        Path jarFilePath = getHiveContribJarPath();
        long cacheBeforeAddJar;
        long cacheAfterClose;
        // Force the cache clear so we know its empty
        invalidateReflectionUtlCache();
        cacheBeforeAddJar = getReflectionUtilCacheSize();
        System.out.println(("CONSTRUCTOR_CACHE size before add jar: " + cacheBeforeAddJar));
        System.out.println(("CONSTRUCTOR_CACHE as map before add jar:" + (getReflectionUtilCache().asMap())));
        Assert.assertTrue(("FAILED: CONSTRUCTOR_CACHE size before add jar: " + cacheBeforeAddJar), (cacheBeforeAddJar == 0));
        // Add the jar file
        Statement stmt = TestJdbcWithMiniHS2.conTestDb.createStatement();
        stmt.execute(("ADD JAR " + (jarFilePath.toString())));
        // Create a temporary function using the jar
        stmt.execute((("CREATE TEMPORARY FUNCTION add_func AS '" + (TestJdbcWithMiniHS2.testUdfClassName)) + "'"));
        // Execute the UDF
        ResultSet res = stmt.executeQuery((("SELECT add_func(int_col, 1) from " + (TestJdbcWithMiniHS2.tableName)) + " limit 1"));
        Assert.assertTrue(res.next());
        TimeUnit.SECONDS.sleep(7);
        // Have to force a cleanup of all expired entries here because its possible that the
        // expired entries will still be counted in Cache.size().
        // Taken from:
        // http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/cache/CacheBuilder.html
        cleanUpReflectionUtlCache();
        cacheAfterClose = getReflectionUtilCacheSize();
        System.out.println(("CONSTRUCTOR_CACHE size after connection close: " + cacheAfterClose));
        Assert.assertTrue(("FAILED: CONSTRUCTOR_CACHE size after connection close: " + cacheAfterClose), (cacheAfterClose == 0));
        stmt.execute("DROP TEMPORARY FUNCTION IF EXISTS add_func");
        stmt.close();
    }

    @Test
    public void testPermFunc() throws Exception {
        // This test assumes the hive-contrib JAR has been built as part of the Hive build.
        // Also dependent on the UDFExampleAdd class within that JAR.
        Path jarFilePath = getHiveContribJarPath();
        Statement stmt = TestJdbcWithMiniHS2.conTestDb.createStatement();
        ResultSet res;
        // Add the jar file
        stmt.execute(("ADD JAR " + (jarFilePath.toString())));
        // Register function
        String queryStr = ((("CREATE FUNCTION example_add AS '" + (TestJdbcWithMiniHS2.testUdfClassName)) + "' USING JAR '") + jarFilePath) + "'";
        stmt.execute(queryStr);
        // Call describe
        res = stmt.executeQuery((("DESCRIBE FUNCTION " + (TestJdbcWithMiniHS2.testDbName)) + ".example_add"));
        checkForNotExist(res);
        // Use UDF in query
        res = stmt.executeQuery((("SELECT example_add(1, 2) FROM " + (TestJdbcWithMiniHS2.tableName)) + " LIMIT 1"));
        Assert.assertTrue("query has results", res.next());
        Assert.assertEquals(3, res.getInt(1));
        Assert.assertFalse("no more results", res.next());
        // A new connection should be able to call describe/use function without issue
        Connection conn2 = TestJdbcWithMiniHS2.getConnection(TestJdbcWithMiniHS2.testDbName);
        Statement stmt2 = conn2.createStatement();
        stmt2.execute(("USE " + (TestJdbcWithMiniHS2.testDbName)));
        res = stmt2.executeQuery((("DESCRIBE FUNCTION " + (TestJdbcWithMiniHS2.testDbName)) + ".example_add"));
        checkForNotExist(res);
        res = stmt2.executeQuery((((("SELECT " + (TestJdbcWithMiniHS2.testDbName)) + ".example_add(1, 1) FROM ") + (TestJdbcWithMiniHS2.tableName)) + " LIMIT 1"));
        Assert.assertTrue("query has results", res.next());
        Assert.assertEquals(2, res.getInt(1));
        Assert.assertFalse("no more results", res.next());
        conn2.close();
        stmt.execute((("DROP FUNCTION IF EXISTS " + (TestJdbcWithMiniHS2.testDbName)) + ".example_add"));
        stmt.close();
    }

    @Test
    public void testTempTable() throws Exception {
        // Create temp table with current connection
        String tempTableName = "tmp1";
        Statement stmt = TestJdbcWithMiniHS2.conTestDb.createStatement();
        stmt.execute((("CREATE TEMPORARY TABLE " + tempTableName) + " (key string, value string)"));
        stmt.execute(((("load data local inpath '" + (TestJdbcWithMiniHS2.kvDataFilePath.toString())) + "' into table ") + tempTableName));
        String resultVal = "val_238";
        String queryStr = ((("SELECT * FROM " + tempTableName) + " where value = '") + resultVal) + "'";
        ResultSet res = stmt.executeQuery(queryStr);
        Assert.assertTrue(res.next());
        Assert.assertEquals(resultVal, res.getString(2));
        res.close();
        stmt.close();
        // Test getTables()
        DatabaseMetaData md = TestJdbcWithMiniHS2.conTestDb.getMetaData();
        Assert.assertTrue(((md.getConnection()) == (TestJdbcWithMiniHS2.conTestDb)));
        ResultSet rs = md.getTables(null, null, tempTableName, null);
        boolean foundTable = false;
        while (rs.next()) {
            String tableName = rs.getString(3);
            if (tableName.equalsIgnoreCase(tempTableName)) {
                Assert.assertFalse("Table not found yet", foundTable);
                foundTable = true;
            }
        } 
        Assert.assertTrue("Found temp table", foundTable);
        // Test getTables() with no table name pattern
        rs = md.getTables(null, null, null, null);
        foundTable = false;
        while (rs.next()) {
            String tableName = rs.getString(3);
            if (tableName.equalsIgnoreCase(tempTableName)) {
                Assert.assertFalse("Table not found yet", foundTable);
                foundTable = true;
            }
        } 
        Assert.assertTrue("Found temp table", foundTable);
        // Test getColumns()
        rs = md.getColumns(null, null, tempTableName, null);
        Assert.assertTrue("First row", rs.next());
        Assert.assertTrue(rs.getString(3).equalsIgnoreCase(tempTableName));
        Assert.assertTrue(rs.getString(4).equalsIgnoreCase("key"));
        Assert.assertEquals(Types.VARCHAR, rs.getInt(5));
        Assert.assertTrue("Second row", rs.next());
        Assert.assertTrue(rs.getString(3).equalsIgnoreCase(tempTableName));
        Assert.assertTrue(rs.getString(4).equalsIgnoreCase("value"));
        Assert.assertEquals(Types.VARCHAR, rs.getInt(5));
        // A second connection should not be able to see the table
        Connection conn2 = DriverManager.getConnection(TestJdbcWithMiniHS2.miniHS2.getJdbcURL(TestJdbcWithMiniHS2.testDbName), System.getProperty("user.name"), "bar");
        Statement stmt2 = conn2.createStatement();
        stmt2.execute(("USE " + (TestJdbcWithMiniHS2.testDbName)));
        boolean gotException = false;
        try {
            res = stmt2.executeQuery(queryStr);
        } catch (SQLException err) {
            // This is expected to fail.
            Assert.assertTrue(("Expecting table not found error, instead got: " + err), err.getMessage().contains("Table not found"));
            gotException = true;
        }
        Assert.assertTrue("Exception while querying non-existing temp table", gotException);
        conn2.close();
    }

    @Test
    public void testReplDumpResultSet() throws Exception {
        String tid = ((TestJdbcWithMiniHS2.class.getCanonicalName().toLowerCase().replace('.', '_')) + "_") + (System.currentTimeMillis());
        String testPathName = ((System.getProperty("test.warehouse.dir", "/tmp")) + (Path.SEPARATOR)) + tid;
        Path testPath = new Path(testPathName);
        FileSystem fs = testPath.getFileSystem(new HiveConf());
        Statement stmt = TestJdbcWithMiniHS2.conDefault.createStatement();
        try {
            stmt.execute(("set hive.repl.rootdir = " + testPathName));
            ResultSet rs = stmt.executeQuery(("repl dump " + (TestJdbcWithMiniHS2.testDbName)));
            ResultSetMetaData rsMeta = rs.getMetaData();
            Assert.assertEquals(2, rsMeta.getColumnCount());
            int numRows = 0;
            while (rs.next()) {
                numRows++;
                URI uri = new URI(rs.getString(1));
                int notificationId = rs.getInt(2);
                Assert.assertNotNull(uri);
                Assert.assertEquals(testPath.toUri().getScheme(), uri.getScheme());
                Assert.assertEquals(testPath.toUri().getAuthority(), uri.getAuthority());
                // In test setup, we append '/next' to hive.repl.rootdir and use that as the dump location
                Assert.assertEquals(((testPath.toUri().getPath()) + "/next"), uri.getPath());
                Assert.assertNotNull(notificationId);
            } 
            Assert.assertEquals(1, numRows);
        } finally {
            // Clean up
            fs.delete(testPath, true);
        }
    }

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

    private static class ExceptionHolder {
        Throwable throwable;
    }

    @Test
    public void testFetchSize() throws Exception {
        // Test setting fetch size below max
        Connection fsConn = TestJdbcWithMiniHS2.getConnection(TestJdbcWithMiniHS2.miniHS2.getJdbcURL("default", "fetchSize=50", ""), System.getProperty("user.name"), "bar");
        Statement stmt = fsConn.createStatement();
        stmt.execute("set hive.server2.thrift.resultset.serialize.in.tasks=true");
        int fetchSize = stmt.getFetchSize();
        Assert.assertEquals(50, fetchSize);
        stmt.close();
        fsConn.close();
        // Test setting fetch size above max
        fsConn = TestJdbcWithMiniHS2.getConnection(TestJdbcWithMiniHS2.miniHS2.getJdbcURL("default", ("fetchSize=" + ((TestJdbcWithMiniHS2.miniHS2.getHiveConf().getIntVar(HIVE_SERVER2_THRIFT_RESULTSET_MAX_FETCH_SIZE)) + 1)), ""), System.getProperty("user.name"), "bar");
        stmt = fsConn.createStatement();
        stmt.execute("set hive.server2.thrift.resultset.serialize.in.tasks=true");
        fetchSize = stmt.getFetchSize();
        Assert.assertEquals(TestJdbcWithMiniHS2.miniHS2.getHiveConf().getIntVar(HIVE_SERVER2_THRIFT_RESULTSET_MAX_FETCH_SIZE), fetchSize);
        stmt.close();
        fsConn.close();
    }

    /**
     * A test that checks that Lineage is correct when a multiple concurrent
     * requests are make on a connection
     */
    @Test
    public void testConcurrentLineage() throws Exception {
        // setup to run concurrent operations
        Statement stmt = TestJdbcWithMiniHS2.conTestDb.createStatement();
        setSerializeInTasksInConf(stmt);
        stmt.execute("drop table if exists testConcurrentLineage1");
        stmt.execute("drop table if exists testConcurrentLineage2");
        stmt.execute("create table testConcurrentLineage1 (col1 int)");
        stmt.execute("create table testConcurrentLineage2 (col2 int)");
        // clear vertices list
        ReadableHook.clear();
        // run 5 sql inserts concurrently
        int numThreads = 5;
        // set to 1 for single threading
        int concurrentCalls = 5;
        ExecutorService pool = Executors.newFixedThreadPool(numThreads);
        try {
            List<TestJdbcWithMiniHS2.InsertCallable> tasks = new ArrayList<>();
            for (int i = 0; i < concurrentCalls; i++) {
                TestJdbcWithMiniHS2.InsertCallable runner = new TestJdbcWithMiniHS2.InsertCallable(TestJdbcWithMiniHS2.conTestDb);
                tasks.add(runner);
            }
            List<Future<Void>> futures = pool.invokeAll(tasks);
            for (Future<Void> future : futures) {
                future.get(20, TimeUnit.SECONDS);
            }
            // check to see that the vertices are correct
            checkVertices();
        } finally {
            // clean up
            stmt.execute("drop table testConcurrentLineage1");
            stmt.execute("drop table testConcurrentLineage2");
            stmt.close();
            pool.shutdownNow();
        }
    }

    /**
     * A Callable that does 2 inserts
     */
    private class InsertCallable implements Callable<Void> {
        private Connection connection;

        InsertCallable(Connection conn) {
            this.connection = conn;
        }

        @Override
        public Void call() throws Exception {
            doLineageInserts(connection);
            return null;
        }

        private void doLineageInserts(Connection connection) throws SQLException {
            Statement stmt = connection.createStatement();
            stmt.execute("insert into testConcurrentLineage1 values (1)");
            stmt.execute("insert into testConcurrentLineage2 values (2)");
        }
    }

    /**
     * Test 'describe extended' on tables that have special white space characters in the row format.
     */
    @Test
    public void testDescribe() throws Exception {
        try (Statement stmt = TestJdbcWithMiniHS2.conTestDb.createStatement()) {
            String table = "testDescribe";
            stmt.execute(("drop table if exists " + table));
            stmt.execute(((("create table " + table) + " (orderid int, orderdate string, customerid int)") + " ROW FORMAT DELIMITED FIELDS terminated by \'\\t\' LINES terminated by \'\\n\'"));
            String extendedDescription = TestJdbcWithMiniHS2.getDetailedTableDescription(stmt, table);
            Assert.assertNotNull("could not get Detailed Table Information", extendedDescription);
            Assert.assertTrue(("description appears truncated: " + extendedDescription), extendedDescription.endsWith(")"));
            Assert.assertTrue(("bad line delimiter: " + extendedDescription), extendedDescription.contains("line.delim=\\n"));
            Assert.assertTrue(("bad field delimiter: " + extendedDescription), extendedDescription.contains("field.delim=\\t"));
            String view = "testDescribeView";
            stmt.execute(((("create view " + view) + " as select * from ") + table));
            String extendedViewDescription = TestJdbcWithMiniHS2.getDetailedTableDescription(stmt, view);
            Assert.assertTrue(("bad view text: " + extendedViewDescription), extendedViewDescription.contains(("viewOriginalText:select * from " + table)));
            Assert.assertTrue(("bad expanded view text: " + extendedViewDescription), extendedViewDescription.contains(("viewExpandedText:select `testdescribe`.`orderid`, `testdescribe`.`orderdate`, " + "`testdescribe`.`customerid` from `testjdbcminihs2`")));
        }
    }
}

