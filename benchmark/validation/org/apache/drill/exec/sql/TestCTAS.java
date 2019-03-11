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
package org.apache.drill.exec.sql;


import ExecConstants.PERSISTENT_TABLE_UMASK;
import UserBitShared.SerializedField;
import java.util.Map;
import org.apache.drill.categories.SqlTest;
import org.apache.drill.categories.UnlikelyTest;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.proto.UserBitShared;
import org.apache.drill.exec.rpc.user.QueryDataBatch;
import org.apache.drill.exec.store.StorageStrategy;
import org.apache.drill.exec.util.StoragePluginTestUtils;
import org.apache.drill.shaded.guava.com.google.common.collect.Maps;
import org.apache.drill.test.BaseTestQuery;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SqlTest.class)
public class TestCTAS extends BaseTestQuery {
    // DRILL-2589
    @Test
    @Category(UnlikelyTest.class)
    public void withDuplicateColumnsInDef1() throws Exception {
        TestCTAS.ctasErrorTestHelper("CREATE TABLE dfs.tmp.%s AS SELECT region_id, region_id FROM cp.`region.json`", String.format("Duplicate column name [%s]", "region_id"));
    }

    // DRILL-2589
    @Test
    @Category(UnlikelyTest.class)
    public void withDuplicateColumnsInDef2() throws Exception {
        TestCTAS.ctasErrorTestHelper("CREATE TABLE dfs.tmp.%s AS SELECT region_id, sales_city, sales_city FROM cp.`region.json`", String.format("Duplicate column name [%s]", "sales_city"));
    }

    // DRILL-2589
    @Test
    @Category(UnlikelyTest.class)
    public void withDuplicateColumnsInDef3() throws Exception {
        TestCTAS.ctasErrorTestHelper(("CREATE TABLE dfs.tmp.%s(regionid, regionid) " + "AS SELECT region_id, sales_city FROM cp.`region.json`"), String.format("Duplicate column name [%s]", "regionid"));
    }

    // DRILL-2589
    @Test
    @Category(UnlikelyTest.class)
    public void withDuplicateColumnsInDef4() throws Exception {
        TestCTAS.ctasErrorTestHelper(("CREATE TABLE dfs.tmp.%s(regionid, salescity, salescity) " + "AS SELECT region_id, sales_city, sales_city FROM cp.`region.json`"), String.format("Duplicate column name [%s]", "salescity"));
    }

    // DRILL-2589
    @Test
    @Category(UnlikelyTest.class)
    public void withDuplicateColumnsInDef5() throws Exception {
        TestCTAS.ctasErrorTestHelper(("CREATE TABLE dfs.tmp.%s(regionid, salescity, SalesCity) " + "AS SELECT region_id, sales_city, sales_city FROM cp.`region.json`"), String.format("Duplicate column name [%s]", "SalesCity"));
    }

    // DRILL-2589
    @Test
    public void whenInEqualColumnCountInTableDefVsInTableQuery() throws Exception {
        TestCTAS.ctasErrorTestHelper(("CREATE TABLE dfs.tmp.%s(regionid, salescity) " + "AS SELECT region_id, sales_city, sales_region FROM cp.`region.json`"), "table's field list and the table's query field list have different counts.");
    }

    // DRILL-2589
    @Test
    public void whenTableQueryColumnHasStarAndTableFiledListIsSpecified() throws Exception {
        TestCTAS.ctasErrorTestHelper(("CREATE TABLE dfs.tmp.%s(regionid, salescity) " + "AS SELECT region_id, * FROM cp.`region.json`"), "table's query field list has a '*', which is invalid when table's field list is specified.");
    }

    // DRILL-2422
    @Test
    @Category(UnlikelyTest.class)
    public void createTableWhenATableWithSameNameAlreadyExists() throws Exception {
        final String newTblName = "createTableWhenTableAlreadyExists";
        final String ctasQuery = String.format("CREATE TABLE dfs.tmp.%s AS SELECT * from cp.`region.json`", newTblName);
        BaseTestQuery.test(ctasQuery);
        BaseTestQuery.errorMsgTestHelper(ctasQuery, String.format("A table or view with given name [%s] already exists in schema [dfs.tmp]", newTblName));
    }

