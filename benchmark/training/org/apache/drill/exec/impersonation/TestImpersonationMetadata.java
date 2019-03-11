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
package org.apache.drill.exec.impersonation;


import DotDrillType.STATS;
import org.apache.drill.categories.SecurityTest;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.categories.UnlikelyTest;
import org.apache.drill.common.exceptions.UserException;
import org.apache.drill.common.exceptions.UserRemoteException;
import org.apache.drill.shaded.guava.com.google.common.base.Joiner;
import org.apache.drill.test.BaseTestQuery;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;


/**
 * Tests impersonation on metadata related queries as SHOW FILES, SHOW TABLES, CREATE VIEW, CREATE TABLE and DROP TABLE
 */
@Category({ SlowTest.class, SecurityTest.class })
public class TestImpersonationMetadata extends BaseTestImpersonation {
    private static final String user1 = "drillTestUser1";

    private static final String user2 = "drillTestUser2";

    private static final String group0 = "drill_test_grp_0";

    private static final String group1 = "drill_test_grp_1";

    static {
        UserGroupInformation.createUserForTesting(TestImpersonationMetadata.user1, new String[]{ TestImpersonationMetadata.group1, TestImpersonationMetadata.group0 });
        UserGroupInformation.createUserForTesting(TestImpersonationMetadata.user2, new String[]{ TestImpersonationMetadata.group1 });
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testDropTable() throws Exception {
        // create tables as user2
        BaseTestQuery.updateClient(TestImpersonationMetadata.user2);
        BaseTestQuery.test("use `%s.user2_workspace1`", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME);
        // create a table that can be dropped by another user in a different group
        BaseTestQuery.test("create table parquet_table_775 as select * from cp.`employee.json`");
        // create a table that cannot be dropped by another user
        BaseTestQuery.test("use `%s.user2_workspace2`", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME);
        BaseTestQuery.test("create table parquet_table_700 as select * from cp.`employee.json`");
        // Drop tables as user1
        BaseTestQuery.updateClient(TestImpersonationMetadata.user1);
        BaseTestQuery.test("use `%s.user2_workspace1`", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME);
        BaseTestQuery.testBuilder().sqlQuery("drop table parquet_table_775").unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format("Table [%s] dropped", "parquet_table_775")).go();
        BaseTestQuery.test("use `%s.user2_workspace2`", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME);
        boolean dropFailed = false;
        try {
            BaseTestQuery.test("drop table parquet_table_700");
        } catch (UserException e) {
            Assert.assertTrue(e.getMessage().contains("PERMISSION ERROR"));
            dropFailed = true;
        }
        Assert.assertTrue("Permission checking failed during drop table", dropFailed);
    }

    // DRILL-3037
    @Test
    @Category(UnlikelyTest.class)
    public void testImpersonatingProcessUser() throws Exception {
        BaseTestQuery.updateClient(BaseTestImpersonation.processUser);
        // Process user start the mini dfs, he has read/write permissions by default
        final String viewName = String.format("%s.drill_test_grp_0_700.testView", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME);
        try {
            BaseTestQuery.test((("CREATE VIEW " + viewName) + " AS SELECT * FROM cp.`region.json`"));
            BaseTestQuery.test((("SELECT * FROM " + viewName) + " LIMIT 2"));
        } finally {
            BaseTestQuery.test(("DROP VIEW " + viewName));
        }
    }

    @Test
    public void testShowFilesInWSWithUserAndGroupPermissionsForQueryUser() throws Exception {
        BaseTestQuery.updateClient(TestImpersonationMetadata.user1);
        // Try show tables in schema "drill_test_grp_1_700" which is owned by "user1"
        int count = BaseTestQuery.testSql(String.format("SHOW FILES IN %s.drill_test_grp_1_700", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME));
        Assert.assertTrue((count > 0));
        // Try show tables in schema "drill_test_grp_0_750" which is owned by "processUser" and has group permissions for "user1"
        count = BaseTestQuery.testSql(String.format("SHOW FILES IN %s.drill_test_grp_0_750", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME));
        Assert.assertTrue((count > 0));
    }

    @Test
    public void testShowFilesInWSWithOtherPermissionsForQueryUser() throws Exception {
        BaseTestQuery.updateClient(TestImpersonationMetadata.user2);
        // Try show tables in schema "drill_test_grp_0_755" which is owned by "processUser" and group0. "user2" is not part of the "group0"
        int count = BaseTestQuery.testSql(String.format("SHOW FILES IN %s.drill_test_grp_0_755", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME));
        Assert.assertTrue((count > 0));
    }

