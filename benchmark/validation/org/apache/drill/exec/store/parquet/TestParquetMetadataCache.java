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
package org.apache.drill.exec.store.parquet;


import Metadata.METADATA_DIRECTORIES_FILENAME;
import Metadata.METADATA_FILENAME;
import MetadataVersion.Constants.SUPPORTED_VERSIONS;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.apache.drill.PlanTestBase;
import org.apache.drill.categories.UnlikelyTest;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.store.parquet.metadata.Metadata;
import org.apache.drill.exec.store.parquet.metadata.MetadataVersion;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


public class TestParquetMetadataCache extends PlanTestBase {
    private static final String TABLE_NAME_1 = "parquetTable1";

    private static final String TABLE_NAME_2 = "parquetTable2";

    @Test
    public void testPartitionPruningWithMetadataCache_1() throws Exception {
        BaseTestQuery.test("refresh table metadata dfs.`%s`", TestParquetMetadataCache.TABLE_NAME_1);
        checkForMetadataFile(TestParquetMetadataCache.TABLE_NAME_1);
        String query = String.format(("select dir0, dir1, o_custkey, o_orderdate from dfs.`%s` " + " where dir0=1994 and dir1 in ('Q1', 'Q2')"), TestParquetMetadataCache.TABLE_NAME_1);
        int expectedRowCount = 20;
        int expectedNumFiles = 2;
        int actualRowCount = BaseTestQuery.testSql(query);
        Assert.assertEquals(expectedRowCount, actualRowCount);
        String numFilesPattern = "numFiles=" + expectedNumFiles;
        String usedMetaPattern = "usedMetadataFile=true";
        String cacheFileRootPattern = String.format("cacheFileRoot=%s/%s/1994", ExecTest.dirTestWatcher.getRootDir().getCanonicalPath(), TestParquetMetadataCache.TABLE_NAME_1);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ numFilesPattern, usedMetaPattern, cacheFileRootPattern }, new String[]{  });
    }

    // DRILL-3917, positive test case for DRILL-4530
    @Test
    @Category(UnlikelyTest.class)
    public void testPartitionPruningWithMetadataCache_2() throws Exception {
        BaseTestQuery.test("refresh table metadata dfs.`%s`", TestParquetMetadataCache.TABLE_NAME_1);
        checkForMetadataFile(TestParquetMetadataCache.TABLE_NAME_1);
        String query = String.format(("select dir0, dir1, o_custkey, o_orderdate from dfs.`%s` " + " where dir0=1994"), TestParquetMetadataCache.TABLE_NAME_1);
        int expectedRowCount = 40;
        int expectedNumFiles = 4;
        int actualRowCount = BaseTestQuery.testSql(query);
        Assert.assertEquals(expectedRowCount, actualRowCount);
        String numFilesPattern = "numFiles=" + expectedNumFiles;
        String usedMetaPattern = "usedMetadataFile=true";
        String cacheFileRootPattern = String.format("cacheFileRoot=%s/%s/1994", ExecTest.dirTestWatcher.getRootDir().getCanonicalPath(), TestParquetMetadataCache.TABLE_NAME_1);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ numFilesPattern, usedMetaPattern, cacheFileRootPattern }, new String[]{ "Filter" });
    }

    // DRILL-3937 (partitioning column is varchar)
    @Test
    @Category(UnlikelyTest.class)
    public void testPartitionPruningWithMetadataCache_3() throws Exception {
        String tableName = "orders_ctas_varchar";
        BaseTestQuery.test("use dfs");
        BaseTestQuery.test(("create table %s (o_orderdate, o_orderpriority) partition by (o_orderpriority) " + "as select o_orderdate, o_orderpriority from dfs.`multilevel/parquet/1994/Q1`"), tableName);
        BaseTestQuery.test("refresh table metadata %s", tableName);
        checkForMetadataFile(tableName);
        String query = String.format("select * from %s where o_orderpriority = '1-URGENT'", tableName);
        int expectedRowCount = 3;
        int expectedNumFiles = 1;
        int actualRowCount = BaseTestQuery.testSql(query);
        Assert.assertEquals(expectedRowCount, actualRowCount);
        String numFilesPattern = "numFiles=" + expectedNumFiles;
        String usedMetaPattern = "usedMetadataFile=true";
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ numFilesPattern, usedMetaPattern }, new String[]{  });
    }

    // DRILL-3937 (partitioning column is binary using convert_to)
    @Test
    @Category(UnlikelyTest.class)
    public void testPartitionPruningWithMetadataCache_4() throws Exception {
        String tableName = "orders_ctas_binary";
        BaseTestQuery.test("use dfs");
        BaseTestQuery.test(("create table %s (o_orderdate, o_orderpriority) partition by (o_orderpriority) " + ("as select o_orderdate, convert_to(o_orderpriority, 'UTF8') as o_orderpriority " + "from dfs.`multilevel/parquet/1994/Q1`")), tableName);
        BaseTestQuery.test("refresh table metadata %s", tableName);
        checkForMetadataFile(tableName);
        String query = String.format("select * from %s where o_orderpriority = '1-URGENT'", tableName);
        int expectedRowCount = 3;
        int expectedNumFiles = 1;
        int actualRowCount = BaseTestQuery.testSql(query);
        Assert.assertEquals(expectedRowCount, actualRowCount);
        String numFilesPattern = "numFiles=" + expectedNumFiles;
        String usedMetaPattern = "usedMetadataFile=true";
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ numFilesPattern, usedMetaPattern }, new String[]{  });
    }

    @Test
    public void testCache() throws Exception {
        String tableName = "nation_ctas";
        BaseTestQuery.test("use dfs");
        BaseTestQuery.test("create table `%s/t1` as select * from cp.`tpch/nation.parquet`", tableName);
        BaseTestQuery.test("create table `%s/t2` as select * from cp.`tpch/nation.parquet`", tableName);
        BaseTestQuery.test("refresh table metadata %s", tableName);
        checkForMetadataFile(tableName);
        String query = String.format("select * from %s", tableName);
        int rowCount = BaseTestQuery.testSql(query);
        Assert.assertEquals(50, rowCount);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "usedMetadataFile=true" }, new String[]{  });
    }

    @Test
    public void testUpdate() throws Exception {
        String tableName = "nation_ctas_update";
        BaseTestQuery.test("use dfs");
        BaseTestQuery.test("create table `%s/t1` as select * from cp.`tpch/nation.parquet`", tableName);
        BaseTestQuery.test("refresh table metadata %s", tableName);
        checkForMetadataFile(tableName);
        Thread.sleep(1000);
        BaseTestQuery.test("create table `%s/t2` as select * from cp.`tpch/nation.parquet`", tableName);
        int rowCount = BaseTestQuery.testSql(String.format("select * from %s", tableName));
        Assert.assertEquals(50, rowCount);
    }

    @Test
    public void testCacheWithSubschema() throws Exception {
        String tableName = "nation_ctas_subschema";
        BaseTestQuery.test("create table dfs.`%s/t1` as select * from cp.`tpch/nation.parquet`", tableName);
        BaseTestQuery.test("refresh table metadata dfs.%s", tableName);
        checkForMetadataFile(tableName);
        int rowCount = BaseTestQuery.testSql(String.format("select * from dfs.%s", tableName));
        Assert.assertEquals(25, rowCount);
    }

    @Test
    @Category(UnlikelyTest.class)
    public void testFix4449() throws Exception {
        BaseTestQuery.runSQL("CREATE TABLE dfs.tmp.`4449` PARTITION BY(l_discount) AS SELECT l_orderkey, l_discount FROM cp.`tpch/lineitem.parquet`");
        BaseTestQuery.runSQL("REFRESH TABLE METADATA dfs.tmp.`4449`");
        BaseTestQuery.testBuilder().sqlQuery(("SELECT COUNT(*) cnt FROM (" + (("SELECT l_orderkey FROM dfs.tmp.`4449` WHERE l_discount < 0.05" + " UNION ALL") + " SELECT l_orderkey FROM dfs.tmp.`4449` WHERE l_discount > 0.02)"))).unOrdered().baselineColumns("cnt").baselineValues(71159L).go();
    }

    @Test
    public void testAbsentPluginOrWorkspaceError() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("refresh table metadata dfs.incorrect.table_name").unOrdered().baselineColumns("ok", "summary").baselineValues(false, "Storage plugin or workspace does not exist [dfs.incorrect]").go();
        BaseTestQuery.testBuilder().sqlQuery("refresh table metadata incorrect.table_name").unOrdered().baselineColumns("ok", "summary").baselineValues(false, "Storage plugin or workspace does not exist [incorrect]").go();
    }

    // DRILL-4511
    @Test
    @Category(UnlikelyTest.class)
    public void testTableDoesNotExistWithEmptyDirectory() throws Exception {
        final String emptyDirName = "empty_directory";
        ExecTest.dirTestWatcher.makeTestTmpSubDir(Paths.get(emptyDirName));
        BaseTestQuery.testBuilder().sqlQuery("refresh table metadata dfs.tmp.`%s`", emptyDirName).unOrdered().baselineColumns("ok", "summary").baselineValues(false, String.format("Table %s is empty and doesn't contain any parquet files.", emptyDirName)).go();
    }

    // DRILL-4511
    @Test
    @Category(UnlikelyTest.class)
    public void testTableDoesNotExistWithIncorrectTableName() throws Exception {
        String tableName = "incorrect_table";
        BaseTestQuery.testBuilder().sqlQuery("refresh table metadata dfs.`%s`", tableName).unOrdered().baselineColumns("ok", "summary").baselineValues(false, String.format("Table %s does not exist.", tableName)).go();
    }

    @Test
    public void testNoSupportedError() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("refresh table metadata cp.`tpch/nation.parquet`").unOrdered().baselineColumns("ok", "summary").baselineValues(false, ("Table tpch/nation.parquet does not support metadata refresh. " + "Support is currently limited to directory-based Parquet tables.")).go();
    }

    // DRILL-4530  // single leaf level partition
    @Test
    @Category(UnlikelyTest.class)
    public void testDrill4530_1() throws Exception {
        // create metadata cache
        BaseTestQuery.test("refresh table metadata dfs.`%s`", TestParquetMetadataCache.TABLE_NAME_2);
        checkForMetadataFile(TestParquetMetadataCache.TABLE_NAME_2);
        // run query and check correctness
        String query1 = String.format(("select dir0, dir1, o_custkey, o_orderdate from dfs.`%s` " + " where dir0=1995 and dir1='Q3'"), TestParquetMetadataCache.TABLE_NAME_2);
        int expectedRowCount = 20;
        int expectedNumFiles = 2;
        int actualRowCount = BaseTestQuery.testSql(query1);
        Assert.assertEquals(expectedRowCount, actualRowCount);
        String numFilesPattern = "numFiles=" + expectedNumFiles;
        String usedMetaPattern = "usedMetadataFile=true";
        String cacheFileRootPattern = String.format("cacheFileRoot=%s/%s/1995/Q3", ExecTest.dirTestWatcher.getRootDir().getCanonicalPath(), TestParquetMetadataCache.TABLE_NAME_2);
        PlanTestBase.testPlanMatchingPatterns(query1, new String[]{ numFilesPattern, usedMetaPattern, cacheFileRootPattern }, new String[]{ "Filter" });
    }

    // DRILL-4530  // single non-leaf level partition
    @Test
    @Category(UnlikelyTest.class)
    public void testDrill4530_2() throws Exception {
        // create metadata cache
        BaseTestQuery.test("refresh table metadata dfs.`%s`", TestParquetMetadataCache.TABLE_NAME_2);
        checkForMetadataFile(TestParquetMetadataCache.TABLE_NAME_2);
        // run query and check correctness
        String query1 = String.format(("select dir0, dir1, o_custkey, o_orderdate from dfs.`%s` " + " where dir0=1995"), TestParquetMetadataCache.TABLE_NAME_2);
        int expectedRowCount = 80;
        int expectedNumFiles = 8;
        int actualRowCount = BaseTestQuery.testSql(query1);
        Assert.assertEquals(expectedRowCount, actualRowCount);
        String numFilesPattern = "numFiles=" + expectedNumFiles;
        String usedMetaPattern = "usedMetadataFile=true";
        String cacheFileRootPattern = String.format("cacheFileRoot=%s/%s/1995", ExecTest.dirTestWatcher.getRootDir().getCanonicalPath(), TestParquetMetadataCache.TABLE_NAME_2);
        PlanTestBase.testPlanMatchingPatterns(query1, new String[]{ numFilesPattern, usedMetaPattern, cacheFileRootPattern }, new String[]{ "Filter" });
    }

    // DRILL-4530  // only dir1 filter is present, no dir0, hence this maps to multiple partitions
    @Test
    @Category(UnlikelyTest.class)
    public void testDrill4530_3() throws Exception {
        // create metadata cache
        BaseTestQuery.test("refresh table metadata dfs.`%s`", TestParquetMetadataCache.TABLE_NAME_2);
        checkForMetadataFile(TestParquetMetadataCache.TABLE_NAME_2);
        // run query and check correctness
        String query1 = String.format(("select dir0, dir1, o_custkey, o_orderdate from dfs.`%s` " + " where dir1='Q3'"), TestParquetMetadataCache.TABLE_NAME_2);
        int expectedRowCount = 40;
        int expectedNumFiles = 4;
        int actualRowCount = BaseTestQuery.testSql(query1);
        Assert.assertEquals(expectedRowCount, actualRowCount);
        String numFilesPattern = "numFiles=" + expectedNumFiles;
        String usedMetaPattern = "usedMetadataFile=true";
        String cacheFileRootPattern = String.format("cacheFileRoot=%s/%s", ExecTest.dirTestWatcher.getRootDir().getCanonicalPath(), TestParquetMetadataCache.TABLE_NAME_2);
        PlanTestBase.testPlanMatchingPatterns(query1, new String[]{ numFilesPattern, usedMetaPattern, cacheFileRootPattern }, new String[]{  });
    }

    // DRILL-4530  // non-existent partition (1 subdirectory's cache file will still be read for schema)
    @Test
    @Category(UnlikelyTest.class)
    public void testDrill4530_4() throws Exception {
        // create metadata cache
        BaseTestQuery.test("refresh table metadata dfs.`%s`", TestParquetMetadataCache.TABLE_NAME_2);
        checkForMetadataFile(TestParquetMetadataCache.TABLE_NAME_2);
        // run query and check correctness
        String query1 = String.format(("select dir0, dir1, o_custkey, o_orderdate from dfs.`%s` " + " where dir0=1995 and dir1='Q6'"), TestParquetMetadataCache.TABLE_NAME_2);
        int expectedRowCount = 0;
        int expectedNumFiles = 1;
        int actualRowCount = BaseTestQuery.testSql(query1);
        Assert.assertEquals(expectedRowCount, actualRowCount);
        String numFilesPattern = "numFiles=" + expectedNumFiles;
        String usedMetaPattern = "usedMetadataFile=true";
        String cacheFileRootPattern = String.format("cacheFileRoot=%s/%s/*/*", ExecTest.dirTestWatcher.getRootDir().getCanonicalPath(), TestParquetMetadataCache.TABLE_NAME_2);
        PlanTestBase.testPlanMatchingPatterns(query1, new String[]{ numFilesPattern, usedMetaPattern, cacheFileRootPattern }, new String[]{  });
    }

    // DRILL-4794
    @Test
    @Category(UnlikelyTest.class)
    public void testDrill4794() throws Exception {
        BaseTestQuery.test("refresh table metadata dfs.`%s`", TestParquetMetadataCache.TABLE_NAME_1);
        checkForMetadataFile(TestParquetMetadataCache.TABLE_NAME_1);
        String query = String.format(("select dir0, dir1, o_custkey, o_orderdate from dfs.`%s` " + " where dir0=1994 or dir1='Q3'"), TestParquetMetadataCache.TABLE_NAME_1);
        int expectedRowCount = 60;
        int expectedNumFiles = 6;
        int actualRowCount = BaseTestQuery.testSql(query);
        Assert.assertEquals(expectedRowCount, actualRowCount);
        String numFilesPattern = "numFiles=" + expectedNumFiles;
        String usedMetaPattern = "usedMetadataFile=true";
        String cacheFileRootPattern = String.format("cacheFileRoot=%s/%s", ExecTest.dirTestWatcher.getRootDir().getCanonicalPath(), TestParquetMetadataCache.TABLE_NAME_1);
        PlanTestBase.testPlanMatchingPatterns(query, new String[]{ numFilesPattern, usedMetaPattern, cacheFileRootPattern }, new String[]{  });
    }

    // DRILL-4786
    @Test
    @Category(UnlikelyTest.class)
    public void testDrill4786_1() throws Exception {
        // create metadata cache
        BaseTestQuery.test("refresh table metadata dfs.`%s`", TestParquetMetadataCache.TABLE_NAME_2);
        checkForMetadataFile(TestParquetMetadataCache.TABLE_NAME_2);
        // run query and check correctness
        String query1 = String.format(("select dir0, dir1, o_custkey, o_orderdate from dfs.`%s` " + " where dir0=1995 and dir1 in ('Q1', 'Q2')"), TestParquetMetadataCache.TABLE_NAME_2);
        int expectedRowCount = 40;
        int expectedNumFiles = 4;
        int actualRowCount = BaseTestQuery.testSql(query1);
        Assert.assertEquals(expectedRowCount, actualRowCount);
        String numFilesPattern = "numFiles=" + expectedNumFiles;
        String usedMetaPattern = "usedMetadataFile=true";
        String cacheFileRootPattern = String.format("cacheFileRoot=%s/%s/1995", ExecTest.dirTestWatcher.getRootDir().getCanonicalPath(), TestParquetMetadataCache.TABLE_NAME_2);
        PlanTestBase.testPlanMatchingPatterns(query1, new String[]{ numFilesPattern, usedMetaPattern, cacheFileRootPattern }, new String[]{  });
    }

    // DRILL-4786
    @Test
    @Category(UnlikelyTest.class)
    public void testDrill4786_2() throws Exception {
        // create metadata cache
        BaseTestQuery.test("refresh table metadata dfs.`%s`", TestParquetMetadataCache.TABLE_NAME_2);
        checkForMetadataFile(TestParquetMetadataCache.TABLE_NAME_2);
        // run query and check correctness
        String query1 = String.format(("select dir0, dir1, o_custkey, o_orderdate from dfs.`%s` " + " where dir0 in (1994, 1995) and dir1 = 'Q3'"), TestParquetMetadataCache.TABLE_NAME_2);
        int expectedRowCount = 40;
        int expectedNumFiles = 4;
        int actualRowCount = BaseTestQuery.testSql(query1);
        Assert.assertEquals(expectedRowCount, actualRowCount);
        String numFilesPattern = "numFiles=" + expectedNumFiles;
        String usedMetaPattern = "usedMetadataFile=true";
        String cacheFileRootPattern = String.format("cacheFileRoot=%s/%s", ExecTest.dirTestWatcher.getRootDir().getCanonicalPath(), TestParquetMetadataCache.TABLE_NAME_2);
        PlanTestBase.testPlanMatchingPatterns(query1, new String[]{ numFilesPattern, usedMetaPattern, cacheFileRootPattern }, new String[]{  });
    }

    // DRILL-4877
    @Test
    @Category(UnlikelyTest.class)
    public void testDrill4877() throws Exception {
        // create metadata cache
        BaseTestQuery.test("refresh table metadata dfs.`%s`", TestParquetMetadataCache.TABLE_NAME_2);
        checkForMetadataFile(TestParquetMetadataCache.TABLE_NAME_2);
        // run query and check correctness
        String query1 = String.format("select max(dir0) as max0, max(dir1) as max1 from dfs.`%s` ", TestParquetMetadataCache.TABLE_NAME_2);
        BaseTestQuery.testBuilder().sqlQuery(query1).unOrdered().baselineColumns("max0", "max1").baselineValues("1995", "Q4").go();
        int expectedNumFiles = 1;// point to selectionRoot since no pruning is done in this query

        String numFilesPattern = "numFiles=" + expectedNumFiles;
        String usedMetaPattern = "usedMetadataFile=true";
        String cacheFileRootPattern = String.format("cacheFileRoot=%s/%s", ExecTest.dirTestWatcher.getRootDir().getCanonicalPath(), TestParquetMetadataCache.TABLE_NAME_2);
        PlanTestBase.testPlanMatchingPatterns(query1, new String[]{ numFilesPattern, usedMetaPattern, cacheFileRootPattern }, new String[]{  });
    }

    // DRILL-3867
    @Test
    public void testMoveCache() throws Exception {
        final String tableName = "nation_move";
        final String newTableName = "nation_moved";
        try {
            BaseTestQuery.test("use dfs");
            BaseTestQuery.test("create table `%s/t1` as select * from cp.`tpch/nation.parquet`", tableName);
            BaseTestQuery.test("create table `%s/t2` as select * from cp.`tpch/nation.parquet`", tableName);
            BaseTestQuery.test("refresh table metadata %s", tableName);
            checkForMetadataFile(tableName);
            File srcFile = new File(ExecTest.dirTestWatcher.getRootDir(), tableName);
            File dstFile = new File(ExecTest.dirTestWatcher.getRootDir(), newTableName);
            FileUtils.moveDirectory(srcFile, dstFile);
            Assert.assertFalse("Cache file was not moved successfully", srcFile.exists());
            int rowCount = BaseTestQuery.testSql(String.format("select * from %s", newTableName));
            Assert.assertEquals("An incorrect result was obtained while querying a table with metadata cache files", 50, rowCount);
        } finally {
            BaseTestQuery.test("drop table if exists %s", newTableName);
        }
    }

    @Test
    public void testOldMetadataVersions() throws Exception {
        final Path tablePath = Paths.get("absolute_paths_metadata");
        final Path rootMetadataPath = Paths.get("parquet", "metadata_files_with_old_versions");
        // gets folders with different metadata cache versions
        String[] metadataPaths = ExecTest.dirTestWatcher.getRootDir().toPath().resolve(rootMetadataPath).toFile().list();
        for (String metadataPath : metadataPaths) {
            try {
                BaseTestQuery.test("use dfs.tmp");
                // creating two inner directories to leverage METADATA_DIRECTORIES_FILENAME metadata file as well
                final Path absolutePathsMetadataT1 = tablePath.resolve("t1");
                final Path absolutePathsMetadataT2 = tablePath.resolve("t2");
                String createQuery = "create table `%s` as select * from cp.`tpch/nation.parquet`";
                BaseTestQuery.test(createQuery, absolutePathsMetadataT1);
                BaseTestQuery.test(createQuery, absolutePathsMetadataT2);
                Path relativePath = rootMetadataPath.resolve(metadataPath);
                File metaFile = ExecTest.dirTestWatcher.copyResourceToTestTmp(relativePath.resolve("metadata_directories.requires_replace.txt"), tablePath.resolve(METADATA_DIRECTORIES_FILENAME));
                ExecTest.dirTestWatcher.replaceMetaDataContents(metaFile, ExecTest.dirTestWatcher.getDfsTestTmpDir(), null);
                metaFile = ExecTest.dirTestWatcher.copyResourceToTestTmp(relativePath.resolve("metadata_table.requires_replace.txt"), tablePath.resolve(METADATA_FILENAME));
                ExecTest.dirTestWatcher.replaceMetaDataContents(metaFile, ExecTest.dirTestWatcher.getDfsTestTmpDir(), null);
                metaFile = ExecTest.dirTestWatcher.copyResourceToTestTmp(relativePath.resolve("metadata_table_t1.requires_replace.txt"), absolutePathsMetadataT1.resolve(METADATA_FILENAME));
                ExecTest.dirTestWatcher.replaceMetaDataContents(metaFile, ExecTest.dirTestWatcher.getDfsTestTmpDir(), null);
                metaFile = ExecTest.dirTestWatcher.copyResourceToTestTmp(relativePath.resolve("metadata_table_t2.requires_replace.txt"), absolutePathsMetadataT2.resolve(METADATA_FILENAME));
                ExecTest.dirTestWatcher.replaceMetaDataContents(metaFile, ExecTest.dirTestWatcher.getDfsTestTmpDir(), null);
                String query = String.format("select * from %s", tablePath);
                int expectedRowCount = 50;
                int expectedNumFiles = 1;// point to selectionRoot since no pruning is done in this query

                int actualRowCount = BaseTestQuery.testSql(query);
                Assert.assertEquals("An incorrect result was obtained while querying a table with metadata cache files", expectedRowCount, actualRowCount);
                String numFilesPattern = "numFiles=" + expectedNumFiles;
                String usedMetaPattern = "usedMetadataFile=true";
                String cacheFileRootPattern = String.format("cacheFileRoot=%s/%s", ExecTest.dirTestWatcher.getDfsTestTmpDir().getCanonicalPath(), tablePath);
                PlanTestBase.testPlanMatchingPatterns(query, new String[]{ numFilesPattern, usedMetaPattern, cacheFileRootPattern }, new String[]{ "Filter" });
            } finally {
                BaseTestQuery.test("drop table if exists %s", tablePath);
            }
        }
    }

    @Test
    public void testSpacesInMetadataCachePath() throws Exception {
        final String pathWithSpaces = "path with spaces";
        try {
            BaseTestQuery.test("use dfs");
            // creating multilevel table to store path with spaces in both metadata files (METADATA and METADATA_DIRECTORIES)
            BaseTestQuery.test("create table `%s` as select * from cp.`tpch/nation.parquet`", pathWithSpaces);
            BaseTestQuery.test("create table `%1$s/%1$s` as select * from cp.`tpch/nation.parquet`", pathWithSpaces);
            BaseTestQuery.test("refresh table metadata `%s`", pathWithSpaces);
            checkForMetadataFile(pathWithSpaces);
            String query = String.format("select * from `%s`", pathWithSpaces);
            int expectedRowCount = 50;
            int expectedNumFiles = 1;// point to selectionRoot since no pruning is done in this query

            int actualRowCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("An incorrect result was obtained while querying a table with metadata cache files", expectedRowCount, actualRowCount);
            String numFilesPattern = "numFiles=" + expectedNumFiles;
            String usedMetaPattern = "usedMetadataFile=true";
            String cacheFileRootPattern = String.format("cacheFileRoot=%s/%s", ExecTest.dirTestWatcher.getRootDir().getCanonicalPath(), pathWithSpaces);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ numFilesPattern, usedMetaPattern, cacheFileRootPattern }, new String[]{ "Filter" });
        } finally {
            BaseTestQuery.test("drop table if exists `%s`", pathWithSpaces);
        }
    }

    @Test
    public void testFutureUnsupportedMetadataVersion() throws Exception {
        final String unsupportedMetadataVersion = "unsupported_metadata_version";
        try {
            BaseTestQuery.test("use dfs.tmp");
            BaseTestQuery.test("create table `%s` as select * from cp.`tpch/nation.parquet`", unsupportedMetadataVersion);
            MetadataVersion lastVersion = SUPPORTED_VERSIONS.last();
            // Get the future version, which is absent in MetadataVersions.SUPPORTED_VERSIONS set
            String futureVersion = new MetadataVersion(((lastVersion.getMajor()) + 1), 0).toString();
            File metaDataFile = ExecTest.dirTestWatcher.copyResourceToTestTmp(Paths.get("parquet", "unsupported_metadata", "unsupported_metadata_version.requires_replace.txt"), Paths.get(unsupportedMetadataVersion, METADATA_FILENAME));
            ExecTest.dirTestWatcher.replaceMetaDataContents(metaDataFile, ExecTest.dirTestWatcher.getDfsTestTmpDir(), futureVersion);
            String query = String.format("select * from %s", unsupportedMetadataVersion);
            int expectedRowCount = 25;
            int expectedNumFiles = 1;
            int actualRowCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("An incorrect result was obtained while querying a table with metadata cache files", expectedRowCount, actualRowCount);
            String numFilesPattern = "numFiles=" + expectedNumFiles;
            String usedMetaPattern = "usedMetadataFile=false";// ignoring metadata cache file

            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ numFilesPattern, usedMetaPattern }, new String[]{ "Filter" });
        } finally {
            BaseTestQuery.test("drop table if exists %s", unsupportedMetadataVersion);
        }
    }

    @Test
    public void testCorruptedMetadataFile() throws Exception {
        final String corruptedMetadata = "corrupted_metadata";
        try {
            BaseTestQuery.test("use dfs.tmp");
            BaseTestQuery.test("create table `%s` as select * from cp.`tpch/nation.parquet`", corruptedMetadata);
            ExecTest.dirTestWatcher.copyResourceToTestTmp(Paths.get("parquet", "unsupported_metadata", "corrupted_metadata.requires_replace.txt"), Paths.get(corruptedMetadata, METADATA_FILENAME));
            String query = String.format("select * from %s", corruptedMetadata);
            int expectedRowCount = 25;
            int expectedNumFiles = 1;
            int actualRowCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("An incorrect result was obtained while querying a table with metadata cache files", expectedRowCount, actualRowCount);
            String numFilesPattern = "numFiles=" + expectedNumFiles;
            String usedMetaPattern = "usedMetadataFile=false";// ignoring metadata cache file

            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ numFilesPattern, usedMetaPattern }, new String[]{ "Filter" });
        } finally {
            BaseTestQuery.test("drop table if exists %s", corruptedMetadata);
        }
    }

    @Test
    public void testEmptyMetadataFile() throws Exception {
        final String emptyMetadataFile = "empty_metadata_file";
        try {
            BaseTestQuery.test("use dfs.tmp");
            BaseTestQuery.test("create table `%s` as select * from cp.`tpch/nation.parquet`", emptyMetadataFile);
            ExecTest.dirTestWatcher.copyResourceToTestTmp(Paths.get("parquet", "unsupported_metadata", "empty_metadata_file.requires_replace.txt"), Paths.get(emptyMetadataFile, METADATA_FILENAME));
            String query = String.format("select * from %s", emptyMetadataFile);
            int expectedRowCount = 25;
            int expectedNumFiles = 1;
            int actualRowCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("An incorrect result was obtained while querying a table with metadata cache files", expectedRowCount, actualRowCount);
            String numFilesPattern = "numFiles=" + expectedNumFiles;
            String usedMetaPattern = "usedMetadataFile=false";// ignoring metadata cache file

            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ numFilesPattern, usedMetaPattern }, new String[]{ "Filter" });
        } finally {
            BaseTestQuery.test("drop table if exists %s", emptyMetadataFile);
        }
    }

    @Test
    public void testRootMetadataFileIsAbsent() throws Exception {
        final String rootMetaCorruptedTable = "root_meta_corrupted_table";
        File dataDir = ExecTest.dirTestWatcher.copyResourceToRoot(Paths.get("multilevel", "parquet"), Paths.get(rootMetaCorruptedTable));
        try {
            BaseTestQuery.test("use dfs");
            BaseTestQuery.test("refresh table metadata `%s`", rootMetaCorruptedTable);
            checkForMetadataFile(rootMetaCorruptedTable);
            File rootMetadataFile = FileUtils.getFile(dataDir, METADATA_FILENAME);
            Assert.assertTrue(String.format("Metadata cache file '%s' isn't deleted", rootMetadataFile.getPath()), rootMetadataFile.delete());
            setTimestampToZero(dataDir);
            String query = String.format(("select dir0, dir1, o_custkey, o_orderdate from `%s` " + " where dir0=1994 or dir1='Q3'"), rootMetaCorruptedTable);
            int expectedRowCount = 60;
            int expectedNumFiles = 6;
            int actualRowCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("An incorrect result was obtained while querying a table with metadata cache files", expectedRowCount, actualRowCount);
            String numFilesPattern = "numFiles=" + expectedNumFiles;
            String usedMetaPattern = "usedMetadataFile=false";
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ numFilesPattern, usedMetaPattern }, new String[]{ "cacheFileRoot", "Filter" });
        } finally {
            FileUtils.deleteQuietly(dataDir);
        }
    }

    @Test
    @Category(UnlikelyTest.class)
    public void testInnerMetadataFilesAreAbsent() throws Exception {
        final String innerMetaCorruptedTable = "inner_meta_corrupted_table";
        File dataDir = ExecTest.dirTestWatcher.copyResourceToRoot(Paths.get("multilevel", "parquet"), Paths.get(innerMetaCorruptedTable));
        try {
            BaseTestQuery.test("use dfs");
            BaseTestQuery.test("refresh table metadata `%s`", innerMetaCorruptedTable);
            checkForMetadataFile(innerMetaCorruptedTable);
            File firstInnerMetadataFile = FileUtils.getFile(dataDir, "1994", METADATA_FILENAME);
            File secondInnerMetadataFile = FileUtils.getFile(dataDir, "1994", "Q3", METADATA_FILENAME);
            Assert.assertTrue(String.format("Metadata cache file '%s' isn't deleted", firstInnerMetadataFile.getPath()), firstInnerMetadataFile.delete());
            Assert.assertTrue(String.format("Metadata cache file '%s' isn't deleted", secondInnerMetadataFile.getPath()), secondInnerMetadataFile.delete());
            setTimestampToZero(dataDir);
            String query = String.format(("select dir0, dir1, o_custkey, o_orderdate from `%s` " + " where dir0=1994 or dir1='Q3'"), innerMetaCorruptedTable);
            int expectedRowCount = 60;
            int expectedNumFiles = 6;
            int actualRowCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("An incorrect result was obtained while querying a table with metadata cache files", expectedRowCount, actualRowCount);
            String numFilesPattern = "numFiles=" + expectedNumFiles;
            String usedMetaPattern = "usedMetadataFile=false";
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ numFilesPattern, usedMetaPattern }, new String[]{ "cacheFileRoot", "Filter" });
        } finally {
            FileUtils.deleteQuietly(dataDir);
        }
    }

    // DRILL-4264
    @Test
    @Category(UnlikelyTest.class)
    public void testMetadataCacheFieldWithDots() throws Exception {
        final String tableWithDots = "dfs.tmp.`complex_table`";
        try {
            BaseTestQuery.test(("create table %s as\n" + ("select cast(1 as int) as `column.with.dots`, t.`column`.`with.dots`\n" + "from cp.`store/parquet/complex/complex.parquet` t limit 1")), tableWithDots);
            String query = String.format("select * from %s", tableWithDots);
            int expectedRowCount = 1;
            int actualRowCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Row count does not match the expected value", expectedRowCount, actualRowCount);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "usedMetadataFile=false" }, null);
            BaseTestQuery.test("refresh table metadata %s", tableWithDots);
            actualRowCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Row count does not match the expected value", expectedRowCount, actualRowCount);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "usedMetadataFile=true" }, null);
        } finally {
            BaseTestQuery.test(String.format("drop table if exists %s", tableWithDots));
        }
    }

    // DRILL-4139
    @Test
    public void testBooleanPartitionPruning() throws Exception {
        final String boolPartitionTable = "dfs.tmp.`interval_bool_partition`";
        try {
            BaseTestQuery.test(("create table %s partition by (col_bln) as " + "select * from cp.`parquet/alltypes_required.parquet`"), boolPartitionTable);
            String query = String.format("select * from %s where col_bln = true", boolPartitionTable);
            int expectedRowCount = 2;
            int actualRowCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Row count does not match the expected value", expectedRowCount, actualRowCount);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "usedMetadataFile=false" }, new String[]{ "Filter" });
            BaseTestQuery.test("refresh table metadata %s", boolPartitionTable);
            actualRowCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Row count does not match the expected value", expectedRowCount, actualRowCount);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "usedMetadataFile=true" }, new String[]{ "Filter" });
        } finally {
            BaseTestQuery.test("drop table if exists %s", boolPartitionTable);
        }
    }

    // DRILL-4139
    @Test
    public void testIntWithNullsPartitionPruning() throws Exception {
        try {
            BaseTestQuery.test(("create table dfs.tmp.`t5/a` as\n" + (("select 100 as mykey from cp.`tpch/nation.parquet`\n" + "union all\n") + "select col_notexist from cp.`tpch/region.parquet`")));
            BaseTestQuery.test(("create table dfs.tmp.`t5/b` as\n" + (("select 200 as mykey from cp.`tpch/nation.parquet`\n" + "union all\n") + "select col_notexist from cp.`tpch/region.parquet`")));
            String query = "select mykey from dfs.tmp.`t5` where mykey = 100";
            int actualRowCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Row count does not match the expected value", 25, actualRowCount);
            BaseTestQuery.test("refresh table metadata dfs.tmp.`t5`");
            actualRowCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Row count does not match the expected value", 25, actualRowCount);
        } finally {
            BaseTestQuery.test("drop table if exists dfs.tmp.`t5`");
        }
    }

    // DRILL-4139
    @Test
    @Category(UnlikelyTest.class)
    public void testPartitionPruningWithIsNull() throws Exception {
        try {
            BaseTestQuery.test(("create table dfs.tmp.`t6/a` as\n" + "select col_notexist as mykey from cp.`tpch/region.parquet`"));
            BaseTestQuery.test(("create table dfs.tmp.`t6/b` as\n" + "select 100 as mykey from cp.`tpch/region.parquet`"));
            String query = "select mykey from dfs.tmp.t6 where mykey is null";
            int actualRowCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Row count does not match the expected value", 5, actualRowCount);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "usedMetadataFile=false" }, new String[]{ "Filter" });
            BaseTestQuery.test("refresh table metadata dfs.tmp.`t6`");
            actualRowCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Row count does not match the expected value", 5, actualRowCount);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "usedMetadataFile=true" }, new String[]{ "Filter" });
        } finally {
            BaseTestQuery.test("drop table if exists dfs.tmp.`t6`");
        }
    }

    // DRILL-4139
    @Test
    public void testPartitionPruningWithIsNotNull() throws Exception {
        try {
            BaseTestQuery.test(("create table dfs.tmp.`t7/a` as\n" + "select col_notexist as mykey from cp.`tpch/region.parquet`"));
            BaseTestQuery.test(("create table dfs.tmp.`t7/b` as\n" + "select 100 as mykey from cp.`tpch/region.parquet`"));
            String query = "select mykey from dfs.tmp.t7 where mykey is null";
            int actualRowCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Row count does not match the expected value", 5, actualRowCount);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "usedMetadataFile=false" }, new String[]{ "Filter" });
            BaseTestQuery.test("refresh table metadata dfs.tmp.`t7`");
            actualRowCount = BaseTestQuery.testSql(query);
            Assert.assertEquals("Row count does not match the expected value", 5, actualRowCount);
            PlanTestBase.testPlanMatchingPatterns(query, new String[]{ "usedMetadataFile=true" }, new String[]{ "Filter" });
        } finally {
            BaseTestQuery.test("drop table if exists dfs.tmp.`t7`");
        }
    }

    @Test
    public void testEmptyDirectoryWithMetadataFile() throws Exception {
        final String emptyDirNameWithMetadataFile = "empty_directory";
        ExecTest.dirTestWatcher.makeTestTmpSubDir(Paths.get(emptyDirNameWithMetadataFile));
        ExecTest.dirTestWatcher.copyResourceToTestTmp(Paths.get("parquet", "metadata_files_with_old_versions", "v3_1", "metadata_table.requires_replace.txt"), Paths.get(emptyDirNameWithMetadataFile, METADATA_FILENAME));
        final BatchSchema expectedSchema = new SchemaBuilder().build();
        BaseTestQuery.testBuilder().sqlQuery("select * from dfs.tmp.`%s`", emptyDirNameWithMetadataFile).schemaBaseLine(expectedSchema).build().run();
    }

    @Test
    public void testEmptyDirectoryWithMetadataDirFile() throws Exception {
        final String emptyDirNameWithMetadataFile = "empty_directory";
        ExecTest.dirTestWatcher.makeTestTmpSubDir(Paths.get(emptyDirNameWithMetadataFile));
        ExecTest.dirTestWatcher.makeTestTmpSubDir(Paths.get(emptyDirNameWithMetadataFile, "t2"));
        ExecTest.dirTestWatcher.makeTestTmpSubDir(Paths.get(emptyDirNameWithMetadataFile, "t1"));
        ExecTest.dirTestWatcher.copyResourceToTestTmp(Paths.get("parquet", "metadata_files_with_old_versions", "v3_1", "metadata_directories.requires_replace.txt"), Paths.get(emptyDirNameWithMetadataFile, METADATA_DIRECTORIES_FILENAME));
        final BatchSchema expectedSchema = new SchemaBuilder().build();
        BaseTestQuery.testBuilder().sqlQuery("select * from dfs.tmp.`%s`", emptyDirNameWithMetadataFile).schemaBaseLine(expectedSchema).build().run();
    }

    @Test
    public void testAutoRefreshPartitionPruning() throws Exception {
        BaseTestQuery.test(("create table dfs.tmp.`orders` partition by (o_orderstatus) as\n" + "select * from cp.`tpch/orders.parquet`"));
        BaseTestQuery.test("refresh table metadata dfs.tmp.`orders`");
        File ordersTable = new File(ExecTest.dirTestWatcher.getDfsTestTmpDir(), "orders");
        // sets last-modified time of directory greater than the time of cache file to force metadata cache file auto-refresh
        Assert.assertTrue("Unable to change the last-modified time of table directory", ordersTable.setLastModified(((new File(ordersTable, Metadata.METADATA_FILENAME).lastModified()) + 100500)));
        String query = "select * from dfs.tmp.`orders`\n" + "where o_orderstatus = 'O' and o_orderdate < '1995-03-10'";
        PlanTestBase.testPlanOneExpectedPattern(query, "numRowGroups=1");
        int actualRowCount = BaseTestQuery.testSql(query);
        Assert.assertEquals("Row count does not match the expected value", 1, actualRowCount);
        // TODO: Check that metadata cache file is actually regenerated, once Drill will use JDK version with resolved JDK-8177809.
    }
}