    // DRILL-2422
    @Test
    @Category(UnlikelyTest.class)
    public void createTableWhenAViewWithSameNameAlreadyExists() throws Exception {
        final String newTblName = "createTableWhenAViewWithSameNameAlreadyExists";
        try {
            BaseTestQuery.test("CREATE VIEW dfs.tmp.%s AS SELECT * from cp.`region.json`", newTblName);
            final String ctasQuery = String.format("CREATE TABLE dfs.tmp.%s AS SELECT * FROM cp.`employee.json`", newTblName);
            BaseTestQuery.errorMsgTestHelper(ctasQuery, String.format("A table or view with given name [%s] already exists in schema [%s]", newTblName, "dfs.tmp"));
        } finally {
            BaseTestQuery.test("DROP VIEW dfs.tmp.%s", newTblName);
        }
    }

    @Test
    public void ctasPartitionWithEmptyList() throws Exception {
        final String newTblName = "ctasPartitionWithEmptyList";
        final String ctasQuery = String.format("CREATE TABLE dfs.tmp.%s PARTITION BY AS SELECT * from cp.`region.json`", newTblName);
        BaseTestQuery.errorMsgTestHelper(ctasQuery, "PARSE ERROR: Encountered \"AS\"");
    }

    // DRILL-3377
    @Test
    public void partitionByCtasColList() throws Exception {
        final String newTblName = "partitionByCtasColList";
        BaseTestQuery.test(("CREATE TABLE dfs.tmp.%s (cnt, rkey) PARTITION BY (cnt) " + "AS SELECT count(*), n_regionkey from cp.`tpch/nation.parquet` group by n_regionkey"), newTblName);
        BaseTestQuery.testBuilder().sqlQuery("select cnt, rkey from dfs.tmp.%s", newTblName).unOrdered().sqlBaselineQuery("select count(*) as cnt, n_regionkey as rkey from cp.`tpch/nation.parquet` group by n_regionkey").build().run();
    }

    // DRILL-3374
    @Test
    public void partitionByCtasFromView() throws Exception {
        final String newTblName = "partitionByCtasFromView";
        final String newView = "partitionByCtasColListView";
        BaseTestQuery.test(("create or replace view dfs.tmp.%s (col_int, col_varchar)  " + "AS select cast(n_nationkey as int), cast(n_name as varchar(30)) from cp.`tpch/nation.parquet`"), newView);
        BaseTestQuery.test("CREATE TABLE dfs.tmp.%s PARTITION BY (col_int) AS SELECT * from dfs.tmp.%s", newTblName, newView);
        BaseTestQuery.testBuilder().sqlQuery("select col_int, col_varchar from dfs.tmp.%s", newTblName).unOrdered().sqlBaselineQuery(("select cast(n_nationkey as int) as col_int, cast(n_name as varchar(30)) as col_varchar " + "from cp.`tpch/nation.parquet`")).build().run();
        BaseTestQuery.test("DROP VIEW dfs.tmp.%s", newView);
    }

    // DRILL-3382
    @Test
    public void ctasWithQueryOrderby() throws Exception {
        final String newTblName = "ctasWithQueryOrderby";
        BaseTestQuery.test(("CREATE TABLE dfs.tmp.%s AS SELECT n_nationkey, n_name, n_comment from " + "cp.`tpch/nation.parquet` order by n_nationkey"), newTblName);
        BaseTestQuery.testBuilder().sqlQuery("select n_nationkey, n_name, n_comment from dfs.tmp.%s", newTblName).ordered().sqlBaselineQuery("select n_nationkey, n_name, n_comment from cp.`tpch/nation.parquet` order by n_nationkey").build().run();
    }

    // DRILL-4392
    @Test
    public void ctasWithPartition() throws Exception {
        final String newTblName = "nation_ctas";
        BaseTestQuery.test(("CREATE TABLE dfs.tmp.%s partition by (n_regionkey) AS " + "SELECT n_nationkey, n_regionkey from cp.`tpch/nation.parquet` order by n_nationkey limit 1"), newTblName);
        BaseTestQuery.testBuilder().sqlQuery("select * from dfs.tmp.%s", newTblName).ordered().sqlBaselineQuery("select n_nationkey, n_regionkey from cp.`tpch/nation.parquet` order by n_nationkey limit 1").build().run();
    }

