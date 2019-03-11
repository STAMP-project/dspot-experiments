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


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hive.service.cli.session.HiveSessionHook;
import org.apache.hive.service.cli.session.HiveSessionHookContext;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class is cloned from TestJdbcWithMiniMR, except use Spark as the execution engine.
 */
public class TestJdbcWithLocalClusterSpark {
    public static final String TEST_TAG = "miniHS2.localClusterSpark.tag";

    public static final String TEST_TAG_VALUE = "miniHS2.localClusterSpark.value";

    public static class LocalClusterSparkSessionHook implements HiveSessionHook {
        @Override
        public void run(HiveSessionHookContext sessionHookContext) throws HiveSQLException {
            sessionHookContext.getSessionConf().set(TestJdbcWithLocalClusterSpark.TEST_TAG, TestJdbcWithLocalClusterSpark.TEST_TAG_VALUE);
        }
    }

    private static MiniHS2 miniHS2 = null;

    private static HiveConf conf;

    private static Path dataFilePath;

    private static String dbName = "mrTestDb";

    private Connection hs2Conn = null;

    private Statement stmt;

    /**
     * Verify that the connection to HS2 with MiniMr is successful.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConnection() throws Exception {
        // the session hook should set the property
        verifyProperty(TestJdbcWithLocalClusterSpark.TEST_TAG, TestJdbcWithLocalClusterSpark.TEST_TAG_VALUE);
    }

    /**
     * Run nonMr query.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNonSparkQuery() throws Exception {
        String tableName = "testTab1";
        String resultVal = "val_238";
        String queryStr = "SELECT * FROM " + tableName;
        testKvQuery(tableName, queryStr, resultVal);
    }

    /**
     * Run nonMr query.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSparkQuery() throws Exception {
        String tableName = "testTab2";
        String resultVal = "val_238";
        String queryStr = ((("SELECT * FROM " + tableName) + " where value = '") + resultVal) + "'";
        testKvQuery(tableName, queryStr, resultVal);
    }

    @Test
    public void testPermFunc() throws Exception {
        // This test assumes the hive-contrib JAR has been built as part of the Hive build.
        // Also dependent on the UDFExampleAdd class within that JAR.
        String udfClassName = "org.apache.hadoop.hive.contrib.udf.example.UDFExampleAdd";
        String mvnRepo = System.getProperty("maven.local.repository");
        String hiveVersion = System.getProperty("hive.version");
        String jarFileName = ("hive-contrib-" + hiveVersion) + ".jar";
        String[] pathParts = new String[]{ "org", "apache", "hive", "hive-contrib", hiveVersion, jarFileName };
        // Create path to hive-contrib JAR on local filesystem
        Path contribJarPath = new Path(mvnRepo);
        for (String pathPart : pathParts) {
            contribJarPath = new Path(contribJarPath, pathPart);
        }
        FileSystem localFs = FileSystem.getLocal(TestJdbcWithLocalClusterSpark.conf);
        Assert.assertTrue(("Hive contrib JAR exists at " + contribJarPath), localFs.exists(contribJarPath));
        String hdfsJarPathStr = "hdfs:///" + jarFileName;
        Path hdfsJarPath = new Path(hdfsJarPathStr);
        // Copy JAR to DFS
        FileSystem dfs = TestJdbcWithLocalClusterSpark.miniHS2.getDFS().getFileSystem();
        dfs.copyFromLocalFile(contribJarPath, hdfsJarPath);
        Assert.assertTrue(("Verify contrib JAR copied to HDFS at " + hdfsJarPath), dfs.exists(hdfsJarPath));
        // Register function
        String queryStr = (((("CREATE FUNCTION example_add AS '" + udfClassName) + "'") + " USING JAR '") + hdfsJarPathStr) + "'";
        stmt.execute(queryStr);
        // Call describe
        ResultSet res;
        res = stmt.executeQuery((("DESCRIBE FUNCTION " + (TestJdbcWithLocalClusterSpark.dbName)) + ".example_add"));
        checkForNotExist(res);
        // Use UDF in query
        String tableName = "testTab3";
        setupKv1Tabs(tableName);
        res = stmt.executeQuery((("SELECT EXAMPLE_ADD(1, 2) FROM " + tableName) + " LIMIT 1"));
        Assert.assertTrue("query has results", res.next());
        Assert.assertEquals(3, res.getInt(1));
        Assert.assertFalse("no more results", res.next());
        // A new connection should be able to call describe/use function without issue
        Connection conn2 = DriverManager.getConnection(TestJdbcWithLocalClusterSpark.miniHS2.getJdbcURL(TestJdbcWithLocalClusterSpark.dbName), System.getProperty("user.name"), "bar");
        Statement stmt2 = conn2.createStatement();
        stmt2.execute(("USE " + (TestJdbcWithLocalClusterSpark.dbName)));
        res = stmt2.executeQuery((("DESCRIBE FUNCTION " + (TestJdbcWithLocalClusterSpark.dbName)) + ".example_add"));
        checkForNotExist(res);
        res = stmt2.executeQuery((((("SELECT " + (TestJdbcWithLocalClusterSpark.dbName)) + ".example_add(1, 1) FROM ") + tableName) + " LIMIT 1"));
        Assert.assertTrue("query has results", res.next());
        Assert.assertEquals(2, res.getInt(1));
        Assert.assertFalse("no more results", res.next());
        stmt.execute(("DROP TABLE " + tableName));
    }

    @Test
    public void testTempTable() throws Exception {
        // Create temp table with current connection
        String tempTableName = "tmp1";
        stmt.execute((("CREATE TEMPORARY TABLE " + tempTableName) + " (key string, value string)"));
        stmt.execute(((("load data local inpath '" + (TestJdbcWithLocalClusterSpark.dataFilePath.toString())) + "' into table ") + tempTableName));
        String resultVal = "val_238";
        String queryStr = ((("SELECT * FROM " + tempTableName) + " where value = '") + resultVal) + "'";
        verifyResult(queryStr, resultVal, 2);
        // A second connection should not be able to see the table
        Connection conn2 = DriverManager.getConnection(TestJdbcWithLocalClusterSpark.miniHS2.getJdbcURL(TestJdbcWithLocalClusterSpark.dbName), System.getProperty("user.name"), "bar");
        Statement stmt2 = conn2.createStatement();
        stmt2.execute(("USE " + (TestJdbcWithLocalClusterSpark.dbName)));
        boolean gotException = false;
        try {
            stmt2.executeQuery(queryStr);
        } catch (SQLException err) {
            // This is expected to fail.
            Assert.assertTrue(("Expecting table not found error, instead got: " + err), err.getMessage().contains("Table not found"));
            gotException = true;
        }
        Assert.assertTrue("Exception while querying non-existing temp table", gotException);
    }
}