    @Test
    public void testShowFilesInWSWithNoPermissionsForQueryUser() throws Exception {
        BaseTestQuery.updateClient(TestImpersonationMetadata.user2);
        // Try show tables in schema "drill_test_grp_1_700" which is owned by "user1"
        int count = BaseTestQuery.testSql(String.format("SHOW FILES IN %s.drill_test_grp_1_700", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME));
        Assert.assertEquals(0, count);
    }

    @Test
    public void testShowSchemasAsUser1() throws Exception {
        // "user1" is part of "group0" and has access to following workspaces
        // drill_test_grp_1_700 (through ownership)
        // drill_test_grp_0_750, drill_test_grp_0_770 (through "group" category permissions)
        // drill_test_grp_0_755, drill_test_grp_0_777 (through "others" category permissions)
        BaseTestQuery.updateClient(TestImpersonationMetadata.user1);
        BaseTestQuery.testBuilder().sqlQuery("SHOW SCHEMAS LIKE '%drill_test%'").unOrdered().baselineColumns("SCHEMA_NAME").baselineValues(String.format("%s.drill_test_grp_0_750", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME)).baselineValues(String.format("%s.drill_test_grp_0_755", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME)).baselineValues(String.format("%s.drill_test_grp_0_770", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME)).baselineValues(String.format("%s.drill_test_grp_0_777", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME)).baselineValues(String.format("%s.drill_test_grp_1_700", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME)).go();
    }

    @Test
    public void testShowSchemasAsUser2() throws Exception {
        // "user2" is part of "group0", but part of "group1" and has access to following workspaces
        // drill_test_grp_0_755, drill_test_grp_0_777 (through "others" category permissions)
        BaseTestQuery.updateClient(TestImpersonationMetadata.user2);
        BaseTestQuery.testBuilder().sqlQuery("SHOW SCHEMAS LIKE '%drill_test%'").unOrdered().baselineColumns("SCHEMA_NAME").baselineValues(String.format("%s.drill_test_grp_0_755", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME)).baselineValues(String.format("%s.drill_test_grp_0_777", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME)).go();
    }

    @Test
    public void testCreateViewInDirWithUserPermissionsForQueryUser() throws Exception {
        final String viewSchema = (BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME) + ".drill_test_grp_1_700";// Workspace dir owned by "user1"

        TestImpersonationMetadata.testCreateViewTestHelper(TestImpersonationMetadata.user1, viewSchema, "view1");
    }

    @Test
    public void testCreateViewInDirWithGroupPermissionsForQueryUser() throws Exception {
        // Workspace dir owned by "processUser", workspace group is "group0" and "user1" is part of "group0"
        final String viewSchema = (BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME) + ".drill_test_grp_0_770";
        TestImpersonationMetadata.testCreateViewTestHelper(TestImpersonationMetadata.user1, viewSchema, "view1");
    }

    @Test
    public void testCreateViewInDirWithOtherPermissionsForQueryUser() throws Exception {
        // Workspace dir owned by "processUser", workspace group is "group0" and "user2" is not part of "group0"
        final String viewSchema = (BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME) + ".drill_test_grp_0_777";
        TestImpersonationMetadata.testCreateViewTestHelper(TestImpersonationMetadata.user2, viewSchema, "view1");
    }

    @Test
    public void testCreateViewInWSWithNoPermissionsForQueryUser() throws Exception {
        // Workspace dir owned by "processUser", workspace group is "group0" and "user2" is not part of "group0"
        final String viewSchema = (BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME) + ".drill_test_grp_0_755";
        final String viewName = "view1";
        BaseTestQuery.updateClient(TestImpersonationMetadata.user2);
        BaseTestQuery.test(("USE " + viewSchema));
        final String query = (("CREATE VIEW " + viewName) + " AS SELECT ") + "c_custkey, c_nationkey FROM cp.`tpch/customer.parquet` ORDER BY c_custkey;";
        final String expErrorMsg = "PERMISSION ERROR: Permission denied: user=drillTestUser2, access=WRITE, inode=\"/drill_test_grp_0_755";
        BaseTestQuery.errorMsgTestHelper(query, expErrorMsg);
        // SHOW TABLES is expected to return no records as view creation fails above.
        BaseTestQuery.testBuilder().sqlQuery("SHOW TABLES").expectsEmptyResultSet().go();
        BaseTestQuery.test("SHOW FILES");
    }

    @Test
    public void testCreateTableInDirWithUserPermissionsForQueryUser() throws Exception {
        final String tableWS = "drill_test_grp_1_700";// Workspace dir owned by "user1"

        TestImpersonationMetadata.testCreateTableTestHelper(TestImpersonationMetadata.user1, tableWS, "table1");
    }