    @Test
    public void testPartitionByForAllTypes() throws Exception {
        final String location = "partitioned_tables_with_nulls";
        final String ctasQuery = "create table %s partition by (%s) as %s";
        final String tablePath = "dfs.tmp.`%s/%s_%s`";
        // key - new table suffix, value - data query
        final Map<String, String> variations = Maps.newHashMap();
        variations.put("required", "select * from cp.`parquet/alltypes_required.parquet`");
        variations.put("optional", "select * from cp.`parquet/alltypes_optional.parquet`");
        variations.put("nulls_only", "select * from cp.`parquet/alltypes_optional.parquet` where %s is null");
        final QueryDataBatch result = BaseTestQuery.testSqlWithResults("select * from cp.`parquet/alltypes_required.parquet` limit 0").get(0);
        for (UserBitShared.SerializedField field : result.getHeader().getDef().getFieldList()) {
            final String fieldName = field.getNamePart().getName();
            for (Map.Entry<String, String> variation : variations.entrySet()) {
                final String table = String.format(tablePath, location, fieldName, variation.getKey());
                final String dataQuery = String.format(variation.getValue(), fieldName);
                BaseTestQuery.test(ctasQuery, table, fieldName, dataQuery, fieldName);
                BaseTestQuery.testBuilder().sqlQuery("select * from %s", table).unOrdered().sqlBaselineQuery(dataQuery).build().run();
            }
        }
        result.release();
    }

    @Test
    public void createTableWithCustomUmask() throws Exception {
        BaseTestQuery.test("use dfs.tmp");
        String tableName = "with_custom_permission";
        StorageStrategy storageStrategy = new StorageStrategy("000", false);
        FileSystem fs = ExecTest.getLocalFileSystem();
        try {
            BaseTestQuery.test("alter session set `%s` = '%s'", PERSISTENT_TABLE_UMASK, storageStrategy.getUmask());
            BaseTestQuery.test("create table %s as select 'A' from (values(1))", tableName);
            Path tableLocation = new Path(ExecTest.dirTestWatcher.getDfsTestTmpDir().getAbsolutePath(), tableName);
            Assert.assertEquals("Directory permission should match", storageStrategy.getFolderPermission(), fs.getFileStatus(tableLocation).getPermission());
            Assert.assertEquals("File permission should match", storageStrategy.getFilePermission(), fs.listLocatedStatus(tableLocation).next().getPermission());
        } finally {
            BaseTestQuery.test("alter session reset `%s`", PERSISTENT_TABLE_UMASK);
            BaseTestQuery.test("drop table if exists %s", tableName);
        }
    }

    // DRILL-5952
    @Test
    public void testCreateTableIfNotExistsWhenTableWithSameNameAlreadyExists() throws Exception {
        final String newTblName = "createTableIfNotExistsWhenATableWithSameNameAlreadyExists";
        try {
            String ctasQuery = String.format("CREATE TABLE %s.%s AS SELECT * from cp.`region.json`", StoragePluginTestUtils.DFS_TMP_SCHEMA, newTblName);
            BaseTestQuery.test(ctasQuery);
            ctasQuery = String.format("CREATE TABLE IF NOT EXISTS %s.%s AS SELECT * FROM cp.`employee.json`", StoragePluginTestUtils.DFS_TMP_SCHEMA, newTblName);
            BaseTestQuery.testBuilder().sqlQuery(ctasQuery).unOrdered().baselineColumns("ok", "summary").baselineValues(false, String.format("A table or view with given name [%s] already exists in schema [%s]", newTblName, StoragePluginTestUtils.DFS_TMP_SCHEMA)).go();
        } finally {
            BaseTestQuery.test("DROP TABLE IF EXISTS %s.%s", StoragePluginTestUtils.DFS_TMP_SCHEMA, newTblName);
        }
    }