    @Test
    public void testCreateTableInDirWithGroupPermissionsForQueryUser() throws Exception {
        // Workspace dir owned by "processUser", workspace group is "group0" and "user1" is part of "group0"
        final String tableWS = "drill_test_grp_0_770";
        TestImpersonationMetadata.testCreateTableTestHelper(TestImpersonationMetadata.user1, tableWS, "table1");
    }

    @Test
    public void testCreateTableInDirWithOtherPermissionsForQueryUser() throws Exception {
        // Workspace dir owned by "processUser", workspace group is "group0" and "user2" is not part of "group0"
        final String tableWS = "drill_test_grp_0_777";
        TestImpersonationMetadata.testCreateTableTestHelper(TestImpersonationMetadata.user2, tableWS, "table1");
    }

    @Test
    public void testCreateTableInWSWithNoPermissionsForQueryUser() throws Exception {
        // Workspace dir owned by "processUser", workspace group is "group0" and "user2" is not part of "group0"
        String tableWS = "drill_test_grp_0_755";
        String tableName = "table1";
        BaseTestQuery.updateClient(TestImpersonationMetadata.user2);
        BaseTestQuery.test("use %s.`%s`", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME, tableWS);
        thrown.expect(UserRemoteException.class);
        thrown.expectMessage(StringContains.containsString(("Permission denied: user=drillTestUser2, " + "access=WRITE, inode=\"/drill_test_grp_0_755")));
        BaseTestQuery.test(("CREATE TABLE %s AS SELECT c_custkey, c_nationkey " + "FROM cp.`tpch/customer.parquet` ORDER BY c_custkey"), tableName);
    }

    @Test
    public void testRefreshMetadata() throws Exception {
        final String tableName = "nation1";
        final String tableWS = "drill_test_grp_1_700";
        BaseTestQuery.updateClient(TestImpersonationMetadata.user1);
        BaseTestQuery.test(("USE " + (Joiner.on(".").join(BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME, tableWS))));
        BaseTestQuery.test(((("CREATE TABLE " + tableName) + " partition by (n_regionkey) AS SELECT * ") + "FROM cp.`tpch/nation.parquet`;"));
        BaseTestQuery.test((("refresh table metadata " + tableName) + ";"));
        BaseTestQuery.test((("SELECT * FROM " + tableName) + ";"));
        final Path tablePath = new Path(((((Path.SEPARATOR) + tableWS) + (Path.SEPARATOR)) + tableName));
        Assert.assertTrue(((BaseTestImpersonation.fs.exists(tablePath)) && (BaseTestImpersonation.fs.isDirectory(tablePath))));
        BaseTestImpersonation.fs.mkdirs(new Path(tablePath, "tmp5"));
        BaseTestQuery.test((("SELECT * from " + tableName) + ";"));
    }

    @Test
    public void testAnalyzeTable() throws Exception {
        final String tableName = "nation1_stats";
        final String tableWS = "drill_test_grp_1_700";
        BaseTestQuery.updateClient(TestImpersonationMetadata.user1);
        BaseTestQuery.test(("USE " + (Joiner.on(".").join(BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME, tableWS))));
        BaseTestQuery.test("ALTER SESSION SET `store.format` = 'parquet'");
        BaseTestQuery.test((("CREATE TABLE " + tableName) + " AS SELECT * FROM cp.`tpch/nation.parquet`;"));
        BaseTestQuery.test((("ANALYZE TABLE " + tableName) + " COMPUTE STATISTICS;"));
        BaseTestQuery.test((("SELECT * FROM " + tableName) + ";"));
        final Path statsFilePath = new Path(((((((Path.SEPARATOR) + tableWS) + (Path.SEPARATOR)) + tableName) + (Path.SEPARATOR)) + (STATS.getEnding())));
        Assert.assertTrue(((BaseTestImpersonation.fs.exists(statsFilePath)) && (BaseTestImpersonation.fs.isDirectory(statsFilePath))));
        FileStatus status = BaseTestImpersonation.fs.getFileStatus(statsFilePath);
        // Verify process user is the directory owner
        assert BaseTestImpersonation.processUser.equalsIgnoreCase(status.getOwner());
        BaseTestImpersonation.fs.mkdirs(new Path(statsFilePath, "tmp5"));
        BaseTestQuery.test((("SELECT * from " + tableName) + ";"));
        BaseTestQuery.test(("DROP TABLE " + tableName));
    }
}