    // DRILL-5952
    @Test
    public void testCreateTableIfNotExistsWhenViewWithSameNameAlreadyExists() throws Exception {
        final String newTblName = "createTableIfNotExistsWhenAViewWithSameNameAlreadyExists";
        try {
            String ctasQuery = String.format("CREATE VIEW %s.%s AS SELECT * from cp.`region.json`", StoragePluginTestUtils.DFS_TMP_SCHEMA, newTblName);
            BaseTestQuery.test(ctasQuery);
            ctasQuery = String.format("CREATE TABLE IF NOT EXISTS %s.%s AS SELECT * FROM cp.`employee.json`", StoragePluginTestUtils.DFS_TMP_SCHEMA, newTblName);
            BaseTestQuery.testBuilder().sqlQuery(ctasQuery).unOrdered().baselineColumns("ok", "summary").baselineValues(false, String.format("A table or view with given name [%s] already exists in schema [%s]", newTblName, StoragePluginTestUtils.DFS_TMP_SCHEMA)).go();
        } finally {
            BaseTestQuery.test("DROP VIEW IF EXISTS %s.%s", StoragePluginTestUtils.DFS_TMP_SCHEMA, newTblName);
        }
    }

    // DRILL-5952
    @Test
    public void testCreateTableIfNotExistsWhenTableWithSameNameDoesNotExist() throws Exception {
        final String newTblName = "createTableIfNotExistsWhenATableWithSameNameDoesNotExist";
        try {
            String ctasQuery = String.format("CREATE TABLE IF NOT EXISTS %s.%s AS SELECT * FROM cp.`employee.json`", StoragePluginTestUtils.DFS_TMP_SCHEMA, newTblName);
            BaseTestQuery.test(ctasQuery);
        } finally {
            BaseTestQuery.test("DROP TABLE IF EXISTS %s.%s", StoragePluginTestUtils.DFS_TMP_SCHEMA, newTblName);
        }
    }

    @Test
    public void testCTASWithEmptyJson() throws Exception {
        final String newTblName = "tbl4444";
        try {
            BaseTestQuery.test(String.format("CREATE TABLE %s.%s AS SELECT * FROM cp.`project/pushdown/empty.json`", StoragePluginTestUtils.DFS_TMP_SCHEMA, newTblName));
        } finally {
            BaseTestQuery.test("DROP TABLE IF EXISTS %s.%s", StoragePluginTestUtils.DFS_TMP_SCHEMA, newTblName);
        }
    }

    @Test
    public void testTableIsCreatedWithinWorkspace() throws Exception {
        String tableName = "table_created_within_workspace";
        try {
            BaseTestQuery.test("CREATE TABLE `%s`.`%s` AS SELECT * FROM cp.`region.json`", StoragePluginTestUtils.DFS_TMP_SCHEMA, ("/" + tableName));
            BaseTestQuery.testBuilder().sqlQuery("SELECT region_id FROM `%s`.`%s` LIMIT 1", StoragePluginTestUtils.DFS_TMP_SCHEMA, tableName).unOrdered().baselineColumns("region_id").baselineValues(0L).go();
        } finally {
            BaseTestQuery.test("DROP TABLE IF EXISTS `%s`.`%s`", StoragePluginTestUtils.DFS_TMP_SCHEMA, tableName);
        }
    }

    @Test
    public void testTableIsFoundWithinWorkspaceWhenNameStartsWithSlash() throws Exception {
        String tableName = "table_found_within_workspace";
        try {
            BaseTestQuery.test("CREATE TABLE `%s`.`%s` AS SELECT * FROM cp.`region.json`", StoragePluginTestUtils.DFS_TMP_SCHEMA, tableName);
            BaseTestQuery.testBuilder().sqlQuery("SELECT region_id FROM `%s`.`%s` LIMIT 1", StoragePluginTestUtils.DFS_TMP_SCHEMA, ("/" + tableName)).unOrdered().baselineColumns("region_id").baselineValues(0L).go();
        } finally {
            BaseTestQuery.test("DROP TABLE IF EXISTS `%s`.`%s`", StoragePluginTestUtils.DFS_TMP_SCHEMA, tableName);
        }
    }
}

